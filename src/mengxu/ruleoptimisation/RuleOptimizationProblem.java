package mengxu.ruleoptimisation;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import mengxu.rule.RuleType;
import mengxu.rule.evolved.GPRule;
import mengxu.ruleevaluation.AbstractEvaluationModel;
import mengxu.taskscheduling.Objective;

import java.util.ArrayList;
import java.util.List;

/**
 * The rule optimisation problem.
 *
 * In the problem, a rule is evaluated by being
 * applied to the training set.
 *
 * Created by YiMei on 29/09/16.
 */
public class RuleOptimizationProblem extends GPProblem implements SimpleProblemForm {

    public static final String P_EVAL_MODEL = "eval-model";

    private AbstractEvaluationModel evaluationModel;

    public List<Objective> getObjectives() {
        return evaluationModel.getObjectives();
    }

    public AbstractEvaluationModel getEvaluationModel() {
        return evaluationModel;
    }

    public void rotateEvaluationModel() {
        evaluationModel.rotate();
    }

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);  //about ADFStack and ADFContext

        Parameter p = base.push(P_EVAL_MODEL);  //yimei.jss.ruleevaluation.MultipleRuleEvaluationModel  here is different with before.
        evaluationModel = (AbstractEvaluationModel)(
                state.parameters.getInstanceForParameter(
                        p, null, AbstractEvaluationModel.class));

        evaluationModel.setup(state, p);
    }

    @Override
    public void evaluate(EvolutionState state,
                         Individual indi,
                         int subpopulation,
                         int threadnum) {
        GPRule rule = new GPRule(RuleType.SEQUENCING,((GPIndividual)indi).trees[0]);

        if (getObjectives().size() > 1) {
            System.err.println("ERROR:");
            System.err.println("Do NOT support more than one objective yet.");
            System.exit(1);
        }

        List rules = new ArrayList();
        List fitnesses = new ArrayList();
        rules.add(rule);
        fitnesses.add(indi.fitness);
        evaluationModel.evaluate(fitnesses, rules, state);

        indi.evaluated = true;
    }

	@Override
	public void normObjective(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		// TODO Auto-generated method stub

	}
}
