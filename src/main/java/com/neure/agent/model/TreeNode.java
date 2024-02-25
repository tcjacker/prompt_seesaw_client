package com.neure.agent.model;


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

    private String status = "";
    private List<TreeNode> children = new ArrayList<>();

    public TreeNode(){}
    // 构造函数
    public TreeNode(String name, String type) {
        super(name);
        this.name = name;
        this.type = type;
    }

    public TreeNode(String name) {
        super(name);
        this.name = name;
    }

    // 添加子节点
    public void addChild(TreeNode child) {
        children.add(child);
        super.add(child);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }
}
