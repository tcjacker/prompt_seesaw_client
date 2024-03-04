package com.neure.agent.model;

import java.util.Date;

/**
 * HistoryItem
 *
 * @author tc
 * @date 2024-03-01 00:31
 */
public class HistoryItem {

    Integer id;
    String displayText;
    String response;
    String request;

    public Date requestTime = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


    @Override
    public String toString() {
        return displayText;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }
}
