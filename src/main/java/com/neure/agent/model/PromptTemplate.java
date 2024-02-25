package com.neure.agent.model;

/**
 * PromptTemplate
 *
 * @author tc
 * @date 2024-02-25 15:45
 */
public class PromptTemplate {

    private Integer id = 0;
    private String content = "";

    private String status = "";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
