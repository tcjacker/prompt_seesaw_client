package com.neure.agent.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.neure.agent.client.HttpRequestClient;
import com.neure.agent.constant.TreeType;
import com.neure.agent.model.*;
import com.neure.agent.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * BackEndServer
 *
 * @author tc
 * @date 2024-02-25 14:55
 */
@Slf4j
public class BackEndServer {

    Session session;


    public BackEndServer(Session session) {
        this.session = session;
    }

    public TreeNode reNamePrompt(String nodeName) {
        return new TreeNode(nodeName);
    }


    public TreeNode getPromptTree() {
        int projectId = session.projectId;
        Project project = queryProject(projectId);
        if (project == null) {
            return initialTree();
        }
        TreeNode rootData = new TreeNode(project.getName(), TreeType.ROOT.type(),TreeType.ROOT.type());
        TreeNode section = new TreeNode("Section", TreeType.SECTION_FOLDER.type(),TreeType.SECTION.type());
        TreeNode prompt = new TreeNode("Prompt", TreeType.PROMPT_FOLDER.type(),TreeType.PROMPT.type());
        rootData.add(section);
        rootData.add(prompt);
        session.setSectionTree(section);
        session.setPromptTree(prompt);
        return rootData;
    }

    private TreeNode initialTree() {
        TreeNode root = new TreeNode("空", TreeType.ROOT.type(),TreeType.ROOT.type());
        TreeNode section = new TreeNode("Section", TreeType.SECTION_FOLDER.type(),TreeType.SECTION.type());
        TreeNode prompt = new TreeNode("Prompt", TreeType.PROMPT_FOLDER.type(),TreeType.PROMPT.type());
        root.add(section);
        root.add(prompt);
        return root;
    }

    public Project queryProject(int id) {
        String requestUrl = session.url + "project/get/" + id;
        DefaultResponse<Project> response = HttpRequestClient.sendGet(requestUrl,Project.class);
        if (response.isSuccess()){
            return response.getBody();
        }
        return null;
    }



    /**
     * 只支持全量更新
     *
     * @param treeNode
     */
    public void updateProjectTree(String type) {
        if (TreeType.SECTION.type().equalsIgnoreCase(type)){
            TreeNode sectionTree = session.getSectionTree();
            //TODO:更新section
        }else if (TreeType.PROMPT.type().equalsIgnoreCase(type)){
            TreeNode pompt = session.getPromptTree();
            //TODO:更新prompt
        }


    }


    public boolean addPromptTreeNode(TreeNode node) {
        PromptTemplate promptTemplate = new PromptTemplate();
        promptTemplate.setName(node.getName());
        promptTemplate.setProjectId(session.projectId);
        promptTemplate.setContent(" ");
        String requestUrl = session.url + "prompt_template/create";
        DefaultResponse<Integer> response = HttpRequestClient.sendPost(requestUrl,promptTemplate,Integer.class);
        if (response.isSuccess()){
            int id = response.getBody();
            if (id == -1){
                return false;
            }
            node.setId(id);
            return true;
        }
        return false;
    }

    public boolean addSectionTreeNode(TreeNode node) {
        PromptSection promptSection = new PromptSection();
        promptSection.setName(node.getName());
        promptSection.setProjectId(session.projectId);
        promptSection.setContent(" ");
        String requestUrl = session.url + "prompt_section/create";
        DefaultResponse<Integer> response = HttpRequestClient.sendPost(requestUrl,promptTemplate,Integer.class);
        if (response.isSuccess()){
            int id = response.getBody();
            if (id == -1){
                return false;
            }
            node.setId(id);
            return true;
        }
        return false;
    }

    public PromptTemplate getPrompt(Integer id) {
        return null;
    }

    public PromptSection getSection(Integer id) {
        return null;
    }

    public String sendRequest(String content, Integer id, String model, Double temperature) {
        return null;
    }

    public boolean publishPrompt(Integer id) {
        return false;
    }

    public List<LLMRequestLog> history(Integer id) {
        return null;
    }

    public boolean addNode(TreeNode childNode, String type) {
        return switch (TreeType.get(type)) {
            case SECTION -> addSectionTreeNode(childNode);
            case PROMPT -> addPromptTreeNode(childNode);
            default -> false;
        };
    }
}
