package com.neure.agent.ui;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * TreeNode
 *
 * @author tc
 * @date 2024-02-24 21:43
 */
@Data
public class TreeNode {
    private String name = "";
    private String type = "";
    private List<TreeNode> children = new ArrayList<>();

    public TreeNode(){}
    // 构造函数
    public TreeNode(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // 添加子节点
    public void addChild(TreeNode child) {
        children.add(child);
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


}
