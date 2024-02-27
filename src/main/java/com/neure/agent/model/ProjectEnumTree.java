package com.neure.agent.model;

/**
 * ProjectEnumTree
 *
 * @author tc
 * @date 2024-02-27 23:33
 */
public class ProjectEnumTree {
    EnumTree sections;

    EnumTree templates;

    public EnumTree getSections() {
        return sections;
    }

    public void setSections(EnumTree sections) {
        this.sections = sections;
    }

    public EnumTree getTemplates() {
        return templates;
    }

    public void setTemplates(EnumTree templates) {
        this.templates = templates;
    }
}
