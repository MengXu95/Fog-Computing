package mengxu.rule.job.basic;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

public class PT extends AbstractRule {

    public PT(RuleType type) {
        name = "\"PT\"";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        return taskOption.getProcTime();
    }
}
