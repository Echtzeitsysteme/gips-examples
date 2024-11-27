package org.gips.examples.incrementalp2p.visualization.implementation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.gips.examples.incrementalp2p.common.CommonConstants;
import org.gips.examples.incrementalp2p.common.Guard;
import org.gips.examples.incrementalp2p.visualization.contracts.GraphVisualizer;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConfiguration;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConnection;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationDataProvider;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationNode;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationUpdatesDataProvider;
import org.gips.examples.incrementalp2p.visualization.implementation.mappers.UIColor;
import org.gips.examples.incrementalp2p.visualization.implementation.mappers.VisJsMapper;

import com.google.inject.Inject;

//https://visjs.github.io/vis-network/examples/
//https://visjs.github.io/vis-network/docs/network/
public class VisJsGraphVisualizer implements GraphVisualizer {
	private final static Logger logger = Logger.getLogger(VisJsGraphVisualizer.class);

	private static final String SAVE_FILE_NAME = "fancyFileName";
	private static final Charset charset = StandardCharsets.UTF_8;

	private String dataFile;
	private String htmlFile;
	private String functionsFile;
	private String legendFile;
	private String networkFile;

	@Inject
	VisualizationDataProvider provider;
	@Inject
	VisualizationUpdatesDataProvider updatesNodesProvider;
	@Inject
	VisJsMapper visJsMapper;

	@Inject
	VisJsGraphVisualizer(final VisualizationConfiguration config) {
		this.htmlFile = "GraphLayout.html";
		this.functionsFile = "functions.js";
		this.dataFile = "data.js";
		this.legendFile = "legend.js";
		this.networkFile = String.format("network_%s.js", config.graph());
	}

	@Override
	public void createGraph(final String path, final String id) {
		Guard.againstMissingDirectory(path);

		try {
			createHtml(path, id);
			createData(path, id);
			createFunctions(path);
			createNetwork(path);
			createLegend(path);
			logger.info("Written graph to " + path);

		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error while creating graph", e);
		}
	}

	private void createHtml(final String folder, final String id) throws Exception {
		var content = readFile(htmlFile);

		content = content.replaceAll("data.js", targetDataFileName(id));
		content = content.replaceAll("network.js", networkFile);

		content = content.replace(SAVE_FILE_NAME, id);

		writeFile(folder, targetHtmlFileName(id), content);
	}

	private void createData(final String folder, final String id) throws Exception {
		var content = readFile(dataFile);

		content = content.replaceAll("replaceRootName", toJsString(CommonConstants.RootName));

		content = content.replaceAll("replaceRootColor", toJsString(UIColor.RootNode));
		content = content.replaceAll("replaceRelayColor", toJsString(UIColor.RelayClientNode));
		content = content.replaceAll("replaceClientColor", toJsString(UIColor.ClientNode));
		content = content.replaceAll("replaceEdgeColor", toJsString(UIColor.Edge));

		content = content.replaceAll("replaceNodes", getNodes());
		content = content.replaceAll("replaceEdges", getEdges());

		content = content.replaceAll("replaceRemovedNodes", getRemovedNodes());
		content = content.replaceAll("replaceUpdatedEdges", getUpdatedEdges());

		content = content.replaceAll("replaceAdditionalNodes", getAdditionalNodes());
		content = content.replaceAll("replaceAdditionalEdges", getAdditionalEdges());

		writeFile(folder, targetDataFileName(id), content);
	}

	private void createLegend(final String folder) throws Exception {
		copyFile(folder, legendFile);
	}

	private void createNetwork(final String folder) throws Exception {
		copyFile(folder, networkFile);
	}

	private void createFunctions(final String folder) throws Exception {
		copyFile(folder, functionsFile);
	}

	private void copyFile(final String folder, final String file) throws Exception {
		var content = readFile(file);

		writeFile(folder, file, content);
	}

	private String readFile(final String file) throws URISyntaxException, IOException {
		var sourceUri = getClass().getResource(file).toURI();

		if (sourceUri.getPath() == null) {
			var location = "visualization" + File.separator + file;

			sourceUri = Paths.get(location).toUri();
		}

		var sourcePath = Paths.get(sourceUri);

		var content = new String(Files.readAllBytes(sourcePath), charset);
		return content;
	}

	private void writeFile(final String folder, final String file, final String content) throws IOException {
		var targetPath = Paths.get(folder, file);
		Files.write(targetPath, content.getBytes(charset));
	}

	private String getAdditionalEdges() {
		return updatesNodesProvider.getAdditionalEdges().stream().map(x -> createEdge(x)).reduce("",
				(x, y) -> x + "\r\n" + y);
	}

	private String getAdditionalNodes() {
		return updatesNodesProvider.getAdditionalNodes().stream() //
				// UI show Additional nodes as leaves
				.map(x -> new VisualizationNode(x.id(), x.name(), false, x.value())) //
				.map(x -> createNode(x)).reduce("", (x, y) -> x + "\r\n" + y);
	}

	private String getUpdatedEdges() {
		return updatesNodesProvider.getUpdatedEdges().stream().map(x -> createEdge(x)).reduce("",
				(x, y) -> x + "\r\n" + y);
	}

	private String getRemovedNodes() {
		return updatesNodesProvider.getRemovdNodes().stream().map(x -> createNode(x)).reduce("",
				(x, y) -> x + "\r\n" + y);
	}

	private String getNodes() {
		return provider.getNodes().stream().map(x -> createNode(x)).reduce("", (x, y) -> x + "\r\n" + y);
	}

	private String getEdges() {
		return provider.getConnections().stream().map(x -> createEdge(x)).reduce("", (x, y) -> x + "\r\n" + y);
	}

	private String createNode(final VisualizationNode node) {
		return visJsMapper.createNode(node);
	}

	private String createEdge(final VisualizationConnection connection) {
		return visJsMapper.createEdge(connection);
	}

	private String targetHtmlFileName(final String id) {
		return String.format("Graph_%s.html", id);
	}

	private String targetDataFileName(final String id) {
		return String.format("data_%s.js", id);
	}

	private static String toJsString(final String data) {
		return String.format("'%s'", data);
	}

	@Override
	public File getGraphFile(final String path, final String id) {
		return new File(path, targetHtmlFileName(id));
	}
	
}
