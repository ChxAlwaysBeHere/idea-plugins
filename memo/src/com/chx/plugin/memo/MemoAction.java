package com.chx.plugin.memo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author chenxi
 */
public class MemoAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Logger.init();

        if (new MemoDialog().showAndGet()) {
            // ok
        }
    }
}
