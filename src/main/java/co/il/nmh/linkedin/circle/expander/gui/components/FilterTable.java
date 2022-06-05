package co.il.nmh.linkedin.circle.expander.gui.components;

import co.il.nmh.easy.swing.components.table.EasyTable;
import co.il.nmh.easy.swing.components.table.data.ComboBoxData;
import co.il.nmh.easy.swing.components.table.listeners.EasyTableComboBoxRender;
import co.il.nmh.easy.swing.components.table.listeners.EasyTableComboChangedListener;
import co.il.nmh.easy.swing.components.text.EasyTextField;
import co.il.nmh.linkedin.circle.expander.data.Filter;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

public class FilterTable extends EasyTable implements EasyTableComboBoxRender, EasyTableComboChangedListener {
    private static final String VALUE_COLUMN = "Value";
    private static final String ACTION_COLUMN = "Action";
    private static final String INCLUDE_ACTION = "include";
    private static final String EXCLUDE_ACTION = "exclude";
    private static final Collection<String> ACTIONS = Arrays.asList(INCLUDE_ACTION, EXCLUDE_ACTION);

    private boolean enabled;

    public FilterTable() {
        super(new String[]{VALUE_COLUMN, ACTION_COLUMN});

        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        setColumnMinWidth(VALUE_COLUMN, 150);

        initTable();
    }

    private void initTable() {
        setComboColumn(ACTION_COLUMN, this);
        addComboboxListener(this);

        this.enabled = true;
    }

    @Override
    public Component renderObject(String columnName, Object value) {
        if (value instanceof Filter) {
            Filter filter = (Filter) value;
            EasyTextField easyTextField = new EasyTextField(10);
            easyTextField.setText(filter.getValue());
            easyTextField.setEditable(false);

            return easyTextField;
        }

        return new JLabel(value.toString());
    }

    @Override
    public ComboBoxData getComboBoxData(String columnName, Object value) {
        if (value instanceof Filter) {
            Filter filter = (Filter) value;

            ComboBoxData comboBoxData = new ComboBoxData();

            if (ACTION_COLUMN.equals(columnName)) {
                comboBoxData.setItems(ACTIONS);
            }

            comboBoxData.setSelectedIndex(filter.isInclude() ? 0 : 1);

            return comboBoxData;
        }

        return null;
    }

    @Override
    public void comboChanged(Object value, String columnName, int row, int col, int index) {
        if (!enabled) return;

        if (columnName.equals(ACTION_COLUMN)) {
            ((Filter) value).setInclude(index == 0);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
