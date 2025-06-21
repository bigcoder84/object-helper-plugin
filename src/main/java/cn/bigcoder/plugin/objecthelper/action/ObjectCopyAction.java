package cn.bigcoder.plugin.objecthelper.action;

import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getOperateFieldName;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getOperatePsiClass;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getPsiClassName;

import cn.bigcoder.plugin.objecthelper.common.util.NotificationUtils;
import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.generator.copy.SmartObjectCopyGenerator;
import cn.bigcoder.plugin.objecthelper.ui.ClassSearchDialog;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class ObjectCopyAction extends AbstractClassAnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent anAction) {
        PsiClass sourcePsiClass = getOperatePsiClass(anAction);
        String operateFieldName = getOperateFieldName(anAction);
        if (sourcePsiClass == null || StringUtils.isEmpty(operateFieldName)) {
            NotificationUtils.notifyInfo(anAction.getProject(), "The source object type was not obtained");
            return;
        }
        Project project = anAction.getProject();
        PsiClass targetPsiClass = null;
        if (project != null) {
            ClassSearchDialog dialog = new ClassSearchDialog(project, "Search target object class");
            if (dialog.showAndGet()) {
                targetPsiClass = dialog.getSelectedClass();
            }
        }
        if (targetPsiClass == null) {
            NotificationUtils.notifyInfo(anAction.getProject(), "The target object type was not obtained");
            return;
        }
        String targetParameterName = StringUtils.firstLowerCase(
                Objects.requireNonNull(getPsiClassName(targetPsiClass)));
        // 防止方法入参和返回参数名称一致
        if (operateFieldName.equals(targetParameterName)) {
            targetParameterName = targetParameterName + "Res";
        }
        // 代码生成器
        Generator generator = new SmartObjectCopyGenerator(sourcePsiClass, targetPsiClass, operateFieldName,
                targetParameterName);

        insertCode(project, generator.generate());
    }

    /**
     * 将代码插入光标处
     *
     * @param project
     * @param copyCodeStr
     */
    private void insertCode(Project project, String copyCodeStr) {
        // 检查代码末尾是否有换行符，没有则添加
        if (!copyCodeStr.endsWith("\n")) {
            copyCodeStr += "\n";
        }
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) {
            return;
        }
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        Document document = editor.getDocument();

        // 获取当前光标所在行号
        int lineNumber = document.getLineNumber(offset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int lineEndOffset = document.getLineEndOffset(lineNumber);

        // 检查当前行在光标之后是否有非空白字符
        CharSequence lineText = document.getCharsSequence().subSequence(offset, lineEndOffset);
        boolean hasNonWhitespaceAfterCursor = !lineText.toString().trim().isEmpty();

        // 获取当前行的缩进
        String indent = getLineIndent(document, lineNumber);
        // 判断当前行是否为方法头
        String fullLineText = document.getCharsSequence().subSequence(lineStartOffset, lineEndOffset).toString();
        if (isMethodHeader(fullLineText)) {
            // 若是方法头，缩进增加一格，假设一格为 4 个空格
            indent += "    ";
        }

        // 为插入的代码添加缩进
        String indentedCopyCodeStr = addIndentToCode(copyCodeStr, indent);

        if (!hasNonWhitespaceAfterCursor) {
            // 如果光标之后没有非空白字符，删除当前行
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.deleteString(lineStartOffset, lineEndOffset);
            });
            offset = lineStartOffset;
        } else {
            // 如果光标之后有非空白字符，将插入位置移动到下一行开头
            int nextLineStart = document.getLineStartOffset(lineNumber + 1);
            offset = nextLineStart;
        }

        int finalOffset = offset;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.insertString(finalOffset, indentedCopyCodeStr);
        });
    }

    /**
     * 获取指定行的缩进
     * @param document 文档对象
     * @param lineNumber 行号
     * @return 缩进字符串
     */
    private String getLineIndent(Document document, int lineNumber) {
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        CharSequence lineText = document.getCharsSequence().subSequence(lineStartOffset, lineEndOffset);
        int indentLength = 0;
        while (indentLength < lineText.length() && Character.isWhitespace(lineText.charAt(indentLength))) {
            indentLength++;
        }
        return lineText.subSequence(0, indentLength).toString();
    }

    /**
     * 判断当前行是否为方法头
     * @param lineText 当前行的文本内容
     * @return 如果是方法头返回 true，否则返回 false
     */
    private boolean isMethodHeader(String lineText) {
        // 去除注释内容，避免注释中的括号影响判断
        lineText = removeComments(lineText);
        // 去除前后空白字符
        lineText = lineText.trim();
        // 简单判断，包含 ( 和 ) 且不包含 ; 认为是方法头
        return lineText.contains("(") && lineText.contains(")") && !lineText.contains(";");
    }


    /**
     * 移除字符串中的注释内容
     * @param lineText 包含注释的字符串
     * @return 移除注释后的字符串
     */
    private String removeComments(String lineText) {
        // 移除单行注释
        int singleCommentIndex = lineText.indexOf("//");
        if (singleCommentIndex != -1) {
            lineText = lineText.substring(0, singleCommentIndex);
        }
        // 移除多行注释开始标记之后的内容（简单处理，不处理嵌套情况）
        int multiCommentStartIndex = lineText.indexOf("/*");
        if (multiCommentStartIndex != -1) {
            lineText = lineText.substring(0, multiCommentStartIndex);
        }
        return lineText;
    }

    /**
     * 为代码的每一行添加缩进
     * @param code 原始代码
     * @param indent 缩进字符串
     * @return 添加缩进后的代码
     */
    private String addIndentToCode(String code, String indent) {
        StringBuilder indentedCode = new StringBuilder();
        String[] lines = code.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            indentedCode.append(indent).append(lines[i]);
            if (i < lines.length - 1) {
                indentedCode.append("\n");
            }
        }
        return indentedCode.toString();
    }

    @Override
    public boolean actionShow(AnActionEvent anActionEvent) {
        PsiClass sourcePsiClass = getOperatePsiClass(anActionEvent);
        String operateFieldName = getOperateFieldName(anActionEvent);
        return sourcePsiClass != null && !StringUtils.isEmpty(operateFieldName);
    }
}