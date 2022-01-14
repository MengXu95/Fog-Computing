package mengxu.ruleoptimisation;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.util.Parameter;
import mengxu.algorithm.FCFS;
import mengxu.rule.RuleType;
import mengxu.rule.evolved.GPRule;
import mengxu.ruleevaluation.AbstractEvaluationModel;
import mengxu.taskscheduling.Objective;

import java.util.ArrayList;
import java.util.List;

public class MultipleTreeRuleOptimizationProblem extends RuleOptimizationProblem {

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

	 public void normObjective(EvolutionState state, Individual indi, int subpopulation, int threadnum) {

		 GPRule sequencingRule = new GPRule(RuleType.SEQUENCING, ((GPIndividual) indi).trees[0]);
		 GPRule routingRule = new GPRule(RuleType.ROUTING, ((GPIndividual) indi).trees[1]);

		 List rules = new ArrayList();
		 List fitnesses = new ArrayList();

		 rules.add(sequencingRule);
		 rules.add(routingRule);

		 fitnesses.add(indi.fitness);

		 evaluationModel.normObjective(fitnesses, rules, state);
	 }


	public void evaluate(EvolutionState state, Individual indi, int subpopulation, int threadnum) {

		//GPRule rule = new GPRule(RuleType.SEQUENCING, ((GPIndividual) indi).trees[0]);

		//modified by fzhang 23.5.2018  read two rules from one individual
		GPRule sequencingRule;
		GPRule routingRule;
		if(((GPIndividual) indi).trees.length == 1){
			sequencingRule = null;
			routingRule = new GPRule(RuleType.ROUTING, ((GPIndividual) indi).trees[0]);
		}
		else{
			sequencingRule = new GPRule(RuleType.SEQUENCING, ((GPIndividual) indi).trees[0]);
			routingRule = new GPRule(RuleType.ROUTING, ((GPIndividual) indi).trees[1]);
		}


		List rules = new ArrayList();
		List fitnesses = new ArrayList();

		//rules.add(rule);
		//modified by fzhang  to save two rules for evaluating from one individual
		rules.add(sequencingRule);
		rules.add(routingRule);

		fitnesses.add(indi.fitness);

		evaluationModel.evaluate(fitnesses, rules, state);

		indi.evaluated = true;
	}
}
