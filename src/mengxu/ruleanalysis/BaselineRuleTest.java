package mengxu.ruleanalysis;

import ec.Fitness;
import ec.gp.GPNode;
import ec.multiobjective.MultiObjectiveFitness;
import mengxu.algorithm.FCFS;
import mengxu.algorithm.HEFT;
import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.rule.evolved.GPRule;
import mengxu.taskscheduling.Objective;
import mengxu.taskscheduling.SchedulingSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaselineRuleTest {

	 	public static final long simSeed = 968356;

		protected String trainPath; //the directory of training things
	    protected RuleTypeV2 ruleType;
	    protected int numRuns;
	    protected String testScenario;
	    protected String testSetName;
	    protected List<Objective> objectives; // The objectives to test.
	    protected int numTrees;
	    protected AbstractRule baselineSequencingRule;
		protected AbstractRule baselineRoutingRule;

	    public BaselineRuleTest(String trainPath, RuleTypeV2 ruleType, int numRuns,
                                String testScenario, String testSetName,
                                List<Objective> objectives, int numTrees,
								AbstractRule baselineSequencingRule,
								AbstractRule baselineRoutingRule) {
	        this.trainPath = trainPath;
	        this.ruleType = ruleType;
	        this.numRuns = numRuns;
	        this.testScenario = testScenario;
	        this.testSetName = testSetName;
	        this.objectives = objectives;
	        this.numTrees = numTrees;
	        this.baselineSequencingRule = baselineSequencingRule;
	        this.baselineRoutingRule = baselineRoutingRule;
	    }

	    public BaselineRuleTest(String trainPath, RuleTypeV2 ruleType, int numRuns,
                                String testScenario, String testSetName, int numTreess,
								AbstractRule baselineSequencingRule,
								AbstractRule baselineRoutingRule) {
	        this(trainPath, ruleType, numRuns, testScenario, testSetName,
					new ArrayList<>(), numTreess, baselineSequencingRule,baselineRoutingRule);
	    }

	    public String getTrainPath() {
	        return trainPath;
	    }

	    public RuleTypeV2 getRuleType() {
	        return ruleType;
	    }

	    public int getNumRuns() {
	        return numRuns;
	    }

	    public int getnumTrees() { return numTrees; }

	    public String getTestScenario() {
	        return testScenario;
	    }

	    public List<Objective> getObjectives() {
	        return objectives;
	    }

	    public void setObjectives(List<Objective> objectives) {
			this.objectives = objectives;
		}

		public void addObjective(Objective objective) {
			this.objectives.add(objective);
		}

		public void addObjective(String objective) {
			addObjective(Objective.get(objective));
		}

		//generate testset using simseed, replications
		public SchedulingSet generateTestSet() {
	        return SchedulingSet.generateSet(simSeed, objectives, 30);
	    }

		public void writeToCSV() {
	        SchedulingSet testSet = generateTestSet();
	        File targetPath = new File(trainPath + "test"); //create a folder named "test" in trainPath
	        if (!targetPath.exists()) {
	            targetPath.mkdirs();
	        }

	        File csvFile = new File(targetPath + "/" + testSetName + ".csv"); //create a .csv to save the test result

//	        List<TestResult> testResults = new ArrayList<>();
			Fitness fitness = new MultiObjectiveFitness();
			this.baselineSequencingRule.calcFitness(  //in calcFitness(), it will check which one is routing/sequencing rule
					fitness, null,
					testSet, this.baselineRoutingRule, objectives);


	        try {
	            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile()));
	            /*writer.write("Run,Generation,SeqRuleSize,SeqRuleUniqueTerminals,RoutRuleSize," +
	                    "RoutRuleUniqueTerminals,Obj,TrainFitness,TestFitness, TrainTime, BadRun");*/
	            
	            writer.write("Run,TestFitness");
	            writer.newLine();

	            //for (int i = 0; i < 1; i++) {
	            for (int i = 0; i < numRuns; i++) {
					if (objectives.size() == 1) {
						writer.write(i + "," + fitness.fitness());
//									result.getGenerationalGenotypeDiversityStat(j)+ ","+
//									result.getGenerationalPhenotypeDiversityStat(j)+ ","+
//									result.getGenerationalEntropyDiversityStat(j)+ ","+
//									result.getGenerationalPseudoIsomorphsDiversityStat(j)+ ","+
//									result.getGenerationalEditOneDiversityStat(j)+ ","+
//									result.getGenerationalEditTwoDiversityStat(j));
//									result.getGenerationalPCDiversityStat(j));
						writer.newLine();
					}
					else {
//	                      //todo: need modified for multi-objective
					}
	            }
	            writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }


	    /**
	     * Call this main method with several parameters
	     *
	     * /Users/dyska/Desktop/Uni/COMP489/GPJSS/grid_results/dynamic/raw/coevolution-fixed/0.85-max-flowtime/
	     * simple-rule
	     * 30
	     * null
	     * result
	     * 2
	     * 1
	     * max-flowtime
	     */
		public static void main(String[] args) {
			int idx = 0;
			String trainPath = args[idx];
	        idx ++;
	        RuleTypeV2 ruleType = RuleTypeV2.get(args[idx]);
			idx ++;
	        int numRuns = Integer.valueOf(args[idx]); //30
	        idx ++;
	        String testScenario = args[idx]; //dynamic
	        idx ++;
	        String testSetName = args[idx]; //missing-0.85-4.0
	        idx ++;
	        int numTrees = Integer.valueOf(args[idx]); //2
	        idx ++;
			int numObjectives = Integer.valueOf(args[idx]); //1
			idx ++;

			//RuleTest ruleTest = new RuleTest(trainPath, ruleType, numRuns, testScenario, testSetName, numTrees);
			//modified by fzhang  24.5.2018  use multipleTreeRuleTest
			AbstractRule baselineSequencingRule = new HEFT(RuleType.SEQUENCING);
			AbstractRule baselineRoutingRule = new HEFT(RuleType.ROUTING);
			BaselineRuleTest multipletreeruleTest = new BaselineRuleTest(trainPath, ruleType, numRuns, testScenario, testSetName, numTrees, baselineSequencingRule, baselineRoutingRule);

			for (int i = 0; i < numObjectives; i++) {
				multipletreeruleTest.addObjective(args[idx]);
				idx ++;
			}

			multipletreeruleTest.writeToCSV();
		}
}
