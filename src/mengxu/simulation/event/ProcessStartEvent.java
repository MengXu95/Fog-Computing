package mengxu.simulation.event;

import mengxu.simulation.RoutingDecisionSituation;
import mengxu.simulation.SequencingDecisionSituation;
import mengxu.taskscheduling.MobileDevice;
import mengxu.taskscheduling.Process;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.ServerType;

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

            //for print
//            System.out.println("task " + process.getTaskOption().getTask().getId() + " is processed by mobileDevice itself and is started at time " + time);

        }
        else{
            Server server = process.getServer();
            server.setReadyTime(process.getFinishTime());
//        server.incrementBusyTime(process.getDuration());

            mobileDevice.addEvent(
                    new ProcessFinishEvent(process.getFinishTime(), process, mobileDevice));

            //for print
//            if(server.getType() == ServerType.CLOUD){
//                System.out.println("task " + process.getTaskOption().getTask().getId() + " is processed by " + server.getType() + " " + (server.getId()-5) + " and is started at time " + time);
//            }
//            else{
//                System.out.println("task " + process.getTaskOption().getTask().getId() + " is processed by " + server.getType() + " " + server.getId() + " and is atarted at time " + time);
//            }
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
