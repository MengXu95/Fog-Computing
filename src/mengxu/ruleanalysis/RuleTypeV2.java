package mengxu.ruleanalysis;


import java.util.HashMap;
import java.util.Map;

/**
 * The type of rule.
 * Created by YiMei on 1/10/16.
 */
public enum RuleTypeV2 {

    SIMPLE_RULE("simple-rule"),
	//fzhang 17.11.2018  test rule in multi-objective
	MULTIOBJECTIVE_RULE("multiobjective-rule");

    private final String name;

    RuleTypeV2(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Reverse-lookup map
    private static final Map<String, RuleTypeV2> lookup = new HashMap<>();

    static {
        for (RuleTypeV2 a : RuleTypeV2.values()) {
            lookup.put(a.getName(), a);
        }
    }

    public static RuleTypeV2 get(String name) {
        return lookup.get(name);
    }

    public boolean isMultiobjective() 
    {
        switch (this) {
            case SIMPLE_RULE:
                return false;
            //fzhang 17.11.2018  test rule in multi-objective
            case MULTIOBJECTIVE_RULE:
            	return true;
            default:
                return false;
        }
    }
}
