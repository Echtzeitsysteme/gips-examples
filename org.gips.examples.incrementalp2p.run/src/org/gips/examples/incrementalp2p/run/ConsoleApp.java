package org.gips.examples.incrementalp2p.run;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.gips.examples.incrementalp2p.common.models.WaitingClient;

public class ConsoleApp {
	private static int MaxClients = 15;
	private static int NewClients = 6;

	final static Logger logger = Logger.getLogger(ConsoleApp.class);

	public static void main(final String[] args) {
		Logger.getRootLogger().setLevel(Level.INFO);
		logger.info("Start");

		try {
			run(args);
		} catch (Exception e) {
			logger.error("Error: ", e);
			System.exit(1);
		}

		System.exit(0);

	}

	private static void run(final String[] args) {
		setArgs(args);
		var clients = createClients();
		var additionalClients = createAdditionalClients();
		new RunModule().run(clients, additionalClients, true);
	}

	private static void setArgs(final String[] args) {
		if (args.length >= 1) {
			MaxClients = Integer.parseInt(args[1]);
			logger.info("Set MaxClients to " + MaxClients);
		}
		if (args.length >= 2) {
			NewClients = Integer.parseInt(args[2]);
			logger.info("Set NewClients to " + NewClients);
		}
	}

	private static List<WaitingClient> createClients() {
		return createClients(MaxClients, "Client");
	}

	private static List<WaitingClient> createAdditionalClients() {
		return createClients(NewClients, "New Client");
	}

	private static List<WaitingClient> createClients(final int count, final String prefix) {
		return IntStream.rangeClosed(1, count).boxed().map(x -> new WaitingClient(prefix + x))
				.collect(Collectors.toList());
	}

}
