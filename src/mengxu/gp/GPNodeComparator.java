package mengxu.gp;

import ec.gp.GPNode;
import ec.gp.GPTree;
import mengxu.gp.function.Div;
import mengxu.gp.function.Mul;
import mengxu.gp.terminal.AttributeGPNode;
import mengxu.gp.terminal.JobShopAttribute;

/**
 * Compare two GP nodes to see if they are equivalent.
 *
 * Created by YiMei on 5/10/16.
 */
public class GPNodeComparator {

    public static boolean TreeEquals(GPTree t1, GPTree t2){
        GPNode node1 = t1.child;
        GPNode node2 = t2.child;

        return equals(node1, node2);
//        return equals(node1, node2);
    }

    public static boolean equals(GPNode o1, GPNode o2) {
        if (o1.toString().equals(o2.toString())) {
            if (o1.children.length == o2.children.length) {
                if (o1.children.length == 0)
                    return true;

                switch (o1.toString()) {
                    case "+":
                        return sameChildrenUnordered(o1.children, o2.children);
                    case "-":
                        return sameChildrenUnordered(o1.children, o2.children);
                    case "*":
                        return sameChildrenUnordered(o1.children, o2.children);
                    case "/":
                        return sameChildrenUnordered(o1.children, o2.children);
                    case "max":
                        return sameChildrenUnordered(o1.children, o2.children);
                    case "min":
                        return sameChildrenUnordered(o1.children, o2.children);
                    case "if":
                        return sameChildrenOrdered(o1.children, o2.children);
                }
            }
        }

        return false;
    }

    public static boolean sameChildrenOrdered(GPNode[] children1,
                                              GPNode[] children2) {
        for (int i = 0; i < children1.length; i++) {
            boolean same = equals(children1[i], children2[i]);

            if (!same)
                return false;
        }

        return true;
    }

    public static boolean sameChildrenUnordered(GPNode[] children1,
                                                GPNode[] children2) {
        boolean[] matched = new boolean[children2.length];

        for (int i = 0; i < children1.length; i++) {
            boolean foundSame = false;

            for (int j = 0; j < children2.length; j++) {
                if (matched[j])
                    continue;

                boolean same = equals(children1[i], children2[j]);

                if (same) {
                    foundSame = true;
                    matched[j] = true;
                    break;
                }
            }

            if (!foundSame)
                return false;
        }

        return true;
    }

    //2021.2.4 modified by meng xu.
    public static void main(String[] args) {
//        GPNode node1 = new Mul();
//        node1.children = new GPNode[2];
//        node1.children[0] = new AttributeGPNode(JobShopAttribute.DUE_DATE);
//        node1.children[1] = new AttributeGPNode(JobShopAttribute.PROC_TIME);
//
//        GPNode node2 = new Div();
//        node2.children = new GPNode[2];
//        node2.children[0] = new AttributeGPNode(JobShopAttribute.PROC_TIME);
//        node2.children[1] = new AttributeGPNode(JobShopAttribute.DUE_DATE);
//
//        System.out.println(equals(node1, node2));

        GPTree tree1 = new GPTree();
        GPNode node1 = new Mul();
        node1.children = new GPNode[2];
        node1.children[0] = new Div();
        node1.children[0].children = new GPNode[2];
        node1.children[0].children[0] = new AttributeGPNode(JobShopAttribute.PROC_TIME);
        node1.children[0].children[1] = new AttributeGPNode(JobShopAttribute.PROC_TIME);
        node1.children[1] = new AttributeGPNode(JobShopAttribute.PROC_TIME);
        tree1.child = node1;

//        GPTree tree2 = new GPTree();
//        GPNode node2 = new Div();
//        node2.children = new GPNode[2];
//        node2.children[0] = new AttributeGPNode(JobShopAttribute.PROC_TIME);
//        node2.children[1] = new AttributeGPNode(JobShopAttribute.DUE_DATE);
//        tree2.child = node2;

        GPTree tree2 = new GPTree();
        GPNode node2 = new Mul();
        node2.children = new GPNode[2];
        node2.children[0] = new Mul();
        node2.children[0].children = new GPNode[2];
        node2.children[0].children[0] = new AttributeGPNode(JobShopAttribute.PROC_TIME);
        node2.children[0].children[1] = new AttributeGPNode(JobShopAttribute.PROC_TIME);
        node2.children[1] = new AttributeGPNode(JobShopAttribute.PROC_TIME);
        tree2.child = node2;

        System.out.println(TreeEquals(tree1, tree2));

    }
}
