package cn.bigcoder.plugin.objecthelper.action;

import cn.bigcoder.plugin.objecthelper.common.enums.FunctionSwitchEnum;
import cn.bigcoder.plugin.objecthelper.common.util.PsiUtils;
import cn.bigcoder.plugin.objecthelper.common.util.StringUtils;
import cn.bigcoder.plugin.objecthelper.config.PluginConfigState;
import cn.bigcoder.plugin.objecthelper.generator.Generator;
import cn.bigcoder.plugin.objecthelper.generator.method.ObjectCopyMethodGenerator;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;

import static cn.bigcoder.plugin.objecthelper.common.constant.JavaKeyWord.VOID;

public class ObjectCopyMethodAction extends AbstractClassAnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), () -> {
            generateO2O(PsiUtils.getCursorPsiMethod(anActionEvent));
        });
    }

    @Override
    public boolean actionShow(AnActionEvent anActionEvent) {
        return PluginConfigState.getInstance().getObjectCopySwitch() == FunctionSwitchEnum.OPEN
            && check(PsiUtils.getCursorPsiMethod(anActionEvent));
    }

    private void generateO2O(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return;
        }
        // 初始化生成器
        Generator generator = ObjectCopyMethodGenerator.getInstance(psiMethod);
        String methodCode = generator.generate();
        if (StringUtils.isEmpty(methodCode)) {
            return;
        }
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiMethod.getProject());
        // 生成新的PsiMethod
        PsiMethod toMethod = elementFactory.createMethodFromText(generator.generate(), psiMethod);
        psiMethod.replace(toMethod);
    }

    /**
     * 检查当前光标
     * 1. 是否在方法中
     * 2. 方法是否有入参
     * 3. 方法是否有返回值
     *
     * @param psiMethod
     * @return
     */
    private boolean check(PsiMethod psiMethod) {
        if (psiMethod == null
            || PsiUtils.getPsiParameters(psiMethod).isEmpty()
            || VOID.equals(PsiUtils.getMethodReturnClassName(psiMethod))) {
            return false;
        }
        return true;
    }
}
