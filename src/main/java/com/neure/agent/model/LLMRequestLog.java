package com.neure.agent.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * LLMRequestLog
 *
 * @author tc
 * @date 2024-02-25 16:18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LLMRequestLog {

    @JsonProperty("prompt_id")
    public Integer promptId = 0;
    @JsonProperty("prompt_type")
    public String prompt_type;
    public DefaultLLMRequestDTO request;
    public String response = "";

    public String ip;
    @JsonProperty("request_datetime")
    public String requestTime;

    public Integer getPromptId() {
        return promptId;
    }

    public void setPromptId(Integer promptId) {
        this.promptId = promptId;
    }

    public String getPrompt_type() {
        return prompt_type;
    }

    public void setPrompt_type(String prompt_type) {
        this.prompt_type = prompt_type;
    }

    public DefaultLLMRequestDTO getRequest() {
        return request;
    }

    public void setRequest(DefaultLLMRequestDTO request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }
}
