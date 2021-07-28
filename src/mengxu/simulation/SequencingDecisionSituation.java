package mengxu.simulation;

import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

import java.util.ArrayList;
import java.util.List;

public class SequencingDecisionSituation extends DecisionSituation {

    private List<TaskOption> queue;
    private Server server;
    private SystemState systemState;

    public SequencingDecisionSituation(List<TaskOption> queue,
                                       Server server,
                                       SystemState systemState) {
        this.queue = queue;
        this.server = server;
        this.systemState = systemState;
    }

    public List<TaskOption> getQueue() {
        return queue;
    }

    public Server getServer() {
        return server;
    }

    public SystemState getSystemState() {
        return systemState;
    }

    public SequencingDecisionSituation clone() {
        List<TaskOption> clonedQ = new ArrayList<>(queue);
        Server clonedWC = server.clone();
        SystemState clonedState = systemState.clone();

        return new SequencingDecisionSituation(clonedQ, clonedWC, clonedState);
    }
}
