package mengxu.taskscheduling;

import java.util.LinkedList;

public class Server {

    private final int id;
    private final ServerType type;

    private double totalProcTimeInQueue;
    private double totalProcTimeAndUpLoadAndDownLoadTimeInQueue;

    // For simulation.
    private double readyTime;
    private LinkedList<TaskOption> queue;

    public Server(int id, ServerType type, double readyTime, LinkedList<TaskOption> queue) {
        this.id = id;
        this.type = type;
        this.readyTime = readyTime;
        this.queue = queue;
        this.totalProcTimeInQueue = 0;
        this.totalProcTimeAndUpLoadAndDownLoadTimeInQueue = 0;
    }

    public Server(int id, ServerType type) {
        this(id, type,0,new LinkedList<>());
    }

    public int getId() {
        return id;
    }

    public ServerType getType() {
        return type;
    }

    public double getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(double readyTime) {
        this.readyTime = readyTime;
    }

    public void addToQueue(TaskOption taskOption){
        this.queue.add(taskOption);
        this.totalProcTimeInQueue += taskOption.getProcTime();
        this.totalProcTimeAndUpLoadAndDownLoadTimeInQueue += (taskOption.getProcTime() + taskOption.getUploadDelay() + taskOption.getDownloadDelay());
    }

    public LinkedList<TaskOption> getQueue() {
        return queue;
    }

    public void removeFromQueue(TaskOption o) {
        queue.remove(o);
        this.totalProcTimeInQueue -= o.getProcTime();
        this.totalProcTimeAndUpLoadAndDownLoadTimeInQueue -= (o.getProcTime() + o.getUploadDelay() + o.getDownloadDelay());
    }

    public int numTaskInQueue(){
        return queue.size();
    }

    public double totalProcTimeInQueue(){
        return this.totalProcTimeInQueue;
    }

    public double totalProcTimeAndUpLoadAndDownLoadTimeInQueue(){
        return this.totalProcTimeAndUpLoadAndDownLoadTimeInQueue;
    }

    public Server clone(){
        LinkedList<TaskOption> cloneQ = new LinkedList<>(queue);
        return new Server(id,type,readyTime,cloneQ);
    }

    public void reset(double readyTime) {
        queue.clear();
        this.setReadyTime(readyTime);
    }

    public void reset() {
        reset(0.0);
    }
}
