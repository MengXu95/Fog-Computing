package mengxu.algorithm;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

/**
 * FCFS, namely First Come First Scheduling, allocates the first ready task to the first idle VM.
 */
public class FCFS extends AbstractRule {

    public FCFS(RuleType type) {
        name = "FCFS";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        if(this.type == RuleType.SEQUENCING){
            return taskOption.getTask().getJob().getReleaseTime();
//            return taskOption.getReadyTime(); //I think I should use the release time, as the first arrive first serve policy.
            //modified 2022.08.01
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