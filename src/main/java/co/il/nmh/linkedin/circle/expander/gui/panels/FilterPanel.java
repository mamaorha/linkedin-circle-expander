package co.il.nmh.linkedin.circle.expander.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import co.il.nmh.easy.swing.components.list.EasyList;
import co.il.nmh.easy.swing.components.list.listeners.EasyListElementListener;
import co.il.nmh.easy.swing.components.list.listeners.EasyListItemSelectedChangedListener;
import co.il.nmh.easy.swing.components.text.EasyTextField;
import co.il.nmh.easy.swing.components.text.listeners.TextChangedListener;

/**
 * @author Maor Hamami
 */

public class FilterPanel extends JPanel
{
	private static final long serialVersionUID = -6969418307265853019L;

	protected EasyList filterList;
	protected JButton deleteBtn;
	protected EasyTextField filterTb;
	protected JButton addFilterBtn;

	protected Set<String> filter;

	public FilterPanel()
	{
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(5, 0, 5, 0));

		buildPanel();
		addEvents();

		filter = new HashSet<>();
	}

	private void buildPanel()
	{
		filterList = new EasyList();
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
		add(filterList.getComponent(), gridBagConstraints);

		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 0;

		gridBagConstraints.gridy++;
		add(deleteBtn, gridBagConstraints);

		gridBagConstraints.gridy++;
		add(panel, gridBagConstraints);
	}

	private void addEvents()
	{
		filterList.addEasyListElementListener(new EasyListElementListener()
		{
			@Override
			public void elementRemoved(int index, String element)
			{
				filter.remove(element);
			}

			@Override
			public void elementAdd(int index, String element)
			{
				filter.add(element);
			}
		});

		filterList.addEasyListItemSelectedChangedListener(new EasyListItemSelectedChangedListener()
		{
			@Override
			public void selectionClear()
			{
				deleteBtn.setEnabled(false);
			}

			@Override
			public void selected(int[] selectedIndices)
			{
				deleteBtn.setEnabled(true);
			}
		});

		deleteBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				filterList.removeSelected();
			}
		});

		filterTb.addTextChangedListener(new TextChangedListener()
		{
			@Override
			public void textChanged(String newText)
			{
				addFilterBtn.setEnabled(!newText.isEmpty());
			}
		});

		addFilterBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String text = filterTb.getText();

				if (!filter.contains(text))
				{
					filterList.addElement(text);
				}

				filterTb.clear();
				filterTb.requestFocus();
			}
		});
	}

	public void setFilter(Set<String> filter)
	{
		if (null == filter)
		{
			filter = new HashSet<>();
		}

		filterList.clear();

		for (String currFilter : filter)
		{
			filterList.addElement(currFilter);
		}

		this.filter = filter;
	}

	public Set<String> getFilter()
	{
		return filter;
	}

	public void lockGUI(boolean lock)
	{
		filterList.setEnabled(!lock);
	}
}
