package com.neure.agent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * EnumTree
 *
 * @author tc
 * @date 2024-02-27 23:24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnumTree {
    List<EnumTree> children = new ArrayList<>(0);
    private Integer id = -1;
    private String name = "";
    private String type = "";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<EnumTree> getChildren() {
        return children;
    }

    public void setChildren(List<EnumTree> children) {
        this.children = children;
    }
}
