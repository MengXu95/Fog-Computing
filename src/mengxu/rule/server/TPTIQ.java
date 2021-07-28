package mengxu.rule.server;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

public class TPTIQ extends AbstractRule {
    public TPTIQ(RuleType type) {
        name = "\"TPTIQ\"";
        this.type = type;
    }

    @Override
    public double priority(TaskOption taskOption, Server server, SystemState systemState) {
        if(server == null)//this is the mobileDevice
        {
            return taskOption.getMobileDevice().totalProcTimeInQueue();
        }
        return server.totalProcTimeInQueue();
    }
}
