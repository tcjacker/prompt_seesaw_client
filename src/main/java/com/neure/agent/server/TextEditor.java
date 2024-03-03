package com.neure.agent.server;

import com.neure.agent.utils.JacksonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * TextEditor
 *
 * @author tc
 * @date 2024-02-29 23:52
 */
public class TextEditor {

    public final static String PARAMS_PATTERN = "\\{(.*?)\\}";
    static Pattern pattern;

    static {
        pattern = Pattern.compile(PARAMS_PATTERN);
    }


    public static Map<String, String> paramsResolver(String content) {
        List<String> params = extractVariables(content);
        return params.stream().distinct().collect(Collectors.toMap(param -> param, param -> ""));
    }

    private static List<String> extractVariables(String content) {
        List<String> variables = new ArrayList<>();
        Matcher matcher = pattern.matcher(content);
        // Find matches and add them to the list
        while (matcher.find()) {
            variables.add(matcher.group(1)); // group(1) returns the part inside the braces
        }
        return variables;
    }

    public static String analysisParams(String content, Map<String, String> params) {
        // Iterate through each entry in the map
        for (Map.Entry<String, String> entry : params.entrySet()) {
            // Replace all occurrences of the placeholder with the corresponding value
            content = content.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return content;
    }

    public static String paramsResolverStr(String content) {
        return JacksonUtils.ObjectToJsonStr(paramsResolver(content));
    }
}
