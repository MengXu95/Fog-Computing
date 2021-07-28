package mengxu.simulation;

import mengxu.rule.AbstractRule;
import mengxu.simulation.event.AbstractEvent;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.*;
import mengxu.util.random.*;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.List;
import java.util.PriorityQueue;

public class DynamicSimulation {

    private long seed;
    private AbstractIntegerSampler numTasksSampler;
    private AbstractRealSampler procTimeSampler;
    private AbstractRealSampler interReleaseTimeSampler;
    private AbstractRealSampler jobWeightSampler;

    protected AbstractRule sequencingRule;
    protected AbstractRule routingRule;
    protected SystemState systemState;

    private int numJobReleased;
    private int numJobsRecorded;

//    protected PriorityQueue<AbstractEvent> eventQueue;

    public DynamicSimulation(long seed,
                              AbstractRule sequencingRule,
                              AbstractRule routingRule,
                              int numJobsRecorded,
                              int warmupJobs,
                              int numMobileDevice,
                              int numEdgeServer,
                              int numCloudServer,
                              AbstractIntegerSampler numTasksSampler,
                              AbstractRealSampler procTimeSampler,
                              AbstractRealSampler interReleaseTimeSampler,
                              AbstractRealSampler jobWeightSampler,
                              boolean canMobileDeviceProcessTask) {
        this.seed = seed;
        this.sequencingRule = sequencingRule;
        this.routingRule = routingRule;
        this.numJobsRecorded = numJobsRecorded;
        this.systemState = new SystemState();

        this.numTasksSampler = numTasksSampler;
        this.procTimeSampler = procTimeSampler;
        this.interReleaseTimeSampler = interReleaseTimeSampler;
        this.jobWeightSampler = jobWeightSampler;


        for (int i = 0; i < numMobileDevice; i++) {
            MobileDevice mobileDevice = new MobileDevice(i,this.systemState,this.seed,
                    this.numTasksSampler, this.procTimeSampler,
                    this.interReleaseTimeSampler, this.jobWeightSampler,
                    this.sequencingRule,this.routingRule,
                    this.numJobsRecorded,warmupJobs);
            mobileDevice.setCanProcessTask(canMobileDeviceProcessTask);
            systemState.addMobileDevice(mobileDevice);
        }

        for(int i = 0; i < numEdgeServer; i++){
            systemState.addServer(new Server(i, ServerType.EDGE));
        }

        for(int i = numEdgeServer; i < numEdgeServer + numCloudServer; i++){
            systemState.addServer(new Server(i, ServerType.CLOUD));
        }

        setup();

    }

    public DynamicSimulation(long seed,
                             AbstractRule sequencingRule,
                             AbstractRule routingRule,
                             int numJobsRecorded,
                             int warmupJobs,
                             int numMobileDevice,
                             int numEdgeServer,
                             int numCloudServer,
                             int minNumTasks,
                             int maxNumTasks,
                             double minProcTime,
                             double maxProcTime,
                             boolean canMobileDeviceProcessTask){
        this(seed,sequencingRule,routingRule,numJobsRecorded,warmupJobs,numMobileDevice,
                numEdgeServer,numCloudServer,new UniformIntegerSampler(minNumTasks, maxNumTasks),
                new UniformSampler(minProcTime, maxProcTime),new ExponentialSampler(),
                new TwoSixTwoSampler(),canMobileDeviceProcessTask);
    }

    public DynamicSimulation(long seed,
                             AbstractRule sequencingRule,
                             AbstractRule routingRule,
                             int numJobsRecorded,
                             int warmupJobs,
                             int numMobileDevice,
                             int numEdgeServer,
                             int numCloudServer,
                             int minNumTasks,
                             int maxNumTasks,
                             boolean canMobileDeviceProcessTask){
        this(seed,sequencingRule,routingRule,numJobsRecorded,warmupJobs,numMobileDevice,
                numEdgeServer,numCloudServer,new UniformIntegerSampler(minNumTasks, maxNumTasks),
                new UniformSampler(1, 99),new ExponentialSampler(),
                new TwoSixTwoSampler(),canMobileDeviceProcessTask);
    }


//    public void resetState() {
//        systemState.reset();
//        eventQueue.clear();
//        setup();
//    }

    public void reset() {
        systemState.reset();
//        resetState();
    }

    public void setup(){
        this.numJobReleased = 0;
        for(MobileDevice mobileDevice :this.systemState.getMobileDevices()){
//            int num = 0;
//            while(num<5){
                mobileDevice.generateJob();
//            mobileDevice.generateOneFixedJob();
//                num++;
//            }

        }
    }

    public void setSequencingRule(AbstractRule sequencingRule) {
        this.sequencingRule = sequencingRule;
        for(MobileDevice mobileDevice :this.systemState.getMobileDevices()){
            mobileDevice.setSequencingRule(sequencingRule);
        }

    }

//    public void setJobStates(int[] jobStates) { this.jobStates = jobStates; }

    public void setRoutingRule(AbstractRule routingRule) {
        this.routingRule = routingRule;
        for(MobileDevice mobileDevice :this.systemState.getMobileDevices()){
            mobileDevice.setRoutingRule(routingRule);
        }
//        //need to reset state as well, as the operationoptions associated
//        //with workcenters are chosen using this routing rule, so current
//        //values are outdated
//        resetState();
    }

    public AbstractRule getSequencingRule() {
        return sequencingRule;
    }

    public AbstractRule getRoutingRule() {
        return routingRule;
    }

    public void run(){
        for(MobileDevice mobileDevice :this.systemState.getMobileDevices()){
            mobileDevice.run();
        }
//        System.out.println("Schedule complete!");
    }

    public void rerun() {
        //original
        //fzhang 2018.11.5 this is used for generate different instances in a generation.
        //if the replications is 1, does not have influence
        resetState();

        //reset(): reset seed value, will get the same instance
        //reset();
        run();
    }

    public void resetState() {
        systemState.reset();
        for(MobileDevice mobileDevice :this.systemState.getMobileDevices()){
            mobileDevice.eventQueue.clear();
        }
//        eventQueue.clear();
        setup();
    }


    public void rotateSeed() {//this is use for changing seed value in next generation
        //this only relates to generation
        for(MobileDevice mobileDevice :this.systemState.getMobileDevices()){
            mobileDevice.rotateSeed();
        }
        //System.out.println(seed);//when seed=0, after Gen0, the value is 10000, after Gen1, the value is 20000....
    }

    public SystemState getSystemState() {
        return systemState;
    }

    public double meanFlowtime() {
        if(systemState.getJobsCompleted().size() < numJobsRecorded){
//            System.out.println("This is a bad run!");
            return Double.MAX_VALUE;
        }

        double value = 0.0;
        for (Job job : systemState.getJobsCompleted()) {
            value += job.getFlowTime();
        }

        return value/systemState.getJobsCompleted().size();
    }

    //todo:
    public double makespan(){
        if(systemState.getJobsCompleted().size() < numJobsRecorded){
//            System.out.println("This is a bad run!");
            return Double.MAX_VALUE;
        }
        double firstJobReleaseTime = Double.MAX_VALUE;
        double allJobComplete = 0;
        for (Job job : systemState.getJobsCompleted()) {
            if(job.getReleaseTime()<firstJobReleaseTime){
                firstJobReleaseTime = job.getReleaseTime();
            }
            if(job.getCompletionTime()>allJobComplete){
                allJobComplete = job.getCompletionTime();
            }
        }
        return allJobComplete-firstJobReleaseTime;
//        return allJobComplete;
    }

    public double objectiveValue(Objective objective) {
        switch (objective) {
            case MAKESPAN:
                return makespan();
            case MEAN_FLOWTIME:
                return meanFlowtime();
        }

        return -1.0;
    }

    public static DynamicSimulation standardFull(
            long seed,
            AbstractRule sequencingRule,
            AbstractRule routingRule,
            int numJobsRecorded,
            int warmupJobs,
            int numMobileDevice,
            int numEdgeServer,
            int numCloudServer,
            int minNumTasks,
            int maxNumTasks,
            double minProcTime,
            double maxProcTime,
            boolean canMobileDeviceProcessTask) {
        return new DynamicSimulation(seed,sequencingRule,routingRule,numJobsRecorded,warmupJobs,numMobileDevice,
                numEdgeServer,numCloudServer,new UniformIntegerSampler(minNumTasks, maxNumTasks),
                new UniformSampler(minProcTime, maxProcTime),new ExponentialSampler(),
                new TwoSixTwoSampler(),canMobileDeviceProcessTask);
    }

//    public static DynamicSimulation standardMissing(
//            long seed,
//            AbstractRule sequencingRule,
//            AbstractRule routingRule,
//            int numWorkCenters,
//            int numJobsRecorded,
//            int warmupJobs,
//            double utilLevel,
//            double dueDateFactor) {
//        return new DynamicSimulation(seed, sequencingRule, routingRule, numWorkCenters, numJobsRecorded,
//                warmupJobs,1, numWorkCenters, utilLevel, dueDateFactor, false);
//    }
}
