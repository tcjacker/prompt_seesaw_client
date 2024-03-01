package com.neure.agent.ui;

import com.neure.agent.model.Editable;
import com.neure.agent.model.PromptNode;

import javax.swing.*;

/**
 * PromptTextArea
 *
 * @author tc
 * @date 2024-03-01 20:02
 */
public class PromptTextArea extends JTextArea {

    PromptNode node;

    public void clear(){
        this.node = null;
        this.setText("");
    }

    public void bind(PromptNode pn , String text){
        this.node = pn;
        this.setText(text);
    }

    public PromptNode getNode() {
        return node;
    }

    public void setNode(PromptNode node) {
        this.node = node;
    }
}
