package fr.sparna.rdf.shacl.excel.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.RDFS;

import fr.sparna.rdf.shacl.excel.ModelReadingUtils;

public class NodeShape {
	
	private Resource nodeShape;
	
	public NodeShape (Resource nodeShape) {  
	    this.nodeShape = nodeShape;		
	}
	
	public Double getSHOrder() {
		return Optional.ofNullable(this.nodeShape.getProperty(SHACLM.order)).map(s -> s.getDouble()).orElse(null);
	}
	
	public Resource getSHTargetClass() {
		return Optional.ofNullable(nodeShape.getProperty(SHACLM.targetClass)).map(s -> s.getResource()).orElse(null);
	}

	public Resource getSHTargetObjectOf() {
		return Optional.ofNullable(nodeShape.getProperty(SHACLM.targetObjectsOf)).map(s -> s.getResource()).orElse(null);
	}

	public Resource getSHTargetSubjectsOf() {
		return Optional.ofNullable(nodeShape.getProperty(SHACLM.targetSubjectsOf)).map(s -> s.getResource()).orElse(null);
	}
	
	public List<PropertyShape> getPropertyShapes() {
		List<PropertyShape> propertyShapes = nodeShape.listProperties(SHACLM.property).toList().stream()
				.map(s -> s.getResource())
				.map(r -> new PropertyShape(r))
				.collect(Collectors.toList());
		
		return propertyShapes;
	}

	public Resource getNodeShape() {
		return nodeShape;
	}
	
	public String getRdfsLabel(String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(nodeShape, RDFS.label, lang);
	}
}
