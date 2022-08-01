package mengxu.algorithm;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

/**
 * This method is implemented based on paper "Makespan Optimization of Work ow Application based on Bandwidth Allocation Algorithm in FogCloud Environment"
 * Implemented by mengxu in 2022.07.28
 * */

public class BWAWA extends AbstractRule {

    public BWAWA(RuleType type) {
        name = "BWAWA";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        if(this.type == RuleType.SEQUENCING){
//            return -1;//todo: need to modify when use HEFT
            return taskOption.getTask().getDownwardRankForBWAWA();
//            return -(taskOption.getTask().getDownwardRankForBWAWA());
        }
        else if(this.type == RuleType.ROUTING){
            return taskOption.bandRate();
        }
        else{
            System.out.println("Error! BWAWA");
            return -1;
        }
    }
}