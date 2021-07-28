package mengxu.taskscheduling;

/**
 * Different shop scenarios.
 * @author yimei
 *
 */

public enum Scenario {

	FLOW_SHOP("flow-shop"),
	STATIC_JOB_SHOP("static-job-shop"),
	DYNAMIC_JOB_SHOP("dynamic-job-shop");

	private final String name;

	Scenario(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
