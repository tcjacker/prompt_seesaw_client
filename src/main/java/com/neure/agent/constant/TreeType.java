package com.neure.agent.constant;

import java.util.Arrays;

/**
 * TreeType
 *
 * @author tc
 * @date 2024-02-26 13:40
 */
public enum TreeType {

    ROOT("root"),
    FOLDER("folder"),
    PROMPT("prompt"),
    SECTION("section");
    private String type = "";
    private TreeType(String type){
        this.type = type;
    }

    public String type(){
        return type;
    }

    public TreeType get(String type){
        return Arrays.stream(TreeType.values()).filter(i->i.type().equals(type))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
