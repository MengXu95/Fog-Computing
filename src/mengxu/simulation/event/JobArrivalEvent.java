package mengxu.simulation.event;

import mengxu.simulation.RoutingDecisionSituation;
import mengxu.simulation.SequencingDecisionSituation;
import mengxu.taskscheduling.*;

import java.util.List;

public class JobArrivalEvent extends AbstractEvent{

    private Job job;

    public JobArrivalEvent(double time, Job job, MobileDevice mobileDevice) {
        super(time, mobileDevice);
        this.job = job;
    }

    public JobArrivalEvent(Job job, MobileDevice mobileDevice) {
        this(job.getReleaseTime(), job, mobileDevice);
    }

    @Override
    public void trigger(MobileDevice mobileDevice) {
        if(job.getJobType() == JobType.LINKED){
            Task task = job.getTaskList().get(0);

            for (TaskOption taskOption : task.getTaskOptions())
                taskOption.setReadyTime(job.getReleaseTime());

            //get options of operation, and SystemState
            RoutingDecisionSituation decisionSituation = new RoutingDecisionSituation(
                    task.getTaskOptions(), mobileDevice.getSystemState());

            //System.out.println("===================routing=============");
            //use routing rule to decide which option we will use !!!!!
            TaskOption taskOption =
                    mobileDevice.getRoutingRule().nextTaskOption(decisionSituation);

            //operationOption.setReadyTime(job.getReleaseTime());  //yimei 2019.7.30 move it to above   before routing, the ready time should be set to clocktime

            double taskVisitTime = job.getReleaseTime() + taskOption.getUploadDelay();
            mobileDevice.addEvent(new TaskVisitEvent(taskVisitTime, taskOption, mobileDevice));
//            taskOption.getTask().setDispatch(true);//modified by mengxu 2021.08.03
            mobileDevice.generateJob();//todo: need modified
        }
        else if(job.getJobType() == JobType.DAG){
            List<Task> tasklist = job.getFirstArriveReadyTask();

            for(Task task:tasklist){
                for (TaskOption taskOption : task.getTaskOptions())
                    taskOption.setReadyTime(job.getReleaseTime());

                //get options of operation, and SystemState
                RoutingDecisionSituation decisionSituation = new RoutingDecisionSituation(
                        task.getTaskOptions(), mobileDevice.getSystemState());

                //System.out.println("===================routing=============");
                //use routing rule to decide which option we will use !!!!!
                TaskOption taskOption =
                        mobileDevice.getRoutingRule().nextTaskOption(decisionSituation);


                //operationOption.setReadyTime(job.getReleaseTime());  //yimei 2019.7.30 move it to above   before routing, the ready time should be set to clocktime

                double taskVisitTime = job.getReleaseTime() + taskOption.getUploadDelay();

//                //for print
//                Server server = taskOption.getServer();
//                if(server.getType() == ServerType.CLOUD){
//                    System.out.println("task " + taskOption.getTask().getId() + " is started to be uploaded to " + server.getType() + " " + (server.getId()-5) + " at time " + job.getReleaseTime());
//                }
//                else if(server.getType() == ServerType.EDGE){
//                    System.out.println("task " + taskOption.getTask().getId() + " is started to be uploaded to " + server.getType() + " " + server.getId() + " at time " + job.getReleaseTime());
//                }
//                else{
//                    System.out.println("task " + taskOption.getTask().getId() + " is not uploaded to servers but stay at mobileDevice itself at time" + job.getReleaseTime());
//                }
//
//                if(server.getType() == ServerType.CLOUD){
//                    System.out.println("task " + taskOption.getTask().getId() + " is uploaded to " + server.getType() + " " + (server.getId()-5) + " at time " + taskVisitTime);
//                }
//                else if(server.getType() == ServerType.EDGE){
//                    System.out.println("task " + taskOption.getTask().getId() + " is uploaded to " + server.getType() + " " + server.getId() + " at time " + taskVisitTime);
//                }
//                else{
//                    System.out.println("task " + taskOption.getTask().getId() + " is not uploaded to servers but stay at mobileDevice itself at time" + taskVisitTime);
//                }

                mobileDevice.addEvent(new TaskVisitEvent(taskVisitTime, taskOption, mobileDevice));
//                taskOption.getTask().setDispatch(true);//modified by mengxu 2021.08.03
            }

            //modified by mengxu 2022.02.22
            if(mobileDevice.getSystemState().getAllNumJobsReleased()<mobileDevice.getNumJobsRecorded()+ mobileDevice.getWarmupJobs()){
                mobileDevice.generateWorkflowJob();
            }

//            mobileDevice.generateWorkflowJob();//modified by mengxu 2021.09.14

            //original
//            mobileDevice.generateJob();
        }

    }

    @Override
    public void addSequencingDecisionSituation(MobileDevice mobileDevice, List<SequencingDecisionSituation> situations, int minQueueLength) {
        trigger(mobileDevice);
    }

    @Override
    public void addRoutingDecisionSituation(MobileDevice mobileDevice, List<RoutingDecisionSituation> situations, int minOptions) {
        trigger(mobileDevice);
    }
}
