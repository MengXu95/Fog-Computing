package mengxu.algorithm;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

/**
 * This method is implemented based on paper "Modified HEFT Algorithm for Task Scheduling in Cloud Environment"
 * Implemented by mengxu in 2022.07.28
 * */

public class MHEFT extends AbstractRule {

    public MHEFT(RuleType type) {
        name = "MHEFT";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        if(this.type == RuleType.SEQUENCING){
//            return -1;//todo: need to modify when use HEFT
            return -(taskOption.getTask().getUpwardRank()); //I think this is the same as traditional HEFT
        }
        else if(this.type == RuleType.ROUTING){
            return taskOption.getProcTime();//This is different with traditional HEFT
            //the MHEFT does depend on the processing time to select processing resource, but this is not suitable for my problem
            //as it might always select one processing resource and in my problem, we consider workflow streams not single workflow.
//            return taskOption.getEarliestExecutionFinishTime();
        }
        else{
            System.out.println("Error! MHEFT");
            return -1;
        }
    }
}