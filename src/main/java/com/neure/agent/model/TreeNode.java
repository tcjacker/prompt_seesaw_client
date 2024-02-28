package com.neure.agent.model;


import com.neure.agent.constant.TreeType;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 * TreeNode
 *
 * @author tc
 * @date 2024-02-24 21:43
 */
public class TreeNode extends DefaultMutableTreeNode {

    private Integer id = -1;
    private String name = "";
    private String type = "";

    private String baseType = "";

    private String status = "";
    private List<TreeNode> children = new ArrayList<>();

    public TreeNode() {
    }

    // 构造函数
    public static TreeNode build(String name, String type, String baseType) {
        TreeNode node = build(name, type);
        if (TreeType.PROMPT_FOLDER.type().equalsIgnoreCase(type) || TreeType.PROMPT.type().equalsIgnoreCase(type)) {
            node.baseType = TreeType.PROMPT.type();
        } else if (TreeType.SECTION_FOLDER.type().equalsIgnoreCase(type) || TreeType.SECTION.type().equalsIgnoreCase(type)) {
            node.baseType = TreeType.SECTION.type();
        } else if (TreeType.FOLDER.type().equalsIgnoreCase(type)) {
            node.baseType = baseType;
        }
        return node;
    }

    private TreeNode(String name) {
        super(name);
        this.name = name;
    }

    public static TreeNode build(String name, String type) {
        String n = buildName(name, type);
        TreeNode node = new TreeNode(n);
        node.type = type;
        return node;

    }

    public static String buildName(String name, String type) {
        if (name.contains(".")) {
            throw new IllegalArgumentException();
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

    public void setName(String name) {
        if (this.name == null){
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.userObject = name;
    }



    // 添加子节点
    public void addChild(TreeNode child) {
        children.add(child);
        super.add(child);

    }

    public void add(TreeNode child) {
        addChild(child);
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBaseType(String type) {
        this.baseType = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setChildren(List<TreeNode> children) {
        for (TreeNode t : children) {
            add(t);
        }
    }


    public String getBaseType() {
        return baseType;
    }
}
