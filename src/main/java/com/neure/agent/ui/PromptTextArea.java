package com.neure.agent.ui;

import com.neure.agent.model.PromptNode;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * PromptTextArea
 *
 * @author tc
 * @date 2024-03-01 20:02
 */
public class PromptTextArea extends JTextArea {

    PromptNode selectNode;



    JTextArea textArea = new JTextArea(10, 20);




    public void clear(){
        this.selectNode = null;
        this.setText("");
    }

    public void bind(PromptNode pn , String text){
        this.selectNode = pn;
        this.setText(text);
    }

    public PromptNode getSelectNode() {
        return selectNode;
    }

    public void setSelectNode(PromptNode selectNode) {
        this.selectNode = selectNode;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }
}
