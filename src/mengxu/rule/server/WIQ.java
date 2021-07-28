package mengxu.rule.server;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

public class WIQ extends AbstractRule {
    public WIQ(RuleType type) {
        name = "\"WIQ\"";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        if(server == null)//this is the mobileDevice
        {
            return taskOption.getMobileDevice().numTaskInQueue();
        }
        return server.numTaskInQueue();
    }
}
