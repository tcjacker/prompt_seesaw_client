package com.neure.agent.model;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Setting
 *
 * @author tc
 * @date 2024-02-25 18:23
 */
@Slf4j
public class Setting {

    private String url;

    private String projectId;

    private String token;

    private final static String PATH = "src/main/resources/config.yaml";


    public Setting(){
        Yaml yaml = new Yaml();
        try {
            FileInputStream inputStream = new FileInputStream(PATH);
            Map<String, Object> data = yaml.load(inputStream);
            System.out.println(data);

            // 访问具体的配置项
            Map<String, Object> host = (Map<String, Object>) data.get("host");
            if (host != null && host.size() > 0){
                url = (String) host.get("url");
            }

            Map<String, Object> project = (Map<String, Object>) data.get("project");
            if (project != null && project.size() > 0){
                projectId = (String) project.get("id");
            }

            token = (String) data.get("token");

        } catch (FileNotFoundException e) {
            log.error("error is : ",e);
        }
    }

    /**
     * 刷新设置文件
     */
    private void flush(){
        Yaml yaml = new Yaml();
        try {
            // 读取YAML文件
            FileInputStream inputStream = new FileInputStream(PATH);
            Map<String, Object> data = yaml.load(inputStream);

            // 修改数据
            // 访问具体的配置项
            Map<String, Object> host = (Map<String, Object>) data.get("host");
            host.put("url",url);

            Map<String, Object> project = (Map<String, Object>) data.get("project");
            project.put("id",projectId);

           data.put("token",token);

            // 写回YAML文件
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            yaml = new Yaml(options);
            FileWriter writer = new FileWriter(PATH);
            yaml.dump(data, writer);
        } catch (Exception e) {
            log.error("error is : ",e);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        flush();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
        flush();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        flush();
    }
}
