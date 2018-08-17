package co.il.nmh.linkedin.circle.expander.utils;

import java.io.File;
import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.il.nmh.linkedin.circle.expander.properties.LinkedingCircleExpanderProperties;
import co.il.nmh.linkedin.circle.expander.properties.SettingsProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Maor Hamami
 */

@Slf4j
public class SharedResources
{
	public static final SharedResources INSTANCE = new SharedResources();

	protected ObjectMapper objectMapper;
	protected LinkedingCircleExpanderProperties linkedingCircleExpanderProperties;
	protected SettingsProperties settingsProperties;

	private SharedResources()
	{
		objectMapper = new ObjectMapper();

		try
		{
			linkedingCircleExpanderProperties = objectMapper.readValue(ClassLoader.getSystemResourceAsStream("properties.json"), LinkedingCircleExpanderProperties.class);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		File settings = new File("settings.json");

		if (settings.exists() && settings.isFile())
		{
			try
			{
				settingsProperties = objectMapper.readValue(settings, SettingsProperties.class);
			}
			catch (Exception e)
			{
				log.warn("failed to read settings.json");
			}
		}
	}

	public LinkedingCircleExpanderProperties getLinkedingCircleExpanderProperties()
	{
		return linkedingCircleExpanderProperties;
	}

	public SettingsProperties getSettingsProperties()
	{
		if (null == settingsProperties)
		{
			settingsProperties = new SettingsProperties();
		}

		return settingsProperties;
	}

	public void saveSettings()
	{
		try
		{
			PrintWriter writer = new PrintWriter("settings.json", "UTF-8");
			writer.print(objectMapper.writeValueAsString(getSettingsProperties()));
			writer.close();
		}
		catch (Exception e)
		{
			log.error("failed to save settings");
		}
	}
}
