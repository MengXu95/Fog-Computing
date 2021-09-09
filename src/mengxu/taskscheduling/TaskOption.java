package mengxu.taskscheduling;

import com.sun.org.apache.xpath.internal.axes.OneStepIterator;
import mengxu.rule.AbstractRule;
import mengxu.simulation.state.SystemState;

import java.util.ArrayList;
import java.util.List;

public class TaskOption implements Comparable<TaskOption>{

    private final Task task;
    private final Server server;
    private MobileDevice mobileDevice = null;
    private double procTime;

    private double uploadDelay; //modified by mengxu
    private double downloadDelay; //modified by mengxu

    // Attributes for simulation.
    private double readyTime;
    private double priority;

    public TaskOption(Task task, Server server, double procTime, double uploadDelay, double downloadDelay) {
        this.task = task;
        this.server = server;
        this.procTime = procTime;
        this.uploadDelay = uploadDelay;
        this.downloadDelay = downloadDelay;
    }

    public TaskOption(Task task, MobileDevice mobileDevice, double procTime, double uploadDelay, double downloadDelay) {
        this.task = task;
        this.server = null;
        this.procTime = procTime;
        this.uploadDelay = uploadDelay;
        this.downloadDelay = downloadDelay;
        this.mobileDevice = mobileDevice;
    }

    public void setReadyTime(double readyTime) {
        this.readyTime = readyTime;
    }

    public Server getServer() {
        return server;
    }

    public double getReadyTime() {
        return readyTime;
    }

    public Task getTask() {
        return task;
    }

    public void setProcTime(double procTime) {
        this.procTime = procTime;
    }

    public double getProcTime() {
        return procTime;
    }

    public double getDownloadDelay() {
        return downloadDelay;
    }

    public double getUploadDelay() {
        return uploadDelay;
    }

    //    public Task getNext() { return task.getNext(); }

    public TaskOption getNext(SystemState systemState, AbstractRule routingRule) {
        Task nextTask = task.getNext();
        if (nextTask != null) {
            return nextTask.chooseTaskOption(systemState, routingRule);
        } return null;
    }

    public List<TaskOption> getNextDag(SystemState systemState, AbstractRule routingRule) {
        List<TaskOption> taskOptionList = new ArrayList<>();
        if(task.getNextDag()==null){
            return null;
        }
        List<Task> nextTask = task.getNextDag();
        if(nextTask.size()==0){
            System.out.println("Error! Task list size can not be zero!");
        }
        else{
            for(Task task: nextTask){
                taskOptionList.add(task.chooseTaskOption(systemState, routingRule));
            }
        }
        return taskOptionList;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public boolean priorTo(TaskOption other) {
        if (Double.compare(priority, other.priority) < 0)
            return true;

        if (Double.compare(priority, other.priority) > 0)
            return false;

        return task.getJob().getId() < other.task.getJob().getId();
    }

    public MobileDevice getMobileDevice() {
        return mobileDevice;
    }

//    //modified by mengxu 2021.06.03
//    public double getCommunicateTime(){
//        return task.getMaxCommunicateTime();
//        //original
////        return (uploadDelay + downloadDelay)/2;
//    }

    //For HEFT algorithm
    public double getEarliestExecutionStartTime(){
        if(task.getParentTaskList().size() == 0){
            //original
            return this.task.getJob().getReleaseTime();
//            if(server == null){
//                return Math.max(readyTime + getCommunicateTime(), mobileDevice.getReadyTime());
//            }
//            else{
//                return Math.max(readyTime + getCommunicateTime(), server.getReadyTime());
//            } //modified by mengxu 2021.07.23
        }
        //todo: need to check for HEFT. I think it is the ready time. Can ask Xiangtian and Beibei
//        if(server == null){
//            return Math.max(readyTime + getCommunicateTime(), mobileDevice.getReadyTime());
//        }
//        else{
//            return Math.max(readyTime + getCommunicateTime(), server.getReadyTime());
//        }
//        return readyTime;
        if(server == null){
            return Math.max(readyTime, mobileDevice.getReadyTime());
        }
        else{
            return Math.max(readyTime, server.getReadyTime());
        }
//        return Math.max(readyTime, server.getReadyTime());
    }

    public double getEarliestExecutionFinishTime(){
        //todo: need to check if this is right.
        //For my code, the upload time and download time is used respectively.
        //the ready time has added the uploadTime,
        //so I think the EFT has to add the downloadTime,right?
        //Because I think the downloadTime(downloadDelay) from the processor task + the uploadDelay for this task is the communication time between the processor task and this task.
        //todo: need to check and modify where to add uploadDelay and where to add downloadDelay
        return getEarliestExecutionStartTime() + getProcTime();
    }

    @Override
    public int compareTo(TaskOption o) {
        return -1;
    }
}
