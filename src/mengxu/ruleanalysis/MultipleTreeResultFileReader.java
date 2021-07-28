package mengxu.ruleanalysis;

import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import mengxu.rule.evolved.GPRule;
import mengxu.rule.RuleType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultipleTreeResultFileReader extends ResultFileReader {

	public static TestResult readTestResultFromFile(File file, boolean isMultiObjective,
			int numTrees) {
		TestResult result = new TestResult();

		String line;
		Fitness fitnesses;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while (!(line = br.readLine()).equals("Best Individual of Run:")) {
//				while (!(line = br.readLine()).equals(" PARETO FRONTS")) {
				
				if (line.startsWith("Generation")) {
					br.readLine(); // Best individual:

					GPRule sequencingRule;
					GPRule routingRule;

					br.readLine(); // Subpopulation 0:
					br.readLine(); // Evaluated: true

					line = br.readLine(); // read in fitness on following line
					fitnesses = readFitnessFromLine(line, isMultiObjective);

					br.readLine(); // tree 0
					line = br.readLine(); // this is a sequencing rule

					// sequencing rule
//					line = LispSimplifier.simplifyExpression(line);
					sequencingRule = GPRule.readFromLispExpression(RuleType.SEQUENCING, line);

					// routing rule
					br.readLine();
					line = br.readLine();
//					line = LispSimplifier.simplifyExpression(line);
					routingRule = GPRule.readFromLispExpression(RuleType.ROUTING, line);

					Fitness fitness = fitnesses;
					GPRule[] bestRules = new GPRule[numTrees];

					bestRules[0] = sequencingRule; // sequencing rule
					bestRules[1] = routingRule; // routing rule

					result.setBestRules(bestRules);
					result.setBestTrainingFitness(fitness);

					result.addGenerationalRules(bestRules);
					result.addGenerationalTrainFitness(fitness);
//					result.addGenerationalValidationFitnesses((Fitness) fitness.clone());
					result.addGenerationalTestFitnesses((Fitness) fitness.clone());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	//modified by mengxu 2021.05.08 add ensemble result
	public static TestResult readEnsembleTestResultFromFile(File file, RuleType ruleType, boolean isMultiObjective,
													int numTrees) {
		TestResult result = new TestResult();

		String line;
		Fitness fitnesses;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while (!(line = br.readLine()).equals("End")) {
//				while (!(line = br.readLine()).equals(" PARETO FRONTS")) {

				if (line.startsWith("Generation")) {
					br.readLine(); // Ensemble size: 5

					GPRule sequencingRule;
					GPRule routingRule;
					List<GPRule> ensembleSequencingRule = new ArrayList<>();
					List<GPRule> ensembleRoutingRule = new ArrayList<>();
					List<Fitness> ensembleTrainFitness = new ArrayList<>();
					List<Fitness> ensembleValidationFitness = new ArrayList<>();


					int ensembleSize = Integer.parseInt(br.readLine());
					for(int i=0; i<ensembleSize;i++){
						br.readLine(); // Member: 0

						line = br.readLine(); // read in fitness on following line
						fitnesses = readFitnessFromLine(line, isMultiObjective);

						br.readLine(); // tree 0
						line = br.readLine(); // this is a sequencing rule
						sequencingRule = GPRule.readFromLispExpression(RuleType.SEQUENCING, line);

						// routing rule
						br.readLine();
						line = br.readLine();
						routingRule = GPRule.readFromLispExpression(RuleType.ROUTING, line);

						Fitness trainFitness = fitnesses;
						Fitness validationFitness = (Fitness)fitnesses.clone();
						Fitness testFitness = (Fitness)fitnesses.clone();

						ensembleSequencingRule.add(sequencingRule);
						ensembleRoutingRule.add(routingRule);

						ensembleTrainFitness.add(trainFitness);
						ensembleValidationFitness.add(validationFitness);
						Fitness ensembleTestFitness = testFitness;
//						result.addGenerationalEnsembleTestFitnesses(ensembleTestFitness);
					}

//					EnsembleRule ensembleRule = new EnsembleRule(ensembleSequencingRule, ensembleRoutingRule);
//
//					result.setEnsembleRule(ensembleRule);
//
//					result.addGenerationalEnsembleRules(ensembleRule);
//					result.addGenerationalEnsembleTrainFitness(ensembleTrainFitness);
//					result.addGenerationalEnsembleTestFitnesses(ensembleTestFitness);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private static Fitness readFitnessFromLine(String line, boolean isMultiobjective) {
		if (isMultiobjective) {
			return parseFitness(line);//modified by mengxu 2021.05.31
			// TODO read multi-objective fitness line
//			String[] spaceSegments = line.split("\\s+");
//			String[] equation = spaceSegments[1].split("=");
//			double fitness = Double.valueOf(equation[1]);
//			KozaFitness f = new KozaFitness();
//			f.setStandardizedFitness(null, fitness);
//
//			return f;
		} else {
			String[] spaceSegments = line.split("\\s+");
			String[] fitVec = spaceSegments[1].split("\\[|\\]");
			double fitness = Double.valueOf(fitVec[1]);
			MultiObjectiveFitness f = new MultiObjectiveFitness();
			f.objectives = new double[1];
			f.objectives[0] = fitness;

			return f;
		}
	}

	   //24.8.2018  fzhang read badrun into CSV
    public static DescriptiveStatistics readBadRunFromFile(File file) {
        DescriptiveStatistics generationalBadRunStat = new DescriptiveStatistics();

        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while(true) {
                line = br.readLine();

                if (line == null)
                    break;

                String[] commaSegments = line.split(",");
                generationalBadRunStat.addValue(Double.valueOf(commaSegments[1])); //read from excel, the first column is 0
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return generationalBadRunStat;
    }

    //modified by mengxu 2021.05.31
	private static MultiObjectiveFitness parseFitness(String line)
	{
		String[] spaceSegments = line.split("\\s+");//\\s��ʾ   �ո�,�س�,���еȿհ׷�, +�ű�ʾһ����������˼
		MultiObjectiveFitness f = new MultiObjectiveFitness();
		f.objectives = new double[spaceSegments.length - 1];
		for(int i = 1; i < spaceSegments.length; i++)
		{
			String[] equation = spaceSegments[i].split("\\[|\\]");
			double fitness = Double.valueOf(equation[i == 1 ? 1 : 0]);
			f.objectives[i-1] = fitness;
		}

		return f;
//		String[] equation1 = spaceSegments[1].split("\\[|\\]");
//		String[] equation2 = spaceSegments[2].split("\\[|\\]");
//		double fitness1 = Double.valueOf(equation1[1]);
//		double fitness2 = Double.valueOf(equation2[0]);
////		MultiObjectiveFitness f = new MultiObjectiveFitness();
////		f.objectives = new double[2];
//		f.objectives[0] = fitness1;
//		f.objectives[1] = fitness2;
	}
    
}
