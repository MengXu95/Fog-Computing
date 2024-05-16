package mengxu.ruleanalysis;

import ec.gp.GPTree;
import mengxu.rule.RuleType;
import mengxu.rule.evolved.GPRule;

public class RuleToPuml {

    public static void main(String[] args) {
//        GPRule routingRule = GPRule.readFromLispExpression(RuleType.ROUTING,"(/ (/ PT DT) (/ (/ (/ (/ PT DT) (/ (Max WIQ PT) (/ PT DT))) (/ (Max WIQ PT) (/ (/ (/ PT DT) DT) (/ (Max WIQ PT) (Max WIQ PT))))) (/ (+ (Max (/ PT DT) (* WIQ NTR)) PT) (/ PT DT))))");
        GPRule sequencingRule = GPRule.readFromLispExpression(RuleType.SEQUENCING,"(+ (/ (Max (* WIQ TWT) (/ WIQ TWT)) (Max (+ NIQ MRT) (/ TTIQ TIS))) (* (- MRT NIQ) (Min MRT WIQ)))");
        GPTree tree = sequencingRule.getGPTree();
//        GPTree tree = routingRule.getGPTree();
//        String expression = LispSimplifier.simplifyExpression(expression);
//        GPTree tree = LispParser.parseJobShopRule(expression);
        System.out.println(tree.child.makeGraphvizTree());


    }
}
