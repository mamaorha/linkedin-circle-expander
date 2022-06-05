package co.il.nmh.linkedin.circle.expander.properties;

import lombok.Data;

/**
 * @author Maor Hamami
 */
@Data
public class LinkedinCircleExpanderProperties
{
	private String myNetworkPage;
	private String verifyUrl;
	private LoginProperties login;
	private FriendProperties friend;
}
