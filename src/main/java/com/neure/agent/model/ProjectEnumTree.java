package com.neure.agent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * ProjectEnumTree
 *
 * @author tc
 * @date 2024-02-27 23:33
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectEnumTree {

    @JsonProperty("id")
    Integer id;

    @JsonProperty("project_id")
    String projectId;
    @JsonProperty("section_tree")
    List<EnumTree> sections;

    @JsonProperty("template_tree")
    List<EnumTree> templates;

    public List<EnumTree> getSections() {
        return sections;
    }

    public void setSections(List<EnumTree> sections) {
        this.sections = sections;
    }

    public List<EnumTree> getTemplates() {
        return templates;
    }

    public void setTemplates(List<EnumTree> templates) {
        this.templates = templates;
    }
}
