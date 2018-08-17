package co.il.nmh.linkedin.circle.expander.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.JFrame;

import co.il.nmh.easy.swing.components.EasyConsolePanel;
import co.il.nmh.easy.swing.components.EasyPaddingPanel;
import co.il.nmh.linkedin.circle.expander.core.FriendsGrabber;
import co.il.nmh.linkedin.circle.expander.core.listeners.FriendsGrabberListener;
import co.il.nmh.linkedin.circle.expander.data.enums.LogTypeEnum;
import co.il.nmh.linkedin.circle.expander.gui.listeners.ActionPanelListener;
import co.il.nmh.linkedin.circle.expander.gui.listeners.LoginPanelListener;
import co.il.nmh.linkedin.circle.expander.gui.panels.ActionPanel;
import co.il.nmh.linkedin.circle.expander.gui.panels.FilterPanel;
import co.il.nmh.linkedin.circle.expander.gui.panels.LoginPanel;
import co.il.nmh.linkedin.circle.expander.properties.SettingsProperties;
import co.il.nmh.linkedin.circle.expander.utils.SharedResources;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Maor Hamami
 */
@Slf4j
public class GUI extends JFrame implements LoginPanelListener, ActionPanelListener, FriendsGrabberListener
{
	private static final long serialVersionUID = -7225940459313107238L;

	protected FilterPanel filterPanel;
	protected LoginPanel loginPanel;
	protected ActionPanel actionPanel;
	protected EasyConsolePanel consolePanel;

	protected FriendsGrabber friendsGrabber;

	public GUI()
	{
		setTitle("Linkedin Circle Expander");

		buildPanel();
		addEvents();

		pack();
		setVisible(true);

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int height = gd.getDisplayMode().getHeight();
		height -= height / 1.7;

		setMinimumSize(new Dimension(gd.getDisplayMode().getWidth() / 4, height));
		setLocationRelativeTo(null);
	}

	private void buildPanel()
	{
		setLayout(new GridBagLayout());

		SettingsProperties settingsProperties = SharedResources.INSTANCE.getSettingsProperties();

		filterPanel = new FilterPanel();
		filterPanel.setFilter(settingsProperties.getFilter());

		loginPanel = new LoginPanel();
		loginPanel.setUsername(settingsProperties.getUsername());

		actionPanel = new ActionPanel();
		consolePanel = new EasyConsolePanel();

		GridBagConstraints gridBagConstraints = new GridBagConstraints();

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 2;
		gridBagConstraints.weightx = 1;

		gridBagConstraints.gridy++;
		add(new EasyPaddingPanel(filterPanel, 0, 5, 5, 5), gridBagConstraints);

		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.gridy++;
		add(loginPanel, gridBagConstraints);

		gridBagConstraints.gridy++;
		add(actionPanel, gridBagConstraints);

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridy++;

		add(new EasyPaddingPanel(consolePanel, 0, 5, 5, 5), gridBagConstraints);
	}

	private void addEvents()
	{
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				stopFriendsGrabber();
				saveSettings();
				System.exit(0);
			}
		});

		loginPanel.addLoginPanelListener(this);
		actionPanel.addActionPanelListener(this);
	}

	public void lockGUI(boolean lock)
	{
		filterPanel.lockGUI(lock);
		loginPanel.lockGUI(lock);
	}

	@Override
	public void usernameUpdate(String username)
	{
		actionPanel.setUsername(username);
	}

	@Override
	public void passwordUpdate(String password)
	{
		actionPanel.setPassword(password);
	}

	@Override
	public void start()
	{
		stopFriendsGrabber();
		lockGUI(true);

		saveSettings();

		friendsGrabber = new FriendsGrabber(loginPanel.getUsername(), loginPanel.getPassword(), filterPanel.getFilter());
		friendsGrabber.addListener(this);
		friendsGrabber.start();
	}

	@Override
	public void stop()
	{
		stopFriendsGrabber();
		lockGUI(false);
	}

	private void stopFriendsGrabber()
	{
		if (null != friendsGrabber)
		{
			friendsGrabber.interrupt();
			friendsGrabber = null;
		}
	}

	@Override
	public void stopped()
	{
		friendsGrabber = null;
		actionPanel.stop();
	}

	@Override
	public void log(String message, LogTypeEnum logType)
	{
		switch (logType)
		{
			case INFO:
				log.info(message);
				consolePanel.writeToConsole(message);
				break;
			case ERROR:
				log.error(message);
				consolePanel.writeToConsole(message, Color.RED);
				break;
		}
	}

	private void saveSettings()
	{
		String username = loginPanel.getUsername();
		Set<String> filter = filterPanel.getFilter();

		SettingsProperties settingsProperties = SharedResources.INSTANCE.getSettingsProperties();
		settingsProperties.setUsername(username);
		settingsProperties.setFilter(filter);

		SharedResources.INSTANCE.saveSettings();
	}
}
