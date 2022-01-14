package mengxu.algorithm.singletreegp;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleStatisticsSaveSingleRulesizeGen extends SimpleStatistics{

	 //get seed
    protected long jobSeed;
	
    //mengxu 2022.01.14 in order to save the routingrulesize in each generation
    List<Long> aveRouRulesizeTree1 = new ArrayList<>();
    
    /** GENERATIONAL: Called immediately before evaluation occurs. */
    public void preEvaluationStatistics(final EvolutionState state)
        {
    	for(int x=0;x<children.length;x++)
            children[x].preEvaluationStatistics(state);
        
    	//fzhang 17.6.2018  get the seed value
    	Parameter p;
  		// Get the job seed.
  		p = new Parameter("seed").push(""+0);
        jobSeed = state.parameters.getLongWithDefault(p, null, 0);
          
        // fzhang 15.6.2018 1. save the individual size in population
 		// 2. calculate the average size of individuals in population
 		// check the average size of sequencing and routing rules in population
        //fzhang 15.6.2018  in order to check the average size of sequencing and routing rules in population
			long aveRouSizeTree1 = 0;
			//int indSizePop = 0; // in order to check whether SeqSizePop1 and RouSizePop2 are calculated correctly
			// should be the sum of SeqSizePop1 and RouSizePop2
			for (int subpop = 0; subpop < state.population.subpops.length; subpop++) {
				int RouSizeTree1 = 0;
				for (int inds = 0; inds < state.population.subpops[subpop].individuals.length; inds++) {
					GPIndividual indi = (GPIndividual) state.population.subpops[subpop].individuals[inds];
					RouSizeTree1 += indi.trees[0].child.numNodes(GPNode.NODESEARCH_ALL);
				}
				aveRouSizeTree1 = RouSizeTree1 / state.population.subpops[subpop].individuals.length;

				aveRouRulesizeTree1.add(aveRouSizeTree1);
			}
		
		if(state.generation == state.numGenerations-1) {
			//fzhang  15.6.2018  save the size of rules in each generation
		    File rulesizeFile = new File("job." + jobSeed + ".aveGenRulesize.csv"); // jobSeed = 0
		    
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(rulesizeFile));
				writer.write("Gen,aveSeqRuleSize,aveRouRuleSize,avePairSize");
				writer.newLine();
				for (int gen = 0; gen < aveRouRulesizeTree1.size(); gen++) {
					writer.write(gen + "," + 1 + "," + aveRouRulesizeTree1.get(gen) + "," +
							(1 + aveRouRulesizeTree1.get(gen))/2);
					writer.newLine();
				}
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	 			
			/*System.out.println(SeqSizeTree0);
			System.out.println(RouSizeTree1);
			System.out.println(aveSeqRulesizeTree0.get(state.generation));
			System.out.println(aveRouRulesizeTree1.get(state.generation));*/
	 		
	 	//fzhang 15.6.2018 in order to check whether SeqSizePop1 and RouSizePop2 are calculated correctly (YES)
	 	/*	for (int pop = 0; pop < state.population.subpops.length; pop++) {
	 			for (int ind = 0; ind < state.population.subpops[pop].individuals.length; ind++) {
	 				indSizePop += state.population.subpops[pop].individuals[ind].size();
	 			}
	 		}
	 		System.out.println(indSizePop);*/
		}	
        }
	
}
