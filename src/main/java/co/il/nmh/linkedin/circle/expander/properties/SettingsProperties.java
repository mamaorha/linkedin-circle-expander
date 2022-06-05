package co.il.nmh.linkedin.circle.expander.properties;

import co.il.nmh.linkedin.circle.expander.data.Filter;
import lombok.Data;

import java.util.Set;

/**
 * @author Maor Hamami
 */
@Data
public class SettingsProperties
{
	private String username;
	private Set<Filter> filter;
}
