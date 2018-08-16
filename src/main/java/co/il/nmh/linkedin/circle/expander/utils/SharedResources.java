package co.il.nmh.linkedin.circle.expander.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.il.nmh.linkedin.circle.expander.properties.LinkedingCircleExpanderProperties;

/**
 * @author Maor Hamami
 */

public class SharedResources
{
	public static final SharedResources INSTANCE = new SharedResources();

	protected LinkedingCircleExpanderProperties linkedingCircleExpanderProperties;

	private SharedResources()
	{
		ObjectMapper objectMapper = new ObjectMapper();

		try
		{
			linkedingCircleExpanderProperties = objectMapper.readValue(ClassLoader.getSystemResourceAsStream("properties.json"), LinkedingCircleExpanderProperties.class);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public LinkedingCircleExpanderProperties getLinkedingCircleExpanderProperties()
	{
		return linkedingCircleExpanderProperties;
	}
}
