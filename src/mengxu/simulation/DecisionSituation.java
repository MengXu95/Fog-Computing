package mengxu.simulation;

import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.TaskOption;

import java.util.List;

public abstract class DecisionSituation {

    private List<TaskOption> queue = null;
    private SystemState systemState = null;

    public List<TaskOption> getQueue() {
        return queue;
    }

    public SystemState getSystemState() {
        return systemState;
    }
}
