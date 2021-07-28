package mengxu.ruleevaluation;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import mengxu.rule.AbstractRule;
import mengxu.simulation.DynamicSimulation;
import mengxu.taskscheduling.Server;
//import yimei.jss.jobshop.WorkCenter;
//import yimei.jss.rule.AbstractRule;
//import yimei.jss.simulation.Simulation;

import java.util.List;

public class MultipleTreeMultipleRuleEvaluationModel extends MultipleRuleEvaluationModel{
	  //========================rule evaluatation============================
    @Override
    public void evaluate(List<Fitness> currentFitnesses,
                         List<AbstractRule> rules,
                         EvolutionState state) {
        //expecting 2 rules here - one routing rule and one sequencing rule
        if (rules.size() != 2) {
            System.out.println("Rule evaluation failed!");
            System.out.println("Expecting 2 rules, only 1 found.");
            return;
        }

        //System.out.println(rules.size()); //2 repeat
        countInd++;

        AbstractRule sequencingRule = rules.get(0); // for each arraylist in list, they have two elements, the first one is sequencing rule and the second one is routing rule
        AbstractRule routingRule = rules.get(1);
        //System.out.println(sequencingRule);  //"GPRule"  repeat
        //System.out.println(routingRule);   //"GPRule"  repeat

        //System.out.println(objectives.size()); //1  repeat
        //code taken from Abstract Rule
        double[] fitnesses = new double[objectives.size()];

        List<DynamicSimulation> simulations = schedulingSet.getSimulations();
        int col = 0;

        //System.out.println(simulations.size()); // 1 repeat
        //System.out.println(schedulingSet.getReplications().get(0)); //1 repeat

        for (int j = 0; j < simulations.size(); j++) {
            DynamicSimulation simulation = simulations.get(j);

            //========================change here======================================
            simulation.setSequencingRule(sequencingRule); //indicate different individuals
            simulation.setRoutingRule(routingRule);
            //System.out.println(simulation);
            simulation.run();

            for (int i = 0; i < objectives.size(); i++) {
            	//2018.10.23  cancel normalized process
//                double normObjValue = simulation.objectiveValue(objectives.get(i))  // this line: the value of makespan
//                        / schedulingSet.getObjectiveLowerBound(i, col);

            	double ObjValue = simulation.objectiveValue(objectives.get(i)); // this line: the value of makespan
                
            	
                //in essence, here is useless. because if w.numOpsInQueue() > 100, the simulation has been canceled in run(). here is a double check
                for (Server w: simulation.getSystemState().getServers()) {
                    if (w.numTaskInQueue() > 100) {
                        //this was a bad run

                    	//fzhang cancel normalized process
//                      normObjValue = Double.MAX_VALUE;
                    	ObjValue = Double.MAX_VALUE;

                    	//System.out.println(systemState.getJobsInSystem().size());
                    	//System.out.println(systemState.getJobsCompleted().size());

                    	//normObjValue = normObjValue*(systemState.getJobsInSystem().size()/systemState.getJobsCompleted().size());
                        countBadrun++;
                        break;
                    }
                }

              //2018.10.23  cancel normalized process
//                fitnesses[i] += normObjValue;  //the value of fitness is the normalization of the objective value
                fitnesses[i] += ObjValue;  
            }
            col++;

            //schedulingSet.getReplications().get(j) = 1, only calculate once, skip this part here
            for (int k = 1; k < schedulingSet.getReplications().get(j); k++) {
                simulation.rerun();

                for (int i = 0; i < objectives.size(); i++) {
                	  //2018.10.23  cancel normalized process
//                    double normObjValue = simulation.objectiveValue(objectives.get(i))
//                            / schedulingSet.getObjectiveLowerBound(i, col);
//                    fitnesses[i] += normObjValue;
                	
                	double ObjValue = simulation.objectiveValue(objectives.get(i));
                    fitnesses[i] += ObjValue;
                }

                col++;
            } 

            simulation.reset();
        }

      //modified by fzhang 18.04.2018  in order to check this loop works or not after add filter part: does not work
       // if(countBadrun>0) {
        //System.out.println(state.generation);
     	//System.out.println("The number of badrun grasped in model: "+ countBadrun);
       // }

        for (int i = 0; i < fitnesses.length; i++) {
            fitnesses[i] /= col;
        }

        //System.out.println(currentFitnesses.size()); //1
        for (Fitness fitness: currentFitnesses) {
            MultiObjectiveFitness f = (MultiObjectiveFitness) fitness;
            f.setObjectives(state, fitnesses);
        }

        //modified by fzhang 23.5.2018  save bad run information for one population
        //if(countInd % 1024 == 0) {
   /*     if(countInd % state.population.subpops[0].individuals.length == 0) {
            genNumBadRun.add(countBadrun);
            countBadrun = 0;
           }

        //if(countInd == 1024*51)
        if(countInd == state.population.subpops[0].individuals.length*state.numGenerations)
            WriteCountBadrun(state,null);*/
    }

    //modified by fzhang 26.4.2018   write bad run times to *.csv
/*    public void WriteCountBadrun(EvolutionState state, final Parameter base) {
    	Parameter p;
		// Get the job seed.
		p = new Parameter("seed").push(""+0);
        jobSeed = state.parameters.getLongWithDefault(p, null, 0);
        File countBadRunFile = new File("job." + jobSeed + ".BadRun.csv");

     	try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(countBadRunFile));
			writer.write("generation,numTotalBadRun");
			writer.newLine();
            for(int cutPoint = 0; cutPoint < genNumBadRun.size(); cutPoint++) {
   	 	        writer.write(cutPoint + "," +genNumBadRun.get(cutPoint));
   		    writer.newLine();
            }
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }*/
}
