package mengxu.taskscheduling;

import mengxu.simulation.DynamicSimulation;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * The set of scheduling problems. The set includes:
 *   1. A list of simulations.
 *   2. Number of replications for each simulation.
 *   3. The objective lower bound matrix: (i,j) - the lower bound of objective i in replication j.
 *
 * Created by YiMei on 28/09/16.
 */
public class SchedulingSet {

    private List<DynamicSimulation> simulations;
    private List<Integer> replications;
    private RealMatrix objectiveLowerBoundMtx;

    public SchedulingSet(List<DynamicSimulation> simulations,
                         List<Integer> replications,
                         List<Objective> objectives) {
        this.simulations = simulations;
        this.replications = replications;
//        createObjectiveLowerBoundMatrix(objectives);
        
        //fzhang 2018.12.20 if we do not want use the lowerBounds, just comment this
        //lowerBoundsFromBenchmarkRule(objectives);
    }

    public List<DynamicSimulation> getSimulations() {
        return simulations;
    }

    public List<Integer> getReplications() {
        return replications;
    }

    public RealMatrix getObjectiveLowerBoundMtx() {
        return objectiveLowerBoundMtx;
    }

    public double getObjectiveLowerBound(int row, int col) {
        return objectiveLowerBoundMtx.getEntry(row, col);
    }

    public void setReplications(List<Integer> replications) {
        this.replications = replications;
    }

//    public void setRule(AbstractRule rule) {
//        for (DynamicSimulation simulation : simulations) {
//            simulation.setSequencingRule(rule);
//        }
//    }

    public void rotateSeed(List<Objective> objectives) {
        for (DynamicSimulation simulation : simulations) {
            simulation.rotateSeed();
        }

        //fzhang 2018.12.20  if we do not use lowerBounds, just comment it
        //lowerBoundsFromBenchmarkRule(objectives);
    }

//    private void createObjectiveLowerBoundMatrix(List<Objective> objectives) {
//        int rows = objectives.size();
//        int cols = 0;
//        for (int rep : replications) {
//            cols += rep;
//        }
//        objectiveLowerBoundMtx = new Array2DRowRealMatrix(rows, cols);
//    }
//
//    private void lowerBoundsFromBenchmarkRule(List<Objective> objectives) {
//        for (int i = 0; i < objectives.size(); i++) {
//            Objective objective = objectives.get(i);
//            AbstractRule benchmarkSeqRule = objective.benchmarkSequencingRule();
//            AbstractRule benchmarkRoutingRule = objective.benchmarkRoutingRule();
//
//            int col = 0;
//            for (int j = 0; j < simulations.size(); j++) {
//                DynamicSimulation simulation = simulations.get(j);
//                simulation.setSequencingRule(benchmarkSeqRule);
//                simulation.setRoutingRule(benchmarkRoutingRule);
//                simulation.rerun(); //this will make sure benchmark rules affect everything
//
//                double value = simulation.objectiveValue(objective);
//                objectiveLowerBoundMtx.setEntry(i, col, value);
////                System.out.println("objective1LowerBound: "+ this.getObjectiveLowerBound(i, col));
//
//                col ++;
//
//                for (int k = 1; k < replications.get(j); k++) {
//                    simulation.rerun();
//                    value = simulation.objectiveValue(objective);
//                    objectiveLowerBoundMtx.setEntry(i, col, value);
//                    col ++;
//                }
//                simulation.reset();
//            }
//
//        }
//    }

//    public SchedulingSet surrogate(int numWorkCenters, int numJobsRecorded,
//                                   int warmupJobs, List<Objective> objectives) {
//        List<DynamicSimulation> surrogateSimulations = new ArrayList<>();
//        List<Integer> surrogateReplications = new ArrayList<>();
//
//        for (int i = 0; i < simulations.size(); i++) {
//            surrogateSimulations.add(
//                    simulations.get(i).surrogate(
//                    numWorkCenters, numJobsRecorded, warmupJobs));
//            surrogateReplications.add(1);
//        }
//
//        return new SchedulingSet(surrogateSimulations,
//                surrogateReplications, objectives);
//    }
//
//    public SchedulingSet surrogateBusy(int numWorkCenters, int numJobsRecorded,
//                                   int warmupJobs, List<Objective> objectives) {
//        List<Simulation> surrogateSimulations = new ArrayList<>();
//        List<Integer> surrogateReplications = new ArrayList<>();
//
//        for (int i = 0; i < simulations.size(); i++) {
//            surrogateSimulations.add(
//                    simulations.get(i).surrogateBusy(
//                            numWorkCenters, numJobsRecorded, warmupJobs));
//            surrogateReplications.add(1);
//        }
//
//        return new SchedulingSet(surrogateSimulations,
//                surrogateReplications, objectives);
//    }
//
    public static SchedulingSet dynamicFullSet(long simSeed,
                                               List<Objective> objectives,
                                               String workflowScale,
                                               int reps) {
        List<DynamicSimulation> simulations = new ArrayList<>();

        //original
      /*  simulations.add(
                DynamicSimulation.standardFull(simSeed, null, null, 10, 4000, 1000,
                        utilLevel, dueDateFactor));*/

      //fzhang 2019.2.12 test should be also with 5000 jobs
//        simulations.add(//small 1
//                DynamicSimulation.standardFull(simSeed, null, null, 20,
//        0, 1, 10, 10, 10, 20,
//        true));
//        simulations.add(//small 2
//                DynamicSimulation.standardFull(simSeed, null, null, 20,
//                        0, 3, 10, 15, 20, 30,
//                        true));
        int minWorkflowID = -1;
        int maxWorkflowID = -1;
        if(workflowScale.equals("small")){
            minWorkflowID = 0;
            maxWorkflowID = 4;
        }
        else if(workflowScale.equals("middle")){
            minWorkflowID = 5;
            maxWorkflowID = 9;
        }
        else if(workflowScale.equals("large")){
            minWorkflowID = 10;
            maxWorkflowID = 14;
        }
        else if(workflowScale.equals("huge")){
            minWorkflowID = 15;
            maxWorkflowID = 19;
        }
        else if(workflowScale.equals("hybird-small-middle")){
            minWorkflowID = 0;
            maxWorkflowID = 9;
        }
        else if(workflowScale.equals("hybird-small-middle-large")){
            minWorkflowID = 0;
            maxWorkflowID = 14;
        }
        else if(workflowScale.equals("hybird-small-middle-large-huge")){
            minWorkflowID = 0;
            maxWorkflowID = 19;
        }
        else{
            System.out.println("Initial workflow scale error!!!");
        }

        //small 1 2 3
//        DynamicSimulation simulation = new DynamicSimulation(simSeed,
//                null, null, 20, 10,
//                3, 20, 20, minWorkflowID,
//                maxWorkflowID, true);

        //medium 1 2 3
//        DynamicSimulation simulation = new DynamicSimulation(simSeed,
//                null, null, 30, 15,
//                3, 30, 30, minWorkflowID,
//                maxWorkflowID, true);


        //large 1 2 3
        DynamicSimulation simulation = new DynamicSimulation(simSeed,
                null, null, 50, 25,
                1, 60, 60, minWorkflowID,
                maxWorkflowID, true);


        simulations.add(simulation);
        List<Integer> replications = new ArrayList<>();
        replications.add(reps);

        return new SchedulingSet(simulations, replications, objectives);
    }

//    public static SchedulingSet dynamicMissingSet(long simSeed,
//                                                  double utilLevel,
//                                                  double dueDateFactor,
//                                                  List<Objective> objectives,
//                                                  int reps) {
//        List<DynamicSimulation> simulations = new ArrayList<>();
//      //original
//    /*    simulations.add(
//                DynamicSimulation.standardMissing(simSeed, null, null, 10, 4000, 1000,
//                        utilLevel, dueDateFactor));*/
//
//        //fzhang 2019.2.12 test should be also with 5000 jobs
//        simulations.add(
//                DynamicSimulation.standardMissing(simSeed, null, null, 10, 5000, 1000,
//                        utilLevel, dueDateFactor));
//
//        List<Integer> replications = new ArrayList<>();
//        replications.add(reps);
//        return new SchedulingSet(simulations, replications, objectives);
//    }

    public static SchedulingSet generateSet(long simSeed,
                                            List<Objective> objectives,
                                            String workflowScale,
                                            int replications) {
        return SchedulingSet.dynamicFullSet(simSeed, objectives, workflowScale, replications);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SchedulingSet that = (SchedulingSet) o;

        if (!simulations.equals(that.simulations)) return false;
        if (!replications.equals(that.replications)) return false;
        return objectiveLowerBoundMtx.equals(that.objectiveLowerBoundMtx);
    }

    @Override
    public int hashCode() {
        int result = simulations.hashCode();
        result = 31 * result + replications.hashCode();
        result = 31 * result + objectiveLowerBoundMtx.hashCode();
        return result;
    }
}
