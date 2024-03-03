package com.neure.agent.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.neure.agent.constant.TreeType;
import com.neure.agent.model.*;
import com.neure.agent.server.BackEndServer;
import com.neure.agent.server.Session;
import com.neure.agent.server.TextEditor;
import com.neure.agent.utils.JacksonUtils;
import com.neure.agent.utils.StringUtils;
import com.neure.agent.utils.TreeUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    PromptNode rootData;

    Setting setting;

    JTextField httpTextField;

    DefaultListModel<HistoryItem> historyModel;

    PromptTextArea detailTextArea;

    JTextArea requestParamsTextArea;

    JTextArea httpResponseArea;


    JLabel statusLabel = new JLabel("就绪");

//    JTextArea historyTextArea;


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
        detailTextArea = new PromptTextArea();
        detailTextArea.setEditable(false);
        JScrollPane textScrollPane = new JScrollPane(detailTextArea);

        // 右侧HTTP请求部分
        JPanel httpPanel = initialHttpPanel();

        // 定义历史记录列表模型
        JSplitPane historyDetailSplitPane = initialHistoryPane();


        // 使用JSplitPane分割窗口
        JSplitPane splitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, httpPanel, historyDetailSplitPane);
        splitPaneRight.setDividerLocation(200); // Adjust divider
        JSplitPane splitPaneCenter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textScrollPane, splitPaneRight);
        splitPaneCenter.setDividerLocation(800);
        JSplitPane splitPaneLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, splitPaneCenter);
        splitPaneLeft.setDividerLocation(150);

        getContentPane().add(splitPaneLeft, BorderLayout.CENTER);

        // 创建菜单栏
        JMenuBar menuBar = initialMenuBar();
        // 将菜单栏设置到窗体（JFrame）中
        getContentPane().add(menuBar, BorderLayout.NORTH);
        //设置右下角提示框
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));

        // 消息显示标签

        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusBar.add(statusLabel, BorderLayout.EAST);

        // 将状态栏面板添加到主窗口（frame）的底部
        getContentPane().add(statusBar, BorderLayout.SOUTH);


    }

    // 显示消息的方法
    public void showStatusMessage(String message, String level) {
        statusLabel.setText(message);
        if ("Error".equalsIgnoreCase(level)) {
            statusLabel.setForeground(Color.RED); // 设置字体颜色为红色
        }
        // 设置定时器，让消息在5秒后消失
        Timer timer = new Timer(5000, e -> {
            statusLabel.setText("就绪");
            statusLabel.setForeground(Color.BLACK); // 消息消失后重置字体颜色为默认颜色
        });
        timer.setRepeats(false); // 确保定时器只执行一次
        timer.start();
    }

    public void flushView(Integer projectId) {
        setting.setProjectId(projectId);
        this.session.setProjectId(projectId);
        this.session.setToken(setting.getToken());
        this.session.setUrl(setting.getUrl());

        rootData = initialTreeNode();
        if (rootData == null) {
            JOptionPane.showMessageDialog(null, "项目ID不存在！", "错误", JOptionPane.ERROR_MESSAGE);
        }
        DefaultTreeModel newTreeModel = new DefaultTreeModel(rootData);
        tree.setModel(newTreeModel);

        httpTextField.setText(setting.getUrl());
        detailTextArea.clear();
        requestParamsTextArea.setText("");
        historyModel.clear();


    }

    public void autoSave() {
        if (detailTextArea == null) {
            return;
        }
        PromptNode node = detailTextArea.getNode();
        if (node == null) {
            return;
        }
        boolean isSuccess = backEndServer.savePromptContent(detailTextArea);
        if (isSuccess) {
            requestParamsTextArea.setText(TextEditor.paramsResolverStr(detailTextArea.getText()));
            showStatusMessage(node.getName() + "保存成功!", "");
        }else {
            showStatusMessage(node.getName() + "保存失败!", "Error");
        }

    }

    private JSplitPane initialHistoryPane() {
        historyModel = new DefaultListModel<>();
        JList<HistoryItem> historyList = new JList<>(historyModel);

        // 定义显示详细返回内容的文本区域
        JTextArea paramTextArea = new JTextArea();
        paramTextArea.setEditable(false);
        JTextArea responseTextArea = new JTextArea();
        responseTextArea.setEditable(false);


        historyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                HistoryItem selectedValue = historyList.getSelectedValue();
                if (selectedValue != null) {
                    paramTextArea.setText("参数: " + selectedValue.param());
                    responseTextArea.setText("响应: " + selectedValue.response());
                }
            }
        });

        JScrollPane historyScrollPane = new JScrollPane(historyList);
        JScrollPane paramScrollPane = new JScrollPane(paramTextArea);
        JScrollPane responseScrollPane = new JScrollPane(responseTextArea);

        // 创建一个垂直分割面板包含参数和响应的文本区域
        JSplitPane detailSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, paramScrollPane, responseScrollPane);
        detailSplitPane.setDividerLocation(150); // 根据需要调整

        // 使用JSplitPane分割历史记录和详细内容的显示
        JSplitPane historyDetailSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, historyScrollPane, detailSplitPane);
        historyDetailSplitPane.setDividerLocation(300); // 根据需要调整

        return historyDetailSplitPane;
    }

    private JMenuBar initialMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 创建文件菜单及其菜单项
        JMenu fileMenu = new JMenu("文件");
        JMenuItem newProject = new JMenuItem("新建project");
        JMenuItem exitItem = new JMenuItem("退出");
        JMenuItem saveMenuItem = new JMenuItem("保存");

        // 将菜单项添加到文件菜单
        fileMenu.add(newProject);
        fileMenu.addSeparator(); // 添加分隔线
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator(); // 添加分隔线
        fileMenu.add(exitItem);

        // 创建设置菜单
        JMenu settingsMenu = new JMenu("设置");
        JMenuItem projectIdItem = new JMenuItem("projectId");
        JMenuItem hostUrlItem = new JMenuItem("主机");
        settingsMenu.add(projectIdItem);
        settingsMenu.add(hostUrlItem);


        saveMenuItem.addActionListener(e -> {
            boolean isOk = backEndServer.savePromptContent(detailTextArea);
            if (isOk){
                requestParamsTextArea.setText(TextEditor.paramsResolverStr(detailTextArea.getText()));
            }
            handleResponse(isOk, "保存");
        });

        // 将文件和设置菜单添加到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);


//        // 为新建prompt菜单项添加事件处理器（根据需要实现）
        newProject.addActionListener(e -> {
            JTextField nameTextField = new JTextField();
            nameTextField.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    check();
                }

                public void removeUpdate(DocumentEvent e) {
                    check();
                }

                public void insertUpdate(DocumentEvent e) {
                    check();
                }

                // 检查文本字段内容并更新边框颜色
                private void check() {
                    if (nameTextField.getText().trim().isEmpty() || nameTextField.getText().length() <= 3) {
                        // 用户输入为空，设置边框颜色为红色
                        nameTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
                    } else {
                        // 输入非空，恢复默认边框
                        nameTextField.setBorder(UIManager.getBorder("TextField.border"));
                    }
                }
            });

            JTextField descriptionTextField = new JTextField();
            final JComponent[] inputs = new JComponent[]{
                    new JLabel("项目名称"),
                    nameTextField,
                    new JLabel("项目描述"),
                    descriptionTextField
            };
            int result = JOptionPane.showConfirmDialog(null, inputs, "创建项目", JOptionPane.DEFAULT_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String name = nameTextField.getText();
                String description = descriptionTextField.getText();
                Integer projectId = backEndServer.createProject(name, description);
                if (projectId > 0) {
                    flushView(projectId);
                }
                handleResponse(projectId != -1, "创建项目");
            }
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
                        flushView(projectId);
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
            if (url == null){
                return;
            }
            if (url.startsWith("http://")) {
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

    private void handleResponse(boolean isOk, String action) {
        if (isOk) {
            JOptionPane.showMessageDialog(null, action + "成功！", "成功", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, action + "失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel initialHttpPanel() {
        JPanel httpPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        httpTextField = new JTextField(session.getUrl());
        httpResponseArea = new JTextArea();
        httpResponseArea.setEditable(false);

        requestParamsTextArea = new JTextArea();
        requestParamsTextArea.setEditable(true);

        // 模型选择下拉列表和温度输入框
        String[] models = {"gpt-3.5-turbo", "gpt-4","gpt-4-turbo-preview","gpt-4-32k"};
        JComboBox<String> modelComboBox = new JComboBox<>(models);
        PlaceholderTextField temperatureField = new PlaceholderTextField();
        temperatureField.setPlaceholder("输入温度，0~1.0之间");

        JButton httpSendButton = new JButton("发送请求");
        httpSendButton.addActionListener(e -> {
            double t = 0.9;
            if (!StringUtils.isDecimal(temperatureField.getText())) {
                showStatusMessage("温度必须是小数，已被改成默认0.9", "Error");
            } else {
                t = Double.parseDouble(temperatureField.getText());
            }
            if (detailTextArea == null || detailTextArea.getNode() == null) {
                return;
            }
            //异步保存
            CompletableFuture.runAsync(() -> backEndServer.savePromptContent(detailTextArea));
            String content = detailTextArea.getText();
            content = backEndServer.compiles(content);
            if (content == null) {
                showStatusMessage("编译失败", "Error");
                return;
            }
            String textParams = requestParamsTextArea.getText();
            Map<String, String> params = null;
            if (textParams != null && textParams.length() > 0) {
                try {
                    params = JacksonUtils.StrToObject(textParams, new TypeReference<HashMap<String, String>>() {
                    });
                    content = TextEditor.analysisParams(content, params);
                } catch (JsonProcessingException ex) {
                    showStatusMessage("请求的prompt的参数格式错误", "Error");
                    return;
                }
            }
            LLMRequest request = new LLMRequest();
            request.setModel((String) modelComboBox.getSelectedItem());
            request.setTemperature(t);
            request.setPrompt(content);
            request.setPromptId(detailTextArea.getNode().getId());
            request.setType(detailTextArea.getNode().getType());
            String response = backEndServer.sendRequest(request,httpTextField.getText());
            httpResponseArea.setText(response);
            historyModel.clear();
            flashHistoryTo(detailTextArea.getNode());
        });
        JButton publishButton = new JButton("发布prompt");
        httpSendButton.addActionListener(e -> publish(httpTextField.getText(), httpResponseArea));
        JScrollPane httpResponseScrollPane = new JScrollPane(httpResponseArea);
        JScrollPane requestParamsScrollPane = new JScrollPane(requestParamsTextArea); // 为了滚动

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
        gbc.gridy = 3; // 调整为放在httpResponseScrollPane上方
        gbc.gridwidth = 2; // 占满整行
        gbc.weightx = 1.0;
        gbc.weighty = 0.5; // 可以根据需要调整
        gbc.fill = GridBagConstraints.BOTH; // 填充方式
        httpPanel.add(requestParamsScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
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

        // 为JTree添加鼠标监听器以显示弹出菜单
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showMenu(e, popupMenu, addItem, renameItem, deleteItem);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());

                if (selRow != -1) {
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {
                        PromptNode selectedNode = (PromptNode) selPath.getLastPathComponent();
                        if (e.getClickCount() == 1) {
                            handleTreeNodeClick(selectedNode);
                        } else if (e.getClickCount() == 2) {
                            handleTreeNodeDoubleClick(selectedNode);
                        }
                    }

                }
            }
        });

        // 实现删除操作
        deleteItem.addActionListener(e -> {
            PromptNode selectedNode = (PromptNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null) {
                return;
            }
            PromptNode parent = (PromptNode) selectedNode.getParent();
            parent.deleteChild(selectedNode);
            treeModel.removeNodeFromParent(selectedNode);
            CompletableFuture.runAsync(() -> backEndServer.updateProjectTree());
        });

        // 实现重命名操作
        renameItem.addActionListener(e -> {
            PromptNode selectedNode = (PromptNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null) {
                return;
            }
            String newName = JOptionPane.showInputDialog(null, "输入新名称:", selectedNode.getUserObject());
            if (newName == null || newName.trim().isEmpty()) {
                return;
            }

            if (TreeType.FOLDER.type().equalsIgnoreCase(selectedNode.getType())) {
                boolean hasSameName = selectedNode.getChildren().stream().anyMatch(i -> i.getName().equalsIgnoreCase(newName));
                if (hasSameName) {
                    JOptionPane.showMessageDialog(null, "名字已存在", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (TreeType.ROOT.type().equalsIgnoreCase(selectedNode.getType())) {
                boolean isChanged = backEndServer.renameProject(newName);
                if (isChanged) {
                    JOptionPane.showMessageDialog(null, "更新成功", "成功", JOptionPane.PLAIN_MESSAGE);
                    selectedNode.setName(newName);
                    treeModel.nodeChanged(selectedNode);
                } else {
                    JOptionPane.showMessageDialog(null, "更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
                return;

            } else {
                if (!newName.endsWith(PromptNode.nameSuffix(selectedNode.getType()))) {
                    JOptionPane.showMessageDialog(null, "不需要修改后缀", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean is = backEndServer.checkName(newName, selectedNode.getType());
                if (!is) {
                    JOptionPane.showMessageDialog(null, "名字已存在", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean isUpdated = backEndServer.updateName(newName, selectedNode.getType(), selectedNode.getId());
                if (!isUpdated) {
                    JOptionPane.showMessageDialog(null, "名字更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            //异步更新树
            selectedNode.setName(newName);
            treeModel.nodeChanged(selectedNode);
            CompletableFuture.runAsync(() -> backEndServer.updateProjectTree());
        });


        // 为新增子节点菜单项添加动作监听器
        addItem.addActionListener(e -> {
            PromptNode selectedNode = (PromptNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                // 创建下拉列表让用户选择节点类型
                String[] types = new String[]{selectedNode.getBaseType(), TreeType.FOLDER.type()};
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
                    if (!backEndServer.checkName(name, type)) {
                        JOptionPane.showMessageDialog(null, "有重复名称", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (name != null && !name.trim().isEmpty()) {
                        // 根据选择创建新的节点
                        PromptNode childNode = PromptNode.build(name, type, selectedNode.getBaseType());
                        if (TreeType.PROMPT.type().equalsIgnoreCase(type) || TreeType.SECTION.type().equalsIgnoreCase(type)) {
                            boolean isSuccess = backEndServer.addNode(childNode, type);
                            if (!isSuccess) {
                                JOptionPane.showMessageDialog(null, "创建节点失败", "错误", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        selectedNode.addChild(childNode);
                        // 通知模型节点已经发生变化，以刷新显示
                        treeModel.nodesWereInserted(selectedNode, new int[]{selectedNode.getIndex(childNode)});
                        // 异步更新数据库数据
                        CompletableFuture.runAsync(() -> backEndServer.updateProjectTree());
                    }
                }
            }
        });
        return tree;
    }

    /**
     * 处理单击
     *
     * @param selectedNode 选择的节点
     */
    private void handleTreeNodeClick(PromptNode selectedNode) {
        if (!TreeUtils.isEditable(selectedNode)) {
            return;
        }
        Editable editable = backEndServer.getPrompt(selectedNode);
        if (editable == null) {
            showStatusMessage(selectedNode.getName() + "：获取节点内容失败", "Error");
            detailTextArea.setEditable(false);
            return;
        }
        detailTextArea.setEditable(true);
        detailTextArea.bind(selectedNode, editable.getContent());
        requestParamsTextArea.setText(TextEditor.paramsResolverStr(editable.getContent()));

        flashHistoryTo(selectedNode);
    }

    private void flashHistoryTo(PromptNode selectedNode) {
        historyModel.clear();
        httpResponseArea.setText("");
        List<HistoryItem> historyItemList = backEndServer.queryHistory(selectedNode);
        historyModel.addAll(historyItemList);
    }

    /**
     * 处理双击
     *
     * @param selectedNode
     */
    private void handleTreeNodeDoubleClick(PromptNode selectedNode) {
    }

    private void showMenu(MouseEvent e, JPopupMenu popupMenu, JMenuItem addItem, JMenuItem renameItem, JMenuItem deleteItem) {
        int row = tree.getClosestRowForLocation(e.getX(), e.getY());
        tree.setSelectionRow(row);
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            PromptNode selectedNode = (PromptNode) path.getLastPathComponent();
            if (TreeType.ROOT.type().equalsIgnoreCase(selectedNode.getType())) {
                popupMenu.remove(deleteItem);
                popupMenu.remove(addItem);
                popupMenu.add(renameItem);
                popupMenu.show(tree, e.getX(), e.getY());
                return;
            }
            if (TreeUtils.isFolder(selectedNode)) {
                // 如果是folder类型，显示“添加子节点”菜单项
                popupMenu.add(addItem);
            } else {
                // 如果不是，移除该菜单项确保它不显示
                popupMenu.remove(addItem);
            }
            if (TreeType.PROMPT_FOLDER.type().equalsIgnoreCase(selectedNode.getType())
                    || TreeType.SECTION_FOLDER.type().equals(selectedNode.getType())) {
                popupMenu.remove(renameItem);
                popupMenu.remove(deleteItem);
            } else {
                popupMenu.add(renameItem);
                popupMenu.add(deleteItem);
            }
            popupMenu.show(tree, e.getX(), e.getY());
        }
    }

    private PromptNode initialTreeNode() {
        return backEndServer.getPromptTree();
    }


    private void publish(String urlString, JTextArea responseArea) {

    }

    private static DefaultMutableTreeNode convertToTreeNode(PromptNode promptNode) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(promptNode.getName());
        for (PromptNode child : promptNode.getChildren()) {
            node.add(convertToTreeNode(child));
        }
        return node;
    }

}
