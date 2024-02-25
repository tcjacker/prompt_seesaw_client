package com.neure.agent.main;


import com.neure.agent.server.BackEndServer;
import com.neure.agent.server.Session;
import com.neure.agent.ui.HttpTextEditorGUI;

import javax.swing.*;

public class App  {

    public static void main(String[] args) {
        Session session = initialSession();
        BackEndServer backEndServer = initialServer(session);
        // 在事件调度线程中创建和显示GUI，以确保线程安全
        SwingUtilities.invokeLater(() -> {
            HttpTextEditorGUI frame = new HttpTextEditorGUI(session,backEndServer);
            frame.setVisible(true);
        });
    }

    private static BackEndServer initialServer(Session session) {
        return new BackEndServer();
    }

    private static Session initialSession() {
        return new Session();
    }
}
