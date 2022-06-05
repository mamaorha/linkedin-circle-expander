package co.il.nmh.linkedin.circle.expander.gui.panels;

import co.il.nmh.easy.swing.components.EasyScrollPane;
import co.il.nmh.easy.swing.components.table.listeners.EasyTableElementListener;
import co.il.nmh.easy.swing.components.table.listeners.EasyTableItemSelectedChangedListener;
import co.il.nmh.easy.swing.components.text.EasyTextField;
import co.il.nmh.linkedin.circle.expander.data.Filter;
import co.il.nmh.linkedin.circle.expander.gui.components.FilterTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Maor Hamami
 */

public class FilterPanel extends JPanel {
    private static final long serialVersionUID = -6969418307265853019L;

    protected FilterTable filterTable;
    protected JButton deleteBtn;
    protected EasyTextField filterTb;
    protected JButton addFilterBtn;

    protected Set<Filter> filters;

    public FilterPanel() {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(5, 0, 5, 0));

        buildPanel();
        addEvents();

        filters = new HashSet<>();
    }

    private void buildPanel() {
        filterTable = new FilterTable();
        deleteBtn = new JButton("delete");
        deleteBtn.setEnabled(false);

        JLabel filterLbl = new JLabel("filter:");
        filterTb = new EasyTextField(25);
        addFilterBtn = new JButton("add");

        JPanel panel = new JPanel();
        panel.add(filterLbl);
        panel.add(filterTb);
        panel.add(addFilterBtn);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.gridx++;

        gridBagConstraints.gridy++;
        add(new EasyScrollPane(filterTable, 150, 150), gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;

        gridBagConstraints.gridy++;
        add(deleteBtn, gridBagConstraints);

        gridBagConstraints.gridy++;
        add(panel, gridBagConstraints);
    }

    private void addEvents() {
        filterTable.addEasyTableElementListener(new EasyTableElementListener() {
            @Override
            public void elementRemoved(int index, Object element) {
                if(element instanceof Filter) {
                    filters.remove(element);
                }
            }

            @Override
            public void elementAdd(int index, Object element) {
                filters.add((Filter) element);
            }
        });

        filterTable.addEasyTableItemSelectedChangedListener(new EasyTableItemSelectedChangedListener() {
            @Override
            public void selectionClear() {
                deleteBtn.setEnabled(false);
            }

            @Override
            public void selected(int[] selectedRows) {
                deleteBtn.setEnabled(selectedRows.length > 0);
            }
        });

        deleteBtn.addActionListener(e -> filterTable.deleteSelectedRows());

        filterTb.addTextChangedListener(newText -> addFilterBtn.setEnabled(!newText.isEmpty()));

        addFilterBtn.addActionListener(e -> {
            String text = filterTb.getText();
            Filter filter = new Filter(text, false);

            if (!filters.contains(filter)) {
                filterTable.add(filter);
            }

            filterTb.clear();
            filterTb.requestFocus();
        });
    }

    public void setFilter(Set<Filter> filters) {
        if (null == filters) {
            filters = new HashSet<>();
        }

        filterTable.clear();

        for (Filter currFilter : filters) {
            filterTable.add(currFilter);
        }

        this.filters = filters;
    }

    public Set<Filter> getFilters() {
        return filters;
    }

    public void lockGUI(boolean lock) {
        deleteBtn.setEnabled(!lock && filterTable.getSelectedRows().length > 0);
        filterTable.setEnabled(!lock);
    }
}
