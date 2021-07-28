package mengxu.algorithm;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

public class CPOP extends AbstractRule {

    public CPOP(RuleType type) {
        name = "\"CPOP\"";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        if(this.type == RuleType.SEQUENCING){
            return -(taskOption.getTask().getUPDOWNRank());
        }
        else if(this.type == RuleType.ROUTING){
            if(taskOption.getTask().getJob().getCriticalPath().contains(taskOption.getTask())){
                return taskOption.getTask().getJob().getCriticalPathProcessTime();
            }
            else{
                return taskOption.getEarliestExecutionFinishTime();
            }
        }
        else{
            System.out.println("Error! CPOP");
            return -1;
        }
    }
}