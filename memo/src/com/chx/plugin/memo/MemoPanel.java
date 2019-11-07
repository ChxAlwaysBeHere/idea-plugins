package com.chx.plugin.memo;

import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import org.apache.commons.lang3.math.NumberUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author chenxi
 * @date 2019-10-30
 */
public class MemoPanel extends JBPanel<MemoPanel> {

    private Memos state;

    private JTable table;

    private JTextArea contentArea;

    private JButton addButton;

    private JButton removeButton;

    public MemoPanel(Memos state) {
        this.state = state;

        try {
            init();
        } catch (Exception e) {
            Logger.error("Fail to initialize Memo");
        }
    }

    public void init() {
        initTable();
        initContentArea();
        initButton();

        arrangeLayout();
    }

    protected void initTable() {
        List<Memos.Item> items = state.getItems();
        List<String> columns = Arrays.asList("优先级", "创建时间", "标题");
        List<Boolean> editableFlags = Arrays.asList(true, false, true);
        List<Class<?>> columnClasses = Arrays.asList(int.class, String.class, String.class);

        table = new JBTable();
        TableModel tableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return items.size();
            }

            @Override
            public int getColumnCount() {
                return columns.size();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return items.get(rowIndex).getPriority();
                    case 1:
                        return LocalDateTime.ofInstant(Instant.ofEpochMilli(items.get(rowIndex).getCreateTimestamp()), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    case 2:
                        return items.get(rowIndex).getTitle();
                    default:
                        return "";
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        if (NumberUtils.isDigits(aValue.toString())) {
                            items.get(rowIndex).setPriority(Integer.parseInt(aValue.toString()));
                        }
                        break;
                    case 1:
                        // not allowed
                        break;
                    case 2:
                        items.get(rowIndex).setTitle(aValue.toString());
                        break;
                    default:
                        // impossible
                }
                fireTableCellUpdated(rowIndex, columnIndex);
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return editableFlags.get(columnIndex);
            }

            @Override
            public String getColumnName(int column) {
                return columns.get(column);
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnClasses.get(columnIndex);
            }

        };
        table.setModel(tableModel);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return -1 * o1.compareTo(o2);
            }
        });
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
        sorter.setSortsOnUpdates(true);
        for (int i = 0; i < columns.size(); i++) {
            if (i == 0) {
                sorter.setSortable(i, true);
            } else {
                sorter.setSortable(i, false);
            }
        }
        table.setRowSorter(sorter);

        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JBTextField()));
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JBTextField()));

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int viewRowIndex = table.getSelectedRow();
                if (viewRowIndex < 0) {
                    if (Objects.nonNull(contentArea)) {
                        contentArea.setText("");
                    }
                } else {
                    int modelRowIndex = table.getRowSorter().convertRowIndexToModel(viewRowIndex);
                    if (Objects.nonNull(contentArea)) {
                        String content = items.get(modelRowIndex).getContent();
                        contentArea.setText(content);
                    }
                }
            }
        });
        table.clearSelection();
        table.getSelectionModel().setSelectionInterval(0, 0);
    }

    protected void initContentArea() {
        contentArea = new JTextArea("");
        if (table.getSelectedRow() >= 0) {
            int modelRowIndex = table.getRowSorter().convertRowIndexToModel(table.getSelectedRow());
            contentArea.setText(state.getItems().get(modelRowIndex).getContent());
        }
        contentArea.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent event) {
                int viewRowIndex = table.getSelectedRow();
                if (viewRowIndex < 0) {
                    return;
                }

                int modelRowIndex = table.getRowSorter().convertRowIndexToModel(table.getSelectedRow());
                try {
                    String content = event.getDocument().getText(0, event.getDocument().getLength());
                    state.getItems().get(modelRowIndex).setContent(content);
                } catch (BadLocationException e1) {
                    Logger.error("memo item content not saved");
                }
            }
        });
        contentArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (table.getSelectedRow() < 0) {
                    contentArea.setEditable(false);
                } else {
                    contentArea.setEditable(true);
                }
            }
        });
    }

    protected void initButton() {
        addButton = new JButton("添加");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                state.getItems().add(new Memos.Item());

                int modelRowIndex = state.getItems().size() - 1;
                ((AbstractTableModel) table.getModel()).fireTableRowsInserted(modelRowIndex, modelRowIndex);

                int viewRowIndex = table.getRowSorter().convertRowIndexToView(modelRowIndex);
                table.setRowSelectionInterval(viewRowIndex, viewRowIndex);
            }
        });

        removeButton = new JButton("删除");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int viewRowIndex = table.getSelectedRow();
                if (viewRowIndex < 0) {
                    return;
                }
                table.clearSelection();

                int modelRowIndex = table.getRowSorter().convertRowIndexToModel(viewRowIndex);
                state.getItems().remove(modelRowIndex);

                ((AbstractTableModel) table.getModel()).fireTableRowsDeleted(modelRowIndex, modelRowIndex);
            }
        });
    }

    protected void arrangeLayout() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(420, 500));

        // table
        JScrollPane tablePane = new JBScrollPane(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(42);
        table.getColumnModel().getColumn(1).setPreferredWidth(126);
        table.setRowHeight(25);
        tablePane.setPreferredSize(new Dimension(400, 300));

        // label
        JPanel labelPanel = new JBPanel<>();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.add(Box.createHorizontalStrut(5));
        labelPanel.add(new JBLabel("描述"));
        labelPanel.add(Box.createHorizontalGlue());

        // content
        JScrollPane contentPane = new JBScrollPane(contentArea);
        contentPane.setPreferredSize(new Dimension(400, 120));

        // button
        JPanel buttonPanel = new JBPanel<>();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(removeButton);

        // add all components
        this.add(tablePane);
        this.add(Box.createVerticalStrut(5));
        this.add(labelPanel);
        this.add(Box.createVerticalStrut(5));
        this.add(contentPane);
        this.add(Box.createVerticalStrut(5));
        this.add(buttonPanel);
    }

}
