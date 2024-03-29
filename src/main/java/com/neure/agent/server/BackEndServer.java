package com.neure.agent.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neure.agent.client.HttpRequestClient;
import com.neure.agent.constant.TreeType;
import com.neure.agent.model.*;
import com.neure.agent.ui.PromptTextArea;
import com.neure.agent.utils.JacksonUtils;
import com.neure.agent.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
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
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


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
        if (id == null || id <=0){
            return null;
        }
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
        if (id == null || id <=0){
            return null;
        }
        String requestUrl = session.getUrl() + "prompt_section/id/" + id;
        DefaultResponse<PromptSection> response = HttpRequestClient.sendGet(requestUrl, PromptSection.class);
        if (response.isSuccess()) {
            return response.getBody();
        } else {
            log.error("Failed To Get Section {}, Message {}, Code {}", id, response.getMessage(), response.getCode());
            return null;
        }
    }

    public String sendRequest(LLMRequest request,String host) {
        String url = session.getUrl();

        if (StringUtils.isNotBlank(host)){
            url = host;
        }
        url = url + "llm/request";
//        request.setJsonFormat(true);

        DefaultResponse<String> response = HttpRequestClient.sendPost(url,request,String.class);
        if (response.isSuccess()){
            return response.getBody();
        }
        return response.getMessage();
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

    public Integer createProject(String projectName,String description) {
        if (projectName == null || projectName.length() <=3){
            return -1;
        }
        Project project = new Project();
        project.setName(projectName);
        project.setDescription(description);
        String url = session.getUrl() + "project/create";
        DefaultResponse<Integer> response = HttpRequestClient.sendPost(url,project,Integer.class);
        if (response.isSuccess()){
            return response.getBody();
        }
        log.warn("failed create project : {}",response.getMessage() );
        return -1;
    }

    /**
     * 根据类型更新section或者prompt
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
        if (selectedNode == null){
            return new ArrayList<>();
        }
        String url = session.getUrl() + "llm/history/";
        Map<String,String> map = new ConcurrentHashMap<>(1);
        if (TreeType.SECTION.type().equalsIgnoreCase(selectedNode.getType())){
            map.put("prompt_section_id",String.valueOf(selectedNode.getId()));
        }else {
            map.put("prompt_template_id",String.valueOf(selectedNode.getId()));
        }
        String responseStr = HttpRequestClient.get(url,map,null);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        DefaultResponse<List<LLMRequestLog>> response = null;
        try {
            response = mapper.readValue(responseStr, new TypeReference<DefaultResponse<List<LLMRequestLog>>>() {
            });
            if (response.isSuccess()){
                return response.getBody().stream().sorted(Comparator.comparing(LLMRequestLog::getRequestTime).reversed())
                        .map(i->{
                            HistoryItem item = new HistoryItem();
                            item.setDisplayText(i.getResponse());
                            item.setRequest(JacksonUtils.ObjectToJsonStr(i.getRequest()));
                            item.setRequestTime(i.requestTime);
                            item.setResponse(i.getResponse());
                            item.setDisplayText(i.getRequestTime());
                            return item;
                        }).collect(Collectors.toList());
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean renameProject(String newName) {
        Project project = new Project();
        project.setName(newName);
        String url = session.getUrl() + "/project/update/"+session.getProjectId();
        DefaultResponse<String> response = HttpRequestClient.sendPut(url,project,String.class);
        return response.isSuccess();
    }

    public boolean savePromptContent(PromptTextArea detailTextArea) {
        if (detailTextArea == null){
           return false;
        }
        PromptNode node = detailTextArea.getNode();
        if (node == null){
            return false;
        }
        Map<String,String> body = new ConcurrentHashMap<>(1);
        body.put("content",detailTextArea.getText());
        String url = session.getUrl();
        if (TreeType.SECTION.type().equalsIgnoreCase(node.getType())){
            url = url + "prompt_section/update/";
        }else {
            url = url + "prompt_template/update/";
        }
        url = url + node.getId();
        DefaultResponse<String> response =  HttpRequestClient.sendPut(url,body,String.class);
        return response.isSuccess();
    }

    public String compiles(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        Map<String,String> params = new ConcurrentHashMap<>(1);
        params.put("content",content);
        String url= session.getUrl() + "prompt_template/compile";
        DefaultResponse<String> response = HttpRequestClient.sendPost(url,params,String.class);
        if (response.isSuccess()){
            return response.getBody();
        }
        return null;
    }
}
