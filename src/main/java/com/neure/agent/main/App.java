package com.neure.agent.main;


import com.neure.agent.server.BackEndServer;
import com.neure.agent.server.Session;
import com.neure.agent.ui.HttpTextEditorGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class App {

    public static void main(String[] args) {
        Session session = initialSession();
        BackEndServer backEndServer = initialServer(session);
        // 在事件调度线程中创建和显示GUI，以确保线程安全
        SwingUtilities.invokeLater(() -> {
            HttpTextEditorGUI frame = new HttpTextEditorGUI(session, backEndServer);
            frame.setVisible(true);
            // 获取JFrame的根面板的InputMap，并将其绑定到WHEN_IN_FOCUSED_WINDOW
            InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            // 定义快捷键为Control + S
            KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
            KeyStroke macosKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.META_DOWN_MASK);
            // 在InputMap中添加这个快捷键，关联到一个自定义的键
            inputMap.put(keyStroke, "autoSave");
            inputMap.put(macosKeyStroke, "autoSave");

            // 获取JFrame的根面板的ActionMap，并定义一个对应的Action
            ActionMap actionMap = frame.getRootPane().getActionMap();
            actionMap.put("autoSave", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 触发autoSave方法
                    frame.autoSave();
                }
            });
        });
    }


    private static BackEndServer initialServer(Session session) {
        return new BackEndServer(session);
    }

    private static Session initialSession() {
        return new Session();
    }
}
