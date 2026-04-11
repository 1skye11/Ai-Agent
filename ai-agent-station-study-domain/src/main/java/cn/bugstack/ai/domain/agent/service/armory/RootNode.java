package cn.bugstack.ai.domain.agent.service.armory;

import cn.bugstack.ai.domain.agent.model.entity.ArmoryCommandEntity;
import cn.bugstack.ai.domain.agent.model.valobj.AiAgentEnumVO;
import cn.bugstack.ai.domain.agent.service.armory.business.data.ILoadDataStrategy;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 根节点，数据加载
 * router() 方法内部会按顺序执行：
 *     1. multiThread()    ← 先加载数据
 *     2. doApplyLogic()   ← 执行业务逻辑（空实现）
 *     3. get()            ← 获取下一个节点
 *     4. 递归调用下一个节点的 apply()
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/6/27 16:47
 */
@Slf4j
@Service
public class RootNode extends AbstractArmorySupport {

    @Resource
    private AiClientApiNode aiClientApiNode;

    private final Map<String, ILoadDataStrategy> loadDataStrategyMap;

    /**
     * 作用：存储所有数据加载策略
     * 来源：Spring 自动注入所有实现 ILoadDataStrategy 接口的 Bean
     * @param loadDataStrategyMap
     */
    public RootNode(Map<String, ILoadDataStrategy> loadDataStrategyMap) {
        this.loadDataStrategyMap = loadDataStrategyMap;
    }

    /**
     * 输入: commandType = "client", commandIdList = ["3001"]
     *     ↓
     * 查找枚举: AiAgentEnumVO.AI_CLIENT
     *     ↓
     * 获取策略名: "aiClientLoadDataStrategy"
     *     ↓
     * 执行策略: AiClientLoadDataStrategy.loadData()
     *     ├─→ 异步查询 ai_client_api      → 1 条
     *     ├─→ 异步查询 ai_client_model    → 1 条
     *     ├─→ 异步查询 ai_client_tool_mcp → 0 条
     *     ├─→ 异步查询 ai_client_system_prompt → 2 条
     *     ├─→ 异步查询 ai_client_advisor  → 1 条
     *     └─→ 异步查询 ai_client          → 1 条
     *     ↓
     * 存入: dynamicContext (供后续节点使用)
     * @param requestParameter
     * @param dynamicContext
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    @Override
    protected void multiThread(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {
//        // 通过策略加载数据
//        // 1. 获取命令类型（如 "client"）
//        String commandType = requestParameter.getCommandType();
//        // 2. 根据类型找到对应的枚举
//        AiAgentEnumVO aiAgentEnumVO= AiAgentEnumVO.getByCode(commandType);
//        // 3. 获取策略 Key（如 "aiClientLoadDataStrategy"）
//        String loadDataStrategyKey= aiAgentEnumVO.getLoadDataStrategy();

        // 4. 从 Map 中获取具体策略
        ILoadDataStrategy loadDataStrategy = loadDataStrategyMap.get(requestParameter.getLoadDataStrategy());
        // 5. 执行数据加载（异步查询 6 张表）
        loadDataStrategy.loadData(requestParameter, dynamicContext);
    }

    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return router(requestParameter, dynamicContext);
    }

    /**
     * 决定下一个执行哪个节点
     * @param armoryCommandEntity
     * @param dynamicContext
     * @return
     * @throws Exception
     */
    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity armoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientApiNode;
    }

}
