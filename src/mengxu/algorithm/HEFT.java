package mengxu.algorithm;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

public class HEFT extends AbstractRule {

    public HEFT(RuleType type) {
        name = "\"HEFT\"";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        if(this.type == RuleType.SEQUENCING){
            return -taskOption.getTask().getUpwardRank();
        }
        else if(this.type == RuleType.ROUTING){
            return taskOption.getEarliestExecutionFinishTime();
        }
        else{
            System.out.println("Error! HEFT");
            return -1;
        }
    }
}