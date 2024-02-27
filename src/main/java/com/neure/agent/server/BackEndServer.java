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
import java.util.stream.Collectors;

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
        TreeNode rootData = new TreeNode(project.getName(), TreeType.ROOT.type(), TreeType.ROOT.type());
        TreeNode section = new TreeNode("section_tree", TreeType.SECTION_FOLDER.type(), TreeType.SECTION.type());
        TreeNode prompt = new TreeNode("template_tree", TreeType.PROMPT_FOLDER.type(), TreeType.PROMPT.type());
        String url = session.getUrl() + "project/tree/get/"+ session.projectId;
        DefaultResponse<ProjectEnumTree> projectEnumTree = HttpRequestClient.sendGet(url,ProjectEnumTree.class);
        if (projectEnumTree.isSuccess() && projectEnumTree.getBody() != null){
            ProjectEnumTree peTree = projectEnumTree.getBody();
            EnumTree sectionE = peTree.getSections();
            EnumTree promptE = peTree.getTemplates();
            section = converter(sectionE);
            prompt = converter(promptE);
        }

        rootData.add(section);
        rootData.add(prompt);
        session.setSectionTree(section);
        session.setPromptTree(prompt);
        return rootData;
    }

    private TreeNode converter(EnumTree e) {
        TreeNode node = new TreeNode();
        node.setId(e.getId());
        node.setName(e.getName());
        node.setType(e.getType());
        List<TreeNode> child = e.getChildren().stream().map(this::converter).toList();
        node.setChildren(child);
        return node;
    }

    private TreeNode initialTree() {
        TreeNode root = new TreeNode("空", TreeType.ROOT.type(), TreeType.ROOT.type());
        TreeNode section = new TreeNode("section_tree", TreeType.SECTION_FOLDER.type(), TreeType.SECTION.type());
        TreeNode prompt = new TreeNode("template_tree", TreeType.PROMPT_FOLDER.type(), TreeType.PROMPT.type());
        root.add(section);
        root.add(prompt);
        return root;
    }

    public Project queryProject(int id) {
        String requestUrl = session.url + "project/get/" + id;
        DefaultResponse<Project> response = HttpRequestClient.sendGet(requestUrl, Project.class);
        if (response.isSuccess()) {
            return response.getBody();
        }
        return null;
    }


    /**
     * 只支持全量更新
     */
    public void updateProjectTree() {
        TreeNode sectionTree = session.getSectionTree();
        TreeNode promptTree = session.getPromptTree();
        EnumTree sections = converter(sectionTree);
        EnumTree prompts = converter(promptTree);
        ProjectEnumTree projectEnumTree = new ProjectEnumTree();
        projectEnumTree.setSections(sections);
        projectEnumTree.setTemplates(prompts);
        String url = session.getUrl() + "project/tree/update/" + session.projectId;
        DefaultResponse<Boolean> defaultResponse = HttpRequestClient.sendPut(url, projectEnumTree, Boolean.class);
        if (!defaultResponse.isSuccess()){
            log.error(defaultResponse.getMessage());
        }
    }

    private EnumTree converter(TreeNode node) {
        EnumTree tree = new EnumTree();
        tree.setId(node.getId());
        tree.setName(node.getName());
        List<EnumTree> children = node.getChildren().stream().map(this::converter).collect(Collectors.toList());
        tree.setChildren(children);
        return tree;
    }


    public boolean addPromptTreeNode(TreeNode node) {
        PromptTemplate promptTemplate = new PromptTemplate();
        promptTemplate.setName(node.getName());
        promptTemplate.setProjectId(session.projectId);
        promptTemplate.setContent(" ");
        String requestUrl = session.url + "prompt_template/create";
        DefaultResponse<Integer> response = HttpRequestClient.sendPost(requestUrl, promptTemplate, Integer.class);
        if (response.isSuccess()) {
            int id = response.getBody();
            if (id == -1) {
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
        promptSection.setType(TreeType.SECTION.type());
        String requestUrl = session.url + "prompt_section/create";
        DefaultResponse<Integer> response = HttpRequestClient.sendPost(requestUrl, promptSection, Integer.class);
        if (response.isSuccess()) {
            int id = response.getBody();
            if (id == -1) {
                return false;
            }
            node.setId(id);
            return true;
        }
        return false;
    }

    public boolean checkName(String name, String type) {
        String url = "";
        if (TreeType.PROMPT.type().equalsIgnoreCase(type)) {
            url = session.getUrl() + "prompt_template/check_name/";
        } else if (TreeType.SECTION.type().equalsIgnoreCase(type)) {
            url = session.getUrl() + "prompt_section/check_name/";
        }
        url = url + session.projectId + "/" + name;
        DefaultResponse<Boolean> response = HttpRequestClient.sendGet(url, Boolean.class);
        return response.isSuccess() && response.getBody();
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
