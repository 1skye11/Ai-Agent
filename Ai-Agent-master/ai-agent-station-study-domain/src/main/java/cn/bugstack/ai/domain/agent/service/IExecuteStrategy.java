package cn.bugstack.ai.domain.agent.service;

import cn.bugstack.ai.domain.agent.model.entity.ExecuteCommandEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface IExecuteStrategy {

    void execute(ExecuteCommandEntity executeCommandEntity, ResponseBodyEmitter emitter) throws  Exception;
}
