package org.gips.examples.incrementalp2p.run;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogToData {

	public static void main(final String[] args) {
		if (args == null || args.length == 0) {
			throw new IllegalArgumentException("Path argument is missing.");
		}

		// Find all log files
		final List<String> files = new LinkedList<>();
		try {
			files.addAll(findFiles(Paths.get(args[0]), "log"));
		} catch (final IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}

		// Find all evaluation data lines
		final List<String> dataLines = new LinkedList<>();
		dataLines.addAll(filesToDataLines(files));

		// Calculate mean values
		dataLinesToMeanValues(dataLines);
	}

	@SuppressWarnings("unchecked")
	private static void dataLinesToMeanValues(final List<String> dataLines) {
		final Map<Integer, List<DataRecord>> data = new HashMap<>();

		// Propagate data to sub lists
		for (final String l : dataLines) {
			final String clients = l.substring(0, l.indexOf(";"));
			if (!data.containsKey(Integer.valueOf(clients))) {
				data.put(Integer.valueOf(clients), new LinkedList<DataRecord>());
			}
			data.get(Integer.valueOf(clients)).add(line2DataRecord(l));
		}

		// Calculations
		final List<DataRecord> means = new LinkedList<DataRecord>();
		for (final List<DataRecord> drs : data.values()) {
			final int clients = drs.get(0).clients();
			double gtSum = 0;
			double ilpSum = 0;
			double totalSum = 0;
			for (final DataRecord r : drs) {
				gtSum += r.gt;
				ilpSum += r.ilp;
				totalSum += r.misc;
			}

			double meanGt = gtSum / drs.size();
			double meanIlp = ilpSum / drs.size();
			double meanMisc = (totalSum - gtSum - ilpSum) / drs.size();

			means.add(new DataRecord(clients, meanGt, meanIlp, meanMisc));
		}

		// Sorting and printing
		Collections.sort(means);
		for (final DataRecord e : means) {
			final StringBuilder sb = new StringBuilder();
			sb.append(e.clients);
			sb.append(";");
			sb.append(e.gt);
			sb.append(";");
			sb.append(e.ilp);
			sb.append(";");
			sb.append(e.misc);
			System.out.println(sb.toString());
		}

	}

	private static Collection<? extends String> filesToDataLines(final List<String> files) {
		final Collection<String> lastLines = new LinkedList<String>();

		for (final String f : files) {
			BufferedReader fileContent;
			try {
				fileContent = new BufferedReader(new FileReader(f));
				String last = "";
				String line;
				while ((line = fileContent.readLine()) != null) {
					last = line;
				}

				lastLines.add(last);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		return lastLines;
	}

	public static List<String> findFiles(final Path path, final String fileExtension) throws IOException {
		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory!");
		}

		List<String> result;
		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(p -> !Files.isDirectory(p)).map(p -> p.toString().toLowerCase())
					.filter(f -> f.endsWith(fileExtension)).collect(Collectors.toList());
		}

		return result;
	}

	private static DataRecord line2DataRecord(final String line) {
		final String[] values = line.split(";");
		return new DataRecord(Integer.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]),
				Double.valueOf(values[3]));
	}

	@SuppressWarnings("rawtypes")
	private record DataRecord(int clients, double gt, double ilp, double misc) implements Comparable {

		@Override
		public int compareTo(final Object o) {
			if (o == null || !(o instanceof DataRecord)) {
				throw new UnsupportedOperationException();
			}

			return this.clients - ((DataRecord) o).clients;
		}
	}

}
