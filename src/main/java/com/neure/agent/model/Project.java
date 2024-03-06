package com.neure.agent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Project
 *
 * @author tc
 * @date 2024-02-26 00:19
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {

    private int id;

    private String name;

    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
