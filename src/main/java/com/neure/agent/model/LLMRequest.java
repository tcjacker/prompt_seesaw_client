package com.neure.agent.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * LLMRequest
 *
 * @author tc
 * @date 2024-03-03 12:53
 */
public class LLMRequest {

    String model;
    @JsonProperty("prompt_id")
    Integer promptId;
    String type = "prompt";
    String prompt;
    List<Map<String,String>> history;
    @JsonProperty("json_format")
    Boolean jsonFormat;
    Double temperature;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<Map<String, String>> getHistory() {
        return history;
    }

    public void setHistory(List<Map<String, String>> history) {
        this.history = history;
    }

    public Boolean getJsonFormat() {
        return jsonFormat;
    }

    public void setJsonFormat(Boolean jsonFormat) {
        this.jsonFormat = jsonFormat;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getPromptId() {
        return promptId;
    }

    public void setPromptId(Integer promptId) {
        this.promptId = promptId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
