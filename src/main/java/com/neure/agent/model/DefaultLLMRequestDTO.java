package com.neure.agent.model;

import java.util.List;

/**
 * DefaultLLMRequestDTO
 *
 * @author tc
 * @date 2024-03-05 23:24
 */
public class DefaultLLMRequestDTO {
    private String model;
    private String prompt;
    private List<String> history;
    private boolean json_format;
    private int frequency_penalty;
    private int presence_penalty;
    private double temperature;
    private double top_p;
    private int prompt_id;
    private String prompt_type;

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

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }

    public boolean isJson_format() {
        return json_format;
    }

    public void setJson_format(boolean json_format) {
        this.json_format = json_format;
    }

    public int getFrequency_penalty() {
        return frequency_penalty;
    }

    public void setFrequency_penalty(int frequency_penalty) {
        this.frequency_penalty = frequency_penalty;
    }

    public int getPresence_penalty() {
        return presence_penalty;
    }

    public void setPresence_penalty(int presence_penalty) {
        this.presence_penalty = presence_penalty;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTop_p() {
        return top_p;
    }

    public void setTop_p(double top_p) {
        this.top_p = top_p;
    }

    public int getPrompt_id() {
        return prompt_id;
    }

    public void setPrompt_id(int prompt_id) {
        this.prompt_id = prompt_id;
    }

    public String getPrompt_type() {
        return prompt_type;
    }

    public void setPrompt_type(String prompt_type) {
        this.prompt_type = prompt_type;
    }
}
