package mengxu.algorithm;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

/**
 * RoundRobin. //todo: need modify!!!
 */
public class RoundRobin extends AbstractRule {

    public RoundRobin(RuleType type) {
        name = "\"RoundRobin\"";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        if(this.type == RuleType.SEQUENCING){
            //todo: compare based on job ID. but not suitable for this simulation model!
            //todo: need modify!.
            return taskOption.getTask().getJob().getId();
        }
        else if(this.type == RuleType.ROUTING){
            //todo: compare based on VM ID. but not suitable for this simulation model!
            //todo: need modify!.
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