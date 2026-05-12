package cn.bugstack.ai.domain.agent.service.armory;

import cn.bugstack.ai.domain.agent.model.entity.ArmoryCommandEntity;
import cn.bugstack.ai.domain.agent.model.valobj.enums.AiAgentEnumVO;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientToolMcpVO;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.alibaba.fastjson.JSON;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * MCP客户端配置节点
 */
@Slf4j
@Service
public class AiClientToolMcpNode extends AbstractArmorySupport{

    @Resource
    private AiClientModelNode aiClientModelNode;

    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai Agent 构建，Tool MCP 工具配置{}", JSON.toJSONString(requestParameter));

        List<AiClientToolMcpVO> aiClientToolMcpVOList=dynamicContext.getValue(dataName());

        if(aiClientToolMcpVOList==null || aiClientToolMcpVOList.size()==0){
            log.warn("没有需要被初始化的 ai client tool mcp");
            return router(requestParameter,dynamicContext);
        }
        for(AiClientToolMcpVO aiClientToolMcpVO : aiClientToolMcpVOList){
            //创建mcp服务
            McpSyncClient mcpSyncClient = createMcpClient(aiClientToolMcpVO);
            //注册mcp对象
            registerBean(beanName(aiClientToolMcpVO.getMcpId()), McpSyncClient.class, mcpSyncClient);
        }
        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity armoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientModelNode;
    }

    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getBeanName(beanId);
    }

    @Override
    protected String dataName() {
        return AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getDataName();
    }

    private McpSyncClient createMcpClient(AiClientToolMcpVO aiClientToolMcpVO) {
        String transportType = aiClientToolMcpVO.getTransportType();

        try {
            switch (transportType) {
                case "sse" -> {
                    AiClientToolMcpVO.TransportConfigSse transportConfigSse = aiClientToolMcpVO.getTransportConfigSse();
                    String baseUri = transportConfigSse.getBaseUri();
                    String sseEndpoint = transportConfigSse.getSseEndpoint();

                    var transport = HttpClientSseClientTransport.builder(baseUri)
                            .sseEndpoint(sseEndpoint)
                            .build();

                    var mcpSyncClient = McpClient.sync(transport)
                            .requestTimeout(Duration.ofSeconds(aiClientToolMcpVO.getRequestTimeout()))
                            .build();
                    var init_sse = mcpSyncClient.initialize();

                    log.info("Tool SSE MCP Initialized {}", init_sse);
                    return mcpSyncClient;

                }
                case "stdio" -> {
                    AiClientToolMcpVO.TransportConfigStdio transportConfigStdio = aiClientToolMcpVO.getTransportConfigStdio();
                    Map<String, AiClientToolMcpVO.TransportConfigStdio.Stdio> stdioMap = transportConfigStdio.getStdio();
                    AiClientToolMcpVO.TransportConfigStdio.Stdio stdio = stdioMap.get(aiClientToolMcpVO.getMcpName());

                    String command = stdio.getCommand();
                    List<String> args = stdio.getArgs();
                    Map<String, String> env = stdio.getEnv();

                    // 检测是否为 SSH 桥接模式（command 为 ssh 且第一个参数包含 @）
                    if ("ssh".equals(command) && args != null && !args.isEmpty() && args.get(0).contains("@")) {
                        log.info("检测到 SSH 桥接模式，目标: {}", args.get(0));
                        
                        // SSH 模式下，将所有参数合并为一条远程命令
                        String remoteCommand = String.join(" ", args.subList(1, args.size()));
                        List<String> sshArgs = new java.util.ArrayList<>();
                        sshArgs.add(args.get(0)); // root@192.168.136.128
                        sshArgs.add(remoteCommand); // docker run ...

                        var stdioParams = ServerParameters.builder(command)
                                .args(sshArgs)
                                .env(env)
                                .build();

                        var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                                .requestTimeout(Duration.ofSeconds(aiClientToolMcpVO.getRequestTimeout())).build();
                        var init_stdio = mcpClient.initialize();

                        log.info("Tool SSH-Stdio MCP Initialized {}", init_stdio);
                        return mcpClient;
                    }

                    // 原有的本地 stdio 模式
                    var stdioParams = ServerParameters.builder(command)
                            .args(args)
                            .env(env)
                            .build();

                    var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                            .requestTimeout(Duration.ofSeconds(aiClientToolMcpVO.getRequestTimeout())).build();
                    var init_stdio = mcpClient.initialize();

                    log.info("Tool Stdio MCP Initialized {}", init_stdio);
                    return mcpClient;
                }
            }
        } catch (Exception e) {
            log.error("MCP 客户端初始化失败: mcpId={}, mcpName={}, transportType={}", 
                    aiClientToolMcpVO.getMcpId(), aiClientToolMcpVO.getMcpName(), transportType, e);
            throw new RuntimeException("MCP 初始化失败: " + aiClientToolMcpVO.getMcpId(), e);
        }

        throw new RuntimeException("err! transportType " + transportType + " not exist!");
    }

}














