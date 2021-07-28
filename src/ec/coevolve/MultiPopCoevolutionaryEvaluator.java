/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.coevolve;

import ec.*;
import ec.gp.GPIndividual;
import ec.simple.*;
import ec.util.*;
//import yimei.jss.ruleoptimisation.RuleOptimizationProblem;
//import yimei.jss.surrogate.Surrogate;
//import yimei.util.lisp.LispParser;

/**
 * MultiPopCoevolutionaryEvaluator.java
 *

 <p>MultiPopCoevolutionaryEvaluator is an Evaluator which performs <i>competitive or cooperative multi-population
 coevolution</i>.  Competitive coevolution is where individuals' fitness is determined by
 testing them against individuals from other subpopulation.  Cooperative coevolution is where individuals
 form teams together with members of other subpopulations, and the individuals' fitness is computed based
 on the performance of such teams.  This evaluator assumes that the problem can only evaluate groups of
 individuals containing one individual from each subpopulation.  Individuals are evaluated regardless of
 whether or not they've been evaluated in the past.

 <p>Your Problem is responsible for updating up the fitness appropriately with values usually obtained
 from teaming up the individual with different partners from the other subpopulations.
 MultiPopCoevolutionaryEvaluator expects to use Problems which adhere to the GroupedProblemForm
 interface, which defines a new evaluate(...) function, plus a preprocess(...) and postprocess(...) function.

 <p>This coevolutionary evaluator is single-threaded -- maybe we'll hack in multithreading later.  It allows
 any number of subpopulations (implicitly, any number of individuals being evaluated together). The order of
 individuals in the subpopulation may be changed during the evaluation process.

 <p>Ordinarily MultiPopCoevolutionaryEvaluator does "parallel" coevolution: all subpopulations are evaluated
 simultaneously, then bred simultaneously.  But if you set the "sequential" parameter in the class
 ec.simple.SimpleBreeder, then MultiPopCoevolutionary behaves in a sequential fashion common in the "classic"
 version of cooperative coevolution: only one subpopulation is evaluated and bred per generation.
 The subpopulation index to breed is determined by taking the generation number, modulo the
 total number of subpopulations.

 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><b>breed</b>.sequential</tt><br>
 <font size=-1>boolean (default = false)</font></td>
 <td valign=top>(should we evaluate and breed a single subpopulation each generation?  Note that this is a SimpleBreeder parameter. )
 </td></tr>

 <tr><td valign=top><i>base.</i><tt>subpop.num-current</tt><br>
 <font size=-1> int &gt;= 0</font></td>
 <td valign=top>(the number of random individuals from any given subpopulation fropm the current population to be selected as collaborators)
 </td></tr>

 <tr><td valign=top><i>base.</i><tt>subpop.num-elites</tt><br>
 <font size=-1> int &gt;= 0</font></td>
 <td valign=top>(the number of elite individuals from any given subpopulation from the previous population to be selected as collaborators. For generation 0, random individuals from the current population will be used.  )
 </td></tr>

 <tr><td valign=top><i>base.</i><tt>subpop.num-prev</tt><br>
 <font size=-1> int &gt;= 0</font></td>
 <td valign=top>(the number of random individuals from any given subpopulation from the previous population to be selected as collaborators.   For generation 0, random individuals from the current population will be used)
 </td></tr>

 <tr><td valign=top><i>base.</i><tt>subpop.X.select-prev</tt><br>
 <font size=-1> instance of ec.SelectionMethod</font></td>
 <td valign=top>(the SelectionMethod used to select partners from the individuals in subpopulation X at the previous generation)
 </td></tr>

 <tr><td valign=top><i>base.</i><tt>subpop.X.select-current</tt><br>
 <font size=-1> instance of ec.SelectionMethod</font></td>
 <td valign=top>(the SelectionMethod used to select partners from the individuals in subpopulation X at the current generation.
 <b>WARNING.</b>  This SelectionMethod must not select based on fitness, since fitness hasn't been set yet.
 RandomSelection is a good choice. )

 <tr><td valign=top><i>base.</i><tt>shuffling</tt><br>
 <font size=-1> boolean (default = false)</font></td>
 <td valign=top>(instead of selecting individuals from )
 </td></tr>


 </table>

 *
 * @author Liviu Panait and Sean Luke
 * @version 2.0
 */

public class MultiPopCoevolutionaryEvaluator extends Evaluator
    {
    private static final long serialVersionUID = 1;

    // the preamble for selecting partners from each subpopulation
    public static final String P_SUBPOP = "subpop";

    // the number of random partners selected from the current generation
    public static final String P_NUM_RAND_IND = "num-current";
    protected int numCurrent;

    // the number of shuffled random partners selected from the current generation
    public static final String P_NUM_SHUFFLED = "num-shuffled";
    protected int numShuffled;

    // the number of elite partners selected from the previous generation
    public static final String P_NUM_ELITE = "num-elites";  //4
    protected int numElite;
    protected Individual[/*subpopulation*/][/*the elites*/] eliteIndividuals;

    // the number of random partners selected from the current and previous generations
    public final static String P_NUM_IND = "num-prev"; //0
    protected int numPrev;
    Population previousPopulation;

    // the selection method used to select the other partners from the previous generation
    public static final String P_SELECTION_METHOD_PREV = "select-prev"; //randomly
    SelectionMethod[] selectionMethodPrev;

    // the selection method used to select the other partners from the current generation
    public static final String P_SELECTION_METHOD_CURRENT = "select-current";
    SelectionMethod[] selectionMethodCurrent;

    //fzhang 2019.5.24
    //create a new object should use this one, this is construct function
   /* public  MultiPopCoevolutionaryEvaluator(){

    }*/

   //fzhang 2019.5.28 in order to use these values in children classes
   public static final String P_POP_ADAPT_FRAC_ELITES = "pop-adapt-frac-elites";
   //public static final String P_POP_ADAPT_FRAC_ADAPTED = "pop-adapt-frac-adapted";
   public double fracElites;
   //public double fracAdapted;

    public void setup( final EvolutionState state, final Parameter base )
        {
        super.setup( state, base ); //load and setup problem, including problem, model, objectives, ADFStack and ADFContent.

        // evaluators are set up AFTER breeders, so I can check this now
        if (state.breeder instanceof SimpleBreeder &&
            ((SimpleBreeder)(state.breeder)).sequentialBreeding)  // we're going sequentil
            state.output.message("The Breeder is breeding sequentially, so the MultiPopCoevolutionaryEvaluator is also evaluating sequentially.");

        // at this point, we do not know the number of subpopulations, so we read it as well from the parameters file
        Parameter tempSubpop = new Parameter( ec.Initializer.P_POP ).push( ec.Population.P_SIZE ); //2
        int numSubpopulations = state.parameters.getInt( tempSubpop, null, 0 ); //2
        if( numSubpopulations <= 0 )
            state.output.fatal( "Parameter not found, or it has a non-positive value.", tempSubpop );

        numElite = state.parameters.getInt( base.push(P_NUM_ELITE), null, 0 ); //4
        if( numElite < 0 )
            state.output.fatal( "Parameter not found, or it has an incorrect value.", base.push(P_NUM_ELITE) );

        numShuffled = state.parameters.getInt( base.push(P_NUM_SHUFFLED), null, 0 );
        if( numShuffled < 0 )
            state.output.fatal( "Parameter not found, or it has an incorrect value.", base.push(P_NUM_SHUFFLED) );

        numCurrent = state.parameters.getInt( base.push(P_NUM_RAND_IND), null, 0 ); //0
        //System.out.println(numCurrent);  //0
        selectionMethodCurrent = new SelectionMethod[numSubpopulations];
        if( numCurrent < 0 )
            state.output.fatal( "Parameter not found, or it has an incorrect value.", base.push(P_NUM_RAND_IND) );
        else if( numCurrent == 0 )
            state.output.message( "Not testing against current individuals:  Current Selection Methods will not be loaded."); //here
        else if( numCurrent > 0 )
            {
            for(int i = 0; i < numSubpopulations; i++)
                {
                selectionMethodCurrent[i] = (SelectionMethod)
                    (state.parameters.getInstanceForParameter(
                        base.push(P_SUBPOP).push(""+i).push(P_SELECTION_METHOD_CURRENT),base.push(P_SELECTION_METHOD_CURRENT),SelectionMethod.class));
                if (selectionMethodCurrent[i] == null)
                    state.output.error("No selection method provided for subpopulation " + i,
                        base.push(P_SUBPOP).push(""+i).push(P_SELECTION_METHOD_CURRENT),
                        base.push(P_SELECTION_METHOD_CURRENT));
                else selectionMethodCurrent[i].setup(state,base.push(P_SUBPOP).push(""+i).push(P_SELECTION_METHOD_CURRENT));
                }
            }

        numPrev = state.parameters.getInt( base.push(P_NUM_IND), null, 0 ); //0
        selectionMethodPrev = new SelectionMethod[numSubpopulations];
        if( numPrev < 0 )
            state.output.fatal( "Parameter not found, or it has an incorrect value.", base.push(P_NUM_IND) );
        else if( numPrev == 0 )
            state.output.message( "Not testing against previous individuals:  Previous Selection Methods will not be loaded."); //here
        else if( numPrev > 0 )
            {
            for(int i = 0; i < numSubpopulations; i++)
                {
                selectionMethodPrev[i] = (SelectionMethod)
                    (state.parameters.getInstanceForParameter(
                        base.push(P_SUBPOP).push(""+i).push(P_SELECTION_METHOD_PREV),base.push(P_SELECTION_METHOD_PREV),SelectionMethod.class));
                if (selectionMethodPrev[i] == null)
                    state.output.error("No selection method provided for subpopulation " + i,
                        base.push(P_SUBPOP).push(""+i).push(P_SELECTION_METHOD_PREV),
                        base.push(P_SELECTION_METHOD_PREV));
                else selectionMethodPrev[i].setup(state,base.push(P_SUBPOP).push(""+i).push(P_SELECTION_METHOD_PREV));
                }
            }

        if( numElite + numCurrent + numPrev + numShuffled <= 0 )
            state.output.error( "The total number of partners to be selected should be > 0." );
        state.output.exitIfErrors();

            fracElites = state.parameters.getDoubleWithDefault(
                    new Parameter(P_POP_ADAPT_FRAC_ELITES), null, 0.0); //0.0
            /*fracAdapted = state.parameters.getDoubleWithDefault(
                    new Parameter(P_POP_ADAPT_FRAC_ADAPTED), null, 1.0); //0.1*/
        }


        //fzhang 2019.6.16
        @Override
        public void evaluatePopulation(EvolutionState state, Population newPop) {

        }

        public boolean runComplete( final EvolutionState state )
        {
        return false;
        }

    /** Returns true if the subpopulation should be evaluated.  This will happen if the Breeder
        believes that the subpopulation should be breed afterwards. */
    public boolean shouldEvaluateSubpop(EvolutionState state, int subpop, int threadnum)
        {
        return (state.breeder instanceof SimpleBreeder &&
            ((SimpleBreeder)(state.breeder)).shouldBreedSubpop(state, subpop, threadnum));
        }

    public void evaluatePopulation(final EvolutionState state)
        {
        // determine who needs to be evaluated
        boolean[] preAssessFitness = new boolean[state.population.subpops.length];
        boolean[] postAssessFitness = new boolean[state.population.subpops.length];
        for(int i = 0; i < state.population.subpops.length; i++)
            {
            postAssessFitness[i] = shouldEvaluateSubpop(state, i, 0);
            //System.out.println(shouldEvaluateSubpop(state, i, 0));  //true  true
            preAssessFitness[i] = postAssessFitness[i] || (state.generation == 0);  // always prepare (set up trials) on generation 0
            }

        // do evaluation
        beforeCoevolutionaryEvaluation( state, state.population, (GroupedProblemForm)p_problem );

        ((GroupedProblemForm)p_problem).preprocessPopulation(state,state.population, preAssessFitness, false);
          performCoevolutionaryEvaluation( state, state.population, (GroupedProblemForm)p_problem );

          //change the elites for cooperative coevolution
        ((GroupedProblemForm)p_problem).postprocessPopulation(state, state.population, postAssessFitness, false);

        afterCoevolutionaryEvaluation( state, state.population, (GroupedProblemForm)p_problem );//change the elites
        }

        /**fzhang 2019.5.25 this prepares elites for cooperative coevolution before the evaluation process
         * that means
        */
    protected void beforeCoevolutionaryEvaluation( final EvolutionState state,
        final Population population,
        final GroupedProblemForm prob )
        {
        if( state.generation == 0 )
            {
            //
            // create arrays for the elite individuals in the population at the previous generation.
            // deep clone the elite individuals as random individuals (in the initial generation, nobody has been evaluated yet).
            //

            // deal with the elites
            eliteIndividuals = new Individual[state.population.subpops.length][numElite]; //2*1 matrix
            // copy the first individuals in each subpopulation (they are already randomly generated)
            //System.out.println(state.population.subpops.length); //2
            //System.out.println(numElite); //4
            //System.out.println(eliteIndividuals.length); //2
           /* ((GPIndividual)state.population.subpops[0].individuals[0]).trees[0] = LispParser.parseJobShopRule("PT");
            ((GPIndividual)state.population.subpops[1].individuals[0]).trees[0] = LispParser.parseJobShopRule("WIQ");*/

            //System.out.println(state.population.subpops[0].individuals.length );  //512
            for( int i = 0 ; i < eliteIndividuals.length ; i++ )//2
                {
                if( numElite > state.population.subpops[i].individuals.length )
                    state.output.fatal( "Number of elite partners is greater than the size of the subpopulation." );
                for( int j = 0; j < numElite ; j++ )
                    eliteIndividuals[i][j] = (Individual)(state.population.subpops[i].individuals[j].clone());  // just take the first N individuals of each subpopulation
                }

          /*  for( int i = 0 ; i < eliteIndividuals.length ; i++ )//2
            {
            for( int j = 0; j < numElite ; j++ )
                System.out.println(eliteIndividuals[i][j]);
            }*/

            //System.out.println(numShuffled); //0
            // test for shuffled
            //make sure the subPopulations have the same size
            if (numShuffled > 0)
                {
                int size = state.population.subpops[0].individuals.length; //512
                for (int i =0; i < state.population.subpops.length; i++) //2
                    {
                    if (state.population.subpops[i].individuals.length != size)
                        state.output.fatal("Shuffling was requested in MultiPopCoevolutionaryEvaluator, but the subpopulation sizes are not the same.  " +
                            "Specifically, subpopulation 0 has size " + size + " but subpopulation " + i + " has size " + state.population.subpops[i].individuals.length);
                    }
                }

            }
        }

    // individuals to evaluate together
    Individual[] inds = null;
    // which individual should have its fitness updated as a result
    boolean[] updates = null;



    protected void shuffle(EvolutionState state, int[] a)
        {
    	//System.out.println(state.random[0]); //nothing be printed out.
        MersenneTwisterFast mtf = state.random[0];
        //System.out.println(state.random[0]);    //nothing be printed out.
        //System.out.println("the value genrated by MersenneTwisterFast is "+ mtf); //nothing be printed out.
        for(int x = a.length - 1; x >= 1; x--)
            {
            int rand = mtf.nextInt(x+1); //rand looks like a random value
            //exchange the values of a[x] and a[rand]
            int obj = a[x];
            a[x] = a[rand];
            a[rand] = obj;
            }
        }



    public void performCoevolutionaryEvaluation( final EvolutionState state,
        final Population population,
        final GroupedProblemForm prob ) {
        int evaluations = 0;

        inds = new Individual[population.subpops.length];
        updates = new boolean[population.subpops.length];//check if we need to evaluate the individuals, because it is so expensive, should be careful.

        // we start by warming up the selection methods
        //System.out.println("numCurrent: "+numCurrent); //0
        //numCurrent: the number of random individuals from any given subpopulation from the current population to be selected as collaborators

        //==========================here skip step 1: load the selectionMethod for current generation==================================
        //selectionMethodCurrent: the selection method used to select the other partners from the current generation
        //System.out.println(selectionMethodCurrent.length); //2, means we have two selectionMethod (each one for each populations) for Current
        //System.out.println(selectionMethodCurrent); //[Lec.SelectionMethod;@5ae63ade   ec.select.RandomSelection
        //numCurrent = 0
        if (numCurrent > 0) {
            for (int i = 0; i < selectionMethodCurrent.length; i++) {
                selectionMethodCurrent[i].prepareToProduce(state, i, 0);  //A default version of prepareToProduce which does nothing.
            }
        }

        //==========================here skip: step 2: load the selectionMethod for previous generation==================================
        //the selection method used to select the other partners from the previous generation
        //System.out.println("numPrev = "+numPrev); //0
        //System.out.println("selectionMethodPrev.length = "+selectionMethodPrev.length); //2
        if (numPrev > 0) {
            for (int i = 0; i < selectionMethodPrev.length; i++) {
                // do a hack here
                Population currentPopulation = state.population; //state.population is current population.
                state.population = previousPopulation;
                selectionMethodPrev[i].prepareToProduce(state, i, 0);  //A default version of prepareToProduce which does nothing.  here state is important, it is the state
                //for previous population
                state.population = currentPopulation; //currentPopulaiton is a temp parameter to save current population
            }
        }

        //step 3: build subPopulaiton, subpops[0] and subpops[1]
        // build subpopulation array to pass in each time
        int[] subpops = new int[state.population.subpops.length];
        //System.out.println(subpops.length);
        for(int j = 0; j < subpops.length; j++) {
            subpops[j] = j;
        }

        //System.out.println(prob);  //yimei.jss.ruleoptimisation.RuleCoevolutionProblem@5ae63ade
        //here skip: step 3: setup the shuffle: here num-shuffled = 0  here, we do not use it.
        if (numShuffled > 0) {
            int[/*numShuffled*/][/*subpop*/][/*shuffledIndividualIndexes*/] ordering = null;
            // build shuffled orderings
            ordering = new int[numShuffled][state.population.subpops.length][state.population.subpops[0].individuals.length]; // if num-shuffled =1  [1][2][512]
            for(int c = 0; c < numShuffled; c++)
                for(int m = 0; m < state.population.subpops.length; m++)
                    {
                    for(int i = 0; i < state.population.subpops[0].individuals.length; i++)
                        ordering[c][m][i] = i; //ordering[0][0][0] = 0, ordering[0][0][1] = 1, ordering[0][0][2] = 2, ordering[0][0][3] = 3
                    if (m != 0)
                        shuffle(state, ordering[c][m]); //ordering = new int[numShuffled][state.population.subpops.length]
                    }

            // for each individual
            for (int i = 0; i < state.population.subpops[0].individuals.length; i++) {
                for (int k = 0; k < numShuffled; k++) {
                    for (int ind = 0; ind < inds.length; ind++) {
                        inds[ind] = state.population.subpops[ind].individuals[ordering[k][ind][i]];
                        updates[ind] = true;
                    }
                    prob.evaluate(state, inds, updates, false, subpops, 0);  ////yimei.jss.ruleoptimisation.RuleCoevolutionProblem@5ae63ade
                    evaluations++;
                }
            }
        }
          //System.out.println(evaluations); //0

//        if (state.generation > 0) {
//            //want to find out whether elite individuals and or their collaborators are being included
//            //in the next generation
//            boolean[][] found = new boolean[2][2]; //should all be false
//
//            for (int subpop = 0; subpop < state.population.subpops.length; subpop++)
//            {
//                GPIndividual eliteInd = (GPIndividual) eliteIndividuals[subpop][0]; //one for each subpop
//                int otherSubpop = (subpop+1)%2;
//                GPIndividual otherEliteCollab = (GPIndividual) eliteIndividuals[otherSubpop][0].fitness.context[0];
//                //checking each individual
//                for (int i = state.population.subpops[subpop].individuals.length-2;
//                     i < state.population.subpops[subpop].individuals.length; i++)
//                {
//                    GPIndividual ind = (GPIndividual) state.population.subpops[subpop].individuals[i];
//                    if (ind.equals(eliteInd) || ind == eliteInd) {
//                        found[subpop][0] = true;
//                    }
//                    if (ind.equals(otherEliteCollab) || ind == otherEliteCollab) {
//                        found[otherSubpop][1] = true;
//                    }
//                }
//            }
//            for (int i = 0; i < 2; ++i) {
//                for (int j = 0; j < 2; ++j) {
//                    if (!found[i][j]) {
//                        if (j == 0) {
//                            System.out.println("Elite missing: "+i+" "+j);
//                        } else {
//                            System.out.println("Collab missing: "+i+" "+j);
//                        }
//                    }
//                }
//            }
//        }

        //==========================useful and important part=======================================
        //step 4: for each subpopulation, j means subPopulation   2*512*4*2= 8192  cost
        for (int j = 0; j < state.population.subpops.length; j++) //0,1
            {
            // now do elites and randoms
        	//System.out.println(!shouldEvaluateSubpop(state, j, 0)); //false
        	//System.out.println(eliteIndividuals[j].length); //4
        	//System.out.println(inds.length); //2
        	//System.out.println(state.population.subpops[j].individuals.length);  //512

            if (!shouldEvaluateSubpop(state, j, 0)) continue;  // don't evaluate this subpopulation

            // for each individual
            for (int i = 0; i < state.population.subpops[j].individuals.length; i++) //512
                {
                Individual individual = state.population.subpops[j].individuals[i];

                // Test against all the elites
                for (int k = 0; k < eliteIndividuals[j].length; k++) { //2
                    for (int ind = 0; ind < inds.length; ind++) { //2
                        if (ind == j) {   //j = 0, 1  (ind j) ---> (0 0) or (1 1) that is to say, this is the subpopulation1
                            inds[ind] = individual; //inds[0] = individual = state.population.subpops[0].individuals[0];
                                                    //inds[1] = individual = state.population.subpops[1].individuals[1];
                                                    //the individuals to evaluate together
                            updates[ind] = true;   // updates[0] = true    updates[1] = true   evaluate
                        }
                        else {  // this is subpopulation2
                            inds[ind] = eliteIndividuals[ind][k];   // (ind j) ---> (0 1) or (1 0)
                                                                    //inds[1] = eliteIndividuals[1][*]   inds[0] = eliteIndividuals[0][*]
                            updates[ind] = false;  // do not evaluate
                        }

                    /*    System.out.println("ind "+ ind);
                        System.out.println("i "+ i);
                        System.out.println("j "+ j);
                        System.out.println("k "+ k);*/
                    }

                    prob.evaluate(state,inds,updates, false, subpops, 0);
                   /* System.out.println("Evaluated finished: population "+ j);
                    System.out.println("individual "+ i);*/
                    evaluations++;
                }
                //System.out.println(evaluations);  //4  8  12 16 20 24 28 32 ... 4096   2*512*4 = 4096  inds[] is used to save the individuals we want to evaluated.

                //here, skip this part: test against random selected individuals of the current population
                for(int k = 0; k < numCurrent; k++) //0  skip this part
                    {
                    for(int ind = 0; ind < inds.length; ind++) //2
                        {
                        if (ind == j) { inds[ind] = individual; updates[ind] = true; }
                        else { inds[ind] = produceCurrent(ind, state, 0); updates[ind] = true; }
                        }
                    prob.evaluate(state,inds,updates, false, subpops, 0);
                    evaluations++;
                    }

                // here, skip this part. Test against random individuals of previous population
                for(int k = 0; k < numPrev; k++)  // 0  skip this part
                    {
                    for(int ind = 0; ind < inds.length; ind++)
                        {
                        if (ind == j) { inds[ind] = individual; updates[ind] = true; }
                        else { inds[ind] = producePrevious(ind, state, 0); updates[ind] = false; }
                        }
                    prob.evaluate(state,inds,updates, false, subpops, 0);
                    evaluations++;
                    }
                }
            }
        //============================================================================================================================

        //here, skip this part
        // now shut down the selection methods
        if (numCurrent > 0)
            for( int i = 0 ; i < selectionMethodCurrent.length; i++)
                selectionMethodCurrent[i].finishProducing( state, i, 0 );  //A default version of finishProducing, which does nothing.

        if (numPrev > 0)
            for( int i = 0 ; i < selectionMethodPrev.length ; i++ )
                {
                // do a hack here
                Population currentPopulation = state.population;
                state.population = previousPopulation;
                selectionMethodPrev[i].finishProducing( state, i, 0 );
                state.population = currentPopulation;
                }

        state.output.message("Evaluations: " + evaluations);
        }


    /** Selects one individual from the previous subpopulation.  If there is no previous
        population, because we're at generation 0, then an individual from the current
        population is selected at random. */
    protected Individual producePrevious(int subpopulation, EvolutionState state, int thread)
        {
        if (state.generation == 0)
            {
            // pick current at random.  Can't use a selection method because they may not have fitness assigned
            return state.population.subpops[subpopulation].individuals[
                state.random[0].nextInt(state.population.subpops[subpopulation].individuals.length)];
            }
        else
            {
            // do a hack here -- back up population, replace with the previous population, run the selection method, replace again
            Population currentPopulation = state.population;
            state.population = previousPopulation;
            Individual selected =
                state.population.subpops[subpopulation].individuals[
                    selectionMethodPrev[subpopulation].produce(subpopulation, state, thread)];
            state.population = currentPopulation;
            return selected;
            }
        }


    /** Selects one individual from the given subpopulation. */
    protected Individual produceCurrent(int subpopulation, EvolutionState state, int thread)
        {
        return state.population.subpops[subpopulation].individuals[
            selectionMethodCurrent[subpopulation].produce(subpopulation, state, thread)];
        }



    protected void afterCoevolutionaryEvaluation( final EvolutionState state,
        final Population population,
        final GroupedProblemForm prob )
        {
        if( numElite > 0 )
            {
            for(int i = 0 ; i < state.population.subpops.length; i++)
                if (shouldEvaluateSubpop(state, i, 0))          // only load elites for subpopulations which are actually changing
                    loadElites( state, i );
            }

        // copy over the previous population
        if (numPrev > 0)
            {
            previousPopulation = (Population)(state.population.emptyClone());
            for( int i = 0 ; i < previousPopulation.subpops.length ; i++ )
                for( int j = 0 ; j < previousPopulation.subpops[i].individuals.length ; j++ )
                    previousPopulation.subpops[i].individuals[j] = (Individual)(state.population.subpops[i].individuals[j].clone());
            }
        }


    void loadElites(final EvolutionState state, int whichSubpop) {
        Subpopulation subpop = state.population.subpops[whichSubpop];

        if (numElite == 1) {
            int best = 0;
            Individual[] oldinds = subpop.individuals;
            for (int x = 1; x < oldinds.length; x++) {
                if (oldinds[x].fitness.betterThan(oldinds[best].fitness)) {
                    best = x;
                    //only want to update eliteIndividual if it is better than current eliteIndividual
                }
            }
            if (state.population.subpops[whichSubpop].individuals[best].fitness.betterThan(
                    eliteIndividuals[whichSubpop][0].fitness)) {
                eliteIndividuals[whichSubpop][0] =
                        (Individual)(state.population.subpops[whichSubpop].individuals[best].clone());
            }
        }
        else if (numElite > 0)  // we'll need to sort
            {
            int[] orderedPop = new int[subpop.individuals.length];
            for(int x=0;x<subpop.individuals.length;x++) orderedPop[x] = x;

            // sort the best so far where "<" means "more fit than"
            QuickSort.qsort(orderedPop, new EliteComparator(subpop.individuals));

            // load the top N individuals
            for( int j = 0 ; j < numElite ; j++ )
                eliteIndividuals[whichSubpop][j] = (Individual)(state.population.subpops[whichSubpop].individuals[orderedPop[j]].clone());
            }
        }

        //fzhang 2019.5.25 in order to get the value from this class
        public Individual[][] getEliteindividual(){
           return this.eliteIndividuals;
        }
    }

class EliteComparator implements SortComparatorL
    {
    Individual[] inds;
    public EliteComparator(Individual[] inds) {super(); this.inds = inds;}
    public boolean lt(long a, long b)
        { return inds[(int)a].fitness.betterThan(inds[(int)b].fitness); }
    public boolean gt(long a, long b)
        { return inds[(int)b].fitness.betterThan(inds[(int)a].fitness); }
    }
