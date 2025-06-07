package cn.bigcoder.plugin.objecthelper.action;

import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getOperateFieldName;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getOperatePsiClass;
import static cn.bigcoder.plugin.objecthelper.common.util.PsiUtils.getPsiClassName;

import cn.bigcoder.plugin.objecthelper.common.util.NotificationUtils;
import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.generator.copy.SmartObjectCopyGenerator;
import cn.bigcoder.plugin.objecthelper.ui.ClassSearchDialog;
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
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) {
            return;
        }
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        Document document = editor.getDocument();
        // 删除光标所在的变量
        int startOffset = findVariableStartOffset(document, offset);
        int endOffset = findVariableEndOffset(document, offset);
        if (startOffset >= 0 && endOffset >= 0) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.deleteString(startOffset, endOffset);
            });
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.insertString(offset, copyCodeStr);
        });
    }

    private int findVariableStartOffset(Document document, int offset) {
        int start = offset;
        while (start > 0) {
            char c = document.getCharsSequence().charAt(start - 1);
            if (!Character.isJavaIdentifierPart(c)) {
                break;
            }
            start--;
        }
        return start;
    }

    private int findVariableEndOffset(Document document, int offset) {
        int end = offset;
        int length = document.getTextLength();
        while (end < length) {
            char c = document.getCharsSequence().charAt(end);
            if (!Character.isJavaIdentifierPart(c)) {
                break;
            }
            end++;
        }
        return end;
    }

    @Override
    public boolean actionShow(AnActionEvent anActionEvent) {
        PsiClass sourcePsiClass = getOperatePsiClass(anActionEvent);
        String operateFieldName = getOperateFieldName(anActionEvent);
        return sourcePsiClass != null && !StringUtils.isEmpty(operateFieldName);
    }
}