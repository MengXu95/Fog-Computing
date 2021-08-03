package mengxu.simulation.event;

import mengxu.simulation.RoutingDecisionSituation;
import mengxu.simulation.SequencingDecisionSituation;
import mengxu.taskscheduling.*;
import mengxu.taskscheduling.Process;

import java.util.List;

public class ProcessFinishEvent extends AbstractEvent{

    private Process process;
    //fzhang 29.8.2018 in order to record the completion time of jobs
    protected long jobSeed;

    public ProcessFinishEvent(double time, Process process, MobileDevice mobileDevice) {
        super(time, mobileDevice);
        this.process = process;
    }

    public ProcessFinishEvent(Process process, MobileDevice mobileDevice) {
        this(process.getFinishTime(), process, mobileDevice);
    }

    @Override
    public void trigger(MobileDevice mobileDevice) {
        if(process.getServer() == null) {//choose mobileDevice to process this task
            process.getTaskOption().getTask().getJob().addProcessFinishEvent(this);
            process.getTaskOption().getTask().setComplete(true);
            mobileDevice.addNumTasksCompleted();

            if (!mobileDevice.getQueue().isEmpty()) {
                SequencingDecisionSituation sequencingDecisionSituation =
                        new SequencingDecisionSituation(mobileDevice.getQueue(), null,
                                mobileDevice.getSystemState());

                //System.out.println("=======================================sequencing==========================================");
                TaskOption dispatchedTask =
                        mobileDevice.getSequencingRule().priorTask(sequencingDecisionSituation);

                mobileDevice.removeFromQueue(dispatchedTask);

                //must wait for machine to be ready
                double processStartTime = Math.max(mobileDevice.getReadyTime(), time);

                Process nextP = new Process(null, dispatchedTask, processStartTime);
                mobileDevice.addEvent(new ProcessStartEvent(nextP, mobileDevice));
            }

            if(process.getTaskOption().getTask().getJob().getJobType() == JobType.DAG){
                if (process.getTaskOption().getNextDag(mobileDevice.getSystemState(),
                        mobileDevice.getRoutingRule()) == null) {
                    Job job = process.getTaskOption().getTask().getJob();
                    if(job.isDone() && !job.isHasAddToCompletedList()){
                        job.setCompletionTime(process.getFinishTime());
                        mobileDevice.completeJob(job);
                        job.setHasAddToCompletedList(true);
                    }
                }
                else {
                    List<TaskOption> nextTaskList = process.getTaskOption().getNextDag(mobileDevice.getSystemState(),
                            mobileDevice.getRoutingRule());
                    for(TaskOption nextTask:nextTaskList){
                        double taskVisitTime = time + nextTask.getUploadDelay();
                        mobileDevice.addEvent(new TaskVisitEvent(taskVisitTime, nextTask, mobileDevice));
//                        nextTask.getTask().setDispatch(true);//modified by mengxu 2021.08.03
                    }
                }
            }
            else if(process.getTaskOption().getTask().getJob().getJobType() == JobType.LINKED){
                TaskOption nextTask = process.getTaskOption().getNext(mobileDevice.getSystemState(),
                        mobileDevice.getRoutingRule());

                if (nextTask == null) {
                    Job job = process.getTaskOption().getTask().getJob();
                    if(job.isDone() && !job.isHasAddToCompletedList()){
                        job.setCompletionTime(process.getFinishTime());
                        mobileDevice.completeJob(job);
                        job.setHasAddToCompletedList(true);
                    }
                }
                else {
                    double taskVisitTime = time + nextTask.getUploadDelay();
                    mobileDevice.addEvent(new TaskVisitEvent(taskVisitTime, nextTask, mobileDevice));
//                    nextTask.getTask().setDispatch(true);//modified by mengxu 2021.08.03
                }
            }
            else{
                System.out.println("Error! Job type is neither LINKED or DAG!");
            }
        }
        else{
            Server server = process.getServer();
            process.getTaskOption().getTask().getJob().addProcessFinishEvent(this);
            process.getTaskOption().getTask().setComplete(true);
            server.addNumTasksCompleted();

//            System.out.println("task " + (process.getTaskOption().getTask().getId()+1) + " completed at processor " +  (server.getId()+1) + " at time " + process.getFinishTime());

            if (!server.getQueue().isEmpty()) {
                SequencingDecisionSituation sequencingDecisionSituation =
                        new SequencingDecisionSituation(server.getQueue(), server,
                                mobileDevice.getSystemState());

                //System.out.println("=======================================sequencing==========================================");
                TaskOption dispatchedTask =
                        mobileDevice.getSequencingRule().priorTask(sequencingDecisionSituation);

                server.removeFromQueue(dispatchedTask);

                //must wait for machine to be ready
                double processStartTime = Math.max(server.getReadyTime(), time);

                Process nextP = new Process(server, dispatchedTask, processStartTime);
                mobileDevice.addEvent(new ProcessStartEvent(nextP, mobileDevice));
            }

            if(process.getTaskOption().getTask().getJob().getJobType() == JobType.DAG){
                if (process.getTaskOption().getNextDag(mobileDevice.getSystemState(),
                        mobileDevice.getRoutingRule()) == null) {
                    Job job = process.getTaskOption().getTask().getJob();
                    if(job.isDone() && !job.isHasAddToCompletedList()){
                        job.setCompletionTime(process.getFinishTime());
                        mobileDevice.completeJob(job);
                        job.setHasAddToCompletedList(true);
                    }
                }
                else {
                    List<TaskOption> nextTaskList = process.getTaskOption().getNextDag(mobileDevice.getSystemState(),
                            mobileDevice.getRoutingRule());
                    for(TaskOption nextTask:nextTaskList){
//                        //modified by mengxu for HEFT test
//                        double startTime = 0;
//                        if(process.getTaskOption().getServer().getId() == nextTask.getServer().getId()){
//                            startTime = time;
//                        }
//                        else{
//                            List<Task> child = process.getTaskOption().getTask().getChildTaskList();
//                            int index = 0;
//                            for(int i=0; i<child.size(); i++){
//                                if(child.get(i).getId() == nextTask.getTask().getId()){
//                                    index = i;
//                                    break;
//                                }
//                            }
//                            startTime = time + process.getTaskOption().getTask().getCommunicateTime(index);
//                        }
//                        mobileDevice.addEvent(new TaskVisitEvent(startTime, nextTask));
                        double taskVisitTime = time + nextTask.getUploadDelay();
                        mobileDevice.addEvent(new TaskVisitEvent(taskVisitTime, nextTask, mobileDevice));
//                        nextTask.getTask().setDispatch(true);//modified by mengxu 2021.08.03
                    }
                }
            }
            else if(process.getTaskOption().getTask().getJob().getJobType() == JobType.LINKED){
                TaskOption nextTask = process.getTaskOption().getNext(mobileDevice.getSystemState(),
                        mobileDevice.getRoutingRule());

                if (nextTask == null) {
                    Job job = process.getTaskOption().getTask().getJob();
                    job.setCompletionTime(process.getFinishTime());
                    mobileDevice.completeJob(job);
                    job.setHasAddToCompletedList(true);
                }
                else {
                    double taskVisitTime = time + nextTask.getUploadDelay();
                    mobileDevice.addEvent(new TaskVisitEvent(taskVisitTime, nextTask, mobileDevice));
//                    nextTask.getTask().setDispatch(true);//modified by mengxu 2021.08.03
                }
            }
            else{
                System.out.println("Error! Job type is neither LINKED or DAG!");
            }
        }


    }


    //todo: need modify
    @Override
    public void addSequencingDecisionSituation(MobileDevice mobileDevice,
                                               List<SequencingDecisionSituation> situations,
                                               int minQueueLength) {
        trigger(mobileDevice);//modified by mengxu
        //original
//        Server server = process.getServer();
//        process.getTaskOption().getTask().getJob().addProcessFinishEvent(this);
//        process.getTaskOption().getTask().setComplete(true);
//
//        if (!server.getQueue().isEmpty()) {
//            SequencingDecisionSituation sequencingDecisionSituation =
//                    new SequencingDecisionSituation(server.getQueue(), server,
//                            mobileDevice.getSystemState());
//
//          /*  if (workCenter.getQueue().size() >= minQueueLength) { //when set operation with different processing time, the queue is hard to >= minQueueLength, an error happen here
//                situations.add(sequencingDecisionSituation.clone());
//            }
//*/
//            //fzhang 2019.9.4 change all the decison size as minQueueLength, in order to keep the matrix has the same length
//            if (server.getQueue().size() == minQueueLength) { //when set operation with different processing time, the queue is hard to >= minQueueLength, an error happen here
//                situations.add(sequencingDecisionSituation.clone());
//            }
//
//            TaskOption dispatchedTask =
//                    mobileDevice.getSequencingRule().priorTask(sequencingDecisionSituation);
//
//            server.removeFromQueue(dispatchedTask);
//
//            //must wait for machine to be ready
//            double processStartTime = Math.max(server.getReadyTime(), time);
//
//            Process nextP = new Process(server, dispatchedTask, processStartTime);
//            mobileDevice.addEvent(new ProcessStartEvent(nextP));
//        }
//
//        TaskOption nextTask = process.getTaskOption().getNext(mobileDevice.getSystemState(),
//                mobileDevice.getRoutingRule());
//
//        if (nextTask == null) {
//            Job job = process.getTaskOption().getTask().getJob();
//            job.setCompletionTime(process.getFinishTime());
//            mobileDevice.completeJob(job);
//        }
//        else {
//            mobileDevice.addEvent(new TaskVisitEvent(time, nextTask));
//        }
    }


    //todo: need modify
    @Override
    public void addRoutingDecisionSituation(MobileDevice mobileDevice,
                                            List<RoutingDecisionSituation> situations,
                                            int minOptions) {

        trigger(mobileDevice);//modified by mengxu
        //original
//        Server server = process.getServer();
//        process.getTaskOption().getTask().getJob().addProcessFinishEvent(this);
//        process.getTaskOption().getTask().setComplete(true);
//
//        if (!server.getQueue().isEmpty()) {
//            SequencingDecisionSituation sequencingDecisionSituation =
//                    new SequencingDecisionSituation(server.getQueue(), server,
//                            mobileDevice.getSystemState());
//
//            TaskOption dispatchedTask =
//                    mobileDevice.getSequencingRule().priorTask(sequencingDecisionSituation);
//
//            server.removeFromQueue(dispatchedTask);
//
//            //must wait for machine to be ready
//            double processStartTime = Math.max(server.getReadyTime(), time);
//
//            Process nextP = new Process(server, dispatchedTask, processStartTime);
//            mobileDevice.addEvent(new ProcessStartEvent(nextP));
//        }
//
//       /* if (process.getOperationOption().getOperation().getNext() != null) {
//            if (process.getOperationOption().getOperation().getNext().getOperationOptions().size()
//                    >= minOptions) {
//                Operation o = process.getOperationOption().getOperation();
//                RoutingDecisionSituation r = o.getNext().routingDecisionSituation(simulation.getSystemState());
//                situations.add(r.clone());
//            }
//        }*/
//
//        //fzhang 2019.9.25 change all the decison size as minQueueLength, in order to keep the matrix has the same length
//        if (process.getTaskOption().getTask().getNext() != null) {
//            if (process.getTaskOption().getTask().getNext().getTaskOptions().size()
//                    == minOptions) {
//                Task task = process.getTaskOption().getTask();
//                RoutingDecisionSituation r = task.getNext().routingDecisionSituation(mobileDevice.getSystemState());
//                situations.add(r.clone());
//            }
//        }
//
//        TaskOption nextTask = process.getTaskOption().getNext(mobileDevice.getSystemState(),
//                mobileDevice.getRoutingRule());
//
//        if (nextTask == null) {
//            Job job = process.getTaskOption().getTask().getJob();
//            job.setCompletionTime(process.getFinishTime());
//            mobileDevice.completeJob(job);
//        }
//        else {
//            mobileDevice.addEvent(new TaskVisitEvent(time, nextTask));
//        }
    }


    @Override
    public String toString() {
        return "mengxu";
    }

    @Override
    public int compareTo(AbstractEvent other) {
        if (time < other.time)
            return -1;

        if (time > other.time)
            return 1;

        if (other instanceof ProcessFinishEvent) {
            ProcessFinishEvent otherPFE = (ProcessFinishEvent)other;

            //modified by mengxu 2021.05.27
            if(process.getServer() == null && otherPFE.getProcess().getServer() == null){
                if(process.getTaskOption().getTask().getJob().getMobileDevice().getId() < otherPFE.process.getTaskOption().getTask().getJob().getMobileDevice().getId()){
                    return -1;
                }
                if(process.getTaskOption().getTask().getJob().getMobileDevice().getId() > otherPFE.process.getTaskOption().getTask().getJob().getMobileDevice().getId()){
                    return 1;
                }
            }
            else if(process.getServer() == null && otherPFE.getProcess().getServer() != null){
                return -1;
            }
            else if(process.getServer() != null && otherPFE.getProcess().getServer() == null){
                return 1;
            }
            else{
                if (process.getServer().getId() < otherPFE.process.getServer().getId())
                    return -1;

                if (process.getServer().getId() > otherPFE.process.getServer().getId())
                    return 1;
            }

//            //original-----------------
//            if (process.getServer().getId() < otherPFE.process.getServer().getId())
//                return -1;
//
//            if (process.getServer().getId() > otherPFE.process.getServer().getId())
//                return 1;
//            //-------------------
        }

        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessFinishEvent that = (ProcessFinishEvent) o;

        return process != null ? process.equals(that.process) : that.process == null;
    }

    @Override
    public int hashCode() {
        return process != null ? process.hashCode() : 0;
    }


    public Process getProcess() {
        return process;
    }
}
