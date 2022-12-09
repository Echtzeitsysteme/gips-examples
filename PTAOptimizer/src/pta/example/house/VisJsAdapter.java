package pta.example.house;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.AbstractMap.SimpleEntry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.PersonTaskAssignmentsPackage;
import PersonTaskAssignments.Week;
import com.google.common.collect.HashBiMap;
import javafx.concurrent.Worker;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class VisJsAdapter extends Application{
	
	protected PersonTaskAssignmentModel model;
	protected WebEngine engine;
	protected JSObject window;
	
	protected HashBiMap<Object, String> objs = HashBiMap.create();
	protected HashBiMap<String, Entry<String, String>> edges = HashBiMap.create();
	protected HashBiMap<Object, HashBiMap<String, Entry<String, String>>> edges2Obj = HashBiMap.create();
	
	public VisJsAdapter() {
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		String file = instancesFolder + "/ConstructionProject1_solved.xmi";
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
		.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(PersonTaskAssignmentsPackage.eNS_URI, PersonTaskAssignmentsPackage.eINSTANCE);
		URI fileURI = URI.createFileURI(file);
		Resource r = rs.getResource(fileURI, true);
		model = (PersonTaskAssignmentModel) r.getContents().get(0);
	}

	@Override
	public void start(Stage stage) throws Exception {
        WebView webview = new WebView();
        Scene scene = new Scene(webview, 2500, 900);
        stage.setScene(scene);
        engine = webview.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.loadContent(VisJsScriptTemplates.getTemplate());
        webview.setVisible(true);
        stage.show();
        
        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == Worker.State.SUCCEEDED) {
            	window = (JSObject)engine.executeScript("window");
            	window.setMember("jfx", this);
            	initModel();
            	
            	engine.executeScript("var network = new vis.Network(container, data, options);");
            	engine.executeScript(VisJsScriptTemplates.getFixLayout());
//            	engine.executeScript(VisJsScriptTemplates.createCallbackOnStabilized());
//            	engine.executeScript(VisJsScriptTemplates.createMoveOnDragStart());
//            	engine.executeScript(VisJsScriptTemplates.createMoveOnDragEnd());
            	engine.executeScript(VisJsScriptTemplates.createDeletionOnClick());
            } 
        });
        
	}
	
	protected void initModel() {
		Map<String, String> nodeAdditions = new LinkedHashMap<>();
		Map<String, Entry<String, String>> edgeAdditions = new LinkedHashMap<>();
		
		for(Week week : model.getWeeks().stream()
				.filter(w -> w.getOffers().stream()
						.filter(o -> !(o.getRequirements() == null || o.getRequirements().isEmpty())).findAny().isPresent())
				.collect(Collectors.toList())) {
			primeObjectForVisJs(nodeAdditions, week, "KW:"+week.getNumber());
			
			if(week.getPrevious() != null) {
				primeEdgeForVisJs(edgeAdditions, week.getPrevious(), week);
			}
		}
		
		model.getPersons().stream().filter(p -> p.getOffers().stream().filter(o -> !(o.getRequirements() == null || o.getRequirements().isEmpty())).findAny().isPresent()).forEach(p -> {
			String id = primeObjectForVisJs(nodeAdditions, p, "P("+p.getName()+")");
			p.getOffers().stream().filter(o -> !(o.getRequirements() == null || o.getRequirements().isEmpty())).forEach(o -> {
				String id2 = primeObjectForVisJs(nodeAdditions, o, "O("+o.getHours()+")");
				primeEdgeForVisJs(edgeAdditions, o, o.getWeek());
				primeEdgeForVisJs(edgeAdditions, o.getWeek(), p);
			});
		});
		
		model.getProjects().stream()
		.filter(p -> p.getTasks().stream()
				.filter(t -> t.getRequirements().stream()
						.filter(r -> !(r.getOffers() == null || r.getOffers().isEmpty())).findAny().isPresent()).findAny().isPresent()).forEach(p -> {
							String id = primeObjectForVisJs(nodeAdditions, p, "PR("+p.getName()+")");
							p.getTasks().forEach(t -> {
								String id2 = primeObjectForVisJs(nodeAdditions, t, "T("+t.getName()+")");
								t.getWeeks().forEach(w -> primeEdgeForVisJs(edgeAdditions, p, t));
								t.getRequirements().forEach(r -> {
									String id3 = primeObjectForVisJs(nodeAdditions, r, "R("+r.getName()+")");
									primeEdgeForVisJs(edgeAdditions, t, r);
									r.getOffers().forEach(o -> {
										primeEdgeForVisJs(edgeAdditions, r, o);
									});
								});
							});
						});
		
		engine.executeScript(VisJsScriptTemplates.addNodes(nodeAdditions));
		engine.executeScript(VisJsScriptTemplates.addEdges(edgeAdditions));
	}
	
	protected String primeObjectForVisJs(Map<String, String> additions, Object obj, String label) {
		String id = String.valueOf(objs.size());
		objs.put(obj, id);
		additions.put(id, label);
		return id;
	}
	
	protected void primeEdgeForVisJs(Map<String, Entry<String, String>> additions, Object src, Object trg) {
		if(!objs.containsKey(src) || !objs.containsKey(trg)) 
			return;
		
		Entry<String, String> pair = new SimpleEntry<String, String>(objs.get(src), objs.get(trg));
		String id = String.valueOf(edges.size());
		
		if(!edges.inverse().containsKey(pair))
			edges.putIfAbsent(id, pair);
		else
			return;
		
		HashBiMap<String, Entry<String, String>> otherEdges = edges2Obj.get(src);
		if(otherEdges == null) {
			otherEdges = HashBiMap.create();
			edges2Obj.put(src, otherEdges);
		}
		if(!otherEdges.inverse().containsKey(pair))
			otherEdges.putIfAbsent(id, pair);
		
		otherEdges = edges2Obj.get(trg);
		if(otherEdges == null) {
			otherEdges = HashBiMap.create();
			edges2Obj.put(trg, otherEdges);
		}
		if(!otherEdges.inverse().containsKey(pair))
			otherEdges.putIfAbsent(id, pair);
		
		additions.put(id, pair);
	}
	
	public void deleteNode(String id) {
		Object obj = objs.inverse().get(id);	
		HashBiMap<String, Entry<String, String>> otherEdges = edges2Obj.get(obj);
		if(otherEdges != null) {
			otherEdges.keySet().forEach(eid -> engine.executeScript(VisJsScriptTemplates.removeEdge(eid)));
		}
		
		engine.executeScript(VisJsScriptTemplates.removeNode(id));
		objs.remove(obj);
	}
	
	public void deactivateLayouting() {
		objs.values().forEach(id -> {
			engine.executeScript(VisJsScriptTemplates.configureNode(id, List.of("allowedToMoveX: false", "allowedToMoveY: false")));
		});
	}

}
