package com.neure.agent.model;


import com.neure.agent.constant.TreeType;
import com.neure.agent.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 * TreeNode
 *
 * @author tc
 * @date 2024-02-24 21:43
 */
@Slf4j
public class PromptNode extends DefaultMutableTreeNode {

    private Integer id = -1;
    private String name = "";
    private String type = "";

    private String baseType = "";

    private String status = "";
    private final List<PromptNode> children = new ArrayList<>();

    public PromptNode() {
    }

    private PromptNode(String name) {
        super(name);
        this.name = name;
    }

    // 构造函数
    public static PromptNode build(String name, String type, String baseType) {
        PromptNode node = build(name, type);
        if (TreeType.PROMPT_FOLDER.type().equalsIgnoreCase(type) || TreeType.PROMPT.type().equalsIgnoreCase(type)) {
            node.baseType = TreeType.PROMPT.type();
        } else if (TreeType.SECTION_FOLDER.type().equalsIgnoreCase(type) || TreeType.SECTION.type().equalsIgnoreCase(type)) {
            node.baseType = TreeType.SECTION.type();
        } else if (TreeType.FOLDER.type().equalsIgnoreCase(type)) {
            node.baseType = baseType;
        }
        return node;
    }

    public static PromptNode build(String name, String type) {
        String n = buildName(name, type);
        PromptNode node = new PromptNode(n);
        node.type = type;
        return node;

    }

    public static String buildName(String name, String type) {
        if (name.endsWith(nameSuffix(type))) {
            return name;
        }
        return name + nameSuffix(type);
    }

    public static String nameSuffix(String type) {
        if (type == null || type.equalsIgnoreCase("")) {
            throw new IllegalArgumentException();
        }
        if (TreeType.SECTION.type().equalsIgnoreCase(type)) {
            return ".sec";
        } else if (TreeType.PROMPT.type().equalsIgnoreCase(type)) {
            return ".pmt";
        } else {
            return "";
        }
    }

    // 添加子节点
    public void addChild(PromptNode child) {
        children.add(child);
        super.add(child);

    }

    public void add(PromptNode child) {
        addChild(child);
    }

    // Getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name == null) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.userObject = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PromptNode> getChildren() {
        return children;
    }

    public void setChildren(List<PromptNode> children) {
        for (PromptNode t : children) {
            add(t);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String type) {
        this.baseType = type;
    }

    public void deleteChild(PromptNode selectedNode) {
        for (int i = 0; i < this.children.size(); i++) {
            PromptNode n = this.children.get(i);
            if (n.id.equals(selectedNode.id)) {
                this.children.remove(i);
                return;
            }
        }
        log.error("selectedNode [{}] 不存在", JacksonUtils.ObjectToJsonStr(selectedNode));
    }
}
