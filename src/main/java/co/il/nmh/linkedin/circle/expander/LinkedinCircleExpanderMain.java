package co.il.nmh.linkedin.circle.expander;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import co.il.nmh.linkedin.circle.expander.core.FriendsGrabber;
import co.il.nmh.linkedin.circle.expander.core.listeners.FriendsGrabberListener;
import co.il.nmh.linkedin.circle.expander.data.enums.LogTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Maor Hamami
 */
@Slf4j
public class LinkedinCircleExpanderMain
{
	public static void main(String[] args) throws InterruptedException
	{
		// TODO - replace this thing with a gui
		if (args.length < 2)
		{
			log.error("must provide username and password in args");
			return;
		}

		String username = args[0];
		String password = args[1];

		Set<String> filter = new HashSet<>();

		for (int i = 2; i < args.length; i++)
		{
			filter.add(args[i]);
		}

		FriendsGrabber friendsGrabber = new FriendsGrabber(username, password, filter);
		friendsGrabber.addListener(new FriendsGrabberListener()
		{
			@Override
			public void stopped()
			{
			}

			@Override
			public void log(String message, LogTypeEnum logType)
			{
				switch (logType)
				{
					case INFO:
						log.info(message);
						break;
					case ERROR:
						log.error(message);
						break;
				}
			}
		});

		friendsGrabber.start();

		Scanner scanner = new Scanner(System.in);
		System.out.print("press any key to stop");
		scanner.next();

		friendsGrabber.interrupt();

		scanner.close();
	}
}
