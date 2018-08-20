package co.il.nmh.linkedin.circle.expander.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Set;

import co.il.nmh.easy.swing.components.EasyConsolePanel;
import co.il.nmh.easy.swing.components.EasyPaddingPanel;
import co.il.nmh.easy.swing.components.gui.EasyFrame;
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
public class GUI extends EasyFrame implements LoginPanelListener, ActionPanelListener, FriendsGrabberListener
{
	private static final long serialVersionUID = -7225940459313107238L;

	protected FilterPanel filterPanel;
	protected LoginPanel loginPanel;
	protected ActionPanel actionPanel;
	protected EasyConsolePanel consolePanel;

	protected FriendsGrabber friendsGrabber;

	public GUI()
	{
		super("Linkedin Circle Expander", 1.7, 4);
	}

	@Override
	protected void buildPanel()
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

	@Override
	protected void addEvents()
	{
		loginPanel.addLoginPanelListener(this);
		actionPanel.addActionPanelListener(this);
	}

	@Override
	public void close()
	{
		stopFriendsGrabber();
		saveSettings();
		super.close();
	}

	@Override
	public void stopped()
	{
		friendsGrabber = null;
		actionPanel.stop();
		lockGUI(false);
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

	public void lockGUI(boolean lock)
	{
		filterPanel.lockGUI(lock);
		loginPanel.lockGUI(lock);
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

	private void stopFriendsGrabber()
	{
		if (null != friendsGrabber)
		{
			friendsGrabber.interrupt();

			try
			{
				friendsGrabber.join();
			}
			catch (InterruptedException e)
			{
			}

			friendsGrabber = null;
		}
	}
}
