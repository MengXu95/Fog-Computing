package mengxu.rule.job.basic;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

public class PTPlusRL extends AbstractRule {

    public PTPlusRL(RuleType type) {
        name = "\"PT+RL\"";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        return taskOption.getProcTime() - taskOption.getReadyTime();
    }
}
