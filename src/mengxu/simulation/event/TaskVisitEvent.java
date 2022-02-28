package mengxu.simulation.event;

import mengxu.simulation.RoutingDecisionSituation;
import mengxu.simulation.SequencingDecisionSituation;
import mengxu.taskscheduling.MobileDevice;
import mengxu.taskscheduling.Process;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;

import java.util.List;

public class TaskVisitEvent extends AbstractEvent{
    private TaskOption taskOption;

    public TaskVisitEvent(double time, TaskOption taskOption, MobileDevice mobileDevice) {
        super(time,mobileDevice);
        this.taskOption = taskOption;
        taskOption.getTask().setDispatch(true);//modified by mengxu 2021.08.03
    }

    public TaskVisitEvent(TaskOption task, MobileDevice mobileDevice) {
        this(task.getReadyTime(), task, mobileDevice);
    }

    @Override
    public void trigger(MobileDevice mobileDevice) {
        taskOption.setReadyTime(time);

        if(taskOption.getServer() == null){//choose mobileDevice to process this task
            Process p = new Process(null, taskOption, time);

            if (mobileDevice.getReadyTime() > time || !mobileDevice.canAddToQueue(p)) {
                mobileDevice.addToQueue(taskOption);
            }
            else {
                mobileDevice.addEvent(new ProcessStartEvent(p,mobileDevice));
            }
        }
        else{
            Server server= taskOption.getServer();
            Process p = new Process(server, taskOption, time);

            if (server.getReadyTime() > time || !mobileDevice.canAddToQueue(p)) {
                server.addToQueue(taskOption);
            }
            else {
                mobileDevice.addEvent(new ProcessStartEvent(p,mobileDevice));
            }
        }

    }

    @Override
    public void addSequencingDecisionSituation(MobileDevice mobileDevice,
                                               List<SequencingDecisionSituation> situations,
                                               int minQueueLength) {
        trigger(mobileDevice);
    }

    @Override
    public void addRoutingDecisionSituation(MobileDevice mobileDevice,
                                            List<RoutingDecisionSituation> situations,
                                            int minQueueLength) {
        trigger(mobileDevice);
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

        if (other instanceof JobArrivalEvent)
            return 1;

        if (other instanceof TaskVisitEvent)
            return 0;

        if (other instanceof ProcessStartEvent)
            return 1;

        return -1;
    }

    public TaskOption getOperationOption() {return taskOption; }
}
