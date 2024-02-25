package com.neure.agent.model;


import java.util.Date;

/**
 * LLMRequestLog
 *
 * @author tc
 * @date 2024-02-25 16:18
 */
public class LLMRequestLog {

    Integer  id = 0;
    Integer promptTemplateId = 0;
    String request = "";
    String response = "";
    Date requestTime = null;
}
