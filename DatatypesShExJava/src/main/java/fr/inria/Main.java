package fr.inria;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import fr.inria.lille.shexjava.graph.RDF4JGraph;
import fr.inria.lille.shexjava.graph.RDFGraph;
import fr.inria.lille.shexjava.schema.Label;
import fr.inria.lille.shexjava.schema.ShexSchema;
import fr.inria.lille.shexjava.schema.parsing.GenParser;
import fr.inria.lille.shexjava.validation.RecursiveValidation;
import fr.inria.lille.shexjava.validation.RefineValidation;
import fr.inria.lille.shexjava.validation.ValidationAlgorithm;

public class Main {

	public static void main(String[] args) throws Exception {
		Path schemaFile = Paths.get("src","main","resources","datatypes.json"); //to change with what you want 
		Path dataFile = Paths.get("src","main","resources","datatypes-data.ttl"); //to change with what you want 
		List<Path> importDirectories = Collections.emptyList();
	
		// load and create the shex schema
		System.out.println("Reading schema");
		ShexSchema schema = GenParser.parseSchema(schemaFile,importDirectories);
		for (Label label:schema.getRules().keySet())
			System.out.println(label+": "+schema.getRules().get(label));
		
		 // load the model
		System.out.println("Reading data");
		String baseIRI = "http://a.example.shex/";
		Model data = Rio.parse(new FileInputStream(dataFile.toFile()), baseIRI, RDFFormat.TURTLE);
		Iterator<Statement> ite = data.iterator();
		while (ite.hasNext())
			System.out.println(ite.next());
		
		// create the RDF graph
		RDFGraph dataGraph = new RDF4JGraph(data);
		
		// choose focus node and shapelabel
		IRI focusNode = SimpleValueFactory.getInstance().createIRI("http://a.example/boolean-true"); //to change with what you want 
		Label shapeLabel = new Label(SimpleValueFactory.getInstance().createIRI("http://a.example/S-boolean")); //to change with what you want 
		
		System.out.println();
		System.out.println("Refine validation:");
		// create the validation algorithm
		ValidationAlgorithm validation = new RefineValidation(schema, dataGraph);   
		//validate
		validation.validate(focusNode, shapeLabel);
		//check the result
		System.out.println("Does "+focusNode+" has shape "+shapeLabel+"? "+validation.getTyping().contains(focusNode, shapeLabel));
		// print all the typing
		//for (Pair<Value,Label> pair:validation.getTyping().asSet())
		//	System.out.println(pair.one+":"+pair.two);
		
		System.out.println();
		System.out.println("Recursive validation:");
		validation = new RecursiveValidation(schema, dataGraph);
		validation.validate(focusNode, shapeLabel);
		//check the result
		System.out.println("Does "+focusNode+" has shape "+shapeLabel+"? "+validation.getTyping().contains(focusNode, shapeLabel));
		// print all the typing
		//for (Pair<Value,Label> pair:validation.getTyping().asSet())
		//	System.out.println(pair.two);
	}

}
