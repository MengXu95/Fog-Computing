package mengxu.simulation.state;

import mengxu.taskscheduling.Job;
import mengxu.taskscheduling.MobileDevice;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.Task;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SystemState {

    private double clockTime;
    private List<Job> jobsInSystem;
    private List<Job> jobsCompleted;

    private List<Server> servers;
    private List<MobileDevice> mobileDevices;
    private int allNumJobsReleased;

    public SystemState(double clockTime, List<Server> servers, List<MobileDevice> mobileDevices,
                       List<Job> jobsInSystem, List<Job> jobsCompleted) {
        this.clockTime = clockTime;
        this.servers = servers;
        this.mobileDevices = mobileDevices;
        this.jobsInSystem = jobsInSystem;
        this.jobsCompleted = jobsCompleted;
        this.allNumJobsReleased = 0;
    }

    public SystemState() {
        this.clockTime = 0;
        this.servers = new ArrayList<>();
        this.mobileDevices = new ArrayList<>();
        this.jobsInSystem = new ArrayList<>();
        this.jobsCompleted = new ArrayList<>();
        this.allNumJobsReleased = 0;
    }

    public double getClockTime() {
        return this.clockTime;
    }

    public void setClockTime(double clockTime) {
        this.clockTime = clockTime;
    }

    public void addMobileDevice(MobileDevice mobileDevice){
        this.mobileDevices.add(mobileDevice);
    }

    public void addServer(Server server){
        this.servers.add(server);
    }

    public List<MobileDevice> getMobileDevices() {
        return mobileDevices;
    }

    public void addJobToSystem(Job job) {
        jobsInSystem.add(job);
    }

    public List<Job> getJobsCompleted() {
        return jobsCompleted;
    }

    public List<Job> getJobsInSystem() {
        return jobsInSystem;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void addAllNumJobsReleased() {
        this.allNumJobsReleased++;
    }

    public int getAllNumJobsReleased() {
        return allNumJobsReleased;
    }

    public void addCompletedJob(Job job) {
//        //check if the job has been finished
//        boolean finish = true;
//        for(Task task:job.getTaskList()){
//            if(!task.isComplete()){
//                finish = false;
//                break;
//
//            }
//        }
//        if(finish){
//            System.out.println("The job finished!");
//        }
        this.jobsCompleted.add(job);
    }

    public void removeJobFromSystem(Job job){
        this.jobsInSystem.remove(job);
    }

    public void reset(long seed, RandomDataGenerator randomDataGenerator) {
        clockTime = 0.0;
        this.allNumJobsReleased = 0;
        jobsInSystem.clear();
        jobsCompleted.clear();//original
        for (Server server : servers) {
            server.reset();
        }
        for (MobileDevice mobiledevice: mobileDevices) {
            mobiledevice.reset(seed, randomDataGenerator);
        }
    }

    public void resetforRerun() {
        clockTime = 0.0;
        this.allNumJobsReleased = 0;
        jobsInSystem.clear();
        jobsCompleted.clear();//original
        for (Server server : servers) {
            server.reset();
        }
        for (MobileDevice mobiledevice: mobileDevices) {
            mobiledevice.resetState();
        }
    }

    @Override
    public SystemState clone() {
        List<Server> clonedServers = new ArrayList<>();
        for (Server s : servers) {
            clonedServers.add(s.clone());
        }

        //rules do not maintain state
        return new SystemState(clockTime, clonedServers, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public String toString() {
        return "SystemState{" +
                "clockTime=" + clockTime +
                ", mobileDevices=" + mobileDevices +
                ", servers=" + servers +
                ", jobsInSystem=" + jobsInSystem +
                ", jobsCompleted=" + jobsCompleted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SystemState that = (SystemState) o;

        if (Double.compare(that.clockTime, clockTime) != 0) return false;
        if (mobileDevices != null ? !mobileDevices.equals(that.mobileDevices) : that.mobileDevices != null) return false;
        if (servers != null ? !servers.equals(that.servers) : that.servers != null) return false;
        if (jobsInSystem != null ? !jobsInSystem.equals(that.jobsInSystem) : that.jobsInSystem != null) return false;
        return jobsCompleted != null ? jobsCompleted.equals(that.jobsCompleted) : that.jobsCompleted == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(clockTime);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (mobileDevices != null ? mobileDevices.hashCode() : 0);
        result = 31 * result + (servers != null ? servers.hashCode() : 0);
        result = 31 * result + (jobsInSystem != null ? jobsInSystem.hashCode() : 0);
        result = 31 * result + (jobsCompleted != null ? jobsCompleted.hashCode() : 0);
        return result;
    }
}
