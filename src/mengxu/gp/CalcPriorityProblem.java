package mengxu.gp;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

/**
 * Created by YiMei on 27/09/16.
 */
public class CalcPriorityProblem extends Problem implements SimpleProblemForm {

    private TaskOption operation;
    private Server server;
    private SystemState systemState;

    public CalcPriorityProblem(TaskOption operation,
                               Server server,
                               SystemState systemState) {
        this.operation = operation;
        this.server = server;
        this.systemState = systemState;
    }
    public TaskOption getTaskOption() {
        return operation;
    }

    public Server getServer() {
        return server;
    }

    public SystemState getSystemState() {
        return systemState;
    }
    @Override
    public void evaluate(EvolutionState state, Individual ind,
                         int subpopulation, int threadnum) {
    }
	@Override
	public void normObjective(EvolutionState state, Individual ind,
			                  int subpopulation, int threadnum) {
		// TODO Auto-generated method stub

	}
}
