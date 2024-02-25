package com.neure.agent.model;


import java.util.Date;

/**
 * LLMRequestLog
 *
 * @author tc
 * @date 2024-02-25 16:18
 */
public class LLMRequestLog {

    public Integer id = 0;
    public Integer promptTemplateId = 0;
    public String request = "";
    public String response = "";
    public Date requestTime = null;


}
