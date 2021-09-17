package mengxu;

import mengxu.algorithm.FCFS;
import mengxu.algorithm.HEFT;
import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.DynamicSimulation;
import mengxu.util.random.*;

import java.util.ArrayList;
import java.util.List;

public class SECtest {


    //todo: need to modify the terminals. 2021.08.02
    //todo: need to modify the rerun(). high priority!!! 2021.08.03
    public static void HEFTcheck() {
        int numJobs = 1;
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
                    0,4,false);

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

    public static void dynamicCheck(){
        int numJobs = 1;
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
//        routing_rule_list.add(new WIQ(RuleType.ROUTING));
//        routing_rule_list.add(new TPTIQ(RuleType.ROUTING));
//        routing_rule_list.add(new TPTIQ(RuleType.ROUTING));
//        routing_rule_list.add(new HEFT(RuleType.ROUTING));
        routing_rule_list.add(new FCFS(RuleType.ROUTING));
//        routing_rule_list.add(GPRule.readFromLispExpression(RuleType.ROUTING, "(* (+ (Min PT DT) NIQ) (+ PT (Max (/ (* NIQ UT) W) (Max (* NIQ UT) (+ (* PT NIQ) W)))))"));
//        routing_rule_list.add(GPRule.readFromLispExpression(RuleType.ROUTING,"(+ (Max (/ (- WIQ TIS) (- (+ WIQ NIQ) (Max NIQ DT))) (+ (+ NIQ PT) (Max (- (Min UT PT) (* UT PT)) TIS))) (+ (Min (* (Min UT DT) (+ UT NIQ)) (/ (- NIQ WIQ) (/ UT WIQ))) (* DT (Max (- UT PT) (* WIQ NIQ)))))"));

//        sequencing_rule_list.add(new RL(RuleType.SEQUENCING));
//        sequencing_rule_list.add(new PT(RuleType.SEQUENCING));
//        sequencing_rule_list.add(new PTPlusRL(RuleType.SEQUENCING));
//        sequencing_rule_list.add(new HEFT(RuleType.SEQUENCING));
        sequencing_rule_list.add(new FCFS(RuleType.SEQUENCING));
//        sequencing_rule_list.add(GPRule.readFromLispExpression(RuleType.SEQUENCING, "(- (- (- TIS NIQ) (* (+ UT W) (- TIS NIQ))) (+ (* (* DT PT) (Max W PT)) (- (/ UT UT) (/ NIQ NIQ))))"));
//        sequencing_rule_list.add(GPRule.readFromLispExpression(RuleType.SEQUENCING,"(/ NIQ TIS)"));

        DynamicSimulation simulation = new DynamicSimulation(0,null,null,
                numJobs, warmupJobs, numMobileDevice, numEdgeServer, numCloudServer,
                0,0,true);
//        simulation.rotateSeed();

        System.out.println("Job number: " + numJobs);
        for(int i=0; i<routing_rule_list.size(); i++){
//            System.out.println("Test " + i + ": ");
            AbstractRule routing_rule = routing_rule_list.get(i);
            AbstractRule sequencing_rule = sequencing_rule_list.get(i);

//            DynamicSimulation simulation = new DynamicSimulation(1,null,null,
//                    numJobs, warmupJobs, numMobileDevice, numEdgeServer, numCloudServer,
//                    10,10,true);
//            simulation.reset();

            simulation.setSequencingRule(sequencing_rule);
            simulation.setRoutingRule(routing_rule);

//            DynamicSimulation simulation = new DynamicSimulation(1,
//                    sequencing_rule, routing_rule, numJobs, warmupJobs,
//                    numMobileDevice, numEdgeServer, numCloudServer, numTasksSampler,
//                    procTimeSampler, interReleaseTimeSampler, jobWeightSampler,
//                    true);


//            System.out.println("Schedule 1 times!");
            for(int time=0; time<1; time++){
//                System.out.println("time: " + time);
                simulation.reset();
                simulation.rerun();
                double meanFlowtime = simulation.meanFlowtime();
                double makespan = simulation.makespan();

//                System.out.println("MobileDevice can process!");
                System.out.println("Routing rule: " + routing_rule.getName());
                System.out.println("Sequencing rule: " + sequencing_rule.getName());
                System.out.println("First job release time: " + simulation.getFirstJobReleaseTime());
//                System.out.println("Mean flowtime: " + meanFlowtime);
                System.out.println("Makespan: " + makespan);
//                System.out.println("Total Job completed: " + simulation.getSystemState().getJobsCompleted().size());

                int numTaskCompleted = 0;

//                for(int mob=0; mob<numMobileDevice; mob++){
////                System.out.println("Job not done: " + simulation.getSystemState().getMobileDevices().get(mob).getJobNotDone());
//                    System.out.println("Job completed of mobiledevice " + mob + " : " + simulation.getSystemState().getMobileDevices().get(mob).getThroughput());
//                    System.out.println("Job released by mobiledevice " + mob + " : " + simulation.getSystemState().getMobileDevices().get(mob).getJobList().size());
//                    System.out.println("Task completed by mobiledevice " + mob + " : " + simulation.getSystemState().getMobileDevices().get(mob).getNumTasksCompleted());
//                    numTaskCompleted += simulation.getSystemState().getMobileDevices().get(mob).getNumTasksCompleted();
//                }
//
//                for(int ser=0; ser<simulation.getSystemState().getServers().size(); ser++){
//                    System.out.println("Task completed by server " + ser + " : " + simulation.getSystemState().getServers().get(ser).getNumTasksCompleted());
//                    numTaskCompleted += simulation.getSystemState().getServers().get(ser).getNumTasksCompleted();
//                }
//
//                System.out.println("Task completed number: " + numTaskCompleted);

//                System.out.println("Job released: " + simulation.getSystemState().getMobileDevices().get(0).getNumJobsReleased());
//                System.out.print("Complete Job ID: [");
//                for(Job job:simulation.getSystemState().getJobsCompleted()){
//                    System.out.print(job.getMobileDevice().getId() + ":" + job.getId() + ",");
//                }
//                System.out.println();
            }


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
    }

    public static void main(String[] args){

//        HEFTcheck();

        dynamicCheck();

    }
}
