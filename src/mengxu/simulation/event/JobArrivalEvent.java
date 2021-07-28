package mengxu.simulation.event;

import mengxu.simulation.DynamicSimulation;
import mengxu.simulation.RoutingDecisionSituation;
import mengxu.simulation.SequencingDecisionSituation;
import mengxu.taskscheduling.*;

import java.util.List;

public class JobArrivalEvent extends AbstractEvent{

    private Job job;

    public JobArrivalEvent(double time, Job job) {
        super(time);
        this.job = job;
    }

    public JobArrivalEvent(Job job) {
        this(job.getReleaseTime(), job);
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
            mobileDevice.addEvent(new TaskVisitEvent(taskVisitTime, taskOption));
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
                mobileDevice.addEvent(new TaskVisitEvent(taskVisitTime, taskOption));
            }
            mobileDevice.generateJob();//todo: need modified
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
