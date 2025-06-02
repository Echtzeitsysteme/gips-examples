package gips.examples.dependencies;

import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class GipsExamplesLogger {

	/**
	 * Logger for system outputs.
	 */
	protected final static Logger logger = Logger.getLogger(GipsExamplesLogger.class.getName());

	static {
		configureLogging(logger);
	}

	public static void configureLogging(final Logger logger) {
		Objects.requireNonNull(logger);

		// Configure logging
		logger.setUseParentHandlers(false);
		final ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new Formatter() {
			@Override
			public String format(final LogRecord record) {
				Objects.requireNonNull(record, "Given log entry was null.");
				return record.getMessage() + System.lineSeparator();
			}
		});
		if (logger.getHandlers().length == 0) {
			logger.addHandler(handler);
		}
	}

}
