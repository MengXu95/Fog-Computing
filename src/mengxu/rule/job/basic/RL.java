package mengxu.rule.job.basic;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Job;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

public class RL extends AbstractRule {

    public RL(RuleType type) {
        name = "\"RL\"";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        return taskOption.getReadyTime();
    }
}
