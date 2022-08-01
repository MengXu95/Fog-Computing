package mengxu.algorithm;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

/**
 * This method has not been implemented!!!
 * */

public class CATS extends AbstractRule {

    public CATS(RuleType type) {
        name = "CATS";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
//        if(this.type == RuleType.SEQUENCING){
////            return -1;//todo: need to modify when use HEFT
//            return -(taskOption.getTask().getUpwardRank());
//        }
//        else if(this.type == RuleType.ROUTING){
//            return taskOption.getEarliestExecutionFinishTime();
//        }
//        else{
//            System.out.println("Error! HEFT");
//            return -1;
//        }
        return -1;
    }
}