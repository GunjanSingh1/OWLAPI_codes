package Dataset_splits.Dataset_splits;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import java.io.*;
import java.util.*;

public class Rename {
    public static String onto_file;
    public static String train_onto_file;
    public static String valid_onto_file;
    public static String train_file;
    public static String valid_file;
    public static String test_file;
    
   

    /**
     * Axioms (non-inferred) are split into train (70%), validation (10%) and test (20%)
     * A training ontology is created for learning the embedding, by removing validation and test axioms
     * @param args
     */
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        if(args.length >= 2){
            onto_file = args[1]; train_onto_file = args[2]; valid_onto_file = args[3];
            train_file = args[4]; valid_file = args[5]; test_file = args[6];
        }
       
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");
            // load range of different parameters from config.properties file
            prop.load(input);
            onto_file = prop.getProperty("onto_file");
            train_onto_file = prop.getProperty("train_onto_file");
            valid_onto_file = prop.getProperty("valid_onto_file");
            train_file = prop.getProperty("train_file");
            valid_file = prop.getProperty("valid_file");
            test_file = prop.getProperty("test_file");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Onto File="+ onto_file);
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.loadOntologyFromOntologyDocument(new FileDocumentSource(new File(onto_file)));
        OWLOntologyManager m1 = OWLManager.createOWLOntologyManager();
        OWLOntology o_main_train = m1.loadOntologyFromOntologyDocument(new FileDocumentSource(new File(onto_file)));
        OWLOntologyManager m2 = OWLManager.createOWLOntologyManager();
        OWLOntology o_main_valid = m2.loadOntologyFromOntologyDocument(new FileDocumentSource(new File(onto_file)));
        OWLDataFactory factory = m.getOWLDataFactory();
        System.out.println("Input Ontology Format="+ m.getOntologyFormat(o));
        System.out.println("Total Axiom Count="+ o.getAxiomCount());
        System.out.println("Total Logical Axiom Count="+ o.getLogicalAxiomCount());
        final IRI iri = o.getOntologyID().getOntologyIRI().get();
        
        System.out.println("IRI="+ iri);
        OWLEntityRenamer renamer = new OWLEntityRenamer(m, Collections.singleton(o));        
        Map<OWLEntity, IRI> entity2IRIMap = new HashMap<>();             
       // manager.save(B);
        int count =0;
        for (OWLNamedIndividual c :o.getIndividualsInSignature()) {
            count=count+1;

            entity2IRIMap.put(factory.getOWLNamedIndividual(c), IRI.create(iri+"#i"+count));       
            o.applyChanges(renamer.changeIRI(entity2IRIMap));         
          }
       /* for (OWLNamedIndividual c :o.getIndividualsInSignature()) {
        System.out.println("new =" + c );
        }*/
          //System.out.println("Total Instances Count="+ count);
          count =0;
          for (OWLClass c : o.getClassesInSignature()) {
        	  entity2IRIMap.put(factory.getOWLClass(c), IRI.create(iri+"#c"+count));       
              o.applyChanges(renamer.changeIRI(entity2IRIMap));  
            count=count+1;
          }
        //  System.out.println("Total Classes Count="+ count);      
          count =0;
          for (OWLObjectProperty c : o.getObjectPropertiesInSignature()) {
        	  entity2IRIMap.put(factory.getOWLObjectProperty(c), IRI.create(iri+"#op"+count));       
              o.applyChanges(renamer.changeIRI(entity2IRIMap));  
            count=count+1;
          }
          //System.out.println("Total Object Properties Count="+ count);   
          count =0;
          for (OWLDataProperty c : o.getDataPropertiesInSignature()) {
        	  entity2IRIMap.put(factory.getOWLDataProperty(c), IRI.create(iri+"#dp"+count));       
              o.applyChanges(renamer.changeIRI(entity2IRIMap));  
            count=count+1;
          }
        System.out.println("Total Data Properties Count="+ count);  
        System.out.println("Total Subclass Axiom Count="+ o.getAxiomCount(AxiomType.SUBCLASS_OF));
        System.out.println("Total Class Assertion Axiom Count="+ o.getAxiomCount(AxiomType.CLASS_ASSERTION));
        System.out.println("Total Object Property Assertion Axiom Count="+ o.getAxiomCount(AxiomType.OBJECT_PROPERTY_ASSERTION));
        System.out.println("Total Data Property Assertion Axiom Count="+ o.getAxiomCount(AxiomType.DATA_PROPERTY_ASSERTION));
        try  {
            File file = new File("saved_file"+ ".owl");
    try {
            if (!file.exists()) {
                
            	file.createNewFile();
            } }catch (IOException e) {
    		System.out.println("Exception Occurred:");
            e.printStackTrace();
      }
       // FunctionalSyntaxDocumentFormat ofn = new FunctionalSyntaxDocumentFormat();
        ManchesterSyntaxDocumentFormat omn = new ManchesterSyntaxDocumentFormat();
      m.saveOntology(o,omn,IRI.create(file.toURI()));
      System.out.println("Training ontology saved.");  }catch (OWLOntologyStorageException e) {
          e.printStackTrace();
      }
       // m.saveOntology(o_main_train, ofn, new FileOutputStream(new File(train_onto_file)));
    //    System.out.println("Training ontology saved.");
       
  
}
    }
