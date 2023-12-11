package fr.sparna.rdf.shacl.excel.model;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;

import fr.sparna.rdf.shacl.excel.ModelReadingUtils;

public class PropertyShape {

	protected Resource propertyShape;	
	
	public PropertyShape(Resource propertyShape) {
		super();
		this.propertyShape = propertyShape;
	}
	
		
	public Resource getPropertyShape() {
		return propertyShape;
	}
	
	public Double getOrder() {
		return Optional.ofNullable(this.propertyShape.getProperty(SHACLM.order)).map(s -> s.getDouble()).orElse(null);
	}

	public Resource getDatatype() {
		return Optional.ofNullable(this.propertyShape.getProperty(SHACLM.datatype)).map(s -> s.getResource()).orElse(null);
	}
	
	public Resource getPath() {
		return Optional.ofNullable(this.propertyShape.getProperty(SHACLM.path)).map(s -> s.getResource()).orElse(null);
	}

	public String getDescription(String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(this.propertyShape, SHACLM.description, lang);
	}

	public String getName(String lang) {
		return ModelReadingUtils.readLiteralInLangAsString(this.propertyShape, SHACLM.name, lang);
	}
	
	public Set<String> getNameAndDescriptionLanguages() {
		Set<String> langs = new HashSet<String>();
		
		List<RDFNode> names = ModelReadingUtils.readObjectAsNodes(propertyShape, SHACLM.name);
		if(names != null) {
			langs.addAll(names.stream()
					.filter(n -> n.isLiteral() && n.asLiteral().getLanguage() != null)
					.map(n -> n.asLiteral().getLanguage())
					.collect(Collectors.toSet())
			);
		}
		
		List<RDFNode> descriptions = ModelReadingUtils.readObjectAsNodes(propertyShape, SHACLM.description);
		if(descriptions != null) {
			langs.addAll(descriptions.stream()
					.filter(n -> n.isLiteral() && n.asLiteral().getLanguage() != null)
					.map(n -> n.asLiteral().getLanguage())
					.collect(Collectors.toSet())
			);
		}
		
		return langs;
	}

}
