

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import java.io.*;
import java.util.*;

public class TrainValidTestSplitsRandom {
    public static String onto_file = "UNIV-BENCH-OWL2DL.owl";
    public static String train_onto_file = "UNIV-BENCH-OWL2EL_train.owl";
    public static String valid_onto_file = "UNIV-BENCH-OWL2EL_valid.owl";
    public static String train_file = "train.csv";
    public static String valid_file = "valid.csv";
    public static String test_file = "test.csv";


    /**
     * Those declared (none-inferred) membership axioms are split into train (70%), valid (10%) and test (20%)
     * A training ontology is created for learning the embedding, by removing valid and test membership axioms
     * One negative sample is generated for each positive axiom in the training set 
     * @param args
     */
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        if(args.length >= 2){
            onto_file = args[1]; train_onto_file = args[2]; valid_onto_file = args[3];
            train_file = args[4]; valid_file = args[5]; test_file = args[6];
        }
        
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.loadOntologyFromOntologyDocument(new FileDocumentSource(new File(onto_file)));
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        //OWLReasoner reasoner = reasonerFactory.createReasoner(o);
        //Set<OWLNamedIndividual> inds = o.getIndividualsInSignature();
        System.out.println("Total Logical Axiom Count="+ o.getLogicalAxiomCount());
        ArrayList<OWLLogicalAxiom> logicalAxioms = new ArrayList<>();
        //Set<OWLLogicalAxiom> axioms = o.getLogicalAxioms();
        for (OWLLogicalAxiom a : o.getLogicalAxioms()) {
        	System.out.println(a);
        	logicalAxioms.add(a);
        	
            }
        Collections.shuffle(logicalAxioms);
        int num_ = logicalAxioms.size();
        int index1_ = (int) ( num_ * 0.7);
        int index2_ = index1_ + (int) (num_ * 0.1);
        ArrayList<OWLLogicalAxiom> logicalAxioms_train = new ArrayList<>(logicalAxioms.subList(0, index1_));
        ArrayList<OWLLogicalAxiom> logicalAxioms_valid = new ArrayList<>(logicalAxioms.subList(index1_, index2_));
        ArrayList<OWLLogicalAxiom> logicalAxioms_test = new ArrayList<>(logicalAxioms.subList(index2_, num_));
        System.out.println(("train (positive): " + logicalAxioms_train.size() + ", valid: " + logicalAxioms_valid.size() +
                ", test: " + logicalAxioms_test.size()));

        save_sample(train_file, logicalAxioms_train);
        save_sample(valid_file, logicalAxioms_valid);
        save_sample(test_file, logicalAxioms_test);
        
        // Remove the test and valid subClassOf axioms from the original ontology
        ArrayList<OWLLogicalAxiom> axioms_remove = new ArrayList<>(logicalAxioms_test);
        axioms_remove.addAll(logicalAxioms_valid);
        for (OWLLogicalAxiom axiom: axioms_remove){
        	//OWLLogicalAxiom axiom_rm = df.getOWLSubClassOfAxiom(axiom[0], axiom[1]);
            RemoveAxiom ra = new RemoveAxiom(o, axiom);
            List<RemoveAxiom> ral = Collections.singletonList(ra);
            m.applyChanges(ral);
        }
        m.saveOntology(o, new FileOutputStream(new File(train_onto_file)));
        System.out.println("Training ontology saved.");
        
     // Remove the test and train subClassOf axioms from the original ontology
        ArrayList<OWLLogicalAxiom> axioms_remove2 = new ArrayList<>(logicalAxioms_test);
        axioms_remove2.addAll(logicalAxioms_train);
        for (OWLLogicalAxiom axiom: axioms_remove2){
        	//OWLLogicalAxiom axiom_rm = df.getOWLSubClassOfAxiom(axiom[0], axiom[1]);
            RemoveAxiom ra = new RemoveAxiom(o, axiom);
            List<RemoveAxiom> ral = Collections.singletonList(ra);
            m.applyChanges(ral);
        }
        m.saveOntology(o, new FileOutputStream(new File(valid_onto_file)));
        System.out.println("validating ontology saved.");
    }

  
    static void save_sample(String file_name, ArrayList<OWLLogicalAxiom> samples) throws IOException {
        File fout = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for(OWLLogicalAxiom s: samples){
            bw.write(s + "\n");
        }
        bw.close();
    }



  
}
