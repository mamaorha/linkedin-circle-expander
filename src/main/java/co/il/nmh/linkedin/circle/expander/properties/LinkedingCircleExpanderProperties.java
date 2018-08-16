package co.il.nmh.linkedin.circle.expander.properties;

import lombok.Data;

/**
 * @author Maor Hamami
 */
@Data
public class LinkedingCircleExpanderProperties
{
	private String myNetworkPage;
	private String verifyUrl;
	private LoginProperties login;
	private FriendProperties friend;
}
