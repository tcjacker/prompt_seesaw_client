package com.neure.agent.ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HttpTextEditorGUI
 *
 * @author tc
 * @date 2024-02-24 22:08
 */
public class HttpTextEditorGUI extends JFrame {

    public HttpTextEditorGUI() {
        setTitle("Prompt-SeeSaw 看见未来");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);

        // 构建树形结构菜单
        TreeNode rootData = new TreeNode("Root", "Type1");
        rootData.addChild(new TreeNode("Child 1", "Type2"));
        rootData.addChild(new TreeNode("Child 2", "Type3"));
        TreeNode child3 = new TreeNode("Child 3", "Type4");
        child3.addChild(new TreeNode("Grandchild 1", "Type5"));
        rootData.addChild(child3);
        DefaultMutableTreeNode rootNode = convertToTreeNode(rootData);
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        JTree tree = new JTree(treeModel);
        JScrollPane treeScrollPane = new JScrollPane(tree);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("删除");
        JMenuItem renameItem = new JMenuItem("重命名");
        popupMenu.add(deleteItem);
        popupMenu.add(renameItem);

        // 为JTree添加鼠标监听器以显示弹出菜单
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row); // 选择鼠标右击的行
                    popupMenu.show(e.getComponent(), e.getX(), e.getY()); // 在鼠标位置显示弹出菜单
                }
            }
        });

        // 实现删除操作
        deleteItem.addActionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null && selectedNode.getParent() != null) { // 确保选中的节点不是根节点
                treeModel.removeNodeFromParent(selectedNode);
            }
        });

        // 实现重命名操作
        renameItem.addActionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                String newName = JOptionPane.showInputDialog(null, "输入新名称:", selectedNode.getUserObject());
                if (newName != null && !newName.trim().isEmpty()) {
                    selectedNode.setUserObject(newName);
                    treeModel.nodeChanged(selectedNode);
                }
            }
        });

        // 中间的富文本编辑框
        JTextPane textPane = new JTextPane();
        JScrollPane textScrollPane = new JScrollPane(textPane);

        // 右侧HTTP请求部分
        JPanel httpPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JTextField httpTextField = new JTextField();
        JTextArea httpResponseArea = new JTextArea();
        httpResponseArea.setEditable(false);

        // 模型选择下拉列表和温度输入框
        String[] models = {"Model 1", "Model 2", "Model 3"};
        JComboBox<String> modelComboBox = new JComboBox<>(models);
        PlaceholderTextField temperatureField = new PlaceholderTextField();
        temperatureField.setPlaceholder("输入温度，0~1.0之间");

        JButton httpSendButton = new JButton("发送请求");
        httpSendButton.addActionListener(e -> sendHttpRequest(textPane.getText(), httpTextField.getText(), modelComboBox.getSelectedItem(), temperatureField.getText(), httpResponseArea));
        JButton publishButton = new JButton("发布prompt");
        httpSendButton.addActionListener(e -> publish(httpTextField.getText(), httpResponseArea));
        JScrollPane httpResponseScrollPane = new JScrollPane(httpResponseArea);

        // 设置组件位置和大小
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0; // 让文本字段水平填充空间
        httpPanel.add(httpTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5; // 分配给下拉列表和温度字段一半的空间
        httpPanel.add(modelComboBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5; // 同上
        httpPanel.add(temperatureField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        httpPanel.add(httpSendButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.5; // 同上
        httpPanel.add(publishButton, gbc);



        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.ABOVE_BASELINE;
//        gbc.weightx = 3.0; // 让滚动面板填充剩余空间
        httpPanel.add(httpResponseScrollPane, gbc);

        // 定义历史记录列表模型
        DefaultListModel<String> historyModel = new DefaultListModel<>();
        JList<String> historyList = new JList<>(historyModel);

        // 定义显示详细返回内容的文本区域
        JTextArea detailTextArea = new JTextArea();
        detailTextArea.setEditable(false);

        // 为历史记录列表添加选择监听器
        historyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedValue = historyList.getSelectedValue();
                // 假设我们可以根据selectedValue获取相应的详细信息
                // 这里仅作为展示，实际应用中需要根据selectedValue获取实际内容
                detailTextArea.setText("详细内容显示: " + selectedValue);
            }
        });

        // 创建滚动面板包装历史列表和详细信息文本区域
        JScrollPane historyScrollPane = new JScrollPane(historyList);
        JScrollPane detailScrollPane = new JScrollPane(detailTextArea);

        // 使用JSplitPane分割历史记录和详细内容的显示
        JSplitPane historyDetailSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, historyScrollPane, detailScrollPane);
        historyDetailSplitPane.setDividerLocation(300); // 根据需要调整分隔条位置


        // 使用JSplitPane分割窗口
        JSplitPane splitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, httpPanel, historyDetailSplitPane);
        splitPaneRight.setDividerLocation(200); // Adjust divider
        JSplitPane splitPaneCenter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textScrollPane, splitPaneRight);
        splitPaneCenter.setDividerLocation(300);
        JSplitPane splitPaneLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, splitPaneCenter);
        splitPaneLeft.setDividerLocation(150);

        getContentPane().add(splitPaneLeft, BorderLayout.CENTER);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();

        // 创建文件菜单及其菜单项
        JMenu fileMenu = new JMenu("文件");
        JMenuItem newPromptItem = new JMenuItem("新建prompt");
        JMenuItem exitItem = new JMenuItem("退出");

        // 将菜单项添加到文件菜单
        fileMenu.add(newPromptItem);
        fileMenu.addSeparator(); // 添加分隔线
        fileMenu.add(exitItem);

        // 创建设置菜单
        JMenu settingsMenu = new JMenu("设置");

        // 将文件和设置菜单添加到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);

        // 为新建prompt菜单项添加事件处理器（根据需要实现）
        newPromptItem.addActionListener(e -> {
            String nodeName = JOptionPane.showInputDialog(null, "请输入节点名称:", "新建节点", JOptionPane.PLAIN_MESSAGE);
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName); // 使用用户输入的名称创建新节点
            treeModel.insertNodeInto(newNode, rootNode, rootNode.getChildCount()); // 将新节点添加到根节点下
            tree.scrollPathToVisible(new TreePath(newNode.getPath()));
        });

        // 为退出菜单项添加事件处理器
        exitItem.addActionListener(e -> {
            System.exit(0); // 关闭程序
        });

// 将菜单栏设置到窗体（JFrame）中
        getContentPane().add(menuBar, BorderLayout.NORTH);
    }

    private void sendHttpRequest(String content, String urlString, Object o, String temperature, JTextArea responseArea) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            in.close();

            responseArea.setText(response.toString());
        } catch (Exception ex) {
            responseArea.setText("Error: " + ex.getMessage());
        }
    }

    private void publish(String urlString, JTextArea responseArea) {

    }

    private static DefaultMutableTreeNode convertToTreeNode(TreeNode treeNode) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(treeNode.getName());
        for (TreeNode child : treeNode.getChildren()) {
            node.add(convertToTreeNode(child));
        }
        return node;
    }

}
