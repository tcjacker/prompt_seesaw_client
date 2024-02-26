package com.neure.agent.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.neure.agent.client.HttpRequestClient;
import com.neure.agent.constant.TreeType;
import com.neure.agent.model.*;
import com.neure.agent.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;

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
        TreeNode rootData = new TreeNode(project.getName(), TreeType.ROOT.type());
        return rootData;
    }

    private TreeNode initialTree() {
        TreeNode root = new TreeNode("空", TreeType.ROOT.type());
        TreeNode section = new TreeNode("Section", TreeType.FOLDER.type());
        TreeNode prompt = new TreeNode("Prompt", TreeType.FOLDER.type());
        root.add(section);
        root.add(prompt);
        return root;
    }

    public Project queryProject(int id) {
        String requestUrl = session.url + "project/get/" + id;
        String responseStr = HttpRequestClient.sendGet(requestUrl);
        log.info(responseStr);
        try {
            DefaultResponse<Project> defaultResponse = JacksonUtils.StrToObject(responseStr, new TypeReference<DefaultResponse<Project>>() {
            });
            if (defaultResponse.getCode() != 200) {
                log.error(defaultResponse.getMessage());
                return null;
            }
            return defaultResponse.getBody();
        } catch (JsonProcessingException e) {
            log.error("response is [ " + responseStr + " ] err:" + e.getMessage());
            return null;
        }
    }

    /**
     * 只支持全量更新
     *
     * @param treeNode
     */
    public void updateProjectTree(TreeNode treeNode, TreeType type) {
        if (treeNode == null || treeNode.getChildren() == null
                || treeNode.getChildren().size() <= 0
                || TreeType.ROOT.type().equalsIgnoreCase(treeNode.getType())) {
            return;
        }
        List<TreeNode> child = treeNode.getChildren();
        TreeNode updateNode = child.stream().filter(i -> type.type().equalsIgnoreCase(i.getType())).findFirst().orElseThrow(IllegalArgumentException::new);



    }


    public boolean addTreeNode(TreeNode node) {
        return false;
    }

    public PromptTemplate getPrompt(Integer id) {
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
}
