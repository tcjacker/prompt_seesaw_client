package com.neure.agent.utils;

import com.neure.agent.constant.TreeType;
import com.neure.agent.model.PromptNode;

import java.util.stream.Stream;

/**
 * TreeUtils
 *
 * @author tc
 * @date 2024-02-29 23:30
 */
public class TreeUtils {


    public static Boolean isEditable(PromptNode node) {
        if (node == null) {
            return false;
        }
        return TreeType.PROMPT.type().equalsIgnoreCase(node.getType()) || TreeType.SECTION.type().equalsIgnoreCase(node.getType());
    }

    public static Boolean isFolder(PromptNode node) {
        if (node == null) {
            return false;
        }
        return Stream.of(TreeType.PROMPT_FOLDER.type(), TreeType.SECTION_FOLDER.type()
                , TreeType.FOLDER.type()).anyMatch(i -> i.equalsIgnoreCase(node.getType()));
    }
}
