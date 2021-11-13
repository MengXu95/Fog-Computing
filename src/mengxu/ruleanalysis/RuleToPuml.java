package mengxu.ruleanalysis;

import ec.gp.GPTree;
import mengxu.rule.RuleType;
import mengxu.rule.evolved.GPRule;

public class RuleToPuml {

    public static void main(String[] args) {
        GPRule routingRule = GPRule.readFromLispExpression(RuleType.ROUTING,"(+ (- (+ (+ PT OWT) (Max TIS (+ PT WIQ))) (* (- OWT (Min W TRANT)) (/ NPT TIS))) (+ TRANT (- (+ (+ (+ PT OWT) (+ TRANT WIQ)) (+ TRANT (+ PT WIQ))) (Min (+ (+ (+ PT OWT) (- OWT W)) (Max MWT WKR)) (- (Max (/ NPT TIS) WKR) (+ NPT W))))))");
        GPRule sequencingRule = GPRule.readFromLispExpression(RuleType.SEQUENCING,"(- (- (- (- (- (- (+ PT NPT) (- TIS WIQ)) (+ WKR OWT)) (+ WKR (- WIQ PT))) (- (- WIQ (+ PT NPT)) PT)) OWT) (- WIQ PT))");
        GPTree tree = sequencingRule.getGPTree();
//        String expression = LispSimplifier.simplifyExpression(expression);
//        GPTree tree = LispParser.parseJobShopRule(expression);
        System.out.println(tree.child.makeGraphvizTree());


    }
}
