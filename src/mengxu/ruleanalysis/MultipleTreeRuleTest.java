package mengxu.ruleanalysis;

import ec.gp.GPNode;
import ec.multiobjective.MultiObjectiveFitness;
import mengxu.rule.AbstractRule;
import mengxu.rule.evolved.GPRule;
import mengxu.taskscheduling.Objective;
import mengxu.taskscheduling.SchedulingSet;
import mengxu.rule.RuleType;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultipleTreeRuleTest {

	 	public static final long simSeed = 968356;

		protected String trainPath; //the directory of training things
	    protected RuleTypeV2 ruleType;
	    protected int numRuns;
	    protected String testScenario;
	    protected String testSetName;
	    protected List<Objective> objectives; // The objectives to test.
	    protected int numTrees;
	    
	    public MultipleTreeRuleTest(String trainPath, RuleTypeV2 ruleType, int numRuns,
                                    String testScenario, String testSetName,
                                    List<Objective> objectives, int numTrees) {
	        this.trainPath = trainPath;
	        this.ruleType = ruleType;
	        this.numRuns = numRuns;
	        this.testScenario = testScenario;
	        this.testSetName = testSetName;
	        this.objectives = objectives;
	        this.numTrees = numTrees;
	    }

	    public MultipleTreeRuleTest(String trainPath, RuleTypeV2 ruleType, int numRuns,
                                    String testScenario, String testSetName, int numTreess) {
	        this(trainPath, ruleType, numRuns, testScenario, testSetName, new ArrayList<>(), numTreess);
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

		public void writeToCSV(String workflowScale) {
	        SchedulingSet testSet = generateTestSet(workflowScale);
	        File targetPath = new File(trainPath + "test"); //create a folder named "test" in trainPath
	        if (!targetPath.exists()) {
	            targetPath.mkdirs();
	        }

	        File csvFile = new File(targetPath + "/" + testSetName + ".csv"); //create a .csv to save the test result

	        List<TestResult> testResults = new ArrayList<>();

	        //for test: which machines are choosen by routing rule, CCGP. Scenario: run1, rule in generaiton 51 numRuns
	        for (int i = 0; i < numRuns; i++) {
	        	System.out.println("Run "+ i);

	        //for (int i = 0; i < 1; i++) {
	            File sourceFile = new File(trainPath + "job." + i + ".out.stat");  //this file keeps the rule
	            TestResult result = MultipleTreeTestResult.readFromFile(sourceFile, ruleType, numTrees);

	            //Didn't bother saving time files
	            File timeFile = new File(trainPath + "job." + i + ".time.csv");
	            result.setGenerationalTimeStat(MultipleTreeResultFileReader.readTimeFromFile(timeFile));

//				//Didn't bother saving time files
//				File diversityFile = new File(trainPath + "job." + i + ".diversities.csv");
//				result.setGenerationalGenotypeDiversityStat(ResultFileReader.readDiversitiesFromFile(diversityFile,1));
//				result.setGenerationalPhenotypeDiversityStat(ResultFileReader.readDiversitiesFromFile(diversityFile,2));
//				result.setGenerationalEntropyDiversityStat(ResultFileReader.readDiversitiesFromFile(diversityFile,3));
//				result.setGenerationalPseudoIsomorphsDiversityStat(ResultFileReader.readDiversitiesFromFile(diversityFile,4));
//				result.setGenerationalEditOneDiversityStat(ResultFileReader.readDiversitiesFromFile(diversityFile,5));
//				result.setGenerationalEditTwoDiversityStat(ResultFileReader.readDiversitiesFromFile(diversityFile,6));
//				result.setGenerationalPCDiversityStat(ResultFileReader.readDiversitiesFromFile(diversityFile,7));



				//24.8.2018 fzhang read badrun in CSV
	      /*      File badrunsFile = new File(trainPath + "job." + i + ".BadRun.csv");
	            result.setGenerationalBadRunStat(MultipleTreeResultFileReader.readBadRunFromFile(badrunsFile));*/

	            long start = System.currentTimeMillis();

//	            result.validate(objectives);

	            //for (int j = 42; j < result.getGenerationalRules().size(); j++) {
	             for (int j = 0; j < result.getGenerationalRules().size(); j++) {
	                AbstractRule[] generationalRules = result.getGenerationalRules(j);
	                if (numTrees == 2) {
	                    generationalRules[0].calcFitness(  //in calcFitness(), it will check which one is routing/sequencing rule
	                            result.getGenerationalTestFitness(j), null,
	                            testSet, generationalRules[1], objectives);
	                }
	                //generationalRules[1] is routing rule

	                System.out.println("Generation " + j + ": test fitness = " +
	                        result.getGenerationalTestFitness(j).fitness());
	            }

	            long finish = System.currentTimeMillis();
	            long duration = (finish - start)/1000;
	            System.out.println("Duration = " + duration + " s.");

	            testResults.add(result);
	        }

	        try {
	            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile()));
	            /*writer.write("Run,Generation,SeqRuleSize,SeqRuleUniqueTerminals,RoutRuleSize," +
	                    "RoutRuleUniqueTerminals,Obj,TrainFitness,TestFitness, TrainTime, BadRun");*/
	            
	            writer.write("Run,Generation,SeqRuleSize,SeqRuleUniqueTerminals,RoutRuleSize," +
	                    "RoutRuleUniqueTerminals,Obj,TrainFitness,TestFitness,TrainTime");
//				writer.write("Run,Generation,SeqRuleSize,SeqRuleUniqueTerminals,RoutRuleSize," +
//						"RoutRuleUniqueTerminals,Obj,TrainFitness,TestFitness,TrainTime," +
//						"GenotypeDiversity, PhenotypeDiversity, EntropyDiversity, PseudoIsomorphsDiversity," +
//						"EditOneDiversity, EditTwoDiversity");
	            writer.newLine();

	            //for (int i = 0; i < 1; i++) {
	            for (int i = 0; i < numRuns; i++) {
	                TestResult result = testResults.get(i);

	                //for (int j = 42; j < result.getGenerationalRules().size(); j++) { //use rules in each generation for testing
	                for (int j = 0; j < result.getGenerationalRules().size(); j++) { //use rules in each generation for testing

	                    MultiObjectiveFitness trainFit =
	                            (MultiObjectiveFitness)result.getGenerationalTrainFitness(j);
	                    MultiObjectiveFitness testFit =
	                            (MultiObjectiveFitness)result.getGenerationalTestFitness(j);
	                    GPRule[] rules = (GPRule[]) result.getGenerationalRules(j);
	                    GPRule seqRule = null;
	                    GPRule routRule = null;
	                    if (numTrees == 2) {
	                        if (rules[0].getType() == RuleType.SEQUENCING) {
	                            seqRule = rules[0];
	                            routRule = rules[1];
	                        } else {
	                            seqRule = rules[1];
	                            routRule = rules[0];
	                        }
	                    } else {
	                        seqRule = rules[0];
	                    }


	                    UniqueTerminalsGatherer gatherer = new UniqueTerminalsGatherer();
	                    int numUniqueTerminalsSeq = seqRule.getGPTree().child.numNodes(gatherer);
	                    int seqRuleSize = seqRule.getGPTree().child.numNodes(GPNode.NODESEARCH_ALL);

	                    int numUniqueTerminalsRout = 0;
	                    int routRuleSize = 0;
	                    if (numTrees == 2) {
	                        gatherer = new UniqueTerminalsGatherer();
	                        numUniqueTerminalsRout = routRule.getGPTree().child.numNodes(gatherer);
	                        routRuleSize = routRule.getGPTree().child.numNodes(GPNode.NODESEARCH_ALL);
	                    }

	                  /*  if (objectives.size() == 1) {
	                        writer.write(i + "," + j + "," +
	                                seqRuleSize + "," +
	                                numUniqueTerminalsSeq + "," +
	                                routRuleSize +"," +
	                                numUniqueTerminalsRout +",0," +
	                                trainFit.fitness() + "," +
	                                testFit.fitness()+ ","+ 
	                                result.getGenerationalTime(j)+ ","+
	                                result.getGenerationalBadRun(j));
	                        writer.newLine();
	                    }*/
	                    
	                    if (objectives.size() == 1) {
	                        writer.write(i + "," + j + "," +
	                                seqRuleSize + "," +
	                                numUniqueTerminalsSeq + "," +
	                                routRuleSize +"," +
	                                numUniqueTerminalsRout +",0," +
	                                trainFit.fitness() + "," +
	                                testFit.fitness()+ ","+ 
	                                result.getGenerationalTime(j));
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
//	                        writer.write(i + "," + j + "," +
//	                                rule.getGPTree().child.numNodes(GPNode.NODESEARCH_ALL) + "," +
//	                                numUniqueTerminals + ",");

	                        for (int k = 0; k < objectives.size(); k++) {
	                            writer.write(k + "," +
	                                    trainFit.getObjective(k) + "," +
	                                    testFit.getObjective(k) + ",");
	                        }
	                        writer.newLine();
	                    }
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
			MultipleTreeRuleTest multipletreeruleTest = new MultipleTreeRuleTest(trainPath, ruleType, numRuns, testScenario, testSetName, numTrees);

			for (int i = 0; i < numObjectives; i++) {
				multipletreeruleTest.addObjective(args[idx]);
				idx ++;
			}
			String workflowScale = "small";
			multipletreeruleTest.writeToCSV(workflowScale);
		}
}
