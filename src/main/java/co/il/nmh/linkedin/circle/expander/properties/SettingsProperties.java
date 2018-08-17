package co.il.nmh.linkedin.circle.expander.properties;

import java.util.Set;

import lombok.Data;

/**
 * @author Maor Hamami
 */
@Data
public class SettingsProperties
{
	private String username;
	private Set<String> filter;
}
