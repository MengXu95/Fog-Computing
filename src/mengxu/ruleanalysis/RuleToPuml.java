package mengxu.ruleanalysis;

import ec.gp.GPTree;
import mengxu.rule.RuleType;
import mengxu.rule.evolved.GPRule;

public class RuleToPuml {

    public static void main(String[] args) {
        GPRule routingRule = GPRule.readFromLispExpression(RuleType.ROUTING,"(+ (* (Max (Min NIQ (* (Min NIQ MRT) (Max (/ TTIQ PT) PT))) PT) NIQ) (* (/ NTR PT) (* (* (/ NTR PT) (Max (Min NIQ MRT) PT)) (Max PT (Min NIQ MRT)))))");
//        GPRule sequencingRule = GPRule.readFromLispExpression(RuleType.SEQUENCING,"(Max (Min (Max (Max (/ (* TWT WIQ) (* TIS MRT)) (Min PT WIQ)) (Min MRT (* DT MRT))) (* (/ TTIQ (+ PT (+ TTIQ TIS))) (- MRT TIS))) (/ NTR MRT))");
//        GPTree tree = sequencingRule.getGPTree();
        GPTree tree = routingRule.getGPTree();
//        String expression = LispSimplifier.simplifyExpression(expression);
//        GPTree tree = LispParser.parseJobShopRule(expression);
        System.out.println(tree.child.makeGraphvizTree());


    }
}
