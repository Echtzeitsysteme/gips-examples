package neural.network.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Json2CsvConverter {

	public static void main(final String[] args) {
		new Json2CsvConverter().run();
	}

	public void run() {
		// determine file path to load JSON file from
		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/../org.gips.nodevalue/";
		final String filePathJsonInput = instanceFolder + "match-export0.json";
		final String filePathCsvOutput = instanceFolder + "match-export0.csv";

		// load JSON file content
		String json = null;
		try {
			json = Files.readString(Path.of(filePathJsonInput));
		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// parse JSON to Java objects
		final Gson gson = new Gson();
		Type listType = new TypeToken<List<Match2Solution>>() {
		}.getType();
		final List<Match2Solution> nodeList = gson.fromJson(json, listType);

		for (final Match2Solution m : nodeList) {
			System.out.println("n1: " + m.properties.get(0).get("value"));
			System.out.println("n2: " + m.properties.get(1).get("value"));
			System.out.println("selected: " + (m.selected == 1));
			System.out.println("---");
		}

		// convert Java objects to CSV
		final StringWriter sw = new StringWriter();
		final CSVFormat csvFormat = CSVFormat.DEFAULT.builder() //
				.setHeader(NodeHeader.class) //
				.build();

		try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
			nodeList.forEach(n -> {
				try {
					printer.printRecord(n.properties.get(0).get("value"), n.properties.get(1).get("value"), n.selected);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			});
		} catch (final IOException e1) {
			e1.printStackTrace();
		}

		try {
			FileUtils.writeStringToFile(new File(filePathCsvOutput), sw.toString().trim(), Charset.forName("US-ASCII"));
		} catch (final IOException e) {
			e.printStackTrace();
		}

		System.out.println(sw.toString().trim());
	}

}
