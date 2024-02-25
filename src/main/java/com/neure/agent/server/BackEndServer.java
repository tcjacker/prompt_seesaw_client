package com.neure.agent.server;

import com.neure.agent.model.LLMRequestLog;
import com.neure.agent.model.PromptTemplate;
import com.neure.agent.model.TreeNode;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * BackEndServer
 *
 * @author tc
 * @date 2024-02-25 14:55
 */
public class BackEndServer {

    Session session;

    private String url;

    public TreeNode getPromptTree(){
        TreeNode rootData = new TreeNode("Root", "Type1");
        rootData.addChild(new TreeNode("Child 1", "Type2"));
        rootData.addChild(new TreeNode("Child 2", "Type3"));
        TreeNode child3 = new TreeNode("Child 3", "Type4");
        child3.addChild(new TreeNode("Grandchild 1", "Type5"));
        rootData.addChild(child3);
        return rootData;
    }

    public boolean addTreeNode(TreeNode node){
        return false;
    }

    public PromptTemplate getPrompt(Integer id){
        return null;
    }

    public String sendRequest(String content,Integer id, String model, Double temperature){
        return null;
    }

    public boolean publishPrompt(Integer id){
        return false;
    }

    public List<LLMRequestLog> history(Integer id){
        return null;
    }
}
