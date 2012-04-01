/**
 * 
 */
package com.ubifeel.app;

/**
 * @author Ze Ji
 *
 */
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.FileManager;

//import org.srs.srs_knowledge.knowledge_engine.*;

public class OntologyDB
{
    public OntologyDB()
    {
	// create an empty model
	this.model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
	
    }

    public OntologyDB(String filename)
    {
	try {
	    //String modelFileName = filename;
	    this.reloadOWLFile(filename);
	}
	catch(IllegalArgumentException e) {
	    System.out.println("Caught Exception : " + e.getMessage());
	}
	}

    public OntologyDB(ArrayList<String> filenames)
    {
	// create an empty model
	this.model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

	//this.model = ModelFactory.createDefaultModel();
	try {
	    for(String filename : filenames) {
		//String modelFileName = filename;
		this.importOntology(filename);
	    }
	}
	catch(IllegalArgumentException e) {
	    System.out.println("Caught Exception : " + e.getMessage());
	}
	 }

    public void importOntology(String filename) 
    {
	System.out.println("Load OWL File: " + filename);
	// use the FileManager to find the input file
	InputStream in = FileManager.get().open(filename);
	if (in == null) {
	    throw new IllegalArgumentException("File: " + filename + " not found");
	}
	
	// read the RDF/XML file
	model.read(in, null);
    }

    public String executeQuery(String queryString)
    {
	//System.out.println(queryString);
	Query query = QueryFactory.create(queryString);

	QueryExecution qe = QueryExecutionFactory.create(query, model);
	ResultSet results = qe.execSelect();

	ByteArrayOutputStream ostream = new ByteArrayOutputStream();
	ResultSetFormatter.out(ostream, results, query);
	//ResultSetFormatter.out(System.out, results, query);
	String r = "";
	try{
	    r = new String(ostream.toByteArray(), "UTF-8");
	    //System.out.println(r);
	}
	catch(Exception e){
	    System.out.println(e.getMessage());
	}
	qe.close();
	return r;
    }
    
    public ArrayList<QuerySolution> executeQueryRaw(String queryString)
    {
	//System.out.println(queryString);
	Query query = QueryFactory.create(queryString);
	
	QueryExecution qe = QueryExecutionFactory.create(query, model);
	ResultSet results = qe.execSelect();

	ArrayList<QuerySolution> resList = new ArrayList<QuerySolution>();
	if(results.hasNext()) {
	    
	    QuerySolution qs = results.next();
	    resList.add(qs);
	}

	qe.close();
	return resList; //results;
    }

    public void reloadOWLFile(String file)
    {
	// create an empty model
	//this.model = ModelFactory.createDefaultModel();
	this.model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

	System.out.println("Load OWL File: " + file);
	// use the FileManager to find the input file
	InputStream in = FileManager.get().open(file);
	if (in == null) {
	    throw new IllegalArgumentException("File: " + file + " not found");
	}
	
	// read the RDF/XML file
	model.read(in, null);
    }
    
    public void printModel()
    { 
	model.write(System.out);
    }

    public Iterator getInstancesOfClass(String className) 
    {
	// get the instances of a class
	OntClass onto = model.getOntClass( className );
	
	if(onto == null) {
	    System.out.println("ONT CLASS IS NULL");
	    return (new ArrayList()).iterator();
	}
	
	Iterator instances = onto.listInstances();
	return instances;
    }

    public String getNamespaceByPrefix(String namespacePrefix)
    {
	model.enterCriticalSection(Lock.READ);
	//http://www.srs-project.eu/ontologies/ipa-kitchen-map.owl#
	String pre = model.getNsPrefixURI(namespacePrefix);
	model.leaveCriticalSection();
	return pre;
    }

    /**
     * @param proNameSpace property namespace
     * @param proLocalName property name
     * @param ind individual object
     * @return statement containing the property info 
     */
    public com.hp.hpl.jena.rdf.model.Statement getPropertyOf(String proNameSpace, String proLocalName, Individual ind ) 
    {
	model.enterCriticalSection(Lock.READ);
	com.hp.hpl.jena.rdf.model.Property property = model.getProperty(proNameSpace, proLocalName);
	com.hp.hpl.jena.rdf.model.Statement stm = ind.getProperty(property);
	model.leaveCriticalSection();
	return stm;
    }

    public OntModel getModel() {
	return model;
    }
   
    
    public boolean removeStatement(Statement stm) 
    {
	// TODO: error checking in future
	model.enterCriticalSection(Lock.WRITE);
	model.remove(stm);
	model.leaveCriticalSection();
	return true;
    }

    public OntModel model;
}
