package mengxu.taskscheduling;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DigraphGenerator;
import edu.princeton.cs.algs4.In;
import mengxu.rule.AbstractRule;
import mengxu.simulation.DynamicSimulation;
import mengxu.simulation.event.AbstractEvent;
import mengxu.simulation.event.JobArrivalEvent;
import mengxu.simulation.event.ProcessStartEvent;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.dag.DigraphGeneratorMX;
import mengxu.util.random.*;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.*;

public class MobileDevice {

    private int id;
    private List<Job> jobList;
    private SystemState systemState;

    public final static int SEED_ROTATION = 10000;
    public long seed;
    public RandomDataGenerator randomDataGenerator;

    private final AbstractIntegerSampler numTasksSampler;
//    private final AbstractRealSampler procTimeSampler;
    private final AbstractRealSampler interReleaseTimeSampler;
    private final AbstractRealSampler jobWeightSampler;
    private final AbstractRealSampler workloadSampler;
    private final AbstractRealSampler taskDataSampler;
    private final AbstractRealSampler taskInputDataSampler;
    private final AbstractRealSampler uploadBandwidthCloudSampler;
    private final AbstractRealSampler downloadBandwidthCloudSampler;
    private final AbstractRealSampler uploadBandwidthEdgeSampler;
    private final AbstractRealSampler downloadBandwidthEdgeSampler;
    private final AbstractRealSampler processingRateCloudSampler;
    private final AbstractRealSampler processingRateEdgeSampler;
//    private final AbstractRealSampler upLoadDelaySampler;
//    private final AbstractRealSampler downLoadDelaySampler;

    protected int numJobsRecorded;
    protected int warmupJobs;
    protected int numJobsReleased;
    protected int numJobsCompleted;

    private final double processingRate;

    private DigraphGeneratorMX digraphGeneratorMX;

    private double readyTime;
    private LinkedList<TaskOption> queue;
    public PriorityQueue<AbstractEvent> eventQueue;

    protected AbstractRule sequencingRule;
    protected AbstractRule routingRule;

    private int jobNotDone;
    private int throughput;
    private boolean canProcessTask;

    private double totalProcTimeInQueue;
    private double totalProcTimeAndUpLoadAndDownLoadTimeInQueue;

    //fzhang 3.6.2018  discard the individual(rule) can not complete the whole jobs well, take a long time (prefer to do part of each job)
    int beforeThroughput; //save the throughput value before updated (a job finished)
    int afterThroughput; //save the throughput value after updated (a job finished)
    int count = 0;

    private int numTasksCompleted;


    public MobileDevice(int id, double processingRate,
                        SystemState systemState, long seed,
                        RandomDataGenerator randomDataGenerator,
                        AbstractIntegerSampler numTasksSampler,
//                        AbstractRealSampler procTimeSampler,
                        AbstractRealSampler workloadSampler,
                        AbstractRealSampler taskDataSampler,
                        AbstractRealSampler taskInputDataSampler,
                        AbstractRealSampler interReleaseTimeSampler,
                        AbstractRealSampler jobWeightSampler,
                        AbstractRealSampler uploadBandwidthCloudSampler,
                        AbstractRealSampler downloadBandwidthCloudSampler,
                        AbstractRealSampler uploadBandwidthEdgeSampler,
                        AbstractRealSampler downloadBandwidthEdgeSampler,
                        AbstractRealSampler processingRateCloudSampler,
                        AbstractRealSampler processingRateEdgeSampler,
                        AbstractRule sequencingRule,
                        AbstractRule routingRule,
                        int numJobsRecorded,
                        int warmupJobs){
        this.id = id;
        this.processingRate = processingRate;
        this.systemState = systemState;
        this.seed = seed;

        this.randomDataGenerator = randomDataGenerator;
        this.randomDataGenerator.reSeed(seed);
        this.numTasksSampler = numTasksSampler;
//        this.procTimeSampler = procTimeSampler;
        this.workloadSampler = workloadSampler;
        this.taskDataSampler = taskDataSampler;
        this.taskInputDataSampler = taskInputDataSampler;

        this.interReleaseTimeSampler = interReleaseTimeSampler;
        this.jobWeightSampler = jobWeightSampler;
//        this.upLoadDelaySampler = new UniformSampler(10, 30);//modified by mengxu. 2021 07.27
//        this.downLoadDelaySampler = new UniformSampler(10, 30);//modified by mengxu. 2021 07.27

        //modified 2021.08.02
        this.uploadBandwidthCloudSampler = uploadBandwidthCloudSampler;
        this.downloadBandwidthCloudSampler = downloadBandwidthCloudSampler;
        this.uploadBandwidthEdgeSampler = uploadBandwidthEdgeSampler;
        this.downloadBandwidthEdgeSampler = downloadBandwidthEdgeSampler;
        this.processingRateCloudSampler = processingRateCloudSampler;
        this.processingRateEdgeSampler = processingRateEdgeSampler;

        setInterReleaseTimeSamplerMean();

        this.sequencingRule = sequencingRule;
        this.routingRule = routingRule;

        this.numJobsReleased = 0;
        this.numJobsCompleted = 0;
        this.numJobsRecorded = numJobsRecorded;
        this.warmupJobs = warmupJobs;
        this.jobList = new ArrayList<>();
        this.eventQueue = new PriorityQueue<>();
        this.queue = new LinkedList<>();
        this.canProcessTask = false;

        this.totalProcTimeInQueue = 0;
        this.totalProcTimeAndUpLoadAndDownLoadTimeInQueue = 0;

        this.numTasksCompleted = 0;
        this.jobNotDone = 0;
        this.throughput = 0;
        this.digraphGeneratorMX = new DigraphGeneratorMX(this.seed);

    }

    public void setCanProcessTask(boolean canProcessTask) {
        this.canProcessTask = canProcessTask;
    }

    public void setInterReleaseTimeSamplerMean() {
        double mean = 100;//what's the meaning of this
        interReleaseTimeSampler.setMean(mean);
    }

    public int getThroughput() {
        return throughput;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public int getNumJobsReleased() {
        return numJobsReleased;
    }

    public void rotateSeed() {//this is use for changing seed value in next generation
        //this only relates to generation
        seed += SEED_ROTATION;
//        reset();
        //System.out.println(seed);//when seed=0, after Gen0, the value is 10000, after Gen1, the value is 20000....
    }

    public void resetState() {
//        systemState.reset();
        this.numJobsReleased = 0;
        this.numJobsCompleted = 0;
        this.totalProcTimeInQueue = 0;
        this.totalProcTimeAndUpLoadAndDownLoadTimeInQueue = 0;
        this.numTasksCompleted = 0;
        this.jobNotDone = 0;
        this.throughput = 0;
        this.readyTime = 0;
        this.digraphGeneratorMX = new DigraphGeneratorMX(this.seed);
        this.count = 0; //modified by mengxu 2021.08.27

        jobList.clear();
        queue.clear();
        eventQueue.clear();
//        setup();
    }

    public void setup(){
        this.numJobsReleased = 0;
        this.throughput = 0;
//        generateOneFixedJob();
        generateJob();
    }

    public void reset() {
        reset(seed);
    }

    public void reset(long seed) {
        reseed(seed);
        resetState();
    }

    public void reseed(long seed) {
        this.seed = seed;
        randomDataGenerator.reSeed(seed);
    }

    public void run(){
        count = 0;
        while(!eventQueue.isEmpty() && throughput < numJobsRecorded){
            AbstractEvent nextEvent = eventQueue.poll();
//            systemState.setClockTime(nextEvent.getTime());
//            nextEvent.trigger(this);

            //fzhang 3.6.2018  fix the stuck problem
            beforeThroughput = throughput; //save the throughput value before updated (a job finished)

            systemState.setClockTime(nextEvent.getTime());
            nextEvent.trigger(this); //nextEvent includes many different types of events

            afterThroughput = throughput; //save the throughput value after updated (a job finished)

            if(throughput > warmupJobs & afterThroughput - beforeThroughput == 0) { //if the value was not updated
                count++;
            }

            //System.out.println("count "+count);//modified by mengxu. 2021.08.27
            if(count > 100000) {
                count = 0;
                systemState.setClockTime(Double.MAX_VALUE);
                eventQueue.clear();
                break;
            }


            //This is used to stop the bad run!!!
            //===================ignore busy machine here==============================
            //when nextEvent was done, check the numOpsInQueue
            if(this.canProcessTask){
                if(this.queue.size() > 100){
                    systemState.setClockTime(Double.MAX_VALUE);
                    eventQueue.clear();
                    break;
                }
            }
            for (Server s: systemState.getServers()) {
                if (s.numTaskInQueue() > 100) {
                    systemState.setClockTime(Double.MAX_VALUE);
                    eventQueue.clear();
                    break;
                }
            }

        }
    }

    public void multiMobileDeviceRun(){
        while(!eventQueue.isEmpty() && throughput < numJobsRecorded){
            AbstractEvent nextEvent = eventQueue.poll();
//            systemState.setClockTime(nextEvent.getTime());
//            nextEvent.trigger(this);

            //fzhang 3.6.2018  fix the stuck problem
            beforeThroughput = throughput; //save the throughput value before updated (a job finished)

            systemState.setClockTime(nextEvent.getTime());
            nextEvent.trigger(this); //nextEvent includes many different types of events

            afterThroughput = throughput; //save the throughput value after updated (a job finished)

            if(throughput > warmupJobs & afterThroughput - beforeThroughput == 0) { //if the value was not updated
                count++;
            }

            //System.out.println("count "+count);
            if(count > 200000) {
                count = 0;
                systemState.setClockTime(Double.MAX_VALUE);
                eventQueue.clear();
                break;
            }


            //This is used to stop the bad run!!!
            //===================ignore busy machine here==============================
            //when nextEvent was done, check the numOpsInQueue
            if(this.canProcessTask){
                if(this.queue.size() > 100){
                    systemState.setClockTime(Double.MAX_VALUE);
                    eventQueue.clear();
                    break;
                }
            }
            for (Server s: systemState.getServers()) {
                if (s.numTaskInQueue() > 100) {
                    systemState.setClockTime(Double.MAX_VALUE);
                    eventQueue.clear();
                    break;
                }
            }

        }
    }

    public void generateJob(){
        double releaseTime = systemState.getClockTime()
                + interReleaseTimeSampler.next(randomDataGenerator);
        double weight = jobWeightSampler.next(randomDataGenerator);
        int numTask = numTasksSampler.next(randomDataGenerator);

        int maxNumEdge = numTask*(numTask-1) / 2;
        int minNumEdge = numTask - 1;
        AbstractIntegerSampler numEdgesSampler = new UniformIntegerSampler(minNumEdge,maxNumEdge);
        int numEdge = numEdgesSampler.next(randomDataGenerator);

        int V = numTask;
        int E = (int)((V*(V-1) / 2 + V-1) / 2);
//        if(numEdge>(long) V*(V-1) / 2){
//            E = V*(V-1) / 2;
//        }
//        else if(numEdge < V-1){
//            E = V-1;
//        }
//        else{
//            E = numEdge;
//        }
//        System.out.println(V + ", " + E);
        Digraph digraph = this.digraphGeneratorMX.rootedOutDAG(V,E);
//        Digraph digraph = DigraphGeneratorMX.link(numTask);//like the structure: 0->1->2->3
        Digraph reverseDigraph = digraph.reverse();
        //random generate task list and their digraph.
        List<Task> taskList = Arrays.asList(new Task[numTask]);
        for( int v = 0; v < digraph.V(); v++){
//            double procTimeCloud = procTimeSampler.next(randomDataGenerator);
//            double procTimeEdge = procTimeCloud + procTimeSampler.next(randomDataGenerator);
//            double procTimeOnMobileDevice = procTimeEdge + procTimeSampler.next(randomDataGenerator);
//            Task task = new Task(v,digraph);
            double workload = this.workloadSampler.next(randomDataGenerator);
            double taskData = this.taskDataSampler.next(randomDataGenerator);
            Task task = new Task(v,workload,taskData);

            taskList.set(v,task);
        }
        for(Task task:taskList){
            for(int childID :digraph.adj(task.getId())){ //add linked children
                task.addChild(taskList.get(childID));
                double taskOutputData = this.taskInputDataSampler.next(randomDataGenerator);
                task.addChildOutputData(childID, taskOutputData);
            }
            for(int parentID : reverseDigraph.adj(task.getId())){
                task.addParent(taskList.get(parentID)); //add linked parents
//                double taskInputData = this.taskInputDataSampler.next(randomDataGenerator);
                double taskInputData = taskList.get(parentID).getChildOutputData(task.getId());
                task.addParentInputData(parentID,taskInputData);
            }

            if(canProcessTask){
                //the mobileDevice is also an option!!!
                double procTimeOnMobileDevice = task.getWorkload()/this.processingRate;
                task.addTaskOption(new TaskOption(task, this, procTimeOnMobileDevice, 0, 0));
            }

            int numOptions = systemState.getServers().size();

            //todo: need to set different processing time for different VMs (cloud and edge)
//            double uploadDelayEdge = upLoadDelaySampler.next(randomDataGenerator);
//            double downloadDelayEdge = downLoadDelaySampler.next(randomDataGenerator);
//            double uploadDelayCloud = uploadDelayEdge + upLoadDelaySampler.next(randomDataGenerator);
//            double downloadDelayCloud = downloadDelayEdge + downLoadDelaySampler.next(randomDataGenerator);

            for(int i=0;i<numOptions;i++){
                if(systemState.getServers().get(i).getType()==ServerType.CLOUD){
                    double procTimeCloud = task.getWorkload()/systemState.getServers().get(i).getProcessingRate();
                    double uploadDelayCloud = (task.getData() + task.getAllInputDataTotal())/systemState.getServers().get(i).getUploadBandwidth();
                    double downloadDelayCloud = (task.getData() + task.getAllOutputDataTotal())/systemState.getServers().get(i).getDownloadBandwidth();
                    task.addTaskOption(new TaskOption(task, systemState.getServers().get(i), procTimeCloud, uploadDelayCloud, downloadDelayCloud));
                }
                else if(systemState.getServers().get(i).getType()==ServerType.EDGE){
                    double procTimeEdge = task.getWorkload()/systemState.getServers().get(i).getProcessingRate();
                    double uploadDelayEdge = (task.getData() + task.getAllInputDataTotal())/systemState.getServers().get(i).getUploadBandwidth();
                    double downloadDelayEdge = (task.getData() + task.getAllOutputDataTotal())/systemState.getServers().get(i).getDownloadBandwidth();
                    task.addTaskOption(new TaskOption(task, systemState.getServers().get(i), procTimeEdge, uploadDelayEdge, downloadDelayEdge));
                }
            }
        }

        Job job = new Job(numJobsReleased, releaseTime, weight, digraph,this,taskList, JobType.DAG);
        for(Task task:taskList){
            task.setJob(job);
        }
        jobList.add(job);
        systemState.addJobToSystem(job);
        numJobsReleased++;
        this.systemState.addAllNumJobsReleased();
        eventQueue.add(new JobArrivalEvent(job,this));
    }

    public void generateOneFixedJob(){
        double releaseTime = 0;
//        double releaseTime = systemState.getClockTime();

        Digraph digraph = DigraphGeneratorMX.FixedDAG();
        int[][] arr = {{14,16,9},
                {13,19,18},
                {11,13,19},
                {13,8,17},
                {12,13,10},
                {13,16,9},
                {7,15,11},
                {5,11,14},
                {18,12,20},
                {21,7,16}};


        Digraph reverseDigraph = digraph.reverse();
        //random generate task list and their digraph.
        List<Task> taskList = Arrays.asList(new Task[10]);
        for( int v = 0; v < digraph.V(); v++){
//            double procTimeOnMobileDevice = procTimeSampler.next(randomDataGenerator);
//            Task task = new Task(v,digraph);
            double workload = this.workloadSampler.next(randomDataGenerator);
            double taskData = this.taskDataSampler.next(randomDataGenerator);
            Task task = new Task(v,workload,taskData);
            if(canProcessTask){
                //the mobileDevice is also an option!!!
                double procTimeOnMobileDevice = task.getWorkload()/this.processingRate;
                task.addTaskOption(new TaskOption(task, this, procTimeOnMobileDevice, 0, 0));
            }

            int numOptions = systemState.getServers().size();
            double uploadDelay = 0;
            double downloadDelay = 0;
            for(int i=0;i<numOptions;i++){
                task.addTaskOption(new TaskOption(task, systemState.getServers().get(i), arr[task.getId()][i], uploadDelay, downloadDelay));
            }
            taskList.set(v,task);
        }
        for(Task task:taskList){
            for(int childID :digraph.adj(task.getId())){ //add linked children
                task.addChild(taskList.get(childID));
                if(task.getId() == 0 && childID ==1){
                    task.addCommunicateTime(18);
                }
                if(task.getId() == 0 && childID ==2){
                    task.addCommunicateTime(12);
                }
                if(task.getId() == 0 && childID ==3){
                    task.addCommunicateTime(9);
                }
                if(task.getId() == 0 && childID ==4){
                    task.addCommunicateTime(11);
                }
                if(task.getId() == 0 && childID ==5){
                    task.addCommunicateTime(14);
                }
                if(task.getId() == 1 && childID ==7){
                    task.addCommunicateTime(19);
                }
                if(task.getId() == 1 && childID ==8){
                    task.addCommunicateTime(16);
                }
                if(task.getId() == 2 && childID ==6){
                    task.addCommunicateTime(23);
                }
                if(task.getId() == 3 && childID ==7){
                    task.addCommunicateTime(27);
                }
                if(task.getId() == 3 && childID ==8){
                    task.addCommunicateTime(23);
                }
                if(task.getId() == 4 && childID ==8){
                    task.addCommunicateTime(13);
                }
                if(task.getId() == 5 && childID ==7){
                    task.addCommunicateTime(15);
                }
                if(task.getId() == 6 && childID ==9){
                    task.addCommunicateTime(17);
                }
                if(task.getId() == 7 && childID ==9){
                    task.addCommunicateTime(11);
                }
                if(task.getId() == 8 && childID ==9){
                    task.addCommunicateTime(13);
                }
            }
            for(int parentID : reverseDigraph.adj(task.getId())){
                task.addParent(taskList.get(parentID)); //add linked parents
//                if(parentID == 0 && task.getId() ==1){
//                    task.addCommunicateTime(18);
//                }
//                if(parentID == 0 && task.getId() ==2){
//                    task.addCommunicateTime(12);
//                }
//                if(parentID == 0 && task.getId() ==3){
//                    task.addCommunicateTime(9);
//                }
//                if(parentID == 0 && task.getId() ==4){
//                    task.addCommunicateTime(11);
//                }
//                if(parentID == 0 && task.getId() ==5){
//                    task.addCommunicateTime(14);
//                }
//                if(parentID == 1 && task.getId() ==7){
//                    task.addCommunicateTime(19);
//                }
//                if(parentID == 1 && task.getId() ==8){
//                    task.addCommunicateTime(16);
//                }
//                if(parentID == 2 && task.getId() ==6){
//                    task.addCommunicateTime(23);
//                }
//                if(parentID == 3 && task.getId() ==7){
//                    task.addCommunicateTime(27);
//                }
//                if(parentID == 3 && task.getId() ==8){
//                    task.addCommunicateTime(23);
//                }
//                if(parentID == 4 && task.getId() ==8){
//                    task.addCommunicateTime(13);
//                }
//                if(parentID == 6 && task.getId() ==9){
//                    task.addCommunicateTime(17);
//                }
//                if(parentID == 7 && task.getId() ==9){
//                    task.addCommunicateTime(11);
//                }
//                if(parentID == 8 && task.getId() ==9){
//                    task.addCommunicateTime(13);
//                }
            }
        }

        Job job = new Job(numJobsReleased, releaseTime, 1, digraph,this,taskList, JobType.DAG);
        for(Task task:taskList){
            task.setJob(job);
        }
        jobList.add(job);
        systemState.addJobToSystem(job);
        numJobsReleased++;
        this.systemState.addAllNumJobsReleased();
        eventQueue.add(new JobArrivalEvent(job,this));
    }

    public int getId() {
        return id;
    }

    public double getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(double readyTime) {
        this.readyTime = readyTime;
    }

    public void addToQueue(TaskOption taskOption){
        this.queue.add(taskOption);
        this.totalProcTimeInQueue += taskOption.getProcTime();
        this.totalProcTimeAndUpLoadAndDownLoadTimeInQueue += (taskOption.getProcTime() + taskOption.getUploadDelay() + taskOption.getDownloadDelay());
    }

    public LinkedList<TaskOption> getQueue() {
        return queue;
    }

    public void removeFromQueue(TaskOption o) {
        queue.remove(o);
//        numTasksCompleted++;
        this.totalProcTimeInQueue -= o.getProcTime();
        this.totalProcTimeAndUpLoadAndDownLoadTimeInQueue -= (o.getProcTime() + o.getUploadDelay() + o.getDownloadDelay());
    }

    public int getNumTasksCompleted() {
        return numTasksCompleted;
    }

    public void addNumTasksCompleted() {
        this.numTasksCompleted++;
    }

    public int numTaskInQueue(){
        return queue.size();
    }

    //todo: need to modify to make it save time
    public double totalProcTimeInQueue(){
        return this.totalProcTimeInQueue;
//        double time = 0;
//        for(TaskOption taskOption:queue){
//            time += taskOption.getProcTime();
//        }
//        return time;
    }

    //todo: need to modify to make it save time
    public double totalProcTimeAndUpLoadAndDownLoadTimeInQueue(){
        return this.totalProcTimeAndUpLoadAndDownLoadTimeInQueue;
//        double time = 0;
//        for(TaskOption taskOption:queue){
//            time += taskOption.getProcTime() + taskOption.getUploadDelay() + taskOption.getDownloadDelay();
//        }
//        return time;
    }

    public SystemState getSystemState() {
        return systemState;
    }

    public void setRoutingRule(AbstractRule routingRule) {
        this.routingRule = routingRule;
    }

    public void setSequencingRule(AbstractRule sequencingRule) {
        this.sequencingRule = sequencingRule;
    }

    public void addEvent(AbstractEvent event){
        this.eventQueue.add(event);
    }

    public boolean checkJobDone(Job job){
        boolean allTaskDone = true;
        for(Task task: job.getTaskList()){
            if(!task.isComplete()){
                allTaskDone = false;
                break;
            }
        }
        return allTaskDone;
    }

    public void completeJob(Job job) {
        if(this.systemState.getMobileDevices().size() == 1){
            //single mobile device
            if(checkJobDone(job)){
                numJobsCompleted ++;  //before only have this line
                if (numJobsReleased > warmupJobs && job.getId() >= 0
                        && job.getId() < numJobsRecorded + warmupJobs) {
                    throughput++;  //before only have this line
                    count = 0;
                    systemState.addCompletedJob(job);
                }
            }
            else{
                jobNotDone ++;
                System.out.println("Error! Job not done.");
            }
//            int a = systemState.getJobsCompleted().size();
//        System.out.println("The number of completed jobs: "+systemState.getJobsCompleted().size());
            systemState.removeJobFromSystem(job);
        }
        else{
            //multiple mobile devices
            if(checkJobDone(job)){
                numJobsCompleted ++;  //before only have this line
                if (this.systemState.getAllNumJobsReleased() > warmupJobs && job.getId() >= 0
                        && job.getId() < numJobsRecorded + warmupJobs) {
                    throughput++;  //before only have this line
                    count = 0;
                    systemState.addCompletedJob(job);
                }
            }
            else{
                jobNotDone ++;
                System.out.println("Error! Job not done.");
            }
//            int a = systemState.getJobsCompleted().size();
//        System.out.println("The number of completed jobs: "+systemState.getJobsCompleted().size());
            systemState.removeJobFromSystem(job);
        }


    }

    public int getJobNotDone() {
        return jobNotDone;
    }

    public int getWarmupJobs() {
        return warmupJobs;
    }

    public int getNumJobsRecorded() {
        return numJobsRecorded;
    }

    public boolean canAddToQueue(Process process) {//???
        Iterator<AbstractEvent> e = eventQueue.iterator();
        if (e.hasNext()) {
            AbstractEvent a = e.next();
            if (a instanceof ProcessStartEvent) {
                if(((ProcessStartEvent) a).getProcess().getServer() == null && process.getServer() == null){
                    if (((ProcessStartEvent) a).getProcess().getTaskOption().getTask().getJob().getMobileDevice().getId() ==
                            process.getTaskOption().getTask().getJob().getMobileDevice().getId()) {
                        return false;
                    }
                }
                else if(((ProcessStartEvent) a).getProcess().getServer() != null && process.getServer() != null){
                    if (((ProcessStartEvent) a).getProcess().getServer().getId() ==
                            process.getServer().getId()) {
//                    System.out.println("can not add to Queue!");
                        return false;
                    }
                }

            }
        }
        return true;
    }

    public boolean isCanProcessTask() {
        return canProcessTask;
    }

    public AbstractRule getRoutingRule() {
        return routingRule;
    }

    public AbstractRule getSequencingRule() {
        return sequencingRule;
    }
}
