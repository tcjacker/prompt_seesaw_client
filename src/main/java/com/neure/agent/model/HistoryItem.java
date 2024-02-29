package com.neure.agent.model;

/**
 * HistoryItem
 *
 * @author tc
 * @date 2024-03-01 00:31
 */
public record HistoryItem(String id, String displayText, String response, String param) {

    @Override
    public String toString() {
        return displayText;
    }
}
