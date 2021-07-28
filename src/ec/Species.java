/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec;
import ec.gp.GPIndividual;
import ec.util.*;
import java.io.*;

/*
 * Species.java
 *
 * Created: Tue Aug 10 20:31:50 1999
 * By: Sean Luke
 */

/**
 * Species is a prototype which defines the features for a set of individuals
 * in the population.  Typically, individuals may breed if they belong to the
 * same species (but it's not a hard-and-fast rule).  Each Subpopulation has
 * one Species object which defines the species for individuals in that
 * Subpopulation.
 *
 * <p>Species are generally responsible for creating individuals, through
 * their newIndividual(...) method.  This method usually clones its prototypical
 * individual and makes some additional modifications to the clone, then returns it.
 * Note that the prototypical individual does <b>not need to be a complete individual</b> --
 * for example, GPSpecies holds a GPIndividual which doesn't have any trees (the tree
 * roots are null).
 *
 * <p>Species also holds a prototypical breeding pipeline meant to breed
 * this individual.  To breed individuals of this species, clone the pipeline
 * and use the clone.

 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>ind</tt><br>
 <font size=-1>classname, inherits and != ec.Individual</font></td>
 <td valign=top>(the class for the prototypical individual for the species)</td></tr>

 <tr><td valign=top><i>base</i>.<tt>fitness</tt><br>
 <font size=-1>classname, inherits and != ec.Fitness</font></td>
 <td valign=top>(the class for the prototypical fitness for the species)</td></tr>

 <tr><td valign=top><i>base</i>.<tt>numpipes</tt><br>
 <font size=-1>int &gt;= 1</font></td>
 <td valign=top>(total number of breeding pipelines for the species)</td></tr>

 <tr><td valign=top><i>base</i>.<tt>pipe</tt><br>
 <font size=-1>classname, inherits and != ec.BreedingPipeline</font></td>
 <td valign=top>(the class for the prototypical Breeding Pipeline)</td></tr>

 </table>


 <p><b>Parameter bases</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>ind</tt></td>
 <td>i_prototype (the prototypical individual)</td></tr>

 <tr><td valign=top><i>base</i>.<tt>pipe</tt></td>
 <td>pipe_prototype (breeding pipeline prototype)</td></tr>

 <tr><td valign=top><i>base</i>.<tt>fitness</tt></td>
 <td>f_prototype (the prototypical fitness)</td></tr>

 </table>



 * @author Sean Luke
 * @version 1.0
 */

public abstract class Species implements Prototype
    {
    public static final String P_INDIVIDUAL = "ind";
    public static final String P_PIPE = "pipe";
    public static final String P_FITNESS = "fitness";

    /** The prototypical individual for this species. */
    public Individual i_prototype;

    /** The prototypical breeding pipeline for this species. */
    public BreedingPipeline pipe_prototype;

    /** The prototypical fitness for individuals of this species. */
    public Fitness f_prototype;

    public Object clone()
        {
        try
            {
            Species myobj = (Species) (super.clone());
            myobj.i_prototype = (Individual) i_prototype.clone();
            myobj.f_prototype = (Fitness) f_prototype.clone();
            myobj.pipe_prototype = (BreedingPipeline) pipe_prototype.clone();
            return myobj;
            }
        catch (CloneNotSupportedException e)
            { throw new InternalError(); } // never happens
        }


    /** Provides a brand-new individual to fill in a population.  The default form
        simply calls clone(), creates a fitness, sets evaluated to false, and sets
        the species.  If you need to make a more custom genotype (as is the case
        for GPSpecies, which requires a light rather than deep clone),
        you will need to override this method as you see fit.
    */

    public Individual newIndividual(final EvolutionState state, int thread)
        {
        Individual newind = (Individual)(i_prototype.clone());

        // Set the fitness
        newind.fitness = (Fitness)(f_prototype.clone());
        newind.evaluated = false;

        // Set the species to me
        newind.species = this;

        // ...and we're ready!
        return newind;
        }

    /**
       Provides an individual read from a stream, including
       the fitness; the individual will
       appear as it was written by printIndividual(...).  Doesn't
       close the stream.  Sets evaluated to false and sets the species.
       If you need to make a more custom mechanism (as is the case
       for GPSpecies, which requires a light rather than deep clone),
       you will need to override this method as you see fit.
    */

    public Individual newIndividual(final EvolutionState state,
        final LineNumberReader reader)
        throws IOException
        {
        Individual newind = (Individual)(i_prototype.clone());

        // Set the fitness
        newind.fitness = (Fitness)(f_prototype.clone());
        newind.evaluated = false; // for sanity's sake, though it's a useless line

        // load that sucker
        newind.readIndividual(state,reader);

        // Set the species to me
        newind.species = this;

        // and we're ready!
        return newind;
        }

    /**
       Provides an individual read from a DataInput source, including
       the fitness.  Doesn't
       close the DataInput.  Sets evaluated to false and sets the species.
       If you need to make a more custom mechanism (as is the case
       for GPSpecies, which requires a light rather than deep clone),
       you will need to override this method as you see fit.
    */

    public Individual newIndividual(final EvolutionState state,
        final DataInput dataInput)
        throws IOException
        {
        Individual newind = (Individual)(i_prototype.clone());

        // Set the fitness
        newind.fitness = (Fitness)(f_prototype.clone());
        newind.evaluated = false; // for sanity's sake, though it's a useless line

        // Set the species to me
        newind.species = this;

        // load that sucker
        newind.readIndividual(state,dataInput);

        // and we're ready!
        return newind;
        }

//        public Individual[] newTwoIndividuals(EvolutionState state, int thread)
//        {
//            GPIndividual[] newinds = new GPIndividual[2];
//
//            //original individual
//            GPIndividual newind0 = ((GPIndividual)(i_prototype)).lightClone();
//
//            // Initialize the trees
//            for (int x=0;x<newind0.trees.length;x++)
//                newind0.trees[x].buildTree(state, thread); //fzhang 2019.1.22 build tree use function and terminals
//
//            // Set the fitness
//            newind0.fitness = (Fitness)(f_prototype.clone());
//            newind0.evaluated = false;
//
//            // Set the species to me
//            newind0.species = this;
//
//            //OBL individual
//            GPIndividual newind1 = ((GPIndividual)(i_prototype)).lightClone();
//
//            // Initialize the trees
//            for (int x=0;x<newind1.trees.length;x++)
//                newind1.trees[x].OBLTree(newind0.trees[x],state, thread); //fzhang 2019.1.22 build tree use function and terminals
//
//            // Set the fitness
//            newind1.fitness = (Fitness)(f_prototype.clone());
//            newind1.evaluated = false;
//
//            // Set the species to me
//            newind1.species = this;
//
//            newinds[0] = newind0;
//            newinds[1] = newind1;
//
//            // ...and we're ready!
//            return newinds;
//        }
//
//
//        //modified by mengxu 2021.03.24
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


    /** The default version of setup(...) loads requested pipelines and calls setup(...) on them and normalizes their probabilities.
        If your individual prototype might need to know special things about the species (like parameters stored in it),
        then when you override this setup method, you'll need to set those parameters BEFORE you call super.setup(...),
        because the setup(...) code in Species sets up the prototype.
        @see Prototype#setup(EvolutionState,Parameter)
    */

    public void setup(final EvolutionState state, final Parameter base)
        {
        Parameter def = defaultBase(); //Returns the default base for this prototype.
        //System.out.println(def);  //gp.species      gp.species

        // load the breeding pipeline, pop.subpop.0.species.pipe = ec.breed.MultiBreedingPipeline
        pipe_prototype = (BreedingPipeline)(
            state.parameters.getInstanceForParameter(
                base.push(P_PIPE),def.push(P_PIPE),BreedingPipeline.class));
        //System.out.println(pipe_prototype);  //ec.breed.MultiBreedingPipeline@1ddc4ec2     ec.breed.MultiBreedingPipeline@133314b
        pipe_prototype.setup(state,base.push(P_PIPE));

        // I promised over in BreedingSource.java that this method would get called.
        state.output.exitIfErrors();

        // load our individual prototype
        i_prototype = (Individual)(state.parameters.getInstanceForParameter(
                base.push(P_INDIVIDUAL),def.push(P_INDIVIDUAL),
                Individual. class));
        //System.out.println(i_prototype);  //ec.gp.GPIndividual@500977346{0}     ec.gp.GPIndividual@20132171{0}

        // set the species to me before setting up the individual, so they know who I am
        i_prototype.species = this;
        i_prototype.setup(state,base.push(P_INDIVIDUAL));
        /*This should be used to set up only those things which you share in common
        with all other individuals in your species; individual-specific items
        which make you <i>you</i> should be filled in by Species.newIndividual(...),
        and modified by breeders. */

        // load our fitness
        f_prototype = (Fitness) state.parameters.getInstanceForParameter(
            base.push(P_FITNESS),def.push(P_FITNESS),
            Fitness.class);
        //System.out.println(f_prototype);
        //ec.multiobjective.MultiObjectiveFitness@1ddc4ec2
        //ec.multiobjective.MultiObjectiveFitness@133314b

        f_prototype.setup(state,base.push(P_FITNESS));  // by default does nothing
        }
    }


