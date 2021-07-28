package mengxu.ruleanalysis;

import java.io.File;

public class MultipleTreeTestResult extends TestResult{
	public static TestResult readFromFile(File file, RuleTypeV2 ruleTypeV2, int numTrees) {

		// modified by fzhang 24.5.2018   for multiple trees of one individual
		return MultipleTreeResultFileReader.readTestResultFromFile(file, ruleTypeV2.isMultiobjective(),
				numTrees);
	}

	public static TestResult readMOFromFile(File file, RuleTypeV2 ruleTypeV2, int numTrees) {

		// modified by fzhang 24.5.2018   for multiple trees of one individual
		return MultipleTreeResultFileReader.readTestResultFromFile(file, true,
				numTrees);
	}

//	public static TestResult readEnsembleFromFile(File file, RuleType ruleType, int numTrees){
//		return MultipleTreeResultFileReader.readEnsembleTestResultFromFile(file, ruleType.isMultiobjective(),
//				numTrees);
//	}
}
