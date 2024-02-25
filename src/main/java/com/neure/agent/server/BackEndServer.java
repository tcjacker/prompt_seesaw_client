package com.neure.agent.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neure.agent.client.HttpRequestClient;
import com.neure.agent.constant.HTTPMethod;
import com.neure.agent.model.*;
import com.neure.agent.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class BackEndServer {

    Session session;



    public BackEndServer(Session session){
        this.session = session;
    }

    public  TreeNode reNamePrompt(String nodeName) {
        return new TreeNode(nodeName);
    }


    public TreeNode getPromptTree(){
        int projectId = session.projectId;
        Project project = queryProject(projectId);
        if (project == null){
            return new TreeNode("空","Type1");
        }
        TreeNode rootData = new TreeNode(project.getName(), "Type1");
        return rootData;
    }

    public Project queryProject(int id){
        String requestUrl = session.url + "project/get/" + id;
        String responseStr = HttpRequestClient.sendGet(requestUrl);
        log.info(responseStr);
        try {
            Response<Project> response = JacksonUtils.StrToObject(responseStr,new TypeReference<Response<Project>>(){});
            if (response.getCode() != 200){
                log.error(response.getMessage());
                return null;
            }
            return response.getBody();
        } catch (JsonProcessingException e) {
            log.error("response is [ " + responseStr + " ] err:" + e.getMessage());
            return null;
        }
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
