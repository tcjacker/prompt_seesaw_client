package com.neure.agent.ui;

import com.neure.agent.model.PromptNode;

import javax.swing.*;

/**
 * PromptMenuItem
 *
 * @author tc
 * @date 2024-03-14 23:43
 */
public class PromptMenuItem extends JMenuItem {

    PromptNode node;
    public PromptMenuItem(String name,PromptNode n) {
        super(name);
        this.node = n;
    }
}
