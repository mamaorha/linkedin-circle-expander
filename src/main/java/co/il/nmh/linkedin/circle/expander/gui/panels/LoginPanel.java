package co.il.nmh.linkedin.circle.expander.gui.panels;

import co.il.nmh.easy.swing.components.text.EasyPasswordField;
import co.il.nmh.easy.swing.components.text.EasyTextField;
import co.il.nmh.easy.swing.listeners.DocumentChangedListener;
import co.il.nmh.linkedin.circle.expander.gui.listeners.LoginPanelListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Maor Hamami
 */

public class LoginPanel extends JPanel
{
	private static final long serialVersionUID = 4065822925077599704L;

	protected EasyTextField usernameTb;
	protected EasyPasswordField passwordTb;

	protected Set<LoginPanelListener> loginPanelListeners;

	public LoginPanel()
	{
		setLayout(new GridBagLayout());

		loginPanelListeners = new HashSet<>();

		buildPanel();
		addEvents();
	}

	private void buildPanel()
	{
		GridBagConstraints gridBagConstraints = new GridBagConstraints();

		JLabel usernameLbl = new JLabel("username: ");
		usernameTb = new EasyTextField(25);

		JLabel passwordLbl = new JLabel("password: ");
		passwordTb = new EasyPasswordField(25);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy++;
		add(usernameLbl, gridBagConstraints);

		gridBagConstraints.gridx++;
		add(usernameTb, gridBagConstraints);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy++;
		add(passwordLbl, gridBagConstraints);

		gridBagConstraints.gridx++;
		add(passwordTb, gridBagConstraints);
	}

	private void addEvents()
	{
		usernameTb.getDocument().addDocumentListener(new DocumentChangedListener()
		{
			@Override
			public void textChanged(DocumentEvent paramDocumentEvent)
			{
				for (LoginPanelListener loginPanelListener : loginPanelListeners)
				{
					loginPanelListener.usernameUpdate(getUsername());
				}
			}
		});

		passwordTb.getDocument().addDocumentListener(new DocumentChangedListener()
		{
			@Override
			public void textChanged(DocumentEvent paramDocumentEvent)
			{
				for (LoginPanelListener loginPanelListener : loginPanelListeners)
				{
					loginPanelListener.passwordUpdate(getPassword());
				}
			}
		});
	}

	public void addLoginPanelListener(LoginPanelListener loginPanelListener)
	{
		loginPanelListeners.add(loginPanelListener);

		loginPanelListener.usernameUpdate(getUsername());
		loginPanelListener.passwordUpdate(getPassword());
	}

	public void setUsername(String username)
	{
		usernameTb.setText(username);
	}

	public String getUsername()
	{
		return usernameTb.getText();
	}

	public String getPassword()
	{
		return passwordTb.getText();
	}

	public void lockGUI(boolean lock)
	{
		usernameTb.setEnabled(!lock);
		passwordTb.setEnabled(!lock);
	}
}
