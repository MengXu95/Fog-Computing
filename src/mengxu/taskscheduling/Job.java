package mengxu.taskscheduling;

import edu.princeton.cs.algs4.Digraph;
import mengxu.simulation.event.ProcessFinishEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Job {
    private final int id;
    private final double releaseTime;
    private final double weight;
    private MobileDevice mobileDevice;
//    private List<JobOption> jobOptions;
    private Digraph digraph;
    private List<Task> taskList;
    private List<ProcessFinishEvent> processFinishEvents;
    private JobType jobType;
    private boolean hasAddToCompletedList = false;

//    private double arrivalTime;
    private double completionTime;

    //for CPOP
    private Task criticalPathStartTask;

    public Job(int id,
               double releaseTime,
               double weight,
               Digraph digraph,
               MobileDevice mobileDevice,
               List<Task> taskList,
               JobType jobType) {
        this.id = id;
        this.releaseTime = releaseTime;
        this.weight = weight;
        this.digraph = digraph;
        this.mobileDevice = mobileDevice;
        this.taskList = taskList;
        this.processFinishEvents = new ArrayList<ProcessFinishEvent>();
        this.jobType = jobType;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public List<Task> getFirstArriveReadyTask() {
        List<Task> firstArriveReadyTask = new ArrayList<>();
        for(Task task:taskList){
            if(task.getParentTaskList().size() == 0){
                firstArriveReadyTask.add(task);
            }
        }
        return firstArriveReadyTask;
    }

    public int getId() {
        return id;
    }

//    public void linkTasks() {
////        Operation next = null;
//        double nextProcTime = 0.0;
//
//        double workRemaining = 0.0;
//        int numOpsRemaining = 0;
//        for (int i = taskList.size()-1; i > -1; i--) {
//            Task task = taskList.get(i);
////            numOpsRemaining += task.getChildTaskList().size();
//
//            double medianProcTime;
//            double medianUploadDelay;
//            double medianDownloadDelay;
//            //for one operation, it has several options
//            double[] procTimes = new double[task.getTaskOptions().size()];
//            double[] uploadDelays = new double[task.getTaskOptions().size()];
//            double[] downloadDelays = new double[task.getTaskOptions().size()];
//            //put different processing time in proceTimes[], but now here the value should be the same
//            for (int j = 0; j < task.getTaskOptions().size(); ++j) {
//                procTimes[j] = task.getTaskOptions().get(j).getProcTime();
//                uploadDelays[j] = task.getTaskOptions().get(j).getUploadDelay();
//                downloadDelays[j] = task.getTaskOptions().get(j).getDownloadDelay();
//            }
//            Arrays.sort(procTimes);
//            Arrays.sort(uploadDelays);
//            Arrays.sort(downloadDelays);
//            //get the median value
//            if (procTimes.length % 2 == 0){
//                //halfway between two points, as even number of elements
//                medianProcTime = (procTimes[procTimes.length/2]
//                        + procTimes[procTimes.length/2 - 1])/2;
//                medianUploadDelay = (uploadDelays[uploadDelays.length/2]
//                        + uploadDelays[uploadDelays.length/2 - 1])/2;
//                medianDownloadDelay = (downloadDelays[downloadDelays.length/2]
//                        + downloadDelays[downloadDelays.length/2 - 1])/2;
//            }
//            else {
//                medianProcTime = procTimes[procTimes.length / 2];
//                medianUploadDelay = uploadDelays[uploadDelays.length / 2];
//                medianDownloadDelay = downloadDelays[downloadDelays.length / 2];
//            }
//
//            //set every option to the same values
//            for (TaskOption option: task.getTaskOptions()) {
//
//                option.setWorkRemaining(workRemaining + medianProcTime + medianUploadDelay + medianDownloadDelay);
//
////                option.setNumOpsRemaining(numOpsRemaining);
////
////                option.setNextProcTime(nextProcTime);
//            }
//
//            //workRemaining is a variable for the whole machines, but for one specific machine
//            workRemaining += medianProcTime + medianUploadDelay + medianDownloadDelay;
//
//            nextProcTime = medianProcTime; //average guess
//            //nextProcTime is the median value of processing time.
//            //in flexible job shop scheduling, we have different processing times, but we do not know the next job will
//            //be assigned to which machine, so guess a value (use median time as next processing time)
//        }
//        totalProcTime = workRemaining;
//        avgProcTime = totalProcTime / taskList.size();
//    }

    public void setCompletionTime(double completionTime) {
        this.completionTime = completionTime;
    }

    public double getCompletionTime() {
        return completionTime;
    }

    public double getFlowTime() {
        return completionTime - releaseTime; // the time period between the job arrives and the job is finished. Including the waiting time
    }

    public void addProcessFinishEvent(ProcessFinishEvent processFinishEvent) {
        processFinishEvents.add(processFinishEvent);
        if(processFinishEvents.size() > taskList.size()){
            System.out.println("error! The size of processFinishEvents can not bigger than the size of taskList!");
        }
    }

    public int getCompletedTaskNumber(){
        return processFinishEvents.size();
    }

    public JobType getJobType() {
        return jobType;
    }

    public MobileDevice getMobileDevice() {
        return mobileDevice;
    }

    public double getReleaseTime() {
        return releaseTime;
    }

    public double getWeight() {
        return weight;
    }

    public boolean isDone(){
        boolean allTaskDone = true;
        for(Task task: taskList){
            if(!task.isComplete()){
                allTaskDone = false;
                break;
            }
        }
        return allTaskDone;
    }

    public boolean isHasAddToCompletedList(){
        return hasAddToCompletedList;
    }

    public void setHasAddToCompletedList(boolean hasAddToCompletedList) {
        this.hasAddToCompletedList = hasAddToCompletedList;
    }

    //for CPOP algorithm
    public double getCriticalPathLength(){
        double criticalPathLength = 0;
        for(Task task:taskList){
            if(task.getParentTaskList().size() == 0){
                if(criticalPathLength < task.getUPDOWNRank()){
                    criticalPathLength = task.getUPDOWNRank();
                    criticalPathStartTask = task;
                }
            }
        }
        return criticalPathLength;
    }

    public List<Task> getCriticalPath(){
        double criticalPathLength = getCriticalPathLength();
        List<Task> criticalPath = new ArrayList<>();
        criticalPath.add(criticalPathStartTask);
        //todo: need modify for CPOP, this is not stop
        while(criticalPath.get(criticalPath.size()-1).getChildTaskList().size() != 0){
            Task currentTask = criticalPath.get(criticalPath.size()-1);
            for(Task task:currentTask.getChildTaskList()){
                if(task.getUPDOWNRank() >= criticalPathLength && task.getUPDOWNRank() <= criticalPathLength){
                    criticalPath.add(task);
                }
            }
        }
        return criticalPath;
    }

    public double getCriticalPathProcessTime(){
        double time = 0;
        for(Task task:getCriticalPath()){
            time += task.getMeanProcessTime();
        }
        return time;
    }
}
