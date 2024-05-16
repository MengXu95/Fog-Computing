package mengxu.algorithm;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

/**
* After implementing the DMWHDBS method based on the paper. It can be seen that, the proposed method is
* implemented based on HEFT by considering cost and budget contraints.
* Implemented in 2022.07.27
* In this case, it is not suitable to be seen as a compare method as we have already HEFT and our objective is
* only about minimising the makespan.
* */

public class DMWHDBS extends AbstractRule {

    public DMWHDBS(RuleType type) {
        name = "DMWHDBS";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        if(this.type == RuleType.SEQUENCING){
//            return -1;
            return -(taskOption.getTask().getUpwardRankForDMWHDBS());//the task with highest upwardRank is selected
        }
        else if(this.type == RuleType.ROUTING){
            return taskOption.getEarliestExecutionFinishTime();//this is the same with HEFT as we do not consider computation cost and budget in our problem,
        }
        else{
            System.out.println("Error! HEFT");
            return -1;
        }
    }
}