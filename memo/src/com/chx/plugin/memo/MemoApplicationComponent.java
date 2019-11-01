package com.chx.plugin.memo;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author chenxi
 */
//@State(name = "MemoApplicationComponent", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
/* Unknown macro: $WORKSPACE_FILE$ ? */
@State(name = "MemoApplicationComponent", storages = @Storage("~/idea-plugin/memo.xml"))
public class MemoApplicationComponent implements ApplicationComponent, PersistentStateComponent<Memos> {

    private Memos memos = new Memos();

    public MemoApplicationComponent() {
    }

    @Override
    public void initComponent() {
        // insert component initialization logic here
    }

    @Override
    public void disposeComponent() {
        // insert component disposal logic here
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "Memo";
    }

    /**
     * called every time the settings are saved
     *
     * @return
     */
    @Nullable
    @Override
    public Memos getState() {
        memos.setLastSaveTimestamp(System.currentTimeMillis());
        return memos;
    }

    @Override
    public void loadState(Memos memoState) {
        this.memos = memoState;
    }

    public Memos getMemos() {
        return memos;
    }

    public void setMemos(Memos memos) {
        this.memos = memos;
    }
}
