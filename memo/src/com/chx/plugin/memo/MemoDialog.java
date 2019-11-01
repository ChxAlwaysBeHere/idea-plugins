package com.chx.plugin.memo;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author chenxi
 * @date 2019-10-25
 */
public class MemoDialog extends DialogWrapper {

    protected MemoDialog() {
        super(true);
        init();
        setTitle("备忘录");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        MemoApplicationComponent applicationComponent = ApplicationManager.getApplication().getComponent(MemoApplicationComponent.class);
        Memos state = applicationComponent.getMemos();

        return new MemoPanel(state);
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = getOKAction();
        okAction.putValue("Name", "关闭");

        return new Action[]{okAction};
    }

}
