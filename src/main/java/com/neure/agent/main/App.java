package com.neure.agent.main;


import com.neure.agent.ui.HttpTextEditorGUI;

import javax.swing.*;

public class App  {

    public static void main(String[] args) {
        // 在事件调度线程中创建和显示GUI，以确保线程安全
        SwingUtilities.invokeLater(() -> {
            HttpTextEditorGUI frame = new HttpTextEditorGUI();
            frame.setVisible(true);
        });
    }
}
