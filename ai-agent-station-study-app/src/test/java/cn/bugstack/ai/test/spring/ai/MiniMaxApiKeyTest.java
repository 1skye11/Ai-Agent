package cn.bugstack.ai.test.spring.ai;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MiniMaxApiKeyTest {

    @Test
    public void testMiniMaxApiKey() {
        try {
            // 构建 OpenAI API 客户端（MiniMax 兼容 OpenAI 格式）
            OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl("https://api.minimax.chat")
                    .apiKey("REPLACE_WITH_YOUR_API_KEY")
                    .completionsPath("v1/chat/completions")
                    .embeddingsPath("v1/embeddings")
                    .build();

            // 创建聊天模型
            OpenAiChatModel chatModel = OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .defaultOptions(OpenAiChatOptions.builder()
                            .model("abab5.5-chat")
                            .build())
                    .build();

            // 发送测试消息
            String response = chatModel.call("你好，请回复 OK");
            
            log.info("✅ MiniMax API Key 有效！");
            log.info("响应内容: {}", response);
            
        } catch (Exception e) {
            log.error("❌ MiniMax API Key 无效或出现错误", e);
            log.error("错误信息: {}", e.getMessage());
        }
    }
}
