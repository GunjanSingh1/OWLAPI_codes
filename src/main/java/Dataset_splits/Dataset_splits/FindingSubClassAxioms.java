package Dataset_splits.Dataset_splits;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import java.io.*;
import java.util.*;

public class FindingSubClassAxioms {
    public static String onto_file = "data/go.owl";
    public static String train_onto_file = "go.train.owl";
    public static String train_file = "train.csv";
    public static String valid_file = "valid.csv";
    public static String test_file = "test.csv";
    public static String class_file = "classes.txt";
    public static String inferred_ancestor_file = "inferred_ancestors.txt";

    /**
     * All declared subsumption axioms are divided into train (70%), valid (10%) and test (20%)
     * A training ontology is created for learning the embedding, by removing valid and test subsumption axioms
     * One negative sample is generated for each subsumption axiom for training
     *
     * For each subClass in a test/valid axiom, all its parents and ancestors (including the inferred) are extracted;
     * in evaluation, the inferred ancestors are excluded from the candidates
     */
    public static void main(String [] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        if(args.length >= 2){
        	
            onto_file = args[1]; train_onto_file = args[2];
            train_file = args[3]; valid_file = args[4]; test_file = args[5];
            class_file = args[6]; inferred_ancestor_file = args[7];
        }

        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.loadOntologyFromOntologyDocument(new FileDocumentSource(new File(onto_file)));
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(o);
        OWLDataFactory df = OWLManager.getOWLDataFactory();

        // save all classes
        ArrayList<OWLClass> classes =  new ArrayList<>();
        ArrayList<String> classes_str = new ArrayList<>();
        for (OWLClass c : o.getClassesInSignature()) {
            String c_str = c.toString();
            if (!classes_str.contains(c_str) && !c_str.equals("<http://www.w3.org/2002/07/owl#Thing>")) {
                classes_str.add(c_str);
                classes.add(c);
            }
        }
        save_classes(classes_str, class_file);
        System.out.println("Classes: " + classes_str.size());

        // get all declared subClassOf axioms
        ArrayList<OWLClass []> subClassAxioms = new ArrayList<>();
        Set<OWLSubClassOfAxiom> axioms = o.getAxioms(AxiomType.SUBCLASS_OF);
        for (OWLSubClassOfAxiom a : axioms) {
            OWLClassExpression supc = a.getSuperClass();
            OWLClassExpression subc = a.getSubClass();
            if (supc instanceof OWLClass && subc instanceof OWLClass) {
                OWLClass [] subClassAxiom = {(OWLClass) subc, (OWLClass) supc};
                subClassAxioms.add(subClassAxiom);
            }
        }
        

        

        // Save samples
        save_subClassAxioms(subClassAxioms, train_file);
        System.out.println("saved.");

        // Get all superClasses
        HashMap<OWLClass, ArrayList<OWLClass>> c_ancestors = new HashMap<>();
        for(OWLClass c : classes){
            ArrayList<OWLClass> ancestors = new ArrayList<>();
            Set<OWLClass> delcared_super_classes = reasoner.getSuperClasses(c, true).getFlattened();
            for (OWLClass ancestor : reasoner.getSuperClasses(c, false).getFlattened()){
                if(classes.contains(ancestor) && !delcared_super_classes.contains(ancestor)){
                    ancestors.add(ancestor);
                }
            }
            c_ancestors.put(c, ancestors);
        }
        save_ancestors(c_ancestors, inferred_ancestor_file);
        System.out.println("Ancestors saved.");

    }


    static void save_classes(ArrayList<String> classes, String class_file) throws IOException {

        FileWriter cw = new FileWriter(class_file);
        BufferedWriter cbw = new BufferedWriter(cw);
        PrintWriter cout = new PrintWriter(cbw);
        for (String s : classes) {
            cout.println(reformat_uri(s));
        }
        cbw.close();
        cw.close();
    }

    static void save_subClassAxioms(ArrayList<OWLClass []> subClasAxioms, String file) throws IOException {
        File fout = new File(file);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for(OWLClass [] axioms: subClasAxioms){
            bw.write(reformat_uri(axioms[0].toString()) + "," + reformat_uri(axioms[1].toString()) + "\n");
        }
        bw.close();
    }

    static void save_subClassAxioms(ArrayList<OWLClass []> subClasAxioms_pos, ArrayList<OWLClass []> subClasAxioms_neg,
                                    String file) throws IOException {
        File fout = new File(file);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for(OWLClass [] axioms: subClasAxioms_pos){
            bw.write(reformat_uri(axioms[0].toString()) + "," + reformat_uri(axioms[1].toString()) + ",1\n");
        }
        for(OWLClass [] axioms: subClasAxioms_neg){
            bw.write(reformat_uri(axioms[0].toString()) + "," + reformat_uri(axioms[1].toString()) + ",0\n");
        }
        bw.close();
    }

    static void save_ancestors(HashMap<OWLClass, ArrayList<OWLClass>> c_ancestors, String file) throws IOException {
        File fout = new File(file);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for(OWLClass c : c_ancestors.keySet()){
            String s = reformat_uri(c.toString());
            for(OWLClass ancestor: c_ancestors.get(c)){
                s = s + ',' + reformat_uri(ancestor.toString());
            }
            bw.write(s + "\n");
        }
        bw.close();

    }

    static String reformat_uri(String s){
        if (s.startsWith("<") && s.endsWith(">")) {
            return s.substring(1, s.length() - 1);
        }else{
            return s;
        }
    }
}
