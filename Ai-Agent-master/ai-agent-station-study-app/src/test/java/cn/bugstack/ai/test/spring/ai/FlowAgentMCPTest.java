package cn.bugstack.ai.test.spring.ai;

import com.alibaba.fastjson.JSON;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/8/9 09:15
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class FlowAgentMCPTest {

    // 注入我们在数据库中配置好的 5008 MCP Bean
    @Resource
    private McpSyncClient ai_client_tool_mcp_5008;

    @Test
    public void test() {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl("https://apis.itedus.cn")
                        .apiKey("REPLACE_WITH_YOUR_API_KEY")
                        .completionsPath("v1/chat/completions")
                        .embeddingsPath("v1/embeddings")
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4.1")
                        // 使用注入的 Bean，而不是自己再去启动 Docker
                        .toolCallbacks(new SyncMcpToolCallbackProvider(ai_client_tool_mcp_5008).getToolCallbacks())
                        .build())
                .build();

        ChatResponse call = chatModel.call(Prompt.builder().messages(new UserMessage("有哪些工具可以使用")).build());
        log.info("测试结果:{}", JSON.toJSONString(call.getResult()));
    }

    /**
     * https://github.com/awesimon/elasticsearch-mcp
     * https://www.npmjs.com/package/@awesome-ai/elasticsearch-mcp
     * npm i @awesome-ai/elasticsearch-mcp
     */
    public McpSyncClient stdioMcpClientElasticsearch() {
        Map<String, String> env = new HashMap<>();
        env.put("ES_HOST", "http://192.168.1.110:9200");
        env.put("ES_API_KEY", "none");

        var stdioParams = ServerParameters.builder("npx")
                .args("-y", "@awesome-ai/elasticsearch-mcp")
                .env(env)
                .build();

        var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                .requestTimeout(Duration.ofSeconds(100)).build();

        var init = mcpClient.initialize();

        System.out.println("Stdio MCP Initialized: " + init);

        return mcpClient;

    }
//普罗米修斯连接到的mcp服务
    public McpSyncClient stdioMcpClient_Grafana() {
        Map<String, String> env = new HashMap<>();
        env.put("GRAFANA_URL", "http://192.168.136.128:9200");
        env.put("GRAFANA_API_KEY", "REPLACE_WITH_YOUR_GRAFANA_API_KEY");

        var stdioParams = ServerParameters.builder("docker")
                .args("run",
                        "--rm",
                        "-i",
                        "-e",
                        "GRAFANA_URL",
                        "-e",
                        "GRAFANA_API_KEY",
                        "mcp/grafana",
                        "-t",
                        "stdio")
                .env(env)
                .build();

        var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                .requestTimeout(Duration.ofSeconds(100)).build();

        var init = mcpClient.initialize();
        log.info("Stdio MCP Initialized: {}", init);

        return mcpClient;
    }

    public McpSyncClient sseMcpClient_BaiduSearch() {
        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport.builder("http://appbuilder.baidu.com/v2/ai_search/mcp/")
                .sseEndpoint("sse?api_key=REPLACE_WITH_YOUR_API_KEY")
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(360)).build();
        var init_sse = mcpSyncClient.initialize();
        log.info("Tool SSE MCP Initialized {}", init_sse);

        return mcpSyncClient;
    }

    public McpSyncClient sseMcpClient_csdn() {

        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport.builder("http://192.168.1.108:8102").build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(180)).build();

        var init = mcpSyncClient.initialize();
        System.out.println("SSE MCP Initialized: " + init);

        return mcpSyncClient;
    }

    public McpSyncClient sseMcpClient02_weixin() {

        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport.builder("http://192.168.1.108:8101").build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(180)).build();

        var init = mcpSyncClient.initialize();
        System.out.println("SSE MCP Initialized: " + init);

        return mcpSyncClient;
    }

}
