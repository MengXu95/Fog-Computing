package mengxu.taskscheduling;

public class Process implements Comparable<Process>{
    private Server server;
    private TaskOption taskOption;
    private double startTime;
    private double finishTime;

    public Process(Server server, TaskOption taskOption, double startTime) {
        this.server = server;
        this.taskOption = taskOption;
        this.startTime = startTime;
        this.finishTime = startTime + taskOption.getProcTime() + taskOption.getDownloadDelay();
    }

    public Server getServer() {
        return server;
    }

    public TaskOption getTaskOption() {
        return taskOption;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getFinishTime() {
        return finishTime;
    }

    public double getDuration() {
        return finishTime - startTime;
    }

    @Override
    public String toString() {
        return "mengxu";
    }

    @Override
    public int compareTo(Process other) {
        if (startTime < other.startTime)
            return -1;

        if (startTime > other.startTime)
            return 1;

        return 0;
    }


    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = server != null ? server.hashCode() : 0;
        result = 31 * result + (taskOption != null ? taskOption.hashCode() : 0);
        temp = Double.doubleToLongBits(startTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(finishTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
