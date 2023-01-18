
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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

public class EntityRenaming {
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
	    //System.out.println(ontologyIRI);
	    OWLEntityRenamer renamer = new OWLEntityRenamer(m, Collections.singleton(o));  
	    ManchesterSyntaxDocumentFormat omn = new ManchesterSyntaxDocumentFormat();
	   // int declarationAxiomCount=o.getAxioms(AxiomType.DECLARATION).size();
	    //int logicalAxiomCount=o.getLogicalAxiomCount();
	    Set<OWLClass> cl=o.getClassesInSignature();
	    int classCount=cl.size();
	    Set<OWLObjectProperty> prop=o.getObjectPropertiesInSignature();
	    int propertyCount=prop.size();
	    Set<OWLNamedIndividual>	ind=o.getIndividualsInSignature();
	    int individualCount=ind.size();
	    Set<Object> set1 = new Random().ints(1, 6000).distinct().limit(classCount).boxed().collect(Collectors.toSet());
	    Set<Object> set2 = new Random().ints(1, 6000).distinct().limit(propertyCount).boxed().collect(Collectors.toSet());
	    Set<Object> set3 = new Random().ints(1, 6000).distinct().limit(individualCount).boxed().collect(Collectors.toSet());
	    Iterator value1 = set1.iterator();
	    Iterator value2 = set2.iterator();
	    Iterator value3 = set3.iterator();
	    
	    for (OWLClass cls : o.getClassesInSignature()) {
	    	String iri = cls.getIRI().getNamespace();
	 	    List<? extends OWLOntologyChange> changes = renamer.changeIRI(
	                cls, IRI.create(iri+"C"+ value1.next()));
	        m.applyChanges(changes);
	      }
	   // System.out.println("PROPERTIES");
	    for (OWLObjectProperty objectProperty : o.getObjectPropertiesInSignature()) {
	    	 String iri = objectProperty.getIRI().getNamespace();
	 	    List<? extends OWLOntologyChange> changes = renamer.changeIRI(
	 	    		objectProperty, 
	                IRI.create( iri+"OP" +  value2.next()));
	        m.applyChanges(changes);
	      }
	    //System.out.println("INDIVIDUALS");
	    for (OWLNamedIndividual individual : o.getIndividualsInSignature()) {
	    	 String iri = individual.getIRI().getNamespace();
	 	    List<? extends OWLOntologyChange> changes = renamer.changeIRI(
	                individual, 
	                IRI.create(iri+"I" + value3.next()));
	        m.applyChanges(changes);
	      }
	    
	    System.out.println("ALL Declaration AXIOMS");
	    for (OWLAxiom axiom : o.getAxioms(AxiomType.DECLARATION)) {
	    	String result = axiom.toString();
	    	if(result.contains("Datatype")||result.contains("DataProperty")||result.contains("AnnotationProperty")) {	
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
	    	if(result.contains("DataSomeValuesFrom")||result.contains("DataAllValuesFrom")||result.contains("ObjectHasValue")||result.contains("DataHasValue")||result.contains("ObjectOneOf")||result.contains("DataOneOf")||result.contains("MaxCardinality")||result.contains("MinCardinality")||result.contains("ExactCardinality")||result.contains("Datatype")||result.contains("DataProperty")) {
	    	}
	    		else {
	         o1.addAxiom(axiom);  
	         }   
	    }
	    for (OWLAxiom axiom : o.getAxioms(AxiomType.SUBCLASS_OF)) {
	    	String result = axiom.toString();
	    	//System.out.println(result);
	    	if(result.contains("DataSomeValuesFrom")||result.contains("DataAllValuesFrom")||result.contains("ObjectHasValue")||result.contains("DataHasValue")||result.contains("ObjectOneOf")||result.contains("DataOneOf")||result.contains("MaxCardinality")||result.contains("MinCardinality")||result.contains("ExactCardinality")||result.contains("Datatype")||result.contains("DataProperty")) {
	    	}
	    		else {
	         o1.addAxiom(axiom);  
	         }   
	    }
	    
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
       	//System.out.println("Saved Ontology Format is Manchester");
	        }catch (OWLOntologyStorageException e) {
	            e.printStackTrace();
	        }
	        } 
}
