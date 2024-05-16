package mengxu.taskscheduling;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.rule.server.WIQ;
import mengxu.simulation.RoutingDecisionSituation;
import mengxu.simulation.state.SystemState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task {
    private int id;
    private Job job;
    private List<Task> parentTaskList;
    private List<Task> childTaskList;

    private double workload; //used to calculate the processing time
    private double data; //used to calculate the communicate time

//    private List<Double> communicateTime;

    private Map<Integer, Double> inputDateMap;
    private Map<Integer, Double> outputDateMap;
//    private List<Double> inputDateMap;
    private double totalInputData;
    private double totalOutputData;

    private boolean complete;
    private boolean dispatch;
    private List<TaskOption> taskOptions;
    public Task(int id, double workload, double data){
        this.id = id;
        this.workload = workload;
        this.data = data;
        this.parentTaskList = new ArrayList<>();
        this.childTaskList = new ArrayList<>();

//        this.inputDate = new ArrayList<>();
        this.inputDateMap = new HashMap<>();//id,data
        this.outputDateMap = new HashMap<>();
        this.totalInputData = 0;
        this.totalOutputData = 0;
//        this.communicateTime = new ArrayList<>();

        this.complete = false;
        this.dispatch = false;
        this.taskOptions = new ArrayList<>();
    }

    public Task(int id){//modified by mengxu 2021.09.14 for generate wirkflow
        this.id = id;
        this.parentTaskList = new ArrayList<>();
        this.childTaskList = new ArrayList<>();
        this.workload = -1;
        this.data = -1;

        this.inputDateMap = new HashMap<>();//id,data
        this.outputDateMap = new HashMap<>();
        this.totalInputData = 0;
        this.totalOutputData = 0;
//        this.communicateTime = new ArrayList<>();

        this.complete = false;
        this.dispatch = false;
        this.taskOptions = new ArrayList<>();
    }

    public void setJob(Job job){
        this.job = job;
    }

    public int getId() {
        return id;
    }

    public void setWorkload(double workload) {
        this.workload = workload;
    }

    public void setData(double data) {
        this.data = data;
    }

    public double getWorkload() {
        return workload;
    }

    public double getData() {
        return data;
    }

    public Task getNext() {
        for(Task task:childTaskList){
            boolean allParentDone = true;
            for(Task parent:task.getParentTaskList()){
                if(!parent.isComplete()){
                    allParentDone = false;
                }
            }
            if(allParentDone){
                return task;
            }
        }
        return null;
    }

    public List<Task> getNextDag() {
        List<Task> taskList = new ArrayList<>();
        boolean jobDone = true;
        for(Task task: job.getTaskList()){
            if(!task.isComplete() && !task.isDispatch()){
                jobDone = false;
                boolean allParentDone = true;
                for(Task parent:task.getParentTaskList()){
                    if(!parent.isComplete()){
                        allParentDone = false;
                    }
                }
                if(allParentDone){
                    taskList.add(task);
                }
            }
        }
        if(jobDone || taskList.size() == 0){
            return null;
        }
        return taskList;
    }

    public TaskOption chooseTaskOption(SystemState systemState, AbstractRule routingRule) {

        RoutingDecisionSituation decisionSituation = routingDecisionSituation(systemState);

        if (routingRule == null) {
            routingRule = new WIQ(RuleType.ROUTING);//need modified
        }
        return routingRule.nextTaskOption(decisionSituation);
    }

    public RoutingDecisionSituation routingDecisionSituation(SystemState systemState) {
        return new RoutingDecisionSituation(taskOptions,systemState);
    }

    public List<Task> getParentTaskList() {
        return parentTaskList;
    }

    public List<Task> getChildTaskList() {
        return childTaskList;
    }


    public boolean isComplete() {
        return complete;
    }

    public boolean isDispatch() {
        return dispatch;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setDispatch(boolean dispatch) {
        this.dispatch = dispatch;
    }

    public Job getJob() {
        return job;
    }

    public List<TaskOption> getTaskOptions() { return taskOptions; }

    public void addTaskOption(TaskOption option) {
        taskOptions.add(option);
    }

    public void addParent(Task task){
        this.parentTaskList.add(task);
    }
    public void addParentInputData(int parentID, double inputData){
        this.inputDateMap.put(parentID,inputData);
        this.totalInputData = this.totalInputData + inputData;
    }

    public double getAllInputDataTotal(){
        return this.totalInputData;
    }

    public double getChildOutputData(int childID){
        return this.outputDateMap.get(childID);
    }

    public void addChild(Task task){
        this.childTaskList.add(task);
    }

    public void addChildOutputData(int childID, double outputData){
        this.outputDateMap.put(childID,outputData);
        this.totalOutputData = this.totalOutputData + outputData;
    }

    //add 2021.09.17
    public void mapClear(){
        this.inputDateMap.clear();
        this.outputDateMap.clear();
    }

    public double getAllOutputDataTotal(){
        return this.totalOutputData;
    }

//    public void addCommunicateTime(double time){
//        this.communicateTime.add(time);
//    }
//
//    public double getCommunicateTime(int succTaskID){
//        return this.communicateTime.get(succTaskID);
//    }
//
//    public double getMaxCommunicateTime(){
//        double mean = 0;
//        if(communicateTime.size()==0){
//            return 0;
//        }
//        for(double time:communicateTime){
//            if(mean < time){
//                mean = time;
//            }
//
//        }
//        return mean;
//    }

    public double getMeanProcessTime(){
        double sumProcessTime = 0;
        for(TaskOption taskOption:taskOptions){
            sumProcessTime += taskOption.getProcTime();
        }
        return sumProcessTime/ taskOptions.size();
    }

    public double getMeanDownloadTime(){
        double meanDownloadTime = 0;
        for(TaskOption taskOption:taskOptions){
            meanDownloadTime += taskOption.getDownloadDelay();
        }
        meanDownloadTime = meanDownloadTime/ taskOptions.size();
        return meanDownloadTime;
    }

    public double getMeanUploadTime(){
        double meanUploadTime = 0;
        for(TaskOption taskOption:taskOptions){
            meanUploadTime += taskOption.getUploadDelay();
        }
        meanUploadTime = meanUploadTime/ taskOptions.size();
        return meanUploadTime;
    }

    public double getMeanCommunicationTimeFromParent(int parentIndex){
        //todo: need to modify, different with the  (wrong I think )
        //the communication time for taskOption is defined as the (uploadDelay + downloadDelay)/2
        double sumCommunicateTime = 0;

        double meanDownloadTimeParentIndex = 0;
        for(TaskOption taskOption:parentTaskList.get(parentIndex).getTaskOptions()){
            meanDownloadTimeParentIndex += taskOption.getDownloadDelay();
        }
        meanDownloadTimeParentIndex = meanDownloadTimeParentIndex/parentTaskList.get(parentIndex).getTaskOptions().size();

        double meanUploadTime = getMeanUploadTime();

        sumCommunicateTime = meanDownloadTimeParentIndex + meanUploadTime;
        return sumCommunicateTime;
    }

    public double getMeanCommunicationTimeToChild(int childIndex){
        //the communication time for taskOption is defined as the (uploadDelay + downloadDelay)/2
        double sumCommunicateTime = 0;

        double meanDownloadTime = getMeanDownloadTime();

        double meanUpDownloadTimeChildIndex = 0;
        for(TaskOption taskOption:childTaskList.get(childIndex).getTaskOptions()){
            meanUpDownloadTimeChildIndex += taskOption.getUploadDelay();
        }
        meanUpDownloadTimeChildIndex = meanUpDownloadTimeChildIndex/childTaskList.get(childIndex).getTaskOptions().size();

        sumCommunicateTime = meanDownloadTime + meanUpDownloadTimeChildIndex;
        return sumCommunicateTime;
    }

    //only use for HEFT algorithm
    public double getUpwardRank(){
        if(childTaskList.size() == 0){
//            return getMeanProcessTime() + getMeanDownloadTime();//original
            return getMeanProcessTime() + getMeanDownloadTime() - this.job.getReleaseTime();//as the workflows arrive over time.
        }
        double upwardRank = getMeanProcessTime();
        double max = 0;
//        for(Task task:childTaskList){
//            double ref = task.getMeanCommunicationTime() + task.getUpwardRank();
        for(int i=0; i< childTaskList.size(); i++){
            double ref = this.getMeanCommunicationTimeToChild(i) + childTaskList.get(i).getUpwardRank();
            if(max < ref){
                max = ref;
            }
        }
//        DecimalFormat df = new DecimalFormat("#.000");
//        return Double.parseDouble((df.format(upwardRank + max)));
        if(parentTaskList.size()==0){//modified by mengxu 2021.09.09
            return upwardRank + max + getMeanUploadTime();
        }
        return upwardRank + max;
    }

    //add by mengxu 2022.07.27, only for DMWHDBS algorithm <need check!>
    public double getOCCW(){
        //todo: modified based on function getMeanCommunicationTimeToChild(int childIndex), need to check

        //step 1: calculate the total download time
        //todo:this might not right, need to check to use mean download time among all the options?
        double totalDownloadTime = 0;
        for(TaskOption taskOption:taskOptions){
            totalDownloadTime += taskOption.getDownloadDelay();
        }

        //step 2: calculate the total upload time with the successors
        //todo:this might not right, need to check to use mean upload time among all the options?
        double totalUploadTime = 0;
        for(int i=0; i< childTaskList.size(); i++){
            for(TaskOption taskOption:childTaskList.get(i).getTaskOptions()){
                totalUploadTime += taskOption.getUploadDelay();
            }
        }

        //the total communication time
        double sumCommunicateTime = totalDownloadTime + totalUploadTime;

        return sumCommunicateTime;
    }

    //add by mengxu 2022.07.27, only for DMWHDBS algorithm <need check!>
    //need to notice this upward rank is different with that in HEFT. The details can be seen in the paper.
    //todo: need to check
    public double getUpwardRankForDMWHDBS(){
        if(childTaskList.size() == 0){
            return getMeanProcessTime() * 1;//as in our problem, the computation cost is zero.
        }

        double upwardRank = getMeanProcessTime() * 1 + getOCCW();
        double max = 0;
        for(int i=0; i< childTaskList.size(); i++){
            double ref = childTaskList.get(i).getUpwardRank();
            if(max < ref){
                max = ref;
            }
        }

        return upwardRank + max;
    }

    //add by mengxu 2022.07.28, only for BWAWA algorithm <need check!>
    //need to notice this downward rank is different with that in HEFT. The details can be seen in the paper.
    //todo: need to check
    public double getDownwardRankForBWAWA(){
        if(parentTaskList.size() == 0){
            return this.job.getReleaseTime();// we need to consider the release time of workflow as the workflow arrive over time in our problem
//            return 0;//the original. as in our problem, the computation cost is zero.
        }

        double downwardRank = getMeanProcessTime();
//        double sumCommunicationTime = 0;
//        for(int i=0; i<parentTaskList.size();i++){
//            sumCommunicationTime += getMeanCommunicationTimeFromParent(i);
////            sumCommunicationTime += getMeanCommunicationTimeFromParent(parentTaskList.get(i).id);
//        }
//        downwardRank += sumCommunicationTime/parentTaskList.size();

        //I should use the max communication cost of all the parents in our problem as we want to give an order of all the tasks in the list.
        double maxCommunicationTime = 0;
        for(int i=0; i<parentTaskList.size();i++){
            double ref = getMeanCommunicationTimeFromParent(i);
            if(maxCommunicationTime < ref){
                maxCommunicationTime = ref;
            }
        }
        downwardRank += maxCommunicationTime;

        double max = 0;
        for(int i=0; i< parentTaskList.size(); i++){
            double ref = parentTaskList.get(i).getDownwardRankForBWAWA();
            if(max < ref){
                max = ref;
            }
        }

        if(childTaskList.size()==0){//add by mengxu 2022.08.03
            return downwardRank + max + getMeanDownloadTime();
        }

        return downwardRank + max;
    }


    //add by mengxu 2022.07.30, only for SDLS algorithm <need check!>
    //need to notice this downward rank is different with that in HEFT. But based on my implemented, it seems that
    //this Sb_Level is very similar with the Upward of HEFT.
    //todo: need to check
    public double getSb_LevelForSDLS(){
        if(childTaskList.size() == 0){
            double meanProcessingRate = 0;
            for(int k=0; k<this.getTaskOptions().size(); k++){
                if(this.getTaskOptions().get(k).getServer() == null){
                    meanProcessingRate += this.getTaskOptions().get(k).getMobileDevice().getProcessingRate();
                }
                else {
                    meanProcessingRate += this.getTaskOptions().get(k).getServer().getProcessingRate();
                }
            }
            meanProcessingRate = meanProcessingRate/this.getTaskOptions().size();
            double meanProcessTime = this.workload/meanProcessingRate;
//            return meanProcessTime + getMeanDownloadTime();//original
            return meanProcessTime + getMeanDownloadTime() - this.job.getReleaseTime();
            //we consider the release time of workflow in our problem as the workflows arrive over time.
        }

        double meanProcessingRate = 0;
        for(int k=0; k<this.getTaskOptions().size(); k++){
            if(this.getTaskOptions().get(k).getServer() == null){
                meanProcessingRate += this.getTaskOptions().get(k).getMobileDevice().getProcessingRate();
            }
            else {
                meanProcessingRate += this.getTaskOptions().get(k).getServer().getProcessingRate();
            }
        }
        meanProcessingRate = meanProcessingRate/this.getTaskOptions().size();
        double meanProcessTime = this.workload/meanProcessingRate;

        double max = 0;
        for(int i=0; i< childTaskList.size(); i++){
            double ref = this.getMeanCommunicationTimeToChild(i) + childTaskList.get(i).getSb_LevelForSDLS();
            if(max < ref){
                max = ref;
            }
        }

        if(parentTaskList.size()==0){//modified by mengxu 2022.08.03
            return meanProcessTime + max + getMeanUploadTime();
        }

        return meanProcessTime + max;
    }

    @Override
    public String toString() {
        return  "ID=" + this.getId() +
                ", done=" + this.complete +
                '}';
    }


//    public double getDownwardRank(){
//        if(parentTaskList.size() == 0){
//            return getMeanUploadTime();
//        }
//        double downwardRank = 0;
//        for(Task task:parentTaskList){
////            double ref = task.getMeanProcessTime() + task.getMeanCommunicationTime() + task.getDownwardRank();
//            int index = 0;
//            for(int i=0;i<task.getChildTaskList().size();i++){
//                if(task.getChildTaskList().get(i).getId() == this.getId()){
//                   index = i;
//                   break;
//                }
//            }
//            double ref = task.getMeanProcessTime() + task.getMeanCommunicationTimeFromParent(index) + task.getDownwardRank();
//            if(downwardRank < ref){
//                downwardRank = ref;
//            }
//        }
////        DecimalFormat df = new DecimalFormat("#.000");
////        return Double.parseDouble((df.format(downwardRank)));
//        return downwardRank;
//    }

//    public double getUPDOWNRank(){
//        return getUpwardRank() + getDownwardRank();
//    }

}
