package com.neure.agent.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * LLMRequestLog
 *
 * @author tc
 * @date 2024-02-25 16:18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LLMRequestLog {

    public Integer id = 0;
    public Integer promptTemplateId = 0;
    public String request = "";
    public String response = "";
    public Date requestTime = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPromptTemplateId() {
        return promptTemplateId;
    }

    public void setPromptTemplateId(Integer promptTemplateId) {
        this.promptTemplateId = promptTemplateId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }
}
