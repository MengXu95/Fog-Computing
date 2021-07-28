package mengxu.rule.evolved;

import ec.gp.GPNode;
import ec.gp.GPTree;
import mengxu.feature.ignore.Ignorer;
import mengxu.gp.CalcPriorityProblem;
import mengxu.gp.GPNodeComparator;
import mengxu.gp.data.DoubleData;
import mengxu.rule.AbstractRule;
import mengxu.rule.RuleType;
import mengxu.simulation.state.SystemState;
import mengxu.taskscheduling.Server;
import mengxu.taskscheduling.TaskOption;
import mengxu.util.lisp.LispParser;

/**
 * The GP-evolved rule.
 * <p>
 * Created by YiMei on 27/09/16.
 */
public class GPRule extends AbstractRule {

    private GPTree gpTree;
    private String lispString;

    public GPRule(RuleType t, GPTree gpTree) {
        name = "\"GPRule\"";
        this.gpTree = gpTree;
        type = t;
    }

    public GPRule(RuleType t, GPTree gpTree, String expression) {
        name = "\"GPRule\"";
        this.lispString = expression;
        this.gpTree = gpTree;
        this.type = t;
    }

    public GPTree getGPTree() {
        return gpTree;
    }

    public void setGPTree(GPTree gpTree) {
        this.gpTree = gpTree;
    }

    public String getLispString() {
        return lispString;
    }

    public static GPRule readFromLispExpression(RuleType type, String expression) {
        GPTree tree = LispParser.parseJobShopRule(expression);

        return new GPRule(type, tree, expression);
    }

    public void ignore(GPNode tree, GPNode feature, Ignorer ignorer) {
    	
    	//System.out.println(tree.depth());
        //System.out.println(feature.depth());
        
        if (tree.depth() < feature.depth())       	
            return;

        if (GPNodeComparator.equals(tree, feature)) {
            ignorer.ignore(tree);

            return;
        }

        if (tree.depth() == feature.depth())
            return;  //after ignoring, check again

        for (GPNode child : tree.children) {
            ignore(child, feature, ignorer);
        }
    }

    public void ignore(GPNode feature, Ignorer ignorer) {
        ignore(gpTree.child, feature, ignorer);
    }

    public double priority(TaskOption taskOption, Server server,
                           SystemState systemState) {
        CalcPriorityProblem calcPrioProb =
                new CalcPriorityProblem(taskOption, server, systemState);

        DoubleData tmp = new DoubleData();
        gpTree.child.eval(null, 0, tmp, null, null, calcPrioProb);

        return tmp.value;
    }
}
