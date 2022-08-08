package mengxu.rule;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import mengxu.simulation.DynamicSimulation;
import mengxu.simulation.RoutingDecisionSituation;
import mengxu.simulation.SequencingDecisionSituation;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.*;

import java.util.List;

public abstract class AbstractRule {
    protected String name;
    protected RuleType type;


    public String getName() {
        return name;
    }

    public RuleType getType() {
        return type;
    }

    public TaskOption nextTaskOption(RoutingDecisionSituation routingDecisionSituation) {

        List<TaskOption> queue = routingDecisionSituation.getQueue();
        SystemState systemState = routingDecisionSituation.getSystemState();
        //================original=================
        //==================start==================
        TaskOption bestTaskOption = queue.get(0);

        bestTaskOption
                .setPriority(priority(bestTaskOption, bestTaskOption.getServer(), systemState));
        // loop all the options, save the best one as "selected" one
        for (int i = 1; i < queue.size(); i++) {
            TaskOption taskOption = queue.get(i);
            taskOption.setPriority(priority(taskOption, taskOption.getServer(), systemState));

            if (taskOption.priorTo(bestTaskOption)) {
                bestTaskOption = taskOption;
            }
        }
        return bestTaskOption;// this links which machine will be chosen.
    }

    public TaskOption priorTask(SequencingDecisionSituation sequencingDecisionSituation) {

        List<TaskOption> queue = sequencingDecisionSituation.getQueue();
        Server server = sequencingDecisionSituation.getServer();
        SystemState systemState = sequencingDecisionSituation.getSystemState();

        //fzhang 2018.10.23  original one
        //============================start==============================
        TaskOption priorTask = queue.get(0);
        priorTask.setPriority(priority(priorTask, server, systemState));

        for (int i = 1; i < queue.size(); i++) {
            TaskOption taskOption = queue.get(i);
            taskOption.setPriority(priority(taskOption, server, systemState));

            if (taskOption.priorTo(priorTask))
                priorTask = taskOption;
        }

        return priorTask;
    }

    public void calcFitness(Fitness fitness, EvolutionState state, SchedulingSet schedulingSet, AbstractRule otherRule,
                            List<Objective> objectives) {
        // whenever fitness is calculated, need a routing rule and a sequencing rule
        if (this.getType() == otherRule.getType()) {
            System.out.println(
                    "We need one routing rule and one sequencing rule, not 2 " + otherRule.getType() + " rules.");
            return;
        }
        AbstractRule routingRule;
        AbstractRule sequencingRule;
        // check type, not here
        if (this.getType() == RuleType.ROUTING) {
            routingRule = this;
            sequencingRule = otherRule;
        } else {
            routingRule = otherRule;
            sequencingRule = this;
        }

        double[] fitnesses = new double[objectives.size()];

        List<DynamicSimulation> simulations = schedulingSet.getSimulations();
//        int col = 0;
        //modified by mengxu 2021.08.27
        int[] col = new int[objectives.size()];

//        System.out.println("Run 0: ");
        //System.out.println("The simulation size is "+simulations.size()); //1
        for (int j = 0; j < simulations.size(); j++) {
            DynamicSimulation simulation = simulations.get(j);
            simulation.setSequencingRule(sequencingRule);
            simulation.setRoutingRule(routingRule);
            // }
            simulation.rerun();

            for (int i = 0; i < objectives.size(); i++) {
                // System.out.println("Makespan:
                // "+simulation.objectiveValue(objectives.get(i)));
                // System.out.println("Benchmark makespan:
                // "+schedulingSet.getObjectiveLowerBound(i, col));

                //fzhang 2018.10.23  cancel normalizing objective
//				double normObjValue = simulation.objectiveValue(objectives.get(i))
//						/ schedulingSet.getObjectiveLowerBound(i, col);

                double ObjValue = simulation.objectiveValue(objectives.get(i));
                //modified by mengxu 2021.08.27
                if(ObjValue >= Double.POSITIVE_INFINITY || ObjValue >= Double.MAX_VALUE){
                    System.out.println("bad 0 fitness: " + ObjValue);
                }else{
                    fitnesses[i] += ObjValue;
                    col[i]++;
                }
                //in essence, here is useless. because if w.numOpsInQueue() > 100, the simulation has been canceled in run(). here is a double check
//                for (Server s: simulation.getSystemState().getServers()) {
//                    if (s.numTaskInQueue() > 100) {
//                        simulation.getSystemState().setClockTime(Double.MAX_VALUE);
//                        simulation.getSystemState().eventQueue.clear();
//                        break;
//                    }
//                }

                //modified by fzhang, 26.4.2018  check in test process, whether there is ba
                //fzhang 2018.10.23  cancel normalizing objective
//				fitnesses[i] += normObjValue;

//                fitnesses[i] += ObjValue; //modified by mengxu 2021.08.27
            }

//            col++;

            //System.out.println("The value of replication is "+schedulingSet.getReplications()); //50
            for (int k = 1; k < schedulingSet.getReplications().get(j); k++) {
//                System.out.println("Run " + k + ": ");
//                simulation.rotateSeed();//modified by mengxu, add on 2021.08.26
                simulation.rerun();

                for (int i = 0; i < objectives.size(); i++) {
//					double normObjValue = simulation.objectiveValue(objectives.get(i))
//							/ schedulingSet.getObjectiveLowerBound(i, col);
//					fitnesses[i] += normObjValue;

                    //fzhang 2018.10.23  cancel normalizing objective
                    double ObjValue = simulation.objectiveValue(objectives.get(i));

                    //modified by mengxu 2021.08.27
                    if(ObjValue >= Double.POSITIVE_INFINITY || ObjValue >= Double.MAX_VALUE){
                        System.out.println("bad " + k +" fitness: " + ObjValue);
                    }
                    else{
                        fitnesses[i] += ObjValue;
                        col[i]++;
                    }

//                    for (WorkCenter w: simulation.getSystemState().getWorkCenters()) {
//                        if (w.numOpsInQueue() > 100) {
//                            //this was a bad run
//
//                            //fzhang cancel normalized process
////                      normObjValue = Double.MAX_VALUE;
//                            ObjValue = Double.MAX_VALUE;
//
//                            //System.out.println(systemState.getJobsInSystem().size());
//                            //System.out.println(systemState.getJobsCompleted().size());
//                            break;
//                        }
//                    }

//                    fitnesses[i] += ObjValue; //modified by mengxu 2021.08.27
                }

//                col++;
            }

            simulation.reset();
        }

//        for (int i = 0; i < fitnesses.length; i++) {
//            fitnesses[i] /= col;
//        }
        //modified by mengxu 2021.07.27
        for (int i = 0; i < fitnesses.length; i++) {
            if(col[i] == 0){
                fitnesses[i] = -1;//all test are bad run!!!
                System.out.println("All test are bad run!!! set fitnesses to -1.");
            }
            else{
                fitnesses[i] /= col[i];
            }
        }
        MultiObjectiveFitness f = (MultiObjectiveFitness) fitness;
        f.setObjectives(state, fitnesses);
    }

    public abstract double priority(TaskOption taskOption, Server server, SystemState systemState);
}
