package cn.bigcoder.plugin.objecthelper.ui;

import cn.bigcoder.plugin.objecthelper.common.util.PsiUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class ClassSearchDialog extends DialogWrapper {

    private final Project project;
    private JBTextField classNameField;
    private JBList<PsiClass> classList;
    private PsiClass selectedClass;
    private Timer timer;
    private static final int DEBOUNCE_DELAY = 800; // 防抖延迟时间，单位：毫秒
    // 定义默认宽度，可按需调整
    private static final int DEFAULT_WIDTH = 700;
    // 定义默认高度，可按需调整
    private static final int DEFAULT_HEIGHT = 600;
    private JLabel loadingLabel;
    private Map<String, List<PsiClass>> searchCache = new HashMap<>();

    public ClassSearchDialog(@Nullable Project project, String title) {
        super(project);
        this.project = project;
        this.setTitle(title);
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.init();
    }

    @Override
    protected void init() {
        super.init();
        // 确保对话框显示后输入框获得焦点
        SwingUtilities.invokeLater(() -> classNameField.requestFocusInWindow());
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 输入框
        classNameField = new JBTextField();
        panel.add(classNameField, BorderLayout.NORTH);

        // 类列表
        classList = new JBList<>();
        classList.setCellRenderer(new ClassListCellRenderer());
        JBScrollPane scrollPane = new JBScrollPane(classList);

        // 创建加载提示标签
        loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        loadingLabel.setVisible(false); // 初始状态隐藏

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(scrollPane, BorderLayout.CENTER);
        listPanel.add(loadingLabel, BorderLayout.SOUTH);

        panel.add(listPanel, BorderLayout.CENTER);

        classNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                debounceUpdateClassList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                debounceUpdateClassList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                debounceUpdateClassList();
            }
        });

        // 给输入框添加键盘事件监听，处理方向下键
        classNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    // 当按下方向下键时，将焦点转移到类列表
                    classList.requestFocusInWindow();
                    if (classList.getModel().getSize() > 0) {
                        // 选中列表第一项
                        classList.setSelectedIndex(0);
                    }
                }
            }
        });

        // 双击列表项确认选择
        classList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedClass = classList.getSelectedValue();
            }
        });

        // 添加双击事件监听
        classList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 判断是否为双击事件
                    if (classList.getSelectedValue() != null) {
                        selectedClass = classList.getSelectedValue();
                        doOKAction(); // 调用确认操作
                    }
                }
            }
        });

        // 监听候选列表的键盘事件，当有按键输入时，将焦点移回输入框
        classList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (Character.isLetterOrDigit(e.getKeyChar()) || Character.isWhitespace(e.getKeyChar())) {
                    classNameField.requestFocusInWindow();
                    // 将按下的字符插入到输入框中
                    classNameField.setText(classNameField.getText() + e.getKeyChar());
                }
            }
        });

        return panel;
    }

    private void debounceUpdateClassList() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 显示加载提示
                SwingUtilities.invokeLater(() -> loadingLabel.setVisible(true));
                // 异步执行搜索操作
                SwingWorker<List<PsiClass>, Void> worker = new SwingWorker<>() {
                    @Override
                    protected List<PsiClass> doInBackground() {
                        return updateClassListInBackground();
                    }

                    @Override
                    protected void done() {
                        try {
                            List<PsiClass> matchedClasses = get();
                            SwingUtilities.invokeLater(() -> {
                                updateClassListUI(matchedClasses);
                                // 隐藏加载提示
                                loadingLabel.setVisible(false);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            // 出现异常也隐藏加载提示
                            SwingUtilities.invokeLater(() -> loadingLabel.setVisible(false));
                        }
                    }
                };
                worker.execute();
            }
        }, DEBOUNCE_DELAY);
    }

    private List<PsiClass> updateClassListInBackground() {
        String query = classNameField.getText().trim();
        if (!query.isEmpty()) {
            if (searchCache.containsKey(query)) {
                return searchCache.get(query);
            }
            List<PsiClass> matchedClasses = PsiUtils.matchPsiClassByName(this.project, query);
            // 按优先级排序
            matchedClasses.sort(this::compareClasses);
            searchCache.put(query, matchedClasses);
            return matchedClasses;
        }
        return Collections.emptyList();
    }

    private void updateClassListUI(List<PsiClass> matchedClasses) {
        classList.setListData(ArrayUtil.toObjectArray(matchedClasses, PsiClass.class));
    }

    private void updateClassList() {
        String query = classNameField.getText().trim();
        if (!query.isEmpty()) {
            List<PsiClass> matchedClasses = PsiUtils.matchPsiClassByName(this.project, query);
            // 按优先级排序
            matchedClasses.sort(this::compareClasses);
            classList.setListData(ArrayUtil.toObjectArray(matchedClasses, PsiClass.class));
        } else {
            classList.setListData(new PsiClass[0]);
        }
    }

    private int compareClasses(PsiClass c1, PsiClass c2) {
        int priority1 = getClassPriority(c1);
        int priority2 = getClassPriority(c2);
        return Integer.compare(priority1, priority2);
    }

    private int getClassPriority(PsiClass cls) {
        if (isProjectClass(cls)) {
            return 1; // 项目内自定义的类
        } else if (isJDKClass(cls)) {
            return 2; // Java JDK 自带的类
        } else {
            return 3; // 其它类
        }
    }

    private boolean isProjectClass(PsiClass cls) {
        if (project == null || cls.getContainingFile() == null) {
            return false;
        }
        VirtualFile virtualFile = cls.getContainingFile().getVirtualFile();
        return virtualFile != null && project.getBasePath() != null && virtualFile.getPath()
                .startsWith(project.getBasePath());
    }

    private boolean isJDKClass(PsiClass cls) {
        String qualifiedName = cls.getQualifiedName();
        return qualifiedName != null && qualifiedName.startsWith("java.");
    }

    @Override
    protected void doOKAction() {
        if (selectedClass == null && classList.getModel().getSize() > 0) {
            selectedClass = classList.getModel().getElementAt(0);
        }
        super.doOKAction();
    }

    public PsiClass getSelectedClass() {
        return selectedClass;
    }

    // 自定义 ListCellRenderer
    private class ClassListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof PsiClass) {
                PsiClass psiClass = (PsiClass) value;
                String className = psiClass.getName();
                String qualifiedName = psiClass.getQualifiedName();
                String query = classNameField.getText().trim().toLowerCase();

                if (className != null && qualifiedName != null) {
                    // 仅高亮显示类名
                    String highlightedClassName = highlightMatches(className, query);

                    // 使用 HTML 格式设置文本样式
                    String text = String.format("%s <font color='gray'>of %s</font>", highlightedClassName,
                            qualifiedName);
                    setText("<html>" + text + "</html>");
                    // 设置 IDEA 官方类图标
                    setIcon(AllIcons.Nodes.Class);

                    // 如果是 JDK 自带的类，背景用浅蓝色展示
                    if (isJDKClass(psiClass)) {
                        setBackground(new Color(61, 50, 35));
                        if (isSelected) {
                            setBackground(new Color(46, 67, 110));
                        }
                    } else if (!isProjectClass(psiClass)) {
                        // 其他类，背景用浅粉色展示
                        setBackground(new Color(61, 50, 35));
                        if (isSelected) {
                            setBackground(new Color(46, 67, 110));
                        }
                    } else {
                        // 项目内的类，保持原有背景
                        setBackground(list.getBackground());
                    }
                }
            }
            return c;
        }

        private String highlightMatches(String input, String query) {
            if (query.isEmpty()) {
                return input;
            }
            StringBuilder result = new StringBuilder();
            int lastIndex = 0;
            int index = input.toLowerCase().indexOf(query);
            while (index != -1) {
                result.append(input, lastIndex, index);
                // 使用亮黄色作为高亮颜色
                result.append("<font style='background-color: #BA9752;color: black;'>")
                        .append(input, index, index + query.length()).append("</font>");
                lastIndex = index + query.length();
                index = input.toLowerCase().indexOf(query, lastIndex);
            }
            result.append(input.substring(lastIndex));
            return result.toString();
        }
    }
}