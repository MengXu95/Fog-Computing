package mengxu;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdRandom;
import mengxu.algorithm.CPOP;
import mengxu.algorithm.HEFT;
import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.rule.evolved.GPRule;
import mengxu.rule.job.basic.PT;
import mengxu.rule.job.basic.PTPlusRL;
import mengxu.rule.job.basic.RL;
import mengxu.rule.server.TPTIQ;
import mengxu.rule.server.WIQ;
import mengxu.simulation.DynamicSimulation;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Job;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;
import mengxu.taskscheduling.dag.DigraphGeneratorMX;
import mengxu.util.random.*;

import java.util.ArrayList;
import java.util.List;

public class SECtest {


    //todo: need to modify the terminals.
    public static void HEFTcheck() {
        int numJobs = 1;
        int warmupJobs = 0;//todo: need to modify the use of warmupJobs
        int numMobileDevice = 1;//todo: need to modify the use of more than one numMobileDevice
        int numEdgeServer = 3;
        int numCloudServer = 0;
        AbstractIntegerSampler numTasksSampler = new UniformIntegerSampler(2, 9);
        AbstractRealSampler procTimeSampler = new UniformSampler(1, 99);
        AbstractRealSampler interReleaseTimeSampler = new ExponentialSampler();
        AbstractRealSampler jobWeightSampler = new TwoSixTwoSampler();

        List<AbstractRule> routing_rule_list = new ArrayList<>();
        List<AbstractRule> sequencing_rule_list = new ArrayList<>();
//        routing_rule_list.add(new WIQ(RuleType.ROUTING));
//        routing_rule_list.add(new TPTIQ(RuleType.ROUTING));
//        routing_rule_list.add(new TPTIQ(RuleType.ROUTING));
        routing_rule_list.add(new HEFT(RuleType.ROUTING));
//        routing_rule_list.add(GPRule.readFromLispExpression(RuleType.ROUTING, "(* (+ (Min PT DT) NIQ) (+ PT (Max (/ (* NIQ UT) W) (Max (* NIQ UT) (+ (* PT NIQ) W)))))"));
//        sequencing_rule_list.add(new RL(RuleType.SEQUENCING));
//        sequencing_rule_list.add(new PT(RuleType.SEQUENCING));
//        sequencing_rule_list.add(new PTPlusRL(RuleType.SEQUENCING));
        sequencing_rule_list.add(new HEFT(RuleType.SEQUENCING));
//        sequencing_rule_list.add(GPRule.readFromLispExpression(RuleType.SEQUENCING, "(- (- (- TIS NIQ) (* (+ UT W) (- TIS NIQ))) (+ (* (* DT PT) (Max W PT)) (- (/ UT UT) (/ NIQ NIQ))))"));

        System.out.println("Job number: " + numJobs);
        for (int i = 0; i < routing_rule_list.size(); i++) {
            System.out.println("Test " + i + ": ");
            if (i == 3) {
                System.out.println("HEFT");
            }
            AbstractRule routing_rule = routing_rule_list.get(i);
            AbstractRule sequencing_rule = sequencing_rule_list.get(i);

//            DynamicSimulation simulation = new DynamicSimulation(1,
//                    sequencing_rule, routing_rule, numJobs, warmupJobs,
//                    numMobileDevice, numEdgeServer, numCloudServer, numTasksSampler,
//                    procTimeSampler, interReleaseTimeSampler, jobWeightSampler,
//                    false);
            DynamicSimulation simulation = new DynamicSimulation(1,sequencing_rule,routing_rule,
                    numJobs, warmupJobs, numMobileDevice, numEdgeServer, numCloudServer,
                    2,10,false);

            simulation.run();
            double meanFlowtime = simulation.meanFlowtime();
            double makespan = simulation.makespan();

            System.out.println("MobileDevice can not process!");
            System.out.println("Routing rule: " + routing_rule.getName());
            System.out.println("Sequencing rule: " + sequencing_rule.getName());
            System.out.println("Mean flowtime: " + meanFlowtime);
            System.out.println("Makespan: " + makespan);
            System.out.println("Job not done: " + simulation.getSystemState().getMobileDevices().get(0).getJobNotDone());
            System.out.println("Job completed: " + simulation.getSystemState().getMobileDevices().get(0).getThroughput());
            System.out.println("Job released: " + simulation.getSystemState().getMobileDevices().get(0).getJobList().size());
//            System.out.println("Job released: " + simulation.getSystemState().getMobileDevices().get(0).getNumJobsReleased());
//            System.out.print("Complete Job ID: [");
//            for(Job job:simulation.getSystemState().getJobsCompleted()){
//                System.out.print(job.getId() + ",");
//            }
            System.out.println();
        }
    }

    public static void main(String[] args){

//        HEFTcheck();

        int numJobs = 10;
        int warmupJobs = 0;//todo: need to modify the use of warmupJobs
        int numMobileDevice = 1;//todo: need to modify the use of more than one numMobileDevice
        int numEdgeServer = 5;
        int numCloudServer = 5;
        AbstractIntegerSampler numTasksSampler = new UniformIntegerSampler(2, 9);
        AbstractRealSampler procTimeSampler = new UniformSampler(1, 99);
        AbstractRealSampler interReleaseTimeSampler = new ExponentialSampler();
        AbstractRealSampler jobWeightSampler = new TwoSixTwoSampler();

        List<AbstractRule> routing_rule_list = new ArrayList<>();
        List<AbstractRule> sequencing_rule_list = new ArrayList<>();
        routing_rule_list.add(new WIQ(RuleType.ROUTING));
        routing_rule_list.add(new TPTIQ(RuleType.ROUTING));
        routing_rule_list.add(new TPTIQ(RuleType.ROUTING));
//        routing_rule_list.add(new HEFT(RuleType.ROUTING));
        routing_rule_list.add(GPRule.readFromLispExpression(RuleType.ROUTING, "(* (+ (Min PT DT) NIQ) (+ PT (Max (/ (* NIQ UT) W) (Max (* NIQ UT) (+ (* PT NIQ) W)))))"));
        routing_rule_list.add(GPRule.readFromLispExpression(RuleType.ROUTING,"(- (+ (/ (- W (+ PT NIQ)) (- (+ NIQ NIQ) (* PT PT))) (Min NIQ (+ (Max TIS PT) (Max WIQ WIQ)))) (/ (/ WIQ TIS) (Min WIQ NIQ)))"));

        sequencing_rule_list.add(new RL(RuleType.SEQUENCING));
        sequencing_rule_list.add(new PT(RuleType.SEQUENCING));
        sequencing_rule_list.add(new PTPlusRL(RuleType.SEQUENCING));
//        sequencing_rule_list.add(new HEFT(RuleType.SEQUENCING));
        sequencing_rule_list.add(GPRule.readFromLispExpression(RuleType.SEQUENCING, "(- (- (- TIS NIQ) (* (+ UT W) (- TIS NIQ))) (+ (* (* DT PT) (Max W PT)) (- (/ UT UT) (/ NIQ NIQ))))"));
        sequencing_rule_list.add(GPRule.readFromLispExpression(RuleType.SEQUENCING,"(* (Max (Max NIQ PT) (* (Max W W) (/ NIQ TIS))) (Min (Min (- NIQ TIS) (Min TIS WIQ)) (Min (Min W PT) (* TIS W))))"));


        System.out.println("Job number: " + numJobs);
        for(int i=0; i<routing_rule_list.size(); i++){
            System.out.println("Test " + i + ": ");
            if(i == 3){
                System.out.println("HEFT");
            }
            AbstractRule routing_rule = routing_rule_list.get(i);
            AbstractRule sequencing_rule = sequencing_rule_list.get(i);

//            DynamicSimulation simulation = new DynamicSimulation(1,
//                    sequencing_rule, routing_rule, numJobs, warmupJobs,
//                    numMobileDevice, numEdgeServer, numCloudServer, numTasksSampler,
//                    procTimeSampler, interReleaseTimeSampler, jobWeightSampler,
//                    true);
            DynamicSimulation simulation = new DynamicSimulation(1,sequencing_rule,routing_rule,
                    numJobs, warmupJobs, numMobileDevice, numEdgeServer, numCloudServer,
                    2,9,true);

            simulation.run();
            double meanFlowtime = simulation.meanFlowtime();
            double makespan = simulation.makespan();

            System.out.println("MobileDevice can not process!");
            System.out.println("Routing rule: " + routing_rule.getName());
            System.out.println("Sequencing rule: " + sequencing_rule.getName());
            System.out.println("Mean flowtime: " + meanFlowtime);
            System.out.println("Makespan: " + makespan);
            for(int mob=0; mob<numMobileDevice; mob++){
                System.out.println("Job not done: " + simulation.getSystemState().getMobileDevices().get(mob).getJobNotDone());
                System.out.println("Job completed: " + simulation.getSystemState().getMobileDevices().get(mob).getThroughput());
                System.out.println("Job released: " + simulation.getSystemState().getMobileDevices().get(mob).getJobList().size());
            }

//            System.out.println("Job released: " + simulation.getSystemState().getMobileDevices().get(0).getNumJobsReleased());
//            System.out.print("Complete Job ID: [");
//            for(Job job:simulation.getSystemState().getJobsCompleted()){
//                System.out.print(job.getId() + ",");
//            }
            System.out.println();

//            simulation = new DynamicSimulation(1,
//                    sequencing_rule, routing_rule, numJobs, warmupJobs,
//                    numMobileDevice, numEdgeServer, numCloudServer, numTasksSampler,
//                    procTimeSampler, interReleaseTimeSampler, jobWeightSampler,
//                    true);
//
//            simulation.run();
//            meanFlowtime = simulation.meanFlowtime();
//            makespan = simulation.makespan();
//
//            System.out.println("MobileDevice can process!");
//            System.out.println("Routing rule: " + routing_rule.getName());
//            System.out.println("Sequencing rule: " + sequencing_rule.getName());
//            System.out.println("Mean flowtime: " + meanFlowtime);
//            System.out.println("Makespan: " + makespan);
//            System.out.println("Job not done: " + simulation.getSystemState().getMobileDevices().get(0).getJobNotDone());
//            System.out.println("Job completed: " + simulation.getSystemState().getJobsCompleted().size());
//            System.out.println();

        }

//        System.out.println("--------------------------------------------");
//        numJobs = 1000;
//        System.out.println("Job number: " + numJobs);
//
//        for(int i=0; i<routing_rule_list.size(); i++){
//            System.out.println("Test " + i + ": ");
//            AbstractRule routing_rule = routing_rule_list.get(i);
//            AbstractRule sequencing_rule = sequencing_rule_list.get(i);
//
//            DynamicSimulation simulation = new DynamicSimulation(1,
//                    sequencing_rule, routing_rule, numJobs, warmupJobs,
//                    numMobileDevice, numEdgeServer, numCloudServer, numTasksSampler,
//                    procTimeSampler, interReleaseTimeSampler, jobWeightSampler,
//                    true);
//
//            simulation.run();
//            double meanFlowtime = simulation.meanFlowtime();
//            double makespan = simulation.makespan();
//
//            System.out.println("MobileDevice can process!");
//            System.out.println("Routing rule: " + routing_rule.getName());
//            System.out.println("Sequencing rule: " + sequencing_rule.getName());
//            System.out.println("Mean flowtime: " + meanFlowtime);
//            System.out.println("Makespan: " + makespan);
//            System.out.println("Job not done: " + simulation.getSystemState().getMobileDevices().get(0).getJobNotDone());
//            System.out.println("Job completed: " + simulation.getSystemState().getJobsCompleted().size());
//            System.out.println();
//
//            simulation = new DynamicSimulation(1,
//                    sequencing_rule, routing_rule, numJobs, warmupJobs,
//                    numMobileDevice, numEdgeServer, numCloudServer, numTasksSampler,
//                    procTimeSampler, interReleaseTimeSampler, jobWeightSampler,
//                    false);
//
//            simulation.run();
//            meanFlowtime = simulation.meanFlowtime();
//            makespan = simulation.makespan();
//
//            System.out.println("MobileDevice can not process!");
//            System.out.println("Routing rule: " + routing_rule.getName());
//            System.out.println("Sequencing rule: " + sequencing_rule.getName());
//            System.out.println("Mean flowtime: " + meanFlowtime);
//            System.out.println("Makespan: " + makespan);
//            System.out.println("Job not done: " + simulation.getSystemState().getMobileDevices().get(0).getJobNotDone());
//            System.out.println("Job completed: " + simulation.getSystemState().getJobsCompleted().size());
//            System.out.println();
//
//        }

    }
}
