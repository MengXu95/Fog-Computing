/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec;

/* 
 * SelectionMethod.java
 * 
 * Created: Mon Aug 30 19:19:56 1999
 * By: Sean Luke
 */

import ec.util.Parameter;
//import mengxu.algorithm.clusterselection.ClusterSelection;
//import yimei.jss.feature.FeatureUtil;

/**
 * A SelectionMethod is a BreedingSource which provides direct IMMUTABLE pointers
 * to original individuals in an old population, not fresh mutable copies.
 * If you use a SelectionMethod as your BreedingSource, you must 
 * SelectionMethods might include Tournament Selection, Fitness Proportional Selection, etc.
 * SelectionMethods don't have parent sources.
 *
 <p><b>Typical Number of Individuals Produced Per <tt>produce(...)</tt> call</b><br>
 Always 1.

 * @author Sean Luke
 * @version 1.0 
 */

public abstract class SelectionMethod extends BreedingSource
    {
    public static final int INDS_PRODUCED = 1;

    public static final String P_PRE_GENERATIONS = "pre-generations";
    private int preGenerations;

    /** Returns 1 (the typical default value) */
    public int typicalIndsProduced() { return INDS_PRODUCED; }

    /** A default version of produces -- this method always returns
        true under the assumption that the selection method works
        with all Fitnesses.  If this isn't the case, you should override
        this to return your own assessment. */
    public boolean produces(final EvolutionState state,
        final Population newpop,
        final int subpopulation,
        final int thread)
        {
        return true;
        }


    /** A default version of prepareToProduce which does nothing.  */
    public void prepareToProduce(final EvolutionState s,
        final int subpopulation,
        final int thread)
        { return; }

    /** A default version of finishProducing, which does nothing. */
    public void finishProducing(final EvolutionState s,
        final int subpopulation,
        final int thread)
        { return; }

    public int produce(final int min, //produce here means get two individuals for crossover
        final int max, 
        final int start,
        final int subpopulation,
        final Individual[] inds,
        final EvolutionState state,
        final int thread) 
        {
        int n=INDS_PRODUCED;
        if (n<min) n = min;
        if (n>max) n = max;

//        //the original
//        for(int q=0;q<n;q++)
//        {
//            inds[start+q] = state.population.subpops[subpopulation].
//                    individuals[produce(subpopulation,state,thread)];
//        }

            for(int q=0;q<n;q++)
            {
                inds[start+q] = state.population.subpops[subpopulation].
                        individuals[produce(subpopulation,state,thread)];
            }

        return n;
        }


        //fzhang 2019.6.15 in the defined generation, when do crossover and mutation, select individuals from specific individuals
        public int produceFrac(final int min, //produce here means get two individuals for crossover
                           final int max,
                           final int start,
                           final int subpopulation,
                           final Individual[] inds,
                           final EvolutionState state,
                           final int thread)
        {
            int n=INDS_PRODUCED;
            if (n<min) n = min;
            if (n>max) n = max;

            preGenerations = state.parameters.getIntWithDefault(new Parameter(P_PRE_GENERATIONS), null, -1);  //50

            for(int q=0;q<n;q++){
//                if(state.generation == preGenerations)
//                    inds[start+q] = FeatureUtil.getNewpop().subpops[subpopulation].
//                            individuals[produce(subpopulation,state,thread)];
//                else
//                    inds[start+q] = state.population.subpops[subpopulation].
//                            individuals[produce(subpopulation,state,thread)];
                inds[start+q] = state.population.subpops[subpopulation].
                        individuals[produce(subpopulation,state,thread)];
            }
            return n;
        }

    /** An alternative form of "produce" special to Selection Methods;
        selects an individual from the given subpopulation and 
        returns its position in that subpopulation. */
    public abstract int produce(final int subpopulation,
        final EvolutionState state,
        final int thread);

    //modified by mengxu 2021.05.07
    public int[] produceTwo(final int subpopulation,
                                final EvolutionState state,
                                final int thread){
        return null;
    }

    }



