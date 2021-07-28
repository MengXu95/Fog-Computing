/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.simple;
import ec.*;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;
import ec.util.*;

/*
 * SimpleBreeder.java
 *
 * Created: Tue Aug 10 21:00:11 1999
 * By: Sean Luke
 */

/**
 * Breeds each subpopulation separately, with no inter-population exchange,
 * and using a generational approach.  A SimpleBreeder may have multiple
 * threads; it divvys up a subpopulation into chunks and hands one chunk
 * to each thread to populate.  One array of BreedingPipelines is obtained
 * from a population's Species for each operating breeding thread.
 *
 * <p>Prior to breeding a subpopulation, a SimpleBreeder may first fill part of the new
 * subpopulation up with the best <i>n</i> individuals from the old subpopulation.
 * By default, <i>n</i> is 0 for each subpopulation (that is, this "elitism"
 * is not done).  The elitist step is performed by a single thread.
 *
 * <p>If the <i>sequential</i> parameter below is true, then breeding is done specially:
 * instead of breeding all Subpopulations each generation, we only breed one each generation.
 * The subpopulation index to breed is determined by taking the generation number, modulo the
 * total number of subpopulations.  Use of this parameter outside of a coevolutionary context
 * (see ec.coevolve.MultiPopCoevolutionaryEvaluator) is very rare indeed.
 *
 * <p>SimpleBreeder adheres to the default-subpop parameter in Population: if either an 'elite'
 * or 'reevaluate-elites' parameter is missing, it will use the default subpopulation's value
 * and signal a warning.
 *
 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><tt><i>base</i>.elite.<i>i</i></tt><br>
 <font size=-1>int >= 0 (default=0)</font></td>
 <td valign=top>(the number of elitist individuals for subpopulation <i>i</i>)</td></tr>
 <tr><td valign=top><tt><i>base</i>.reduce-by.<i>i</i></tt><br>
 <font size=-1>int >= 0 (default=0)</font></td>
 <td valign=top>(how many to reduce subpopulation <i>i</i> by each generation)</td></tr>
 <tr><td valign=top><tt><i>base</i>.minimum-size.<i>i</i></tt><br>
 <font size=-1>int >= 2 (default=2)</font></td>
 <td valign=top>(the minimum size for subpopulation <i>i</i> regardless of reduction)</td></tr>
 <tr><td valign=top><tt><i>base</i>.reevaluate-elites.<i>i</i></tt><br>
 <font size=-1>boolean (default = false)</font></td>
 <td valign=top>(should we reevaluate the elites of subpopulation <i>i</i> each generation?)</td></tr>
 <tr><td valign=top><tt><i>base</i>.sequential</tt><br>
 <font size=-1>boolean (default = false)</font></td>
 <td valign=top>(should we breed just one subpopulation each generation (as opposed to all of them)?)</td></tr>
 </table>
 *
 *
 * @author Sean Luke
 * @version 1.0
 */

public class SimpleBreeder extends Breeder
    {
    public static final String P_ELITE = "elite";
    public static final String P_ELITE_FRAC = "elite-fraction";
    public static final String P_REEVALUATE_ELITES = "reevaluate-elites";
    public static final String P_SEQUENTIAL_BREEDING = "sequential";
    public static final String P_CLONE_PIPELINE_AND_POPULATION = "clone-pipeline-and-population";
    public static final String P_REDUCE_BY = "reduce-by";
    public static final String P_MINIMUM_SIZE = "minimum-size";
    public static final String P_PRE_GENERATIONS = "pre-generations";
    public static final String P_POP_ADAPT_FRAC_ELITES = "pop-adapt-frac-elites";
    /** An array[subpop] of the number of elites to keep for that subpopulation */
    public int[] elite;
    public int[] reduceBy;
    public int[] minimumSize;
    public double[] eliteFrac;
    public boolean[] reevaluateElites;
    public boolean sequentialBreeding;
    public boolean clonePipelineAndPopulation;
    public Population backupPopulation = null;
    private int preGenerations;
    private double fracElites;

    public static final int NOT_SET = -1;

    public ThreadPool pool = new ThreadPool();

    public boolean usingElitism(int subpopulation)
        {
        return (elite[subpopulation] > 0 ) || (eliteFrac[subpopulation] > 0);
        }

    public int numElites(EvolutionState state, int subpopulation)
        {
    	//the number of elites for breeding
        if (elite[subpopulation] != NOT_SET)
            {
            return elite[subpopulation];
            }
        else if (eliteFrac[subpopulation] == 0)
            {
            return 0; // no elites
            }
        else if (eliteFrac[subpopulation] != NOT_SET)
            {
            return (int) Math.max(Math.floor(state.population.subpops[subpopulation].individuals.length * eliteFrac[subpopulation]), 1.0);  // AT LEAST 1 ELITE
            }
        else
            {
            state.output.warnOnce("Elitism error (SimpleBreeder).  This shouldn't be able to happen.  Please report.");
            return 0;  // this shouldn't happen
            }
        }

    public void setup(final EvolutionState state, final Parameter base)
        {
        Parameter p = new Parameter(Initializer.P_POP).push(Population.P_SIZE);
        int size = state.parameters.getInt(p,null,1);  //2 if size is wrong, we'll let Population complain about it -- for us, we'll just make 0-sized arrays and drop out.
        //System.out.println(size);  //2 the size of populations

        eliteFrac = new double[size];
        elite = new int[size];
        for(int i = 0; i < size; i++)
            eliteFrac[i] = elite[i] = NOT_SET;
        reevaluateElites = new boolean[size];//boolean type: check if we need to reevaluate the Elites
        reduceBy = new int[size];  // all zero
        minimumSize = new int[size]; // all zero
        for(int i = 0; i < size; i++)
            minimumSize[i] = 2;  // at least, have two

        sequentialBreeding = state.parameters.getBoolean(base.push(P_SEQUENTIAL_BREEDING), null, false); //false
        if (sequentialBreeding && (size == 1)) // uh oh, this can't be right
            state.output.fatal("The Breeder is breeding sequentially, but you have only one population.", base.push(P_SEQUENTIAL_BREEDING));

        clonePipelineAndPopulation =state.parameters.getBoolean(base.push(P_CLONE_PIPELINE_AND_POPULATION), null, true); //true
        if (!clonePipelineAndPopulation && (state.breedthreads > 1)) // uh oh, this can't be right
            state.output.fatal("The Breeder is not cloning its pipeline and population, but you have more than one thread.", base.push(P_CLONE_PIPELINE_AND_POPULATION));

        int defaultSubpop = state.parameters.getInt(new Parameter(Initializer.P_POP).push(Population.P_DEFAULT_SUBPOP), null, 0); //0
        for(int x=0;x<size;x++) //size = 2
            {
            reduceBy[x] = state.parameters.getIntWithDefault(base.push(P_REDUCE_BY).push(""+x), null, 0); //0
            if (reduceBy[x] < 0)
                state.output.fatal("reduce-by must be set to an integer >= 0.", base.push(P_REDUCE_BY).push(""+x));

            minimumSize[x] = state.parameters.getIntWithDefault(base.push(P_MINIMUM_SIZE).push(""+x), null, 2); //2
            if (minimumSize[x] < 2)
                state.output.fatal("minimum-size must be set to an integer >= 2.", base.push(P_MINIMUM_SIZE).push(""+x));

            // get elites
            if (state.parameters.exists(base.push(P_ELITE).push(""+x),null))  //breed.elite.0 =  2; breed.elite.1 = 2
                {
                if (state.parameters.exists(base.push(P_ELITE_FRAC).push(""+x),null)) //null
                    state.output.error("Both elite and elite-frac specified for subpouplation " + x + ".", base.push(P_ELITE_FRAC).push(""+x), base.push(P_ELITE_FRAC).push(""+x));
                else
                    {
                    elite[x] = state.parameters.getIntWithDefault(base.push(P_ELITE).push(""+x),null,0); //2 here means how mant parents we will need gor breeding
                    if (elite[x] < 0)
                        state.output.error("Elites for subpopulation " + x + " must be an integer >= 0", base.push(P_ELITE).push(""+x));
                    }
                }
            else if (state.parameters.exists(base.push(P_ELITE_FRAC).push(""+x),null))
                {
                eliteFrac[x] = state.parameters.getDoubleWithMax(base.push(P_ELITE_FRAC).push(""+x),null,0.0, 1.0);
                if (eliteFrac[x] < 0.0)
                    state.output.error("Elite Fraction of subpopulation " + x + " must be a real value between 0.0 and 1.0 inclusive", base.push(P_ELITE_FRAC).push(""+x));
                }
            else if (defaultSubpop >= 0)  //0
                {
                if (state.parameters.exists(base.push(P_ELITE).push(""+defaultSubpop),null))
                    {
                    elite[x] = state.parameters.getIntWithDefault(base.push(P_ELITE).push(""+defaultSubpop),null,0); //2
                    if (elite[x] < 0)
                        state.output.warning("Invalid default subpopulation elite value.");  // we'll fail later
                    }
                else if (state.parameters.exists(base.push(P_ELITE_FRAC).push(""+defaultSubpop),null))
                    {
                    eliteFrac[x] = state.parameters.getDoubleWithMax(base.push(P_ELITE_FRAC).push(""+defaultSubpop),null,0.0, 1.0);
                    if (eliteFrac[x] < 0.0)
                        state.output.warning("Invalid default subpopulation elite-frac value.");  // we'll fail later
                    }
                else  // elitism is 0
                    {
                    elite[x] = 0;
                    }
                }
            else // elitism is 0
                {
                elite[x] = 0;
                }

            // get reevaluation
            if (defaultSubpop >= 0 && !state.parameters.exists(base.push(P_REEVALUATE_ELITES).push(""+x),null)) //null
                {
                reevaluateElites[x] = state.parameters.getBoolean(base.push(P_REEVALUATE_ELITES).push(""+defaultSubpop), null, false);
                if (reevaluateElites[x])
                    state.output.warning("Elite reevaluation not specified for subpopulation " + x + ".  Using values for default subpopulation " + defaultSubpop + ": " + reevaluateElites[x]);
                }
            else
                {
                reevaluateElites[x] = state.parameters.getBoolean(base.push(P_REEVALUATE_ELITES).push(""+x), null, false); //false
                }
            }

        state.output.exitIfErrors();
        }

    /** Elites are often stored in the top part of the subpopulation; this function returns what
        part of the subpopulation contains individuals to replace with newly-bred ones
        (up to but not including the elites). */
    public int computeSubpopulationLength(EvolutionState state, Population newpop, int subpopulation, int threadnum)
        {
        if (!shouldBreedSubpop(state, subpopulation, threadnum))
            return newpop.subpops[subpopulation].individuals.length;  // we're not breeding the population, just copy over the whole thing
        return newpop.subpops[subpopulation].individuals.length - (numElites(state, subpopulation)); // we're breeding population, so elitism may have happened
        }

    /** A simple breeder that doesn't attempt to do any cross-
        population breeding.  Basically it applies pipelines,
        one per thread, to various subchunks of a new population. */
    public Population breedPopulation(EvolutionState state)
        {
        Population newpop = null;
        if (clonePipelineAndPopulation) //default value: true
        	//create a newpop with two subpopulations, but the individuals are empty.
            newpop = (Population) state.population.emptyClone();
        else //skip this part
            {
            if (backupPopulation == null)
                backupPopulation = (Population) state.population.emptyClone();
            newpop = backupPopulation;
            newpop.clear(); //** Sets all Individuals in the Population to null, preparing it to be reused. */
            backupPopulation = state.population;  // swap in
            }

        // maybe resize?
        for(int i = 0; i < state.population.subpops.length; i++)
            {
            if (reduceBy[i] > 0) // 0 skip this
                {
                int prospectiveSize = Math.max(
                    Math.max(state.population.subpops[i].individuals.length - reduceBy[i], minimumSize[i]),
                    numElites(state, i));
                if (prospectiveSize < state.population.subpops[i].individuals.length)  // let's resize!
                    {
                    state.output.message("Subpop " + i + " reduced " + state.population.subpops[i].individuals.length + " -> " + prospectiveSize);
                    newpop.subpops[i].resize(prospectiveSize);
                    }
                }
            }

        //!!!!!
        // load elites into top of newpop
        loadElites(state, newpop);     /** A private helper function for breedPopulation which loads elites into
        a subpopulation. */

        // how many threads do we really need?  No more than the maximum number of individuals in any subpopulation
        int numThreads = 0;
        for(int x = 0; x < state.population.subpops.length; x++)
            numThreads = Math.max(numThreads, state.population.subpops[x].individuals.length); //numThreads = 2
        numThreads = Math.min(numThreads, state.breedthreads);
        //System.out.println(state.breedthreads);  //1  so,numThreads =1

        if (numThreads < state.breedthreads)
            state.output.warnOnce("Largest subpopulation size (" + numThreads +") is smaller than number of breedthreads (" + state.breedthreads + "), so fewer breedthreads will be created.");

        int numinds[][] =
            new int[numThreads][state.population.subpops.length];
        int from[][] =
            new int[numThreads][state.population.subpops.length];

        for(int x=0;x<state.population.subpops.length;x++)
            {
            int length = computeSubpopulationLength(state, newpop, x, 0);

            // we will have some extra individuals.  We distribute these among the early subpopulations
            int individualsPerThread = length / numThreads;  // integer division
            int slop = length - numThreads * individualsPerThread;
            int currentFrom = 0;

            for(int y=0;y<numThreads;y++)
                {
                if (slop > 0)
                    {
                    numinds[y][x] = individualsPerThread + 1;
                    slop--;
                    }
                else
                    numinds[y][x] = individualsPerThread;

                if (numinds[y][x] == 0)
                    {
                    state.output.warnOnce("More threads exist than can be used to breed some subpopulations (first example: subpopulation " + x + ")");
                    }

                from[y][x] = currentFrom;
                currentFrom += numinds[y][x];
                }
            }

/*
  for(int y=0;y<state.breedthreads;y++)
  for(int x=0;x<state.population.subpops.length;x++)
  {
  // the number of individuals we need to breed
  int length = computeSubpopulationLength(state, newpop, x, 0);
  // the size of each breeding chunk except the last one
  int firstBreedChunkSizes = length/state.breedthreads;
  // the size of the last breeding chunk
  int lastBreedChunkSize =
  firstBreedChunkSizes + length - firstBreedChunkSizes * (state.breedthreads);

  // figure numinds
  if (y < state.breedthreads-1) // not the last one
  numinds[y][x] = firstBreedChunkSizes;
  else // the last one
  numinds[y][x] = lastBreedChunkSize;

  // figure from
  from[y][x] = (firstBreedChunkSizes * y);
  }
*/
        if (numThreads==1)
            {
            breedPopChunk(newpop,state,numinds[0],from[0],0);
            }
        else
            {
            /*
              Thread[] t = new Thread[numThreads];

              // start up the threads
              for(int y=0;y<numThreads;y++)
              {
              SimpleBreederThread r = new SimpleBreederThread();
              r.threadnum = y;
              r.newpop = newpop;
              r.numinds = numinds[y];
              r.from = from[y];
              r.me = this;
              r.state = state;
              t[y] = new Thread(r);
              t[y].start();
              }

              // gather the threads
              for(int y=0;y<numThreads;y++)
              try
              {
              t[y].join();
              }
              catch(InterruptedException e)
              {
              state.output.fatal("Whoa! The main breeding thread got interrupted!  Dying...");
              }
            */


            // start up the threads
            for(int y=0;y<numThreads;y++)
                {
                SimpleBreederThread r = new SimpleBreederThread();
                r.threadnum = y;
                r.newpop = newpop;
                r.numinds = numinds[y];
                r.from = from[y];
                r.me = this;
                r.state = state;
                pool.start(r, "ECJ Breeding Thread " + y );
                }

            pool.joinAll();
            }
        return newpop;
        }

    /** Returns true if we're doing sequential breeding and it's the subpopulation's turn (round robin,
        one subpopulation per generation).*/
    public boolean shouldBreedSubpop(EvolutionState state, int subpop, int threadnum)
        {
    	//System.out.println(!sequentialBreeding || (state.generation % state.population.subpops.length) == subpop);  //true, true, true
        return (!sequentialBreeding || (state.generation % state.population.subpops.length) == subpop);
        }

    /** A private helper function for breedPopulation which breeds a chunk
        of individuals in a subpopulation for a given thread.
        Although this method is declared
        public (for the benefit of a private helper class in this file),
        you should not call it. */

    protected void breedPopChunk(Population newpop, EvolutionState state, int[] numinds, int[] from, int threadnum)
        {
        for(int subpop=0;subpop<newpop.subpops.length;subpop++)
            {
            // if it's subpop's turn and we're doing sequential breeding...
            if (!shouldBreedSubpop(state, subpop, threadnum))
                {
                // instead of breeding, we should just copy forward this subpopulation.  We'll copy the part we're assigned
                for(int ind=from[subpop] ; ind < numinds[subpop] - from[subpop]; ind++)
                    // newpop.subpops[subpop].individuals[ind] = (Individual)(state.population.subpops[subpop].individuals[ind].clone());
                    // this could get dangerous
                    newpop.subpops[subpop].individuals[ind] = state.population.subpops[subpop].individuals[ind];
                }
            else
                {
                // do regular breeding of this subpopulation
                BreedingPipeline bp = null;
                if (clonePipelineAndPopulation)
                    bp = (BreedingPipeline)newpop.subpops[subpop].species.pipe_prototype.clone();
                else
                    bp = (BreedingPipeline)newpop.subpops[subpop].species.pipe_prototype;

                // check to make sure that the breeding pipeline produces
                // the right kind of individuals.  Don't want a mistake there! :-)
                int x;
                if (!bp.produces(state,newpop,subpop,threadnum))
                    state.output.fatal("The Breeding Pipeline of subpopulation " + subpop + " does not produce individuals of the expected species " + newpop.subpops[subpop].species.getClass().getName() + " or fitness " + newpop.subpops[subpop].species.f_prototype );
                bp.prepareToProduce(state,subpop,threadnum);

                // start breedin'!

                x=from[subpop];
                int upperbound = from[subpop]+numinds[subpop];
                while(x<upperbound) { //8   x = 0...7
                	x += bp.produce(1,upperbound-x,x,subpop,
                        newpop.subpops[subpop].individuals,
                        state,threadnum);
                }
                if (x>upperbound) // uh oh!  Someone blew it!
                    state.output.fatal("Whoa!  A breeding pipeline overwrote the space of another pipeline in subpopulation " + subpop + ".  You need to check your breeding pipeline code (in produce() ).");

                bp.finishProducing(state,subpop,threadnum);
                }
            }
        }


        static class EliteComparator implements SortComparatorL
        {
        Individual[] inds;
        public EliteComparator(Individual[] inds) {super(); this.inds = inds;}
        public boolean lt(long a, long b)
            { return inds[(int)b].fitness.betterThan(inds[(int)a].fitness); }
        public boolean gt(long a, long b)
            { return inds[(int)a].fitness.betterThan(inds[(int)b].fitness); }
        }

    protected void unmarkElitesEvaluated(EvolutionState state, Population newpop)
        {
        for(int sub=0;sub<newpop.subpops.length;sub++)
            {
            if (!shouldBreedSubpop(state, sub, 0))
                continue;
            for(int e=0; e < numElites(state, sub); e++)
                {
                int len = newpop.subpops[sub].individuals.length;
                if (reevaluateElites[sub])
                    newpop.subpops[sub].individuals[len - e - 1].evaluated = false;
                }
            }
        }

    /** A private helper function for breedPopulation which loads elites into
        a subpopulation. */

    protected void loadElites(EvolutionState state, Population newpop)
        {
        // are our elites small enough?
        for(int x=0;x<state.population.subpops.length;x++)
            {
            if (numElites(state, x)>state.population.subpops[x].individuals.length)
                state.output.error("The number of elites for subpopulation " + x + " exceeds the actual size of the subpopulation",
                    new Parameter(EvolutionState.P_BREEDER).push(P_ELITE).push(""+x));
            if (numElites(state, x)==state.population.subpops[x].individuals.length)
                state.output.warning("The number of elites for subpopulation " + x + " is the actual size of the subpopulation",
                    new Parameter(EvolutionState.P_BREEDER).push(P_ELITE).push(""+x));
            }
        state.output.exitIfErrors();

        //=================find out the index of the best indivudual=======================
        // we assume that we're only grabbing a small number (say <10%), so
        // it's not being done multithreaded
        int[] bestIndex = new int[state.population.subpops.length];
        for (int sub = 0; sub < state.population.subpops.length; sub++) {
            int best = 0;
            Individual[] oldinds = state.population.subpops[sub].individuals;
            for (int x = 1; x < oldinds.length; x++) {
                if (oldinds[x].fitness.betterThan(oldinds[best].fitness)) {
                    best = x;
                }
            }
            bestIndex[sub] = best;
            //the index of best individuals in each subpopulation are saved in bestIndex[sub], like bestIndex[0] = 0, bestIndex[1]=145
        }

        for(int sub=0;sub<state.population.subpops.length;sub++) //0  1
            {
        	//System.out.println(!shouldBreedSubpop(state, sub, 0));  //nothing is printed out.
        	//skip here
            if (!shouldBreedSubpop(state, sub, 0))  // don't load the elites for this one, we're not doing breeding of it  true
                {
                continue;
                }

            //System.out.println("numElites(state, sub)  "+numElites(state, sub) );  //always eauql two, seems like no related to eval.num-elites and breed.elite.0
            // if the number of elites is 1, then we handle this by just finding the best one.
            if (numElites(state, sub) == 1) {
                Individual[] oldinds = state.population.subpops[sub].individuals; // sub = 0,   1
                Individual[] inds = newpop.subpops[sub].individuals; // null
                if (state.population.subpops.length > 1) {
                    int otherSubPop = (sub+1)%2;  // otherSubPop = 1,  0
                    Individual[] oldindsOtherSubpop = state.population.subpops[otherSubPop].individuals;
                    //want to also insert context of best individual   save the best
                    //Auxiliary variable, used by coevolutionary processes, to store the individuals
                    //involved in producing this given Fitness value.
                    Individual otherCollab = (Individual) oldindsOtherSubpop[bestIndex[otherSubPop]].fitness.getContext()[sub].clone();
                    inds[inds.length-2] = otherCollab;
                }
                Individual elite = (Individual)(oldinds[bestIndex[sub]].clone());
                inds[inds.length-1] = elite;
            }
            else if (numElites(state, sub)>0)  // we'll need to sort
                {
            	//define int[] orderPop, length = 512 and its elements are from 0 to 511
                int[] orderedPop = new int[state.population.subpops[sub].individuals.length];
                for(int x=0;x<state.population.subpops[sub].individuals.length;x++)
                	orderedPop[x] = x;
                //orderPop[0]= 0, orderPop[1]= 1, orderPop[2]= 2....orderPop[511]= 511

                // sort the best so far where "<" means "not as fit as"
                QuickSort.qsort(orderedPop, new EliteComparator(state.population.subpops[sub].individuals));
                // load the top N individuals

                Individual[] inds = newpop.subpops[sub].individuals; // has not value
                Individual[] oldinds = state.population.subpops[sub].individuals; //has values
                for(int x=inds.length-numElites(state, sub);x<inds.length;x++)//start from 510, because numElites(state,sub)
                    inds[x] = (Individual)(oldinds[orderedPop[x]].clone());
                }
            }

        // optionally force reevaluation
        unmarkElitesEvaluated(state, newpop);
        }


        /**
         *
         * @param pop: evaluate population directly rather than state.population
         * @return fzhang 2019.6.9
         */
        public Population breedPopulation(EvolutionState state, Population pop)
        {
            //newpop is the generated offspring population
            Population newpop = null;
            if (clonePipelineAndPopulation) //default value: true
                //create a newpop with two subpopulations, but the individuals are empty.
                newpop = (Population) pop.emptyClone();
            else //skip this part
            {
                if (backupPopulation == null)
                    backupPopulation = (Population) pop.emptyClone();
                newpop = backupPopulation;
                newpop.clear(); //** Sets all Individuals in the Population to null, preparing it to be reused. */
                backupPopulation = pop;  // swap in
            }

            // maybe resize?
            for(int i = 0; i < state.population.subpops.length; i++)
            {
                if (reduceBy[i] > 0) // 0 skip this
                {
                    int prospectiveSize = Math.max(
                            Math.max(pop.subpops[i].individuals.length - reduceBy[i], minimumSize[i]),
                            numElites(state, i));
                    if (prospectiveSize < pop.subpops[i].individuals.length)  // let's resize!
                    {
                        state.output.message("Subpop " + i + " reduced " + pop.subpops[i].individuals.length + " -> " + prospectiveSize);
                        newpop.subpops[i].resize(prospectiveSize);
                    }
                }
            }

           loadElitesFrac(state, pop, newpop);
            // how many threads do we really need?  No more than the maximum number of individuals in any subpopulation
            int numThreads = 0;
            for(int x = 0; x < pop.subpops.length; x++)
                numThreads = Math.max(numThreads, pop.subpops[x].individuals.length); //numThreads = 2
            numThreads = Math.min(numThreads, state.breedthreads);
            //System.out.println(state.breedthreads);  //1  so,numThreads =1

            if (numThreads < state.breedthreads)
                state.output.warnOnce("Largest subpopulation size (" + numThreads +") is smaller than number of breedthreads (" + state.breedthreads + "), so fewer breedthreads will be created.");

            int numinds[][] =
                    new int[numThreads][pop.subpops.length];
            int from[][] =
                    new int[numThreads][pop.subpops.length];

            for(int x=0;x<pop.subpops.length;x++)
            {
                //int length = computeSubpopulationLength(state, newpop, x, 0);
                int length = pop.subpops[x].individuals.length - (numElitesFrac(state, x));

                // we will have some extra individuals.  We distribute these among the early subpopulations
                int individualsPerThread = length / numThreads;  // integer division
                int slop = length - numThreads * individualsPerThread;
                int currentFrom = 0;

                for(int y=0;y<numThreads;y++)
                {
                    if (slop > 0)
                    {
                        numinds[y][x] = individualsPerThread + 1;
                        slop--;
                    }
                    else
                        numinds[y][x] = individualsPerThread;

                    if (numinds[y][x] == 0)
                    {
                        state.output.warnOnce("More threads exist than can be used to breed some subpopulations (first example: subpopulation " + x + ")");
                    }

                    from[y][x] = currentFrom;
                    currentFrom += numinds[y][x];
                }
            }

            if (numThreads==1)
            {
                breedPopChunk(newpop,state,numinds[0],from[0],0);
            }
            else
            {
                // start up the threads
                for(int y=0;y<numThreads;y++)
                {
                    SimpleBreederThread r = new SimpleBreederThread();
                    r.threadnum = y;
                    r.newpop = newpop;
                    r.numinds = numinds[y];
                    r.from = from[y];
                    r.me = this;
                    r.state = state;
                    pool.start(r, "ECJ Breeding Thread " + y );
                }

                pool.joinAll();
            }
            return newpop;
        }


        public int numElitesFrac(EvolutionState state, int subpopulation)
        {
            preGenerations = state.parameters.getIntWithDefault(
                    new Parameter(P_PRE_GENERATIONS), null, -1);  //50
            fracElites = state.parameters.getDoubleWithDefault(
                    new Parameter(P_POP_ADAPT_FRAC_ELITES), null, 0.0); //0.0

            if (state.generation == preGenerations){
                return (int) (state.population.subpops[subpopulation].individuals.length * fracElites);
            }
            else{
                //the number of elites for breeding
                if (elite[subpopulation] != NOT_SET)
                {
                    return elite[subpopulation];
                }
                else if (eliteFrac[subpopulation] == 0)
                {
                    return 0; // no elites
                }
                else if (eliteFrac[subpopulation] != NOT_SET)
                {
                    return (int) Math.max(Math.floor(state.population.subpops[subpopulation].individuals.length * eliteFrac[subpopulation]), 1.0);  // AT LEAST 1 ELITE
                }
                else
                {
                    state.output.warnOnce("Elitism error (SimpleBreeder).  This shouldn't be able to happen.  Please report.");
                    return 0;  // this shouldn't happen
                }
            }
        }


        //fzhang 2019.6.14 load elites individuals into new population
        protected void loadElitesFrac(EvolutionState state, Population pop, Population newpop)
        {
            // are our elites small enough?
            for(int x=0;x<state.population.subpops.length;x++)
            {
                if (numElitesFrac(state, x)> pop.subpops[x].individuals.length)
                    state.output.error("The number of elites for subpopulation " + x + " exceeds the actual size of the subpopulation",
                            new Parameter(EvolutionState.P_BREEDER).push(P_ELITE).push(""+x));
                if (numElitesFrac(state, x)==pop.subpops[x].individuals.length)
                    state.output.warning("The number of elites for subpopulation " + x + " is the actual size of the subpopulation",
                            new Parameter(EvolutionState.P_BREEDER).push(P_ELITE).push(""+x));
            }
            state.output.exitIfErrors();

            //=================find out the index of the best indivudual=======================
            // we assume that we're only grabbing a small number (say <10%), so
            // it's not being done multithreaded
       /*     int[] bestIndex = new int[state.population.subpops.length];
            for (int sub = 0; sub < state.population.subpops.length; sub++) {
                int best = 0;
                Individual[] oldinds = pop.subpops[sub].individuals;
                for (int x = 1; x < oldinds.length; x++) {
                    if (oldinds[x].fitness.betterThan(oldinds[best].fitness)) {
                        best = x;
                    }
                }
                bestIndex[sub] = best;
                //the index of best individuals in each subpopulation are saved in bestIndex[sub], like bestIndex[0] = 0, bestIndex[1]=145
            }*/

            for(int sub=0;sub<state.population.subpops.length;sub++) //0  1
            {
                //System.out.println(!shouldBreedSubpop(state, sub, 0));  //nothing is printed out.
                //skip here
                if (!shouldBreedSubpop(state, sub, 0))  // don't load the elites for this one, we're not doing breeding of it  true
                {
                    continue;
                }

                //System.out.println("numElites(state, sub)  "+numElites(state, sub) );  //always eauql two, seems like no related to eval.num-elites and breed.elite.0
                // if the number of elites is 1, then we handle this by just finding the best one.
                if (numElitesFrac(state, sub) == 1) {
                    System.out.println("The number of elites is not fracElites!!!");
                    /*Individual[] oldinds = pop.subpops[sub].individuals; // sub = 0,   1
                    Individual[] inds = newpop.subpops[sub].individuals; // null
                    if (state.population.subpops.length > 1) {
                        int otherSubPop = (sub+1)%2;  // otherSubPop = 1,  0
                        Individual[] oldindsOtherSubpop = pop.subpops[otherSubPop].individuals;
                        //want to also insert context of best individual   save the best
                        //Auxiliary variable, used by coevolutionary processes, to store the individuals
                        //involved in producing this given Fitness value.
                        Individual otherCollab = (Individual) oldindsOtherSubpop[bestIndex[otherSubPop]].fitness.getContext()[sub].clone();
                        inds[inds.length-2] = otherCollab;
                    }
                    Individual elite = (Individual)(oldinds[bestIndex[sub]].clone());
                    inds[inds.length-1] = elite;*/
                }
                else if (numElitesFrac(state, sub)>0)  // we'll need to sort
                {
                    //define int[] orderPop, length = 512 and its elements are from 0 to 511
//                    int[] orderedPop = new int[pop.subpops[sub].individuals.length];
//                    for(int x=0;x<pop.subpops[sub].individuals.length;x++)
//                        orderedPop[x] = x;
                    //orderPop[0]= 0, orderPop[1]= 1, orderPop[2]= 2....orderPop[511]= 511

                    // sort the best so far where "<" means "not as fit as"
                    //QuickSort.qsort(orderedPop, new EliteComparator(pop.subpops[sub].individuals));
                    // load the top N individuals

                    Individual[] inds = newpop.subpops[sub].individuals; // has not value
                    Individual[] oldinds = pop.subpops[sub].individuals; //has values
                        for(int x=inds.length-numElitesFrac(state, sub);x<inds.length;x++)//start from 510, because numElites(state,sub)
                            inds[x] = (Individual)(oldinds[x-inds.length+numElitesFrac(state, sub)].clone());
                    }
            }

            // optionally force reevaluation
            unmarkElitesEvaluated(state, newpop);
        }
    }


/** A private helper class for implementing multithreaded breeding */
class SimpleBreederThread implements Runnable
    {
    Population newpop;
    public int[] numinds;
    public int[] from;
    public SimpleBreeder me;
    public EvolutionState state;
    public int threadnum;
    public void run()
        {
        me.breedPopChunk(newpop,state,numinds,from,threadnum);
        }
    }
