package co.il.nmh.linkedin.circle.expander.core.listeners;

import co.il.nmh.linkedin.circle.expander.data.enums.LogTypeEnum;

/**
 * @author Maor Hamami
 */

public interface FriendsGrabberListener
{
	void stopped();

	void log(String message, LogTypeEnum logType);
}
