# rdf2xls : seriaze RDF graphs into tabular Excel files


## What is this ?

This utility serializes an RDF graph file into different tabs of an Excel spreadsheet, based on a SHACL specification. The Excel file can be converted back to RDF using the [xls2rdf](https://xls2rdf.sparna.fr) utility.

This tool aims at supporting the following workflow:
	1. RDF data
	2. Transform to Excel using rdf2xls
	3. Human review or correction of the Excel file
	4. Serialize back to RDF using rdf2xls

In particular, it is used at [Sparna](https://sparna.fr) for the following task:
	1. Analyze an RDF dataset using the [SHACL generation algorithm of SHACL Play](https://shacl-play.sparna.fr/play/generate#documentation), to produce a SHACL analysis of the dataset
	2. Transform the SHACL profile into an Excel spreadsheet
	3. Review, adjust as necessary, maybe to produce a [Sparnatural configuration file](https://docs.sparnatural.eu/SHACL-based-configuration.html)
	4. Transform back into RDF


## Example

For example, given this input RDF file:

```turtle
@prefix skos: <http://www.w3.org/2004/02/skos/core#> .
@prefix ex: <https://data.sparna.fr/example/rdf2xls-example#> .

ex:concept_1 a skos:Concept ;
	skos:prefLabel "first concept"@en;
	skos:prefLabel "premier concept"@fr;
	skos:altLabel "a synonym"@en, "another synonym"@en;
	skos:scopeNote "une note d'utilisation"@fr;
.

ex:concept_1_1 a skos:Concept ;
	skos:prefLabel "child concept"@en;
	skos:prefLabel "concept enfant"@fr;
	skos:altLabel "specific concept"@en;
	skos:scopeNote "blah blah"@fr;
	skos:broader ex:concept_1;
.
```


And this input SHACL template:

```turtle
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix sh: <http://www.w3.org/ns/shacl#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix dc: <http://purl.org/dc/elements/1.1/> .
@prefix skos: <http://www.w3.org/2004/02/skos/core#> .
@prefix skosxl: <http://www.w3.org/2008/05/skos-xl#> .
@prefix sset: <https://data.sparna.fr/ontologies/skos-simple-excel-template#> .

<https://data.sparna.fr/ontologies/skos-simple-excel-template#> a owl:Ontology;
  rdfs:label "SKOS simple Excel template"@en;
.

sset:Concept a sh:NodeShape;
  sh:targetClass skos:Concept;
  rdfs:label "SKOS Concept"@en;
  sh:order "1";
  sh:property 
    sset:ConceptShape_P1,
    sset:ConceptShape_P2,
    sset:ConceptShape_P3,
    sset:ConceptShape_P4,
    sset:ConceptShape_P5,
    sset:ConceptShape_P6
.

sset:ConceptShape_P1 sh:path skos:prefLabel;
  sh:name "label in english"@en;
  sh:description "The label in english"@en;
  sh:languageIn ("en");
  sh:order "1" .

sset:ConceptShape_P2 sh:path skos:prefLabel;
  sh:name "label in french"@en;
  sh:description "The label in French"@en;
  sh:languageIn ("fr");
  sh:order "2" .

sset:ConceptShape_P3 sh:path skos:altLabel;
  sh:name "synonyms @en"@en;
  sh:description "Synonyms in English"@en;
  sh:languageIn ("en");
  sh:order "3" .

sset:ConceptShape_P4 sh:path skos:altLabel;
  sh:name "synonyms @fr"@en;
  sh:description "Synonyms in French"@en;
  sh:languageIn ("fr");
  sh:order "4" .

sset:ConceptShape_P5 sh:path skos:scopeNote;
  sh:name "scope note"@en;
  sh:description "Scope Note"@en;
  sh:order "5" .

sset:ConceptShape_P6 sh:path skos:broader;
  sh:name "broader concept"@en;
  sh:description "The broader concept"@en;
  sh:order "6" .
```

Then the following Excel file is produced:

![]()

Note how the structure of the Excel file, in particular the header line, matches with the [xls2rdf converter rules](https://xls2rdf.sparna.fr/rest/doc.html).


## Command synopsis

This utility requires Java.

To get started, download the latest version from the [releases section](https://github.com/sparna-git/rdf2xls/releases).

The simple synopsis of the command is:

```sh
java -jar rdf2xls-app-1.0.0-onejar.jar excel --input {input_rdf_file} --template {shacl_template_file} --output {output_file.xls}
```

For example:

```sh
java -jar rdf2xls-app-1.0.0-onejar.jar excel --input examples/example-skos.ttl --template examples/example-skos-template.ttl --output examples/example-skos.xls
```

The full help message is the following:

```
Generates an Excel table from a dataset and the description of the table structure in Excel, provided in SHACL.
Usage: excel [options]
  Options:
  * -i, --input
       Input data file that will populate the table. This can be repeated for
       multiple input files, and can point to a directory.
    -l, --language
       Code of the language to use to read titles and descriptions from the
       template.This is mandatory unless the template uses a single language, in which case an
       attempt will be made to guess that unique language
  * -o, --output
       Path to the Excel output file that will be generated
  * -t, --template
       Template SHACL file that defines the table structure.
```

## Supported SHACL constraints

### Node Shapes correspond to tabs

A first tab "Prefixes" is always generated, containing the prefixes declarations.
Each NodeShape declares an additionnal tab in the output Excel. For example, given the following template:

```turtle
ex:Shape_1 a sh:NodeShape;
  sh:targetClass ex:Class1;
  rdfs:label "Class 1"@en;
.

ex:Shape_2 a sh:NodeShape;
  sh:targetClass ex:Class2;
  rdfs:label "Class 2"@en;
```

Then 2 tabs will be generated, "Class 1" and "Class 2".

### Shapes target

The converter supports `sh:targetClass`, `sh:targetSubjectsOf` and `sh:targetObjectsOf` to determine the list of entities that will be listed in each tab.


### Property Shapes correspond to columns

The first column of each tab always is always `URI` and contains the URI. Then each property shape of the NodeShape corresponds to one column. Columns are ordered based on `sh:order`. On each property shape, 3 lines are populated : `sh:name` gives the column human-readable title, `sh:description` gives a human-readable help message, and the technical header file for the xls2rdf converter is converted according to the constraints of the property shapes (see next section).

Given this input SHACL file:

```turtle
ex:Shape_1 a sh:NodeShape;
  sh:targetClass ex:Class1;
  rdfs:label "Class 1"@en;
  sh:property ex:P1, ex:P2;
.

ex:P1 sh:path foaf:firstName ;
	sh:order 1 ;
	sh:name "first name"@en;
	sh:description "A given name"@en ;
.

ex:P2 sh:path foaf:lastName ;
	sh:order 2 ;
	sh:name "last name"@en;
	sh:description "Like, a family name"@en
.
```

Then the following column headers will be generated:

|A given name|Like, a family name|
|first name  | last name         |
|foaf:firstName|foaf:lastName|

### Supported property shapes constraints

The following property shapes constraints are supported:
- `sh:order` controls column order
- `sh:datatype` turns into a [`xxx^^xsd:integer` notation](https://xls2rdf.sparna.fr/rest/doc.html#generating-values-with-datatypes) on the header
- `sh:languageIn` turns into a [`xxxx@en` notation](https://xls2rdf.sparna.fr/rest/doc.html#generating-multilingual-values) on the header. Multiple values of `sh:languageIn` on the same property shape will produce multiple columns of the same predicate, each with a different language
- If the `sh:path` of the property shape is an `sh:inversePath`, this will turn into a [`^xxx` notation](https://xls2rdf.sparna.fr/rest/doc.html#generating-skos-collection-with-object-to-subject-columns) on the header

Lines are sorted according to the first property column (excluding the URI column)