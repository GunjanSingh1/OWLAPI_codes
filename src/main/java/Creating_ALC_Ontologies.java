
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

//import com.cs.myfirst.owlapi.Features.BoilerplateCode.TextFileProcessor;

public class Creating_ALC_Ontologies {
	public static String onto_file = "1.owl";
	public static String file1_count;
	ArrayList<OWLAxiom> axioms = new ArrayList<>();
	public static void main( String[] args ) throws OWLOntologyCreationException, IOException {
		 if(args.length == 1){
	            onto_file = args[0];
	        }
		 if(args.length == 2){
	            onto_file = args[0];
	            file1_count=args[1]; //output file 
	        }
	    OWLOntologyManager m = OWLManager.createOWLOntologyManager();
	    File file = new File (onto_file);
	    OWLOntology o = m.loadOntologyFromOntologyDocument(IRI.create(file));
	    OWLOntology o1;
	    o1 = m.createOntology();
	    Optional<IRI> ontologyIRI = o.getOntologyID().getOntologyIRI();
	    System.out.println(ontologyIRI);
	    OWLEntityRenamer renamer = new OWLEntityRenamer(m, Collections.singleton(o));  
	   // Map<OWLEntity, IRI> entity2IRIMap = new HashMap<>();
	    ManchesterSyntaxDocumentFormat omn = new ManchesterSyntaxDocumentFormat();
	    int classCount=0;
	    int propertyCount=0;
	    int individualCount=0;
	    System.out.println("CLASSES");
	    for (OWLClass cls : o.getClassesInSignature()) {
	    	 //System.out.println(cls);
	    	 //System.out.println("RENAMED to C" + classCount);
	    	String iri = cls.getIRI().getNamespace();
	    	// String iri = cls.getIRI().getShortForm();  To get only the name of entity without IRI
	    	 //System.out.println(iri);
	 	    List<? extends OWLOntologyChange> changes = renamer.changeIRI(
	                cls, IRI.create(iri+"C"+ classCount++));
	 	  
	        m.applyChanges(changes);
	      }
	    System.out.println("PROPERTIES");
	    for (OWLObjectProperty objectProperty : o.getObjectPropertiesInSignature()) {
	    	// System.out.println(objectProperty);
	    	 //System.out.println("RENAMED to OP" + propertyCount++);
	    	 String iri = objectProperty.getIRI().getNamespace();
	 	    List<? extends OWLOntologyChange> changes = renamer.changeIRI(
	 	    		objectProperty, 
	                IRI.create( iri+"OP" + propertyCount++));
	        m.applyChanges(changes);
	      }
	    System.out.println("INDIVIDUALS");
	    for (OWLNamedIndividual individual : o.getIndividualsInSignature()) {
	    	// System.out.println(individual);
	    	// System.out.println("RENAMED to I" + individualCount++);
	    	 String iri = individual.getIRI().getNamespace();
	 	    List<? extends OWLOntologyChange> changes = renamer.changeIRI(
	                individual, 
	                IRI.create(iri+"I" + individualCount++));
	        m.applyChanges(changes);
	      }
	    
	   
	 /*   System.out.println("CLASSES");
	    for (OWLClass cls : o.getClassesInSignature()) {
	    	 System.out.println(cls);
	    	 //System.out.println("RENAMED to C" + classCount);
	 	   // List<? extends OWLOntologyChange> changes = renamer.changeIRI(
	        //        cls, IRI.create("C"+ classCount++));
	       // m.applyChanges(changes);
	      }
	    System.out.println("PROPERTIES");
	    for (OWLObjectProperty objectProperty : o.getObjectPropertiesInSignature()) {
	    	 System.out.println(objectProperty);
	    	// System.out.println("RENAMED to OP" + propertyCount++);
	 	   // List<? extends OWLOntologyChange> changes = renamer.changeIRI(
	 	    //		objectProperty, 
	         //       IRI.create( "OP" + propertyCount++));
	       // m.applyChanges(changes);
	      }
	    System.out.println("INDIVIDUALS");
	    for (OWLNamedIndividual individual : o.getIndividualsInSignature()) {
	    	 System.out.println(individual);
	    	// System.out.println("RENAMED to I" + individualCount++);
	 	   // List<? extends OWLOntologyChange> changes = renamer.changeIRI(
	          //      individual, 
	           //     IRI.create("I" + individualCount++));
	       // m.applyChanges(changes);
	      }
	    */  
	    //ArrayList<OWLAxiom> axioms = new ArrayList<>();
	    System.out.println("ALL Declaration AXIOMS");
	    for (OWLAxiom axiom : o.getAxioms(AxiomType.DECLARATION)) {
	    	String result = axiom.toString();
	    	if(result.contains("Datatype")||result.contains("DataProperty")||result.contains("AnnotationProperty")) {	
	    		//System.out.println("matched"+result);
	    		}
	    		else {
	    			o1.addAxiom(axiom);
	    			}      	
	             }
	    for (OWLAxiom axiom : o.getAxioms(AxiomType.CLASS_ASSERTION)) {
	    	 //o1.addAxiom(axiom);
	         o1.addAxiom(axiom);      	
	             }
	    for (OWLAxiom axiom : o.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
	    	 //o1.addAxiom(axiom);
	    	System.out.println(axiom);
	         o1.addAxiom(axiom);      	
	             }
	    for (OWLAxiom axiom : o.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
	    	 //o1.addAxiom(axiom);
	         o1.addAxiom(axiom);      	
	             }
	    for (OWLAxiom axiom : o.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
	    	 //o1.addAxiom(axiom);
	         o1.addAxiom(axiom);      	
	             }
	    for (OWLAxiom axiom : o.getAxioms(AxiomType.EQUIVALENT_CLASSES)) {
	    	 //o1.addAxiom(axiom);
	    	String result = axiom.toString();
	    	//System.out.println(result);
	    	//String result = axiom.toString();
	    	if(result.contains("DataSomeValuesFrom")||result.contains("DataAllValuesFrom")||result.contains("ObjectHasValue")||result.contains("DataHasValue")||result.contains("ObjectOneOf")||result.contains("DataOneOf")||result.contains("MaxCardinality")||result.contains("MinCardinality")||result.contains("ExactCardinality")||result.contains("Datatype")||result.contains("DataProperty")) {
	    		//System.out.println("matched2"+result);
	    		//System.out.println("here");
	    	}
	   
	    	/*
	    		//String result = totriple(ax.toString());
	    		String[] splited = result.split("\\s+");
	    		System.out.println(splited[0]);
	    		//System.out.println("cool"+splited.length);
	    		for(int i=0;i<splited.length;i++)
	    		{
	    			
	    		if(splited[i]=="MaxCardinality"||splited[i]=="MinCardinality"||splited[i]=="ExactCardinality" ) {
	    			System.out.println("here"+result);
	    			break;
	    		}*/

	    		else {
	         o1.addAxiom(axiom);  
	         }   
	    }
	    for (OWLAxiom axiom : o.getAxioms(AxiomType.SUBCLASS_OF)) {
	    	String result = axiom.toString();
	    	//System.out.println(result);
	    	if(result.contains("DataSomeValuesFrom")||result.contains("DataAllValuesFrom")||result.contains("ObjectHasValue")||result.contains("DataHasValue")||result.contains("ObjectOneOf")||result.contains("DataOneOf")||result.contains("MaxCardinality")||result.contains("MinCardinality")||result.contains("ExactCardinality")||result.contains("Datatype")||result.contains("DataProperty")) {
	    		//System.out.println("matched2"+result);
	    		//System.out.println("here");
	    	}
	    	/*
	    		//String result = totriple(ax.toString());
	    		String[] splited = result.split("\\s+");
	    		System.out.println(splited[0]);
	    		//System.out.println("cool"+splited.length);
	    		for(int i=0;i<splited.length;i++)
	    		{
	    			
	    		if(splited[i]=="MaxCardinality"||splited[i]=="MinCardinality"||splited[i]=="ExactCardinality" ) {
	    			System.out.println("here"+result);
	    			break;
	    		}*/

	    		else {
	         o1.addAxiom(axiom);  
	         }   
	    }
	    
	    
	    
	    	// System.out.println(individual);
	  
	    	// System.out.println("ALL Declaration AXIOMS");
	 	   // for (OWLAxiom individual : o.getAxioms()) {
	 	    	// System.out.println(individual);}
	    	// System.out.println("RENAMED to I" + individualCount++);
	 	   // List<? extends OWLOntologyChange> changes = renamer.changeIRI(
	          //      individual, 
	           //     IRI.create("I" + individualCount++));
	       // m.applyChanges(changes);
	      
	    //AtomicInteger ai = new AtomicInteger();
	   // Set<OWLClass> classes=o.getclassesInSignature();
	    //Set<OWLNamedIndividual> individuals=o.getindividualsInSignature();
	    //.forEach(toRename -> entity2IRIMap.put(toRename, IRI.create("x:ind/" + ai.getAndIncrement())));
	   // o.classesInSignature().forEach(toRename -> entity2IRIMap.put(toRename, IRI.create("x:cls/" + ai.getAndIncrement())));
	    //o.annotationPropertiesInSignature().forEach(toRename -> entity2IRIMap.put(toRename, IRI.create("x:ap/" + ai.getAndIncrement())));
	    //o.dataPropertiesInSignature().forEach(toRename -> entity2IRIMap.put(toRename, IRI.create("x:dp/" + ai.getAndIncrement())));
	   // Set<OWLObjectProperty> objectProperties= o.getobjectPropertiesInSignature();
	   //.forEach(toRename -> entity2IRIMap.put(toRename, IRI.create("x:op/" + ai.getAndIncrement())));
	   // o.applyChanges(renamer.changeIRI(entity2IRIMap));
	   // o.logicalAxioms().forEach(System.out::println);
	   // System.out.println(classes);
	        try  {
	            File file1 = new File(System.getProperty("user.dir")+ "/" + "ALC"+file1_count +".owl");
	try {
	            if (!file1.exists()) {
	                
	            	file1.createNewFile();
	            } }catch (IOException e) {
	   		System.out.println("Exception Occurred:");
		        e.printStackTrace();
		  }
	 	   m.saveOntology(o1,omn,IRI.create(file1.toURI()));
       	System.out.println("Saved Ontology Format is Manchester");
	        }catch (OWLOntologyStorageException e) {
	            e.printStackTrace();
	        }
	        } 
}
