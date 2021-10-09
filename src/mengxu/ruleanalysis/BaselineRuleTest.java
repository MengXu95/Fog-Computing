package mengxu.ruleanalysis;

import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import mengxu.algorithm.*;
import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
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
		protected List<AbstractRule> baselineSequencingRuleList;
		protected List<AbstractRule> baselineRoutingRuleList;
		protected List<String> ruleNameList;

//	    protected AbstractRule baselineSequencingRule;
//		protected AbstractRule baselineRoutingRule;
//		protected String ruleName;

	    public BaselineRuleTest(String trainPath, RuleTypeV2 ruleType, int numRuns,
                                String testScenario, String testSetName,
                                List<Objective> objectives, int numTrees,
								List<AbstractRule> baselineSequencingRuleList,
								List<AbstractRule> baselineRoutingRuleList) {
	        this.trainPath = trainPath;
	        this.ruleType = ruleType;
	        this.numRuns = numRuns;
	        this.testScenario = testScenario;
	        this.testSetName = testSetName;
	        this.objectives = objectives;
	        this.numTrees = numTrees;
	        this.baselineSequencingRuleList = baselineSequencingRuleList;
	        this.baselineRoutingRuleList = baselineRoutingRuleList;
//	        this.ruleName = this.baselineRoutingRule.getName();
	    }

	    public BaselineRuleTest(String trainPath, RuleTypeV2 ruleType, int numRuns,
                                String testScenario, String testSetName, int numTreess,
								List<AbstractRule> baselineSequencingRuleList,
								List<AbstractRule> baselineRoutingRuleList) {
	        this(trainPath, ruleType, numRuns, testScenario, testSetName,
					new ArrayList<>(), numTreess, baselineSequencingRuleList,baselineRoutingRuleList);
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
		public SchedulingSet generateTestSet(String workflowScale) {
	        return SchedulingSet.generateSet(simSeed, objectives, workflowScale, 30);
	    }

		public void MultiRuleWriteToCSV(String workflowScale) {
	    	for(int i=0; i<this.baselineSequencingRuleList.size(); i++){
	    		AbstractRule baselineSequencingRule = this.baselineSequencingRuleList.get(i);
				AbstractRule baselineRoutingRule = this.baselineRoutingRuleList.get(i);
				String ruleName = baselineSequencingRule.getName();
				writeToCSV(workflowScale,ruleName,baselineSequencingRule,baselineRoutingRule);
			}
		}

		public void writeToCSV(String workflowScale, String ruleName, AbstractRule baselineSequencingRule, AbstractRule baselineRoutingRule) {
	        SchedulingSet testSet = generateTestSet(workflowScale);
	        File targetPath = new File(trainPath + "/" + ruleName +"/test"); //create a folder named "test" in trainPath
	        if (!targetPath.exists()) {
	            targetPath.mkdirs();
	        }

	        File csvFile = new File(targetPath + "/" + testSetName + ".csv"); //create a .csv to save the test result

//	        List<TestResult> testResults = new ArrayList<>();
			Fitness fitness = new MultiObjectiveFitness();
			baselineSequencingRule.calcFitness(  //in calcFitness(), it will check which one is routing/sequencing rule
					fitness, null,
					testSet, baselineRoutingRule, objectives);


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
//			AbstractRule baselineSequencingRule1 = new HEFT(RuleType.SEQUENCING);
//			AbstractRule baselineRoutingRule1 = new HEFT(RuleType.ROUTING);
//			AbstractRule baselineSequencingRule2 = new FCFS(RuleType.SEQUENCING);
//			AbstractRule baselineRoutingRule2 = new FCFS(RuleType.ROUTING);
//			AbstractRule baselineSequencingRule3 = new MaxMin(RuleType.SEQUENCING);
//			AbstractRule baselineRoutingRule3 = new MaxMin(RuleType.ROUTING);
//			AbstractRule baselineSequencingRule4 = new MinMin(RuleType.SEQUENCING);
//			AbstractRule baselineRoutingRule4 = new MinMin(RuleType.ROUTING);
			AbstractRule baselineSequencingRule5 = new RoundRobin(RuleType.SEQUENCING);
			AbstractRule baselineRoutingRule5 = new RoundRobin(RuleType.ROUTING);

			List<AbstractRule> baselineSequencingRuleList = new ArrayList<>();
//			baselineSequencingRuleList.add(baselineSequencingRule1);
//			baselineSequencingRuleList.add(baselineSequencingRule2);
//			baselineSequencingRuleList.add(baselineSequencingRule3);
//			baselineSequencingRuleList.add(baselineSequencingRule4);
			baselineSequencingRuleList.add(baselineSequencingRule5);

			List<AbstractRule> baselineRoutingRuleList = new ArrayList<>();
//			baselineRoutingRuleList.add(baselineRoutingRule1);
//			baselineRoutingRuleList.add(baselineRoutingRule2);
//			baselineRoutingRuleList.add(baselineRoutingRule3);
//			baselineRoutingRuleList.add(baselineRoutingRule4);
			baselineRoutingRuleList.add(baselineRoutingRule5);

			BaselineRuleTest multipletreeruleTest = new BaselineRuleTest(trainPath, ruleType, numRuns, testScenario, testSetName, numTrees, baselineSequencingRuleList, baselineRoutingRuleList);

			for (int i = 0; i < numObjectives; i++) {
				multipletreeruleTest.addObjective(args[idx]);
				idx ++;
			}

			String workflowScale = "hybird-small-middle-large";
			multipletreeruleTest.MultiRuleWriteToCSV(workflowScale);
		}
}
