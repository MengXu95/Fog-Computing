package mengxu.taskscheduling;

import mengxu.simulation.event.ProcessFinishEvent;

import java.util.ArrayList;
import java.util.List;

public class Job {
    private final int id;
    private final double releaseTime;
    private final double weight;
    private MobileDevice mobileDevice;
//    private List<JobOption> jobOptions;
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
               MobileDevice mobileDevice,
               List<Task> taskList,
               JobType jobType) {
        this.id = id;
        this.releaseTime = releaseTime;
        this.weight = weight;
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

    public List<ProcessFinishEvent> getProcessFinishEvents() {
        return processFinishEvents;
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
//    public double getCriticalPathLength(){
//        double criticalPathLength = 0;
//        for(Task task:taskList){
//            if(task.getParentTaskList().size() == 0){
//                if(criticalPathLength < task.getUPDOWNRank()){
//                    criticalPathLength = task.getUPDOWNRank();
//                    criticalPathStartTask = task;
//                }
//            }
//        }
//        return criticalPathLength;
//    }

//    public List<Task> getCriticalPath(){
//        double criticalPathLength = getCriticalPathLength();
//        List<Task> criticalPath = new ArrayList<>();
//        criticalPath.add(criticalPathStartTask);
//        //todo: need modify for CPOP, this is not stop
//        while(criticalPath.get(criticalPath.size()-1).getChildTaskList().size() != 0){
//            Task currentTask = criticalPath.get(criticalPath.size()-1);
//            for(Task task:currentTask.getChildTaskList()){
//                if(task.getUPDOWNRank() >= criticalPathLength && task.getUPDOWNRank() <= criticalPathLength){
//                    criticalPath.add(task);
//                }
//            }
//        }
//        return criticalPath;
//    }

//    public double getCriticalPathProcessTime(){
//        double time = 0;
//        for(Task task:getCriticalPath()){
//            time += task.getMeanProcessTime();
//        }
//        return time;
//    }

    @Override
    public String toString() {
        return  "ID=" + this.getId() +
                ", releaseTime=" + this.getReleaseTime() +
                ", taskNumber=" + this.taskList.size() +
                ", taskCompleted=" + this.getProcessFinishEvents().size() +
                ", done=" + this.isDone() +
                '}';
    }
}
