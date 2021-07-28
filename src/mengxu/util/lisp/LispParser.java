package mengxu.util.lisp;

import ec.gp.GPNode;
import ec.gp.GPTree;
import mengxu.gp.function.*;
import mengxu.gp.terminal.AttributeGPNode;
import mengxu.gp.terminal.ConstantTerminal;
import mengxu.gp.terminal.JobShopAttribute;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 * Created by YiMei on 29/09/16.
 */
public class LispParser {

    public static GPTree parseJobShopRule(String expression) {
        GPTree tree = new GPTree();

        expression = expression.trim();

        tree.child = parseNode(expression);
        tree.child.parent = tree;

        return tree;
    }

    private static GPNode parseNode(String expression) {
        GPNode node = null;

        if (expression.charAt(0) == '(') {
            int nextWhiteSpaceIdx = expression.indexOf(' ');
            String func = expression.substring(1, nextWhiteSpaceIdx);
            String argsString = expression.substring(nextWhiteSpaceIdx + 1,
                    expression.length() - 1);
            List<String> args = LispUtil.splitArguments(argsString);

            switch (func) {
                case "+":
                    node = new Add();
                    node.children = new GPNode[2];
                    node.children[0] = parseNode(args.get(0));
                    node.children[1] = parseNode(args.get(1));
                    node.children[0].parent = node;
                    node.children[1].parent = node;
                    node.children[0].argposition = 0;
                    node.children[1].argposition = 1;
                    break;
                case "-":
                    node = new Sub();
                    node.children = new GPNode[2];
                    node.children[0] = parseNode(args.get(0));
                    node.children[1] = parseNode(args.get(1));
                    node.children[0].parent = node;
                    node.children[1].parent = node;
                    node.children[0].argposition = 0;
                    node.children[1].argposition = 1;
                    break;
                case "*":
                    node = new Mul();
                    node.children = new GPNode[2];
                    node.children[0] = parseNode(args.get(0));
                    node.children[1] = parseNode(args.get(1));
                    node.children[0].parent = node;
                    node.children[1].parent = node;
                    node.children[0].argposition = 0;
                    node.children[1].argposition = 1;
                    break;
                case "/":
                    node = new Div();
                    node.children = new GPNode[2];
                    node.children[0] = parseNode(args.get(0));
                    node.children[1] = parseNode(args.get(1));
                    node.children[0].parent = node;
                    node.children[1].parent = node;
                    node.children[0].argposition = 0;
                    node.children[1].argposition = 1;
                    break;
                case "Max":
                    node = new Max();
                    node.children = new GPNode[2];
                    node.children[0] = parseNode(args.get(0));
                    node.children[1] = parseNode(args.get(1));
                    node.children[0].parent = node;
                    node.children[1].parent = node;
                    node.children[0].argposition = 0;
                    node.children[1].argposition = 1;
                    break;
                case "Min":
                    node = new Min();
                    node.children = new GPNode[2];
                    node.children[0] = parseNode(args.get(0));
                    node.children[1] = parseNode(args.get(1));
                    node.children[0].parent = node;
                    node.children[1].parent = node;
                    node.children[0].argposition = 0;
                    node.children[1].argposition = 1;
                    break;
                case "if":
                    node = new If();
                    node.children = new GPNode[3];
                    node.children[0] = parseNode(args.get(0));
                    node.children[1] = parseNode(args.get(1));
                    node.children[2] = parseNode(args.get(2));
                    node.children[0].parent = node;
                    node.children[1].parent = node;
                    node.children[2].parent = node;
                    node.children[0].argposition = 0;
                    node.children[1].argposition = 1;
                    node.children[2].argposition = 2;
                    break;
                default:
                    break;
            }
        }
        else {
            if (NumberUtils.isNumber(expression)) {
                node = new ConstantTerminal(Double.valueOf(expression));
            }
            else {
                node = new AttributeGPNode(JobShopAttribute.get(expression));
            }
            node.children = new GPNode[0];
        }

        return node;
    }

    public static void main(String[] args) {

//        String path = "/local/scratch/Dropbox/Research/JobShopScheduling/ExpResults/";
//        String algo = "simple-gp-basic-terminals";
//        String scenario = "mean-weighted-tardiness-0.85-4";
//
//
//
//        String sourcePath = path + algo + "/" + scenario + "/";
//
//        int numRuns = 30;
//
//        for (int run =  0; run < numRuns; run++) {
//            File sourceFile = new File(sourcePath + "job." + run + ".out.stat");
//            File outFile = new File(sourcePath + "job." + run + ".bestrule.dot");
//
//            List<String> expressions =
//                    ResultFileReader.readLispExpressionFromFile(sourceFile,
//                            RuleType.SIMPLE_RULE, false);
//
//            String bestExpression = expressions.get(expressions.size()-1);
//            GPTree bestTree = LispParser.parseJobShopRule(bestExpression);
//            String bestGraphVizTree = bestTree.child.makeGraphvizTree();
//
//            try {
//                BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.getAbsoluteFile()));
//                writer.write(bestGraphVizTree);
//                writer.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        String expression = "(min (+ (- (max (/ (* (+ NOR W) (+ NOR TIS)) (min (min TIS PT) (max NIQ WIQ))) (min WKR (* PT PT))) (* (/ WIQ (- OWT OWT)) (- (- (min TIS W) (+ PT NIQ)) MWT))) (- (max NOR (min WKR (* PT PT))) (* (/ WIQ (- OWT OWT)) (- (- (min TIS W) (min TIS W)) (+ PT NIQ))))) (+ (- (min TIS W) (- (min TIS W) (+ PT NIQ))) (+ (+ (/ WKR TIS) (* PT PT)) (* PT PT))))" + 
        		"\n" + 
        		"";
        expression = LispSimplifier.simplifyExpression(expression);
        GPTree tree = LispParser.parseJobShopRule(expression);
        System.out.println(tree.child.makeGraphvizTree());
    }
}
