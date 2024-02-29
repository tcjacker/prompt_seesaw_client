package com.neure.agent.server;

import com.neure.agent.model.PromptNode;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Session
 *
 * @author tc
 * @date 2024-02-25 14:27
 */
public class Session {

    int projectId ;

    String token;

    String url;

    PromptNode sectionTree;

    PromptNode promptTree;
    private final ConcurrentMap<String, Object> params = new ConcurrentHashMap<>();

    public void set(String key, Object o) {
        params.put(key, o);
    }

    public Object get(String key) {
        return params.get(key);
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PromptNode getSectionTree() {
        return sectionTree;
    }

    public void setSectionTree(PromptNode sectionTree) {
        this.sectionTree = sectionTree;
    }

    public PromptNode getPromptTree() {
        return promptTree;
    }

    public void setPromptTree(PromptNode promptTree) {
        this.promptTree = promptTree;
    }
}
