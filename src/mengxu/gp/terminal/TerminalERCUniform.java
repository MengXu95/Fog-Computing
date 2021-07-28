package mengxu.gp.terminal;

import ec.EvolutionState;
import ec.gp.ERC;
import ec.gp.GPFunctionSet;
import ec.util.Parameter;
import mengxu.gp.GPRuleEvolutionState;

/**
 * The terminal ERC, with uniform selection.
 *
 * @author yimei
 */

public class TerminalERCUniform extends TerminalERC {

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        //Assume here we are dealing with simple gp
        int subPopNum = 0;
        if (base.toString().endsWith("1")) {
            subPopNum = 1;
        }

        terminal = ((GPRuleEvolutionState)state).pickTerminalRandom(subPopNum);
    }

    @Override
    public void resetNode(EvolutionState state, int thread) {
        //Assume here we are dealing with simple gp
        int subPopNum = 0;
        terminal = ((GPRuleEvolutionState)state).pickTerminalRandom(subPopNum);

        if (terminal instanceof ERC) {
            ERC ercTerminal = new DoubleERC();
            ercTerminal.resetNode(state, thread);
            terminal = ercTerminal;
        }
    }

    //============================use this one=================================
    //mutateERC will call this method
    @Override
    public void resetNode(EvolutionState state, int thread, GPFunctionSet set) {
        //Assume here we are dealing with simple gp
        int subPopNum = 0;
        if (set.toString().endsWith("1")) {
            subPopNum = 1;
        }

        //fzhang random pick a terminal---original
        terminal = ((GPRuleEvolutionState)state).pickTerminalRandom(subPopNum);

        //fzhang 2019.5.27 another terminal with different parameters
        //terminal = ((GPRuleEvolutionState)state).pickTerminalRandom(state, subPopNum);

        if (terminal instanceof ERC) {
            ERC ercTerminal = new DoubleERC();
            ercTerminal.resetNode(state, thread, set);
            terminal = ercTerminal;
        }
    }

    @Override
    public void mutateERC(EvolutionState state, int thread, GPFunctionSet set) {
        resetNode(state, thread, set);
    }
}
