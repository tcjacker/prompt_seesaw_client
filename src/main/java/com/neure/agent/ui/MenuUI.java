package com.neure.agent.ui;

import javax.swing.*;
import java.util.Objects;

/**
 * MenuUI
 *
 * @author tc
 * @date 2024-02-24 18:48
 */
public class MenuUI {

    public static final MenuUI INSTANCE = new MenuUI();

    private MenuUI() {
    }

    public static MenuUI getInstance() {
        return Objects.requireNonNullElseGet(INSTANCE, MenuUI::new);
    }

    public JMenuBar build() {
        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        // 创建文件菜单
        JMenu fileMenu = new JMenu("文件");
        menuBar.add(fileMenu);
        // 添加菜单项
        JMenuItem openItem = new JMenuItem("打开");
        JMenuItem saveItem = new JMenuItem("保存");
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        return menuBar;
    }


}
