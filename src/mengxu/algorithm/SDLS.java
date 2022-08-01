package mengxu.algorithm;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

/**
 * This method is implemented based on paper "Scheduling Precedence Constrained Stochastic Tasks on Heterogeneous Cluster Systems"
 * Implemented by mengxu in 2022.07.30
 * */

public class SDLS extends AbstractRule {

    public SDLS(RuleType type) {
        name = "SDLS";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        if(this.type == RuleType.SEQUENCING){
//            return -1;//todo: need to modify when use HEFT
            return -(taskOption.getTask().getSb_LevelForSDLS()); //I think this is the same as traditional HEFT
        }
        else if(this.type == RuleType.ROUTING){
            return -(taskOption.getSDL());//This is different with traditional HEFT
//            return taskOption.getEarliestExecutionFinishTime();
        }
        else{
            System.out.println("Error! SDLS");
            return -1;
        }
    }
}