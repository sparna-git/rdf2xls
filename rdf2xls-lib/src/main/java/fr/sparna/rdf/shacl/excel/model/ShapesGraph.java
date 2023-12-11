package fr.sparna.rdf.shacl.excel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.RDF;

public class ShapesGraph {

	protected Model model;
	
	public ShapesGraph(Model model) {
		super();
		this.model = model;
	}


	public List<NodeShape> getNodeShapes() {
		
		// read graph for the building the recovery all the head columns
		List<Resource> nodeShapes = model.listResourcesWithProperty(RDF.type, SHACLM.NodeShape).toList();

		// also read everything object of an sh:node or sh:qualifiedValueShape, that
		// maybe does not have an explicit rdf:type sh:NodeShape
		List<RDFNode> nodesAndQualifedValueShapesValues = model.listStatements(null, SHACLM.node, (RDFNode) null)
						.andThen(model.listStatements(null, SHACLM.qualifiedValueShape, (RDFNode) null)).toList().stream()
						.map(s -> s.getObject()).collect(Collectors.toList());

		// add those to our list
		for (RDFNode n : nodesAndQualifedValueShapesValues) {
			if (n.isResource() && !nodeShapes.contains(n)) {
				nodeShapes.add(n.asResource());
			}
		}
				
		// Header Class 
		List<NodeShape> nodeShapeTemplateList = new ArrayList<>();		
		for (Resource ns : nodeShapes) {
			NodeShape nodeShapeTemplate = new NodeShape(ns);			
			nodeShapeTemplateList.add(nodeShapeTemplate);
		}
		
		
		return nodeShapeTemplateList;
	}


}
