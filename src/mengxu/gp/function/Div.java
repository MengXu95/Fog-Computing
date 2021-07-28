/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package mengxu.gp.function;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import mengxu.gp.data.DoubleData;

/**
 *
 * Functional GPNode: Div (protected). When the demoninator is zero, it returns 1.
 *
 * @author yimei
 *
 */

public class Div extends GPNode {

    public String toString() {
		return "/";
	}

/*
  public void checkConstraints(final EvolutionState state,
  final int tree,
  final GPIndividual typicalIndividual,
  final Parameter individualBase)
  {
  super.checkConstraints(state,tree,typicalIndividual,individualBase);
  if (children.length!=2)
  state.output.error("Incorrect number of children for node " +
  toStringForError() + " at " +
  individualBase);
  }
*/
    public int expectedChildren() {
    	return 2;
    }

    public void eval(final EvolutionState state,
    		final int thread,
    		final GPData input,
    		final ADFStack stack,
    		final GPIndividual individual,
    		final Problem problem) {

        double result;
        DoubleData rd = ((DoubleData)(input));

        children[0].eval(state,thread,input,stack,individual,problem);
        result = rd.value;

        children[1].eval(state,thread,input,stack,individual,problem);

        //mofified by fzhang  22.5.2018   in java, we code can automatically control this, so we do not need to do extra action.
        // when the division is 0, the value will be infinity
        if (Double.compare(rd.value, 0) == 0) {
            rd.value = 1;
            //rd.value = Double.POSITIVE_INFINITY;
        	//rd.value = Double.NEGATIVE_INFINITY;
        	//rd.value = Double.MAX_VALUE; // no big difference with Double.POSITIVE_INFINITY
        	//rd.value = 0;
        }
        else {
            rd.value = result / rd.value;
        }

       //value will be infinity fzhang 22.9.2018
//       rd.value = result / rd.value;

        //rd.value = result / Math.sqrt(1 + rd.value*rd.value);
    }
}

