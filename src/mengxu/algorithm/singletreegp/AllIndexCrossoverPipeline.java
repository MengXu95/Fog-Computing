package mengxu.algorithm.singletreegp;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPInitializer;
import ec.gp.GPNode;
import ec.gp.GPTree;
import ec.gp.koza.CrossoverPipeline;

/**
 * Created by fzhang on 26.05.2018.
 */
public class AllIndexCrossoverPipeline extends CrossoverPipeline {
    /**
     * Overrides the normal crossover pipeline to use all pairs of the SAME TREE INDEX in each individual when performing crossover. This is necessary where the tree locations ~mean something~.
     * <p>
     * Unfortunately, this is an ugly extension due to the parent class design, but the changed parts from the parent can be found between the >>>>>>>START<<<<<<< and >>>>>>>>END<<<<<<<< tags.
     */

    //can crossover between different trees
    @Override
    public int produce(final int min,
                       final int max,
                       final int start,
                       final int subpopulation,
                       final Individual[] inds,
                       final EvolutionState state,
                       final int thread)

    {
        // how many individuals should we make?
        int n = typicalIndsProduced();
        if (n < min) n = min;
        if (n > max) n = max;

        // should we bother?
        if (!state.random[thread].nextBoolean(likelihood))
            return reproduce(n, start, subpopulation, inds, state, thread, true);  // DO produce children from source -- we've not done so already


        GPInitializer initializer = ((GPInitializer) state.initializer);

        for (int q = start; q < n + start; /* no increment */)  // keep on going until we're filled up
        {
            // grab two individuals from our sources
            if (sources[0] == sources[1])  // grab from the same source
                sources[0].produce(2, 2, 0, subpopulation, parents, state, thread);
            else // grab from different sources
            {
                sources[0].produce(1, 1, 0, subpopulation, parents, state, thread);
                sources[1].produce(1, 1, 1, subpopulation, parents, state, thread);
            }

            // at this point, parents[] contains our two selected individuals


            int length = parents[0].trees.length;
            if (tree1 == TREE_UNFIXED && tree2 == TREE_UNFIXED && (parents[0].trees.length == parents[1].trees.length)) {
                GPNode[] p1 = new GPNode[length];
                GPNode[] p2 = new GPNode[length];
                for (int t = 0; t < length; t++) {
                    // prepare the nodeselectors
                    nodeselect1.reset();
                    nodeselect2.reset();


                    // pick some nodes


                    for (int x = 0; x < numTries; x++) {

                        GPNode p11;
                        GPNode p21;
                        // validity results...
                        boolean res1;
                        boolean res2;
                        // pick a node in individual 1
                        p11 = nodeselect1.pickNode(state, subpopulation, thread, parents[0], parents[0].trees[t]);

                        // pick a node in individual 2
                        p21 = nodeselect2.pickNode(state, subpopulation, thread, parents[1], parents[1].trees[t]);

                        // check for depth and swap-compatibility limits
                        //  System.err.println(maxDepth + " " + maxSize);
                        res1 = verifyPoints(initializer, p21, p11);  // p2 can fill p1's spot -- order is important!
                        if (n - (q - start) < 2 || tossSecondParent) res2 = true;
                        else
                            res2 = verifyPoints(initializer, p11, p21);  // p1 can fill p2's spot -- order is important!

                        // did we get something that had both nodes verified?
                        // we reject if EITHER of them is invalid.  This is what lil-gp does.
                        // Koza only has numTries set to 1, so it's compatible as well.
                        if (res1 && res2) {
                            p1[t] = p11;
                            p2[t] = p21;
                            break;
                        }
                    }


                }


                // Create some new individuals based on the old ones -- since
                // GPTree doesn't deep-clone, this should be just fine.  Perhaps we
                // should change this to proto off of the main species prototype, but
                // we have to then copy so much stuff over; it's not worth it.

                GPIndividual j1 = parents[0].lightClone();
                GPIndividual j2 = null;
                if (n - (q - start) >= 2 && !tossSecondParent) j2 = parents[1].lightClone();

                // Fill in various tree information that didn't get filled in there
                j1.trees = new GPTree[parents[0].trees.length];
                if (n - (q - start) >= 2 && !tossSecondParent) j2.trees = new GPTree[parents[1].trees.length];

                // at this point, p1 or p2, or both, may be null.
                // If not, swap one in.  Else just copy the parent.

                for (int x = 0; x < j1.trees.length; x++) {
                    if (p1[x] != null)  // we've got a tree with a kicking cross position!
                    {
                        j1.trees[x] = parents[0].trees[x].lightClone();
                        j1.trees[x].owner = j1;
                        j1.trees[x].child = parents[0].trees[x].child.cloneReplacing(p2[x], p1[x]);
                        j1.trees[x].child.parent = j1.trees[x];
                        j1.trees[x].child.argposition = 0;
                        j1.evaluated = false;
                    }  // it's changed
                    else {
                        j1.trees[x] = parents[0].trees[x].lightClone();
                        j1.trees[x].owner = j1;
                        j1.trees[x].child = (GPNode) (parents[0].trees[x].child.clone());
                        j1.trees[x].child.parent = j1.trees[x];
                        j1.trees[x].child.argposition = 0;
                    }
                }

                if (n - (q - start) >= 2 && !tossSecondParent)
                    for (int x = 0; x < j2.trees.length; x++) {
                        if (p2[x] != null)  // we've got a tree with a kicking cross position!
                        {
                            j2.trees[x] = parents[1].trees[x].lightClone();
                            j2.trees[x].owner = j2;
                            j2.trees[x].child = parents[1].trees[x].child.cloneReplacing(p1[x], p2[x]);
                            j2.trees[x].child.parent = j2.trees[x];
                            j2.trees[x].child.argposition = 0;
                            j2.evaluated = false;
                        } // it's changed
                        else {
                            j2.trees[x] = parents[1].trees[x].lightClone();
                            j2.trees[x].owner = j2;
                            j2.trees[x].child = (GPNode) (parents[1].trees[x].child.clone());
                            j2.trees[x].child.parent = j2.trees[x];
                            j2.trees[x].child.argposition = 0;
                        }
                    }

                // add the individuals to the population
                inds[q] = j1;
                q++;
                if (q < n + start && !tossSecondParent) {
                    inds[q] = j2;
                    q++;
                }


            } else {
                state.output.fatal("GP AllIndexCrossover Pipeline: two individuals chosen for crossover have DIFFERENT numbers of trees! This is not supported -- you may wish to extend this method if you require this behaviour.");

            }

        }
        return n;
    }

}
