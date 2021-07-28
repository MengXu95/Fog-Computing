package mengxu.simulation;

import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.TaskOption;

import java.util.ArrayList;
import java.util.List;

public class RoutingDecisionSituation extends DecisionSituation {

    private List<TaskOption> queue;
    private SystemState systemState;

    public RoutingDecisionSituation(List<TaskOption> taskOptions, SystemState systemState) {
        this.queue = taskOptions;
        this.systemState = systemState;
    }

    public List<TaskOption> getQueue() {
        return queue;
    }

    public SystemState getSystemState() {
        return systemState;
    }

    public RoutingDecisionSituation clone() {
        List<TaskOption> clonedQ = new ArrayList<>(queue);
        SystemState clonedState = systemState.clone();

        return new RoutingDecisionSituation(clonedQ, clonedState);
    }
}