package mengxu.algorithm;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

/**
 * MinMin,.
 */
public class MinMin extends AbstractRule {

    public MinMin(RuleType type) {
        name = "\"MinMin\"";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        if(this.type == RuleType.SEQUENCING){
            return taskOption.getProcTime();
        }
        else if(this.type == RuleType.ROUTING){
            if(server==null){
                return taskOption.getMobileDevice().getReadyTime();
            }
            return server.getReadyTime();
        }
        else{
            System.out.println("Error! FCFS");
            return -1;
        }
    }
}