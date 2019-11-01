package com.chx.plugin.memo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author chenxi
 * @date 2019-10-18
 */
public class Memos implements Serializable {

    private Long lastSaveTimestamp;

    private List<Item> items = new ArrayList<>();

    public Memos() {
    }

    public Long getLastSaveTimestamp() {
        return lastSaveTimestamp;
    }

    public void setLastSaveTimestamp(Long lastSaveTimestamp) {
        this.lastSaveTimestamp = lastSaveTimestamp;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Memos memoState = (Memos) o;

        return items.equals(memoState.items);
    }

    public static class Item implements Serializable {

        private final int MIN_PRIORITY = 10;
        private final int MAX_PRIORITY = 1;
        private final int DEFAULT_PRIORITY = MIN_PRIORITY;

        private String title;

        private String content;

        private int priority;

        private Long createTimestamp;

        private Long updateTimestamp;

        public Item() {
            this.priority = DEFAULT_PRIORITY;
            this.title = "";
            this.content = "";
            this.createTimestamp = System.currentTimeMillis();
            this.updateTimestamp = System.currentTimeMillis();
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            if (!Objects.equals(this.title, title)) {
                setUpdateTimestamp(System.currentTimeMillis());
            }
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            if (!Objects.equals(this.content, content)) {
                setUpdateTimestamp(System.currentTimeMillis());
            }
            this.content = content;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            if (!Objects.equals(this.priority, priority)) {
                setUpdateTimestamp(System.currentTimeMillis());
            }
            this.priority = priority;
        }

        public Long getCreateTimestamp() {
            return createTimestamp;
        }

        public void setCreateTimestamp(Long createTimestamp) {
            this.createTimestamp = createTimestamp;
        }

        public Long getUpdateTimestamp() {
            return updateTimestamp;
        }

        public void setUpdateTimestamp(Long updateTimestamp) {
            this.updateTimestamp = updateTimestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            if (priority != item.priority) return false;
            if (!title.equals(item.title)) return false;
            if (!content.equals(item.content)) return false;
            return createTimestamp.equals(item.createTimestamp);
        }

        @Override
        public int hashCode() {
            int result = title.hashCode();
            result = 31 * result + content.hashCode();
            result = 31 * result + priority;
            result = 31 * result + createTimestamp.hashCode();
            return result;
        }
    }

}
