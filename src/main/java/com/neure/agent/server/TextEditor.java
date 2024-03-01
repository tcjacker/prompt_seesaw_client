package com.neure.agent.server;

import com.neure.agent.utils.JacksonUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TextEditor
 *
 * @author tc
 * @date 2024-02-29 23:52
 */
public class TextEditor {

    public static Map<String, String> paramsResolver(String content) {
        return new ConcurrentHashMap<>();
    }

    public static String paramsResolverStr(String content) {
        return JacksonUtils.ObjectToJsonStr(paramsResolver(content));
    }
}
