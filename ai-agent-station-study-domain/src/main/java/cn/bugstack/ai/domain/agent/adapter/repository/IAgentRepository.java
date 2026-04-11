package cn.bugstack.ai.domain.agent.adapter.repository;

import cn.bugstack.ai.domain.agent.model.valobj.*;

import java.util.List;
import java.util.Map;

/**
 * AI Agent 仓储接口
 * 负责 AI 客户端相关数据的查询和装配
 *
 * @author xiaofuge bugstack.cn @小傅哥
 */
public interface IAgentRepository {

    /**
     * 根据客户端ID列表查询 API 配置信息
     *
     * @param clientIdList 客户端ID列表
     * @return API 配置值对象列表（包含 baseUrl、apiKey、completionsPath 等）
     */
    List<AiClientApiVO> queryAiClientApiVOListByClientIds(List<String> clientIdList);

    /**
     * 根据客户端ID列表查询对话模型配置信息
     *
     * @param clientIdList 客户端ID列表
     * @return 对话模型配置值对象列表（包含 modelId、modelName、modelType 等）
     */
    List<AiClientModelVO> AiClientModelVOByClientIds(List<String> clientIdList);

    /**
     * 根据客户端ID列表查询 MCP 工具配置信息
     *
     * @param clientIdList 客户端ID列表
     * @return MCP 工具配置值对象列表（包含 mcpId、mcpName、transportType、transportConfig 等）
     */
    List<AiClientToolMcpVO> AiClientToolMcpVOByClientIds(List<String> clientIdList);

    /**
     * 根据客户端ID列表查询系统提示词配置信息（列表形式）
     *
     * @param clientIdList 客户端ID列表
     * @return 系统提示词配置值对象列表（包含 promptId、promptName、promptContent 等）
     */
    List<AiClientSystemPromptVO> AiClientSystemPromptVOByClientIds(List<String> clientIdList);

    /**
     * 根据客户端ID列表查询系统提示词配置信息（Map 形式，便于快速查找）
     *
     * @param clientIdList 客户端ID列表
     * @return 系统提示词 Map，key 为 promptId，value 为提示词配置对象
     */
    Map<String, AiClientSystemPromptVO> queryAiClientSystemPromptMapByClientIds(List<String> clientIdList);

    /**
     * 根据客户端ID列表查询顾问（Advisor）配置信息
     *
     * @param clientIdList 客户端ID列表
     * @return 顾问配置值对象列表（包含 advisorId、advisorName、advisorType、chatMemory、ragAnswer 等）
     */
    List<AiClientAdvisorVO> AiClientAdvisorVOByClientIds(List<String> clientIdList);

    /**
     * 根据客户端ID列表查询客户端基础配置信息
     *
     * @param clientIdList 客户端ID列表
     * @return 客户端配置值对象列表（包含 clientId、clientName、description、关联的 modelId/promptIdList/mcpIdList/advisorIdList 等）
     */
    List<AiClientVO> AiClientVOByClientIds(List<String> clientIdList);

    /**
     * 根据模型ID列表查询关联的 API 配置信息
     *
     * @param modelIdList 模型ID列表
     * @return API 配置值对象列表
     */
    List<AiClientApiVO> queryAiClientApiVOListByModelIds(List<String> modelIdList);

    /**
     * 根据模型ID列表查询模型配置信息
     *
     * @param modelIdList 模型ID列表
     * @return 模型配置值对象列表
     */
    List<AiClientModelVO> AiClientModelVOByModelIds(List<String> modelIdList);

}
