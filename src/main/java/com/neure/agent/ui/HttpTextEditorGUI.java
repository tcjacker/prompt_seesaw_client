package com.neure.agent.ui;

import com.neure.agent.constant.TreeType;
import com.neure.agent.model.Setting;
import com.neure.agent.model.TreeNode;
import com.neure.agent.server.BackEndServer;
import com.neure.agent.server.Session;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Stream;

/**
 * HttpTextEditorGUI
 *
 * @author tc
 * @date 2024-02-24 22:08
 */
public class HttpTextEditorGUI extends JFrame {

    Session session;

    BackEndServer backEndServer;

    JTree tree;

    DefaultTreeModel treeModel;

    TreeNode rootData;

    Setting setting;

    JTextField httpTextField;

    DefaultListModel<String> historyModel;

    JTextArea detailTextArea;


    public HttpTextEditorGUI(Session session, BackEndServer backEndServer) {

        this.session = session;
        this.backEndServer = backEndServer;
        this.setting = new Setting();
        this.session.setProjectId(setting.getProjectId());
        this.session.setToken(setting.getToken());
        this.session.setUrl(setting.getUrl());

        setTitle("Prompt-SeeSaw 看见未来");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        // 构建树形结构菜单
        tree = initialTree();
        JScrollPane treeScrollPane = new JScrollPane(tree);


        // 中间的富文本编辑框
        JTextPane textPane = new JTextPane();
        JScrollPane textScrollPane = new JScrollPane(textPane);

        // 右侧HTTP请求部分
        JPanel httpPanel = initialHttpPanel(textPane);

        // 定义历史记录列表模型
        JSplitPane historyDetailSplitPane = initialHistoryPane();


        // 使用JSplitPane分割窗口
        JSplitPane splitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, httpPanel, historyDetailSplitPane);
        splitPaneRight.setDividerLocation(200); // Adjust divider
        JSplitPane splitPaneCenter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textScrollPane, splitPaneRight);
        splitPaneCenter.setDividerLocation(300);
        JSplitPane splitPaneLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, splitPaneCenter);
        splitPaneLeft.setDividerLocation(150);

        getContentPane().add(splitPaneLeft, BorderLayout.CENTER);

        // 创建菜单栏
        JMenuBar menuBar = initialMenuBar();
        // 将菜单栏设置到窗体（JFrame）中
        getContentPane().add(menuBar, BorderLayout.NORTH);

    }


    public void reDraw() {
        this.session.setProjectId(setting.getProjectId());
        this.session.setToken(setting.getToken());
        this.session.setUrl(setting.getUrl());

        rootData = initialTreeNode();
        DefaultTreeModel newTreeModel = new DefaultTreeModel(rootData);
        tree.setModel(newTreeModel);

        httpTextField.setText(setting.getUrl());
        detailTextArea.setText("");
        historyModel.clear();


    }

    private JSplitPane initialHistoryPane() {
        historyModel = new DefaultListModel<>();
        JList<String> historyList = new JList<>(historyModel);

        // 定义显示详细返回内容的文本区域
        detailTextArea = new JTextArea();
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
        return historyDetailSplitPane;
    }

    private JMenuBar initialMenuBar() {
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
        JMenuItem projectIdItem = new JMenuItem("projectId");
        JMenuItem hostUrlItem = new JMenuItem("主机");
        settingsMenu.add(projectIdItem);
        settingsMenu.add(hostUrlItem);


        // 将文件和设置菜单添加到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);

        // 为新建prompt菜单项添加事件处理器（根据需要实现）
        newPromptItem.addActionListener(e -> {
            String nodeName = JOptionPane.showInputDialog(null, "请输入节点名称:", "新建节点", JOptionPane.PLAIN_MESSAGE);
            TreeNode newNode = backEndServer.reNamePrompt(nodeName); // 使用用户输入的名称创建新节点
            treeModel.insertNodeInto(newNode, rootData, rootData.getChildCount()); // 将新节点添加到根节点下
            tree.scrollPathToVisible(new TreePath(newNode.getPath()));
        });

        projectIdItem.addActionListener(e -> {
            boolean isValidInput = false;
            while (!isValidInput) {
                String input = (String) JOptionPane.showInputDialog(null, "请输入项目ID:", "项目ID", JOptionPane.PLAIN_MESSAGE, null, null, String.valueOf(setting.getProjectId()));
                if (input != null) {
                    try {
                        int projectId = Integer.parseInt(input);
                        // 如果转换成功，说明是有效的int类型，可以退出循环
                        isValidInput = true;
                        setting.setProjectId(projectId);
                        reDraw();
                    } catch (NumberFormatException e2) {
                        // 输入的不是int类型，显示错误消息，并让循环继续
                        JOptionPane.showMessageDialog(null, "输入的项目ID不是有效的整数，请重新输入！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    break;
                }

            }

        });

        hostUrlItem.addActionListener(e -> {
            String url = (String) JOptionPane.showInputDialog(null, "输入host地址:", "host地址", JOptionPane.PLAIN_MESSAGE, null, null, session.getUrl());
            if (url != null && url.startsWith("http://")) {
                setting.setUrl(url);
            } else {
                JOptionPane.showMessageDialog(null, "输入的host地址必须以http://开头", "错误", JOptionPane.ERROR_MESSAGE);
            }


        });

        // 为退出菜单项添加事件处理器
        exitItem.addActionListener(e -> {
            System.exit(0); // 关闭程序
        });
        return menuBar;
    }

    private JPanel initialHttpPanel(JTextPane textPane) {
        JPanel httpPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        httpTextField = new JTextField(session.getUrl());
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
        httpPanel.add(httpResponseScrollPane, gbc);
        return httpPanel;
    }

    private JTree initialTree() {
        rootData = initialTreeNode();
        treeModel = new DefaultTreeModel(rootData);
        JPopupMenu popupMenu = new JPopupMenu();
        tree = new JTree(treeModel);
        JMenuItem deleteItem = new JMenuItem("删除");
        JMenuItem renameItem = new JMenuItem("重命名");
        // 添加一个新的菜单项
        JMenuItem addItem = new JMenuItem("添加子节点");
        popupMenu.add(deleteItem);
        popupMenu.add(renameItem);

        // 为JTree添加鼠标监听器以显示弹出菜单
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        TreeNode selectedNode = (TreeNode) path.getLastPathComponent();
                        // 检查节点是否是folder类型
                        // 这里的条件根据实际情况来判断节点是否是“folder”类型
                        if (isAllowed(selectedNode)) {
                            // 如果是folder类型，显示“添加子节点”菜单项
                            popupMenu.add(addItem);
                        } else {
                            // 如果不是，移除该菜单项确保它不显示
                            popupMenu.remove(addItem);
                        }
                        popupMenu.show(tree, e.getX(), e.getY());
                    }
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


        // 为新增子节点菜单项添加动作监听器
        addItem.addActionListener(e -> {
            TreeNode selectedNode = (TreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                // 创建下拉列表让用户选择节点类型
                String[] types = new String[]{selectedNode.getBaseType(),TreeType.FOLDER.type()};
                JComboBox<String> typeComboBox = new JComboBox<>(types);
                JTextField nameTextField = new JTextField();
                final JComponent[] inputs = new JComponent[]{
                        new JLabel("选择类型"),
                        typeComboBox,
                        new JLabel("名称"),
                        nameTextField
                };
                int result = JOptionPane.showConfirmDialog(null, inputs, "新增子节点", JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String type = (String) typeComboBox.getSelectedItem();
                    String name = nameTextField.getText();
                   if (backEndServer.checkName(name,type)){
                       JOptionPane.showMessageDialog(null, "有重复名称", "错误", JOptionPane.ERROR_MESSAGE);
                       return;
                   }
                    if (name != null && !name.trim().isEmpty()) {
                        // 根据选择创建新的节点
                        TreeNode childNode = new TreeNode(name, type,selectedNode.getBaseType());
                        if (TreeType.PROMPT.type().equalsIgnoreCase(type) || TreeType.SECTION.type().equalsIgnoreCase(type)) {
                            boolean isSuccess = backEndServer.addNode(childNode, type);
                            if (!isSuccess){
                                JOptionPane.showMessageDialog(null, "创建节点失败", "错误", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        selectedNode.addChild(childNode);
                        // 通知模型节点已经发生变化，以刷新显示
                        treeModel.nodesWereInserted(selectedNode, new int[]{selectedNode.getIndex(childNode)});
                        // 更新数据库数据
                        backEndServer.updateProjectTree();
                    }
                }
            }
        });


        return tree;
    }

    private boolean isAllowed(TreeNode selectedNode) {
        return Stream.of(TreeType.PROMPT_FOLDER.type(), TreeType.SECTION_FOLDER.type()
                , TreeType.FOLDER.type()).anyMatch(i -> i.equalsIgnoreCase(selectedNode.getType()));
    }

    private TreeNode initialTreeNode() {
        return backEndServer.getPromptTree();
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
