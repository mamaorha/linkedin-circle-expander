package co.il.nmh.linkedin.circle.expander.utils;

import co.il.nmh.linkedin.circle.expander.properties.LinkedinCircleExpanderProperties;
import co.il.nmh.linkedin.circle.expander.properties.SettingsProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.PrintWriter;

/**
 * @author Maor Hamami
 */

@Slf4j
public class SharedResources
{
	public static final SharedResources INSTANCE = new SharedResources();

	protected ObjectMapper objectMapper;
	protected LinkedinCircleExpanderProperties linkedinCircleExpanderProperties;
	protected SettingsProperties settingsProperties;

	private SharedResources()
	{
		objectMapper = new ObjectMapper();

		File localProperties = new File("properties.json");

		if (localProperties.exists())
		{
			try
			{
				linkedinCircleExpanderProperties = objectMapper.readValue(localProperties, LinkedinCircleExpanderProperties.class);
			}

			catch (Exception e)
			{
			}
		}

		if (null == linkedinCircleExpanderProperties)
		{
			try
			{
				linkedinCircleExpanderProperties = objectMapper.readValue(ClassLoader.getSystemResourceAsStream("properties.json"), LinkedinCircleExpanderProperties.class);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
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

	public LinkedinCircleExpanderProperties getLinkedinCircleExpanderProperties()
	{
		return linkedinCircleExpanderProperties;
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
