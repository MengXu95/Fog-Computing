package mengxu.taskscheduling;

import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.rule.job.basic.PT;
import mengxu.rule.server.WIQ;

import java.util.HashMap;
import java.util.Map;

/**
 * The enumeration of all the objectives that may be optimised in job shop scheduling.
 * All the objectives are assumed to be minimised.
 *
 * Created by yimei on 28/09/16.
 *
 */
public enum Objective {

    MAKESPAN("makespan"),
    MEAN_FLOWTIME("mean-flowtime");

    private final String name;

    Objective(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Reverse-lookup map
    private static final Map<String, Objective> lookup = new HashMap<>();

    static {
        for (Objective a : Objective.values()) {
            lookup.put(a.getName(), a);
        }
    }

    public static Objective get(String name) {
        return lookup.get(name);
    }

    public AbstractRule benchmarkSequencingRule() {
        switch (this) {
            case MAKESPAN:
                return new PT(RuleType.SEQUENCING);
            case MEAN_FLOWTIME:
                return new PT(RuleType.SEQUENCING);
        }

        return null;
    }

    public AbstractRule benchmarkRoutingRule() {
        return new WIQ(RuleType.ROUTING);
    }
}
