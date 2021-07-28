package mengxu.taskscheduling.dag;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DigraphGenerator;
import mengxu.util.random.AbstractIntegerSampler;
import mengxu.util.random.UniformIntegerSampler;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.ArrayList;
import java.util.List;

public class TestDigraphGenerator {

    public static String toPuml(Digraph digraph){
        StringBuffer s = new StringBuffer();
        s.append("@startuml\n\n").append("digraph ").append("test_DAG_generator").append(" {\n");
        for( int v = 0; v < digraph.V(); v++){
            s.append("    " + v + ";\n");
        }
        s.append("\n");
        for( int v = 0; v < digraph.V(); v++){
            for( int w : digraph.adj(v) ){
                s.append("    " + v + " -> " + w + ";\n");
            }
        }
        s.append("}\n").append("\n@enduml\n");
        return s.toString();
    }

    public static void main(String[] args) {
        final int vectorCount = 8;

        Digraph digraph = DigraphGeneratorMX.FixedDAG();
        System.out.println(digraph.toString());
        System.out.println(digraph.reverse().toString());
//        System.out.println(toPuml(digraph));
        System.out.println(toPuml(digraph));

//        Digraph digraphNoLink = DigraphGenerator.rootedOutDAG(9, 11);
//        System.out.println(digraphNoLink.toString());
//        System.out.println(digraphNoLink.reverse().toString());
//        System.out.println(toPuml(digraphNoLink));

    }
}
