package mengxu;

import mengxu.algorithm.*;
import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.rule.evolved.GPRule;
import mengxu.simulation.DynamicSimulation;
import mengxu.simulation.event.ProcessFinishEvent;
import mengxu.taskscheduling.Job;
import mengxu.taskscheduling.ServerType;
import mengxu.util.random.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SECtest {


    //todo: need to modify the terminals. 2021.08.02
    //todo: need to modify the rerun(). high priority!!! 2021.08.03
    public static void HEFTcheck() {
        int numJobs = 2;
        int warmupJobs = 0;//todo: need to modify the use of warmupJobs
        int numMobileDevice = 2;//todo: need to modify the use of more than one numMobileDevice
        int numEdgeServer = 2;
        int numCloudServer = 2;
        AbstractIntegerSampler numTasksSampler = new UniformIntegerSampler(2, 9);
        AbstractRealSampler procTimeSampler = new UniformSampler(1, 99);
        AbstractRealSampler interReleaseTimeSampler = new ExponentialSampler();
        AbstractRealSampler jobWeightSampler = new TwoSixTwoSampler();

        List<AbstractRule> routing_rule_list = new ArrayList<>();
        List<AbstractRule> sequencing_rule_list = new ArrayList<>();
//        routing_rule_list.add(new WIQ(RuleType.ROUTING));
//        routing_rule_list.add(new TPTIQ(RuleType.ROUTING));
//        routing_rule_list.add(new TPTIQ(RuleType.ROUTING));
//        routing_rule_list.add(new FCFS(RuleType.ROUTING));
//        routing_rule_list.add(GPRule.readFromLispExpression(RuleType.ROUTING, "(/ (/ PT DT) (/ (/ (/ (/ PT DT) (/ (Max WIQ PT) (/ PT DT))) (/ (Max WIQ PT) (/ (/ (/ PT DT) DT) (/ (Max WIQ PT) (Max WIQ PT))))) (/ (+ (Max (/ PT DT) (* WIQ NTR)) PT) (/ PT DT))))"));

        routing_rule_list.add(GPRule.readFromLispExpression(RuleType.ROUTING, "(+ (* (Max (Min NIQ (* (Min NIQ MRT) (Max (/ TTIQ PT) PT))) PT) NIQ) (* (/ NTR PT) (* (* (/ NTR PT) (Max (Min NIQ MRT) PT)) (Max PT (Min NIQ MRT)))))"));
//        sequencing_rule_list.add(new RL(RuleType.SEQUENCING));
//        sequencing_rule_list.add(new PT(RuleType.SEQUENCING));
//        sequencing_rule_list.add(new PTPlusRL(RuleType.SEQUENCING));
//        sequencing_rule_list.add(new FCFS(RuleType.SEQUENCING));
//        sequencing_rule_list.add(GPRule.readFromLispExpression(RuleType.SEQUENCING, "(+ (/ (Max (* WIQ TWT) (/ WIQ TWT)) (Max (+ NIQ MRT) (/ TTIQ TIS))) (* (- MRT NIQ) (Min MRT WIQ)))"));

        sequencing_rule_list.add(GPRule.readFromLispExpression(RuleType.SEQUENCING, "(* NTR (Min NTR (* (Max (/ (Min NIQ DT) DT) (- (Max PT UT) (Min MRT TTIQ))) (Min NTR NTR))))"));

        System.out.println("Job number: " + numJobs);
        for (int i = 0; i < routing_rule_list.size(); i++) {
            System.out.println("Test " + i + ": ");
            if (i == 3) {
                System.out.println("GPrule");
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
                    1,4,false);

            simulation.run();
            double meanFlowtime = simulation.meanFlowtime();
            double makespan = simulation.makespan();

            System.out.println("MobileDevice can not process!");
            System.out.println("Routing rule: " + routing_rule.getName());
            System.out.println("Sequencing rule: " + sequencing_rule.getName());

            //about server


            System.out.println("Mean flowtime: " + meanFlowtime);
            System.out.println("Makespan: " + makespan);
            System.out.println("Job not done: " + simulation.getSystemState().getMobileDevices().get(0).getJobNotDone());
            System.out.println("Job completed: " + simulation.getSystemState().getMobileDevices().get(0).getThroughput());
//            System.out.println("Job released: " + simulation.getSystemState().getMobileDevices().get(0).getJobList().size());
            System.out.println("Job released: " + simulation.getSystemState().getMobileDevices().get(0).getNumJobsReleased());
            System.out.println("Job released: " + simulation.getSystemState().getMobileDevices().get(1).getNumJobsReleased());

            System.out.print("Complete Job ID: [");
            for(Job job:simulation.getSystemState().getJobsCompleted()){
                System.out.print(job.getId() + ",");
            }
            System.out.println();

            System.out.print("Complete Job information==============: [");
            writeSchedulingResultsToFile(simulation.getSystemState().getJobsCompleted(), numEdgeServer, numMobileDevice);
            System.out.println();
        }
    }

    //2021.7.21 modified by mengxu, to store the selected parent index of each generation
    public static void writeSchedulingResultsToFile(List<Job> jobs, int edgeNum, int numMobileDevice){
        File selectParentIndex = new File("scheduling.csv"); //successedTransfer[i][j]: task j makes a successful transfer for task i.
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(selectParentIndex));
            writer.write("jobID, taskID, processorType, processorID, startTime, completeTime, uploadTime, downloadTime");
            writer.newLine();
            for (Job job:jobs) {
                List<ProcessFinishEvent> ref = job.getProcessFinishEvents();
                for(int i=0; i<ref.size(); i++){
                    int taskID = ref.get(i).getProcess().getTaskOption().getTask().getId();
                    String processorType = "";
                    int processor = 0;
                    if(ref.get(i).getProcess().getServer() == null){
                        processorType = "Device";
                        processor = ref.get(i).getMobileDevice().getId();
                    }
                    else if(ref.get(i).getProcess().getServer().getType() == ServerType.CLOUD){
                        processorType = "Cloud";
                        processor = ref.get(i).getProcess().getServer().getId()+numMobileDevice;
                    }
                    else if(ref.get(i).getProcess().getServer().getType() == ServerType.EDGE){
                        processorType = "Fog";
                        processor = ref.get(i).getProcess().getServer().getId()+numMobileDevice;
                    }

//                    String processor = "";
//                    if(ref.get(i).getProcess().getServer() == null){
//                        processor = "Device " + ref.get(i).getMobileDevice().getId();
//                    }
//                    else if(ref.get(i).getProcess().getServer().getType() == ServerType.CLOUD){
//                        processor = ref.get(i).getProcess().getServer().getType() + " " + (ref.get(i).getProcess().getServer().getId()-edgeNum);
//                    }
//                    else if(ref.get(i).getProcess().getServer().getType() == ServerType.EDGE){
//                        processor = ref.get(i).getProcess().getServer().getType() + " " + (ref.get(i).getProcess().getServer().getId());
//                    }
                    double startTime = ref.get(i).getProcess().getStartTime();
                    double completeTime = ref.get(i).getProcess().getFinishTime() - ref.get(i).getProcess().getTaskOption().getDownloadDelay();
                    double uploadTime = startTime - ref.get(i).getProcess().getTaskOption().getUploadDelay();
                    double downloadTime = ref.get(i).getProcess().getFinishTime();
                    writer.write(job.getId() + "," + taskID + "," + processorType + "," + processor + "," + startTime + "," + completeTime + "," + uploadTime + "," + downloadTime);
                    writer.newLine();
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    //2021.7.21 modified by mengxu, to store the selected parent index of each generation
//    public static void writeSchedulingResultsToFile(List<Job> jobs, int edgeNum, int numMobileDevice){
//        File selectParentIndex = new File("scheduling.csv"); //successedTransfer[i][j]: task j makes a successful transfer for task i.
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(selectParentIndex));
//            writer.write("jobID, taskID, processorType, processorID, startTime, completeTime");
//            writer.newLine();
//            for (Job job:jobs) {
//                List<ProcessFinishEvent> ref = job.getProcessFinishEvents();
//                for(int i=0; i<ref.size(); i++){
//                    int taskID = ref.get(i).getProcess().getTaskOption().getTask().getId();
//                    String processorType = "";
//                    int processor = 0;
//                    if(ref.get(i).getProcess().getServer() == null){
//                        processorType = "Device";
//                        processor = ref.get(i).getMobileDevice().getId();
//                    }
//                    else if(ref.get(i).getProcess().getServer().getType() == ServerType.CLOUD){
//                        processorType = "Cloud";
//                        processor = ref.get(i).getProcess().getServer().getId()+numMobileDevice;
//                    }
//                    else if(ref.get(i).getProcess().getServer().getType() == ServerType.EDGE){
//                        processorType = "Fog";
//                        processor = ref.get(i).getProcess().getServer().getId()+numMobileDevice;
//                    }
//
////                    String processor = "";
////                    if(ref.get(i).getProcess().getServer() == null){
////                        processor = "Device " + ref.get(i).getMobileDevice().getId();
////                    }
////                    else if(ref.get(i).getProcess().getServer().getType() == ServerType.CLOUD){
////                        processor = ref.get(i).getProcess().getServer().getType() + " " + (ref.get(i).getProcess().getServer().getId()-edgeNum);
////                    }
////                    else if(ref.get(i).getProcess().getServer().getType() == ServerType.EDGE){
////                        processor = ref.get(i).getProcess().getServer().getType() + " " + (ref.get(i).getProcess().getServer().getId());
////                    }
//                    double startTime = ref.get(i).getProcess().getStartTime();
//                    double completeTime = ref.get(i).getProcess().getFinishTime() - ref.get(i).getProcess().getTaskOption().getDownloadDelay();
//                    writer.write(job.getId() + "," + taskID + "," + processorType + "," + processor + "," + startTime + "," + completeTime);
//                    writer.newLine();
//                }
//            }
//
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void dynamicCheck(){
        int numJobs = 50;
        int warmupJobs = 0;//todo: need to modify the use of warmupJobs
        int numMobileDevice = 3;//todo: need to modify the use of more than one numMobileDevice
        int numEdgeServer = 60;
        int numCloudServer = 60;
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
        routing_rule_list.add(new FCFS(RuleType.ROUTING));
        routing_rule_list.add(new MaxMin(RuleType.ROUTING));
        routing_rule_list.add(new MinMin(RuleType.ROUTING));
        routing_rule_list.add(new RoundRobin(RuleType.ROUTING));
//        routing_rule_list.add(GPRule.readFromLispExpression(RuleType.ROUTING, "(* (+ (Min PT DT) NIQ) (+ PT (Max (/ (* NIQ UT) W) (Max (* NIQ UT) (+ (* PT NIQ) W)))))"));
//        routing_rule_list.add(GPRule.readFromLispExpression(RuleType.ROUTING,"(+ (Max (/ (- WIQ TIS) (- (+ WIQ NIQ) (Max NIQ DT))) (+ (+ NIQ PT) (Max (- (Min UT PT) (* UT PT)) TIS))) (+ (Min (* (Min UT DT) (+ UT NIQ)) (/ (- NIQ WIQ) (/ UT WIQ))) (* DT (Max (- UT PT) (* WIQ NIQ)))))"));

//        sequencing_rule_list.add(new RL(RuleType.SEQUENCING));
//        sequencing_rule_list.add(new PT(RuleType.SEQUENCING));
//        sequencing_rule_list.add(new PTPlusRL(RuleType.SEQUENCING));
        sequencing_rule_list.add(new HEFT(RuleType.SEQUENCING));
        sequencing_rule_list.add(new FCFS(RuleType.SEQUENCING));
        sequencing_rule_list.add(new MaxMin(RuleType.SEQUENCING));
        sequencing_rule_list.add(new MinMin(RuleType.SEQUENCING));
        sequencing_rule_list.add(new RoundRobin(RuleType.SEQUENCING));
//        sequencing_rule_list.add(GPRule.readFromLispExpression(RuleType.SEQUENCING, "(- (- (- TIS NIQ) (* (+ UT W) (- TIS NIQ))) (+ (* (* DT PT) (Max W PT)) (- (/ UT UT) (/ NIQ NIQ))))"));
//        sequencing_rule_list.add(GPRule.readFromLispExpression(RuleType.SEQUENCING,"(/ NIQ TIS)"));

        DynamicSimulation simulation = new DynamicSimulation(0,null,null,
                numJobs, warmupJobs, numMobileDevice, numEdgeServer, numCloudServer,
                0,14,true);
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
//                System.out.println("First job release time: " + simulation.getFirstJobReleaseTime());
//                System.out.println("Mean flowtime: " + meanFlowtime);
                System.out.println("Makespan: " + makespan);
//                System.out.println("Total Job completed: " + simulation.getSystemState().getJobsCompleted().size());

                System.out.println();

                int numTaskCompleted = 0;

                for(int mob=0; mob<numMobileDevice; mob++){
//                System.out.println("Job not done: " + simulation.getSystemState().getMobileDevices().get(mob).getJobNotDone());
                    System.out.println("Job completed of mobiledevice " + mob + " : " + simulation.getSystemState().getMobileDevices().get(mob).getThroughput());
                    System.out.println("Job released by mobiledevice " + mob + " : " + simulation.getSystemState().getMobileDevices().get(mob).getNumJobsReleased());
                    System.out.println("Task completed by mobiledevice " + mob + " : " + simulation.getSystemState().getMobileDevices().get(mob).getNumTasksCompleted());
                    numTaskCompleted += simulation.getSystemState().getMobileDevices().get(mob).getNumTasksCompleted();
                }

                for(int ser=0; ser<simulation.getSystemState().getServers().size(); ser++){
                    System.out.println("Task completed by server " + ser + " : " + simulation.getSystemState().getServers().get(ser).getNumTasksCompleted());
                    numTaskCompleted += simulation.getSystemState().getServers().get(ser).getNumTasksCompleted();
                }

                System.out.println("Task completed number: " + numTaskCompleted);

                System.out.println("Job released: " + simulation.getSystemState().getMobileDevices().get(0).getNumJobsReleased());
                System.out.print("Complete Job ID: [");
                for(Job job:simulation.getSystemState().getJobsCompleted()){
                    System.out.print(job.getMobileDevice().getId() + ":" + job.getId() + ",");
                }
                System.out.println();
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

        HEFTcheck();

//        dynamicCheck();

    }
}
