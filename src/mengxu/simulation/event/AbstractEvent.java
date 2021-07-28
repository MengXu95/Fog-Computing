package mengxu.simulation.event;

import mengxu.simulation.DynamicSimulation;
import mengxu.simulation.RoutingDecisionSituation;
import mengxu.simulation.SequencingDecisionSituation;
import mengxu.taskscheduling.MobileDevice;
import mengxu.taskscheduling.Server;

import java.util.List;

public abstract class AbstractEvent implements Comparable<AbstractEvent> {
    protected double time;

    public AbstractEvent(double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    public abstract void trigger(MobileDevice mobileDevice);

    public abstract void addSequencingDecisionSituation(MobileDevice mobileDevice,
                                                        List<SequencingDecisionSituation> situations,
                                                        int minQueueLength);

    public abstract void addRoutingDecisionSituation(MobileDevice mobileDevice,
                                                     List<RoutingDecisionSituation> situations,
                                                     int minOptions);

    @Override
    public int compareTo(AbstractEvent other) {
        if (time < other.time)
            return -1;

        if (time > other.time)
            return 1;

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEvent that = (AbstractEvent) o;

        return Double.compare(that.time, time) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(time);
        return (int) (temp ^ (temp >>> 32));
    }
}
