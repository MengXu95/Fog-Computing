/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.gp;
import ec.*;
import ec.util.*;
//import mengxu.algorithm.multipletreegp.GPRuleEvolutionStateUpdateLate;

import java.io.*;

/*
 * GPSpecies.java
 *
 * Created: Tue Aug 31 17:00:10 1999
 * By: Sean Luke
 */

/**
 * GPSpecies is a simple individual which is suitable as a species
 * for GP subpopulations.  GPSpecies' individuals must be GPIndividuals,
 * and often their pipelines are GPBreedingPipelines (at any rate,
 * the pipelines will have to return members of GPSpecies!).
 *
 <p><b>Default Base</b><br>
 gp.species

 *
 * @author Sean Luke
 * @version 1.0
 */

public class GPSpecies extends Species
    {
    public static final String P_GPSPECIES = "species";
    public static final String P_USE_MCTS = "use-mcts";

    public String useMcts;

    public static final String P_MCTS_UPDATE_LATE = "mcts-update-late";
    public String mctsUpdateLate;

    public Parameter defaultBase()
        {
        return GPDefaults.base().push(P_GPSPECIES);
        }

    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state,base);  //load and setup pipe_prototype, i_prototype, i_prototype.species = this, f_prototype  the most important is the i_prototype for individuals

        this.useMcts = state.parameters.getStringWithDefault(new Parameter(P_USE_MCTS), null,"no");

        this.mctsUpdateLate = state.parameters.getStringWithDefault(new Parameter(P_MCTS_UPDATE_LATE), null,"no");

            // check to make sure that our individual prototype is a GPIndividual
        if (!(i_prototype instanceof GPIndividual))
            state.output.fatal("The Individual class for the Species " + getClass().getName() + " is must be a subclass of ec.gp.GPIndividual.", base );
        }

    public Individual newIndividual(EvolutionState state, int thread)
        {
        GPIndividual newind = ((GPIndividual)(i_prototype)).lightClone();

        // Initialize the trees
//        for (int x=0;x<newind.trees.length;x++)
//            newind.trees[x].buildTree(state, thread); //fzhang 2019.1.22 build tree use function and terminals

//        if(useMcts.equals("yes") && !mctsUpdateLate.equals("yes")){
//            //modified by mengxu 2021.04.08
//            for (int x=0;x<newind.trees.length;x++)
//                newind.trees[x].buildTreeMcts(state, thread, x); //fzhang 2019.1.22 build tree use function and terminals
//        }
//        else{
//            for (int x=0;x<newind.trees.length;x++)
//                newind.trees[x].buildTree(state, thread); //fzhang 2019.1.22 build tree use function and terminals
//
//        }

        for (int x=0;x<newind.trees.length;x++)
            newind.trees[x].buildTree(state, thread); //fzhang 2019.1.22 build tree use function and terminals



            // Set the fitness
        newind.fitness = (Fitness)(f_prototype.clone());
        newind.evaluated = false;

        // Set the species to me
        newind.species = this;

//        //modified by mengxu 20210520---------------------
//        if(state instanceof GPRuleEvolutionStateUpdateLate) {
//            String mctsPolicy = ((GPRuleEvolutionStateUpdateLate) state).mctsPolicy;
//            boolean mctsUpdateRate = ((GPRuleEvolutionStateUpdateLate) state).mctses[0].isMctsUpdateLate();
//            if (mctsUpdateRate) {
//                if(mctsPolicy.equals("visitCount")){
//                    GPTree gpTree0 = ((GPIndividual)newind).trees[0];
//                    GPTree gpTree1 = ((GPIndividual)newind).trees[1];
//                    ((GPRuleEvolutionStateUpdateLate) state).mctses[0].frontPropagateVisitCount(gpTree0, newind.fitness.fitness());
//                    ((GPRuleEvolutionStateUpdateLate) state).mctses[1].frontPropagateVisitCount(gpTree1, newind.fitness.fitness());
//                }
//            }
//        }

        // ...and we're ready!
        return newind;
        }


    // A custom version of newIndividual() which guarantees that the
    // prototype is light-cloned before readIndividual is issued
    public Individual newIndividual(final EvolutionState state,
        final LineNumberReader reader)
        throws IOException
        {
        GPIndividual newind = ((GPIndividual)i_prototype).lightClone();

        // Set the fitness -- must be done BEFORE loading!
        newind.fitness = (Fitness)(f_prototype.clone());
        newind.evaluated = false; // for sanity's sake, though it's a useless line

        // load that sucker
        newind.readIndividual(state,reader);

        // Set the species to me
        newind.species = this;

        // and we're ready!
        return newind;
        }


    // A custom version of newIndividual() which guarantees that the
    // prototype is light-cloned before readIndividual is issued
    public Individual newIndividual(final EvolutionState state,
        final DataInput dataInput)
        throws IOException
        {
        GPIndividual newind = ((GPIndividual)i_prototype).lightClone();

        // Set the fitness -- must be done BEFORE loading!
        newind.fitness = (Fitness)(f_prototype.clone());
        newind.evaluated = false; // for sanity's sake, though it's a useless line

        // Set the species to me
        newind.species = this;

        // load that sucker
        newind.readIndividual(state,dataInput);

        // and we're ready!
        return newind;
        }

//    //2021.3.22 modified by mengxu.
//    public Individual[] newTwoIndividuals(EvolutionState state, int thread)
//    {
//        GPIndividual[] newinds = new GPIndividual[2];
//
//        //original individual
//        GPIndividual newind0 = ((GPIndividual)(i_prototype)).lightClone();
//
//        // Initialize the trees
//        for (int x=0;x<newind0.trees.length;x++)
//            newind0.trees[x].buildTree(state, thread); //fzhang 2019.1.22 build tree use function and terminals
//
//        // Set the fitness
//        newind0.fitness = (Fitness)(f_prototype.clone());
//        newind0.evaluated = false;
//
//        // Set the species to me
//        newind0.species = this;
//
//        //OBL individual
//        GPIndividual newind1 = ((GPIndividual)(i_prototype)).lightClone();
//
//        for (int x=0;x<newind1.trees.length;x++)
//            newind1.trees[x].OBLTree(newind0.trees[x],state, thread);
//
//        // Initialize the trees
////        for (int x=0;x<newind1.trees.length;x++)
////            newind1.trees[x]=newind0.trees[x].OBLTree(newind0.trees[x],state, thread); //mengxu 2021.03.24 get the OBL tree.
//
//        // Set the fitness
//        newind1.fitness = (Fitness)(f_prototype.clone());
//        newind1.evaluated = false;
//
//        // Set the species to me
//        newind1.species = this;
//
//        newinds[0] = newind0;
//        newinds[1] = newind1;
//
//        // ...and we're ready!
//        return newinds;
//    }
//
//        public Individual OBLIndividuals(GPIndividual ind, EvolutionState state, int thread)
//        {
//            //original individual
//
//            //OBL individual
//            GPIndividual newind1 = ((GPIndividual)(i_prototype)).lightClone();
//
//            // Initialize the trees
//            for (int x=0;x<newind1.trees.length;x++)
//                newind1.trees[x].OBLTree(ind.trees[x],state, thread); //fzhang 2019.1.22 build tree use function and terminals
//
//            // Set the fitness
//            newind1.fitness = (Fitness)(f_prototype.clone());
//            newind1.evaluated = false;
//
//            // Set the species to me
//            newind1.species = this;
//
//
//            // ...and we're ready!
//            return newind1;
//        }

    }
