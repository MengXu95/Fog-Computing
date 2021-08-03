package mengxu.simulation.event;

import mengxu.simulation.RoutingDecisionSituation;
import mengxu.simulation.SequencingDecisionSituation;
import mengxu.taskscheduling.MobileDevice;
import mengxu.taskscheduling.Process;
import mengxu.taskscheduling.Server;

import java.util.List;

public class ProcessStartEvent extends AbstractEvent{

    private Process process;

    public ProcessStartEvent(double time, Process process, MobileDevice mobileDevice) {
        super(time,mobileDevice);
        this.process = process;
    }

    public ProcessStartEvent(Process process, MobileDevice mobileDevice) {
        this(process.getStartTime(), process,mobileDevice);
    }

    public Process getProcess() {
        return process;
    }

    @Override
    public void trigger(MobileDevice mobileDevice) {
        if(process.getServer() == null){//choose mobileDevice to process this task
            process.getTaskOption().getMobileDevice().setReadyTime(process.getFinishTime());
            mobileDevice.addEvent(
                    new ProcessFinishEvent(process.getFinishTime(), process, mobileDevice));
        }
        else{
            Server server = process.getServer();
            server.setReadyTime(process.getFinishTime());
//        server.incrementBusyTime(process.getDuration());

            mobileDevice.addEvent(
                    new ProcessFinishEvent(process.getFinishTime(), process, mobileDevice));
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

        if (other instanceof ProcessStartEvent)
            return 0;

        if (other instanceof ProcessFinishEvent)
            return -1;

        return 1;
    }
}
