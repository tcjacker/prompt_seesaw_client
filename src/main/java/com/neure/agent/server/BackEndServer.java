package com.neure.agent.server;

import com.neure.agent.client.HttpRequestClient;
import com.neure.agent.constant.TreeType;
import com.neure.agent.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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


    public PromptNode getPromptTree() {
        int projectId = session.getProjectId();
        Project project = queryProject(projectId);
        if (project == null) {
            return null;
        }
        PromptNode rootData = PromptNode.build(project.getName(), TreeType.ROOT.type(), TreeType.ROOT.type());
        PromptNode section = PromptNode.build("section_tree", TreeType.SECTION_FOLDER.type(), TreeType.SECTION.type());
        PromptNode prompt = PromptNode.build("template_tree", TreeType.PROMPT_FOLDER.type(), TreeType.PROMPT.type());
        String url = session.getUrl() + "project/tree/get/" + session.getProjectId();
        DefaultResponse<ProjectEnumTree> projectEnumTree = HttpRequestClient.sendGet(url, ProjectEnumTree.class);
        if (projectEnumTree.isSuccess() && projectEnumTree.getBody() != null) {
            ProjectEnumTree peTree = projectEnumTree.getBody();
            List<EnumTree> sectionE = peTree.getSections();
            List<EnumTree> promptE = peTree.getTemplates();
            if (sectionE != null) {
                List<PromptNode> tmp_section = sectionE.stream().map(this::converter).toList();
                section.setChildren(tmp_section);
            }
            if (promptE != null) {
                List<PromptNode> tmp_prompt = promptE.stream().map(this::converter).toList();
                prompt.setChildren(tmp_prompt);
            }

        }

        rootData.add(section);
        rootData.add(prompt);
        session.setSectionTree(section);
        session.setPromptTree(prompt);
        return rootData;
    }

    private PromptNode converter(EnumTree e) {
        if (e == null) {
            return null;
        }
        PromptNode node = PromptNode.build(e.getName(), e.getType());
        node.setId(e.getId());
        node.setType(e.getType());
        if (e.getChildren() != null) {
            List<PromptNode> child = e.getChildren().stream().map(this::converter).toList();
            node.setChildren(child);
        } else {
            node.setChildren(new ArrayList<>(0));
        }
        return node;
    }

//    private TreeNode initialProject() {
//        TreeNode root = new TreeNode("空", TreeType.ROOT.type(), TreeType.ROOT.type());
//        TreeNode section = new TreeNode("section_tree", TreeType.SECTION_FOLDER.type(), TreeType.SECTION.type());
//        TreeNode prompt = new TreeNode("template_tree", TreeType.PROMPT_FOLDER.type(), TreeType.PROMPT.type());
//        root.add(section);
//        root.add(prompt);
//        return root;
//    }

    public Project queryProject(int id) {
        String requestUrl = session.getUrl() + "project/get/" + id;
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
        PromptNode sectionTree = session.getSectionTree();
        PromptNode promptTree = session.getPromptTree();
        EnumTree sections = converter(sectionTree);
        EnumTree prompts = converter(promptTree);
        Map<String, List<EnumTree>> projectEnumTree = new ConcurrentHashMap<>(2);
        projectEnumTree.put("section_tree", sections.getChildren());
        projectEnumTree.put("template_tree", prompts.getChildren());
        String url = session.getUrl() + "project/tree/update/" + session.getProjectId();
        DefaultResponse<Boolean> defaultResponse = HttpRequestClient.sendPut(url, projectEnumTree, Boolean.class);
        if (!defaultResponse.isSuccess()) {
            log.error(defaultResponse.getMessage());
        }
    }

    private EnumTree converter(PromptNode node) {
        EnumTree tree = new EnumTree();
        tree.setId(node.getId());
        tree.setName(node.getName());
        tree.setType(node.getType());
        List<EnumTree> children = node.getChildren().stream().map(this::converter).collect(Collectors.toList());
        tree.setChildren(children);
        return tree;
    }


    public boolean addPromptTreeNode(PromptNode node) {
        PromptTemplate promptTemplate = new PromptTemplate();
        promptTemplate.setName(node.getName());
        promptTemplate.setProjectId(session.getProjectId());
        promptTemplate.setContent(" ");
        String requestUrl = session.getUrl() + "prompt_template/create";
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

    public boolean addSectionTreeNode(PromptNode node) {
        PromptSection promptSection = new PromptSection();
        promptSection.setName(node.getName());
        promptSection.setProjectId(session.getProjectId());
        promptSection.setContent(" ");
        promptSection.setType(TreeType.SECTION.type());
        String requestUrl = session.getUrl() + "prompt_section/create";
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
        String url;
        if (TreeType.PROMPT.type().equalsIgnoreCase(type)) {
            url = session.getUrl() + "prompt_template/check_name/";
        } else if (TreeType.SECTION.type().equalsIgnoreCase(type)) {
            url = session.getUrl() + "prompt_section/check_name/";
        } else {
            return true;
        }
        url = url + session.getProjectId() + "/" + name;
        DefaultResponse<Boolean> response = HttpRequestClient.sendGet(url, Boolean.class);
        return response.isSuccess() && response.getBody();
    }

    public PromptTemplate getPromptTemplate(Integer id) {
        String requestUrl = session.getUrl() + "prompt_template/id/" + id;
        DefaultResponse<PromptTemplate> response = HttpRequestClient.sendGet(requestUrl, PromptTemplate.class);
        if (response.isSuccess()) {
            return response.getBody();
        } else {
            log.error("Failed To Get Template {}, Message {}, Code {}", id, response.getMessage(), response.getCode());
            return null;
        }
    }

    public PromptSection getSection(Integer id) {
        String requestUrl = session.getUrl() + "prompt_section/id/" + id;
        DefaultResponse<PromptSection> response = HttpRequestClient.sendGet(requestUrl, PromptSection.class);
        if (response.isSuccess()) {
            return response.getBody();
        } else {
            log.error("Failed To Get Section {}, Message {}, Code {}", id, response.getMessage(), response.getCode());
            return null;
        }
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

    public boolean addNode(PromptNode childNode, String type) {
        return switch (TreeType.get(type)) {
            case SECTION -> addSectionTreeNode(childNode);
            case PROMPT -> addPromptTreeNode(childNode);
            default -> false;
        };
    }

    public void createProject(String projectName) {
    }

    /**
     * TODO： 根据类型更新section或者prompt
     *
     * @param name
     * @param type
     * @param id
     */
    public boolean updateName(String name, String type, Integer id) {
        String url = "";
        if (TreeType.PROMPT.type().equalsIgnoreCase(type)) {
            url = session.getUrl() + "prompt_template/update/" + id;
        } else if (TreeType.SECTION.type().equalsIgnoreCase(type)) {
            url = session.getUrl() + "prompt_section/update/" + id;
        } else {
            return true;
        }
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("name", name);
        DefaultResponse<Boolean> response = HttpRequestClient.sendPut(url, map, Boolean.class);
        if (response.isSuccess() && response.getBody()) {
            log.info("success update TreeNode name : {}", name);
            return true;
        } else {
            log.warn("Failed update TreeNode name : {}", name);
            return false;
        }
    }

    public void remove(PromptNode selectedNode) {
        //TODO:delete
    }

    public Editable getPrompt(PromptNode selectedNode) {
        if (TreeType.SECTION.type().equalsIgnoreCase(selectedNode.getType())) {
            return getSection(selectedNode.getId());
        } else if (TreeType.PROMPT.type().equalsIgnoreCase(selectedNode.getType())) {
            return getPromptTemplate(selectedNode.getId());
        }
        throw new IllegalArgumentException();
    }

    public List<HistoryItem> queryHistory(PromptNode selectedNode) {
        return new ArrayList<>();
    }
}
