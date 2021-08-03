package mengxu.gp.terminal;

import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The attributes of the job shop.
 * NOTE: All the attributes are relative to the current time.
 *       This is for making the decision making process memoryless,
 *       i.e. independent of the current time.
 *
 * @author yimei
 */

public enum JobShopAttribute {
    CURRENT_TIME("t"), // the current time
    // The machine-related attributes (independent of the jobs in the queue of the machine).
    NUM_OPS_IN_QUEUE("NIQ"), // the number of tasks in the queue
    MACHINE_READY_TIME("MRT"), // the ready time of the server or mobileDevice
    // The job/operation-related attributes (depend on the jobs in the queue).
    PROC_TIME("PT"), // the processing time of the task
    WEIGHT("W"), // the job weight
    RELEASE_TIME("RT"), // the release time
    TIME_IN_SYSTEM("TIS"),// time in system = t - releaseTime
    WORK_IN_QUEUE("WIQ"),
    UPLOAD_TIME("UT"),
    DOWNLOAD_TIME("DT"),
    TOTAL_TIME_IN_QUEUE("TTIQ");

    private final String name;

    JobShopAttribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Reverse-lookup map
    private static final Map<String, JobShopAttribute> lookup = new HashMap<>();

    static {
        for (JobShopAttribute a : JobShopAttribute.values()) {
            lookup.put(a.getName(), a);
        }
    }

    public static JobShopAttribute get(String name) {
        return lookup.get(name);
    }

    public double value(TaskOption taskOption, Server server, SystemState systemState
    		) {
        double value = -1;

        switch (this) {
            case CURRENT_TIME:
                value = systemState.getClockTime();
                break;
            case NUM_OPS_IN_QUEUE:
                if(taskOption.getServer() == null){
                    value = taskOption.getMobileDevice().getQueue().size();
                }
                else{
                    value = server.getQueue().size();
                }
                break;
            case MACHINE_READY_TIME:
                if(taskOption.getServer() == null){
                    value = taskOption.getMobileDevice().getReadyTime();
                }
                else{
                    value = server.getReadyTime();
                }
                break;
            case PROC_TIME:
                value = taskOption.getProcTime();
                break;
            case WEIGHT:
                value = taskOption.getTask().getJob().getWeight();
                break;
            case RELEASE_TIME:
                value = taskOption.getTask().getJob().getReleaseTime();
                break;
            case TIME_IN_SYSTEM:
                value = systemState.getClockTime() - taskOption.getTask().getJob().getReleaseTime();
                break;
            case WORK_IN_QUEUE:
                if(taskOption.getServer() == null){
                    value = taskOption.getMobileDevice().totalProcTimeInQueue();
                }
                else{
                    value = server.totalProcTimeInQueue();
                }
                break;
            case TOTAL_TIME_IN_QUEUE:
                if(taskOption.getServer() == null){
                    value = taskOption.getMobileDevice().totalProcTimeAndUpLoadAndDownLoadTimeInQueue();
                }
                else{
                    value = server.totalProcTimeAndUpLoadAndDownLoadTimeInQueue();
                }
                break;
            case UPLOAD_TIME:
                value = taskOption.getUploadDelay();
                break;
            case DOWNLOAD_TIME:
                value = taskOption.getDownloadDelay();
                break;
           default:
                System.err.println("Undefined attribute " + name);
                System.exit(1);
        }

        return value;
    }

    public static double valueOfString(String attribute, TaskOption taskOption, Server server,
                                       SystemState systemState,
                                       List<JobShopAttribute> ignoredAttributes) {
        JobShopAttribute a = get(attribute);
        if (a == null) {
            if (NumberUtils.isNumber(attribute)) {
                return Double.valueOf(attribute);
            } else {
                System.err.println(attribute + " is neither a defined attribute nor a number.");
                System.exit(1);
            }
        }

        if (ignoredAttributes.contains(a)) {
            return 1.0;
        } else {
        	  return a.value(taskOption, server, systemState);
        }
    }

    /**
     * Return the basic attributes.
     * @return the basic attributes.
     */
    public static JobShopAttribute[] basicAttributes() {
        return new JobShopAttribute[]{
                JobShopAttribute.CURRENT_TIME,
                JobShopAttribute.NUM_OPS_IN_QUEUE,
                JobShopAttribute.MACHINE_READY_TIME,
                JobShopAttribute.PROC_TIME,
                JobShopAttribute.WEIGHT,
                JobShopAttribute.WORK_IN_QUEUE,
                JobShopAttribute.UPLOAD_TIME,
                JobShopAttribute.DOWNLOAD_TIME,
        };
    }

    /**
     * The attributes relative to the current time.
     * @return the relative attributes.
     */
    //for flexible JSSP
    //fzhang 19.7.2018 for flexible, the next processing time do not know, because we do not know the next operation will
    //be allocated in which machine:  baseline
    public static JobShopAttribute[] relativeAttributes() {
        return new JobShopAttribute[]{
                //server related
                JobShopAttribute.NUM_OPS_IN_QUEUE,
                JobShopAttribute.WORK_IN_QUEUE,
                JobShopAttribute.UPLOAD_TIME,
                JobShopAttribute.DOWNLOAD_TIME,
                JobShopAttribute.PROC_TIME,
//                JobShopAttribute.TOTAL_TIME_IN_QUEUE,

                //job related
                JobShopAttribute.TIME_IN_SYSTEM,
//                JobShopAttribute.WEIGHT,
        };
    }
}
