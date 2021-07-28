package ec.multiobjective.MOEAD;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.multiobjective.nsga2.NSGA2Breeder;

public class MOEADBreeder extends NSGA2Breeder {

	private static final long serialVersionUID = 1L;

	public Population breedPopulation(EvolutionState state) {

		Population oldPop = (Population) state.population;
		Population newPop = (Population) state.population.emptyClone();

		Individual[] oldInds = oldPop.subpops[0].individuals;
		Individual[] newInds = new Individual[oldPop.subpops[0].individuals.length];
		newPop.subpops[0].individuals = newInds;

		// do regular breeding of this subpopulation
		BreedingPipeline bp = (BreedingPipeline) newPop.subpops[0].species.pipe_prototype;

		// Pass the probIndex as the starting point for each pipeline invocation
		for (int probIndex = 0; probIndex < state.population.subpops[0].individuals.length; probIndex++) {
			newInds[probIndex] = (Individual) oldInds[probIndex].clone();
			bp.produce(1, 1, probIndex, 0, newInds, state, 0);
		}

		Individual[] combinedInds = new Individual[oldPop.subpops[0].individuals.length
				+ newPop.subpops[0].individuals.length];
		System.arraycopy(newPop.subpops[0].individuals, 0, combinedInds, 0, newPop.subpops[0].individuals.length);
		System.arraycopy(oldPop.subpops[0].individuals, 0, combinedInds, newPop.subpops[0].individuals.length,
				oldPop.subpops[0].individuals.length);
		newPop.subpops[0].individuals = combinedInds;

		return newPop;
	}
}
