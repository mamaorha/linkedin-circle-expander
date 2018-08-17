package co.il.nmh.linkedin.circle.expander.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;

import co.il.nmh.linkedin.circle.expander.gui.listeners.ActionPanelListener;

/**
 * @author Maor Hamami
 */

public class ActionPanel extends JPanel
{
	private static final long serialVersionUID = -3670118421931416635L;

	protected JButton startBtn;

	protected Set<ActionPanelListener> actionPanelListeners;
	protected String username;
	protected String password;

	public ActionPanel()
	{
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(5, 0, 5, 0));

		actionPanelListeners = new HashSet<>();

		buildPanel();
		addEvents();
	}

	private void buildPanel()
	{
		startBtn = new JButton("Start");
		startBtn.setEnabled(false);

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx++;
		gridBagConstraints.gridy++;

		add(startBtn, gridBagConstraints);
	}

	private void addEvents()
	{
		startBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if ("Start".equals(startBtn.getText()))
				{
					for (ActionPanelListener actionPanelListener : actionPanelListeners)
					{
						actionPanelListener.start();
					}

					startBtn.setText("Stop");
				}

				else if ("Stop".equals(startBtn.getText()))
				{
					stop();
				}
			}
		});
	}

	public void stop()
	{
		for (ActionPanelListener actionPanelListener : actionPanelListeners)
		{
			actionPanelListener.stop();
		}

		startBtn.setText("Start");
	}

	public void addActionPanelListener(ActionPanelListener actionPanelListener)
	{
		actionPanelListeners.add(actionPanelListener);
	}

	public void setUsername(String username)
	{
		this.username = username;
		validateInput();
	}

	public void setPassword(String password)
	{
		this.password = password;
		validateInput();
	}

	private void validateInput()
	{
		startBtn.setEnabled(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password));
	}
}
