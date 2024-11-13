package architecture.cra.gipssolution.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvUtil {

	private CsvUtil() {
	}

	private static CSVFormat format = CSVFormat.DEFAULT.builder().setHeader( //
			"total_solve_time", //
			"total_run_time", //
			"objective_value" //
//			"violation_a_counter", //
//			"violation_c_counter", //
//			"violation_d1_counter", //
//			"violation_d2_counter", //
//			"cohesion", //
//			"coupling", //
//			"cra", //
//			"violations_max", //
//			"violations_lars" //
	).build();

	public static void writeCsvLine(final String csvPath, final String[] content) {
		// If file path is null, do not create a file at all
		if (csvPath == null) {
			return;
		}

		if (content == null || content.length == 0) {
			throw new IllegalArgumentException("Given content was null or empty.");
		}

		// Create parent folder if it does not exist
		final int lastSlash = csvPath.lastIndexOf('/');
		final String parentPath = csvPath.substring(0, lastSlash);
		final File parentFolder = new File(parentPath);
		if (!parentFolder.exists()) {
			parentFolder.mkdir();
		}

		// Write CSV line itself
		try {
			BufferedWriter out;
			// If file does not exist, write header to it
			if (Files.notExists(Path.of(csvPath))) {
				out = Files.newBufferedWriter(Paths.get(csvPath), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
				try (final CSVPrinter printer = new CSVPrinter(out, format)) {
					printer.close();
				}
			}

			out = Files.newBufferedWriter(Paths.get(csvPath), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			try (final CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
				printer.printRecord((Object[]) content);
				printer.close();
			}
			out.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
