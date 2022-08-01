package mengxu.algorithm;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

/**
 * This method is implemented based on paper "Dynamic Scheduling of Workflow for Makespan and Robustness Improvement in the IaaS Cloud"
 * Implemented by mengxu in 2022.07.28
 * Have not been implemented as it performs very similar with HEFT, it just like a dynamic HEFT.
 * */

public class DEFT extends AbstractRule {

    public DEFT(RuleType type) {
        name = "DEFT";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
//        if(this.type == RuleType.SEQUENCING){
////            return -1;//todo: need to modify when use HEFT
//            return -(taskOption.getTask().getUpwardRank()); //I think this is the same as traditional HEFT
//        }
//        else if(this.type == RuleType.ROUTING){
//            return taskOption.getProcTime();//This is different with traditional HEFT
////            return taskOption.getEarliestExecutionFinishTime();
//        }
//        else{
//            System.out.println("Error! DHEFT");
//            return -1;
//        }

        return -1;
    }
}