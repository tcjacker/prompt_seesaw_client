package com.neure.agent.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Session
 *
 * @author tc
 * @date 2024-02-25 14:27
 */
public class Session {

    private final ConcurrentMap<String, Object> session = new ConcurrentHashMap<>();

    public void set(String key, Object o) {
        session.put(key, o);
    }

    public Object get(String key) {
        return session.get(key);
    }
}
