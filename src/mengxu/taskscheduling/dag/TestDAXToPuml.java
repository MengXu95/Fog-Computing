package mengxu.taskscheduling.dag;

import mengxu.taskscheduling.Task;
import mengxu.taskscheduling.WorkflowParser;

import java.util.List;

public class TestDAXToPuml {

    public static String toPuml(List<Task> taskList){
        StringBuffer s = new StringBuffer();
        s.append("@startuml\n\n").append("digraph ").append("test_DAG_generator").append(" {\n");
        for( int v = 0; v < taskList.size(); v++){
            s.append("    " + taskList.get(v).getId() + ";\n");
        }
        s.append("\n");
        for( int v = 0; v < taskList.size(); v++){
            for( int w = 0; w < taskList.get(v).getChildTaskList().size(); w++ ){
                Task child = taskList.get(v).getChildTaskList().get(w);
                s.append("    " + taskList.get(v).getId() + " -> " + child.getId() + ";\n");
            }
        }
        s.append("}\n").append("\n@enduml\n");
        return s.toString();
    }

    public static void main(String[] args) {
        int DAGTypeID = 12;
        String daxpath = DAGTypePath.getDAGTypePathForSubmit(DAGTypeID);//for submit to grid
//        String daxpath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\CyberShake_30.xml";
        WorkflowParser workflowParser = new WorkflowParser(daxpath);
        List<Task> taskList = workflowParser.getTaskList();
//        System.out.println(toPuml(digraph));
        System.out.println(toPuml(taskList));

//        Digraph digraphNoLink = DigraphGenerator.rootedOutDAG(9, 11);
//        System.out.println(digraphNoLink.toString());
//        System.out.println(digraphNoLink.reverse().toString());
//        System.out.println(toPuml(digraphNoLink));

    }
}
