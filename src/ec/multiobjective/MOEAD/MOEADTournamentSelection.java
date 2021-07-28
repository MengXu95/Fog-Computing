//package ec.multiobjective.MOEAD;
//
//import ec.EvolutionState;
//import ec.Individual;
//import ec.select.TournamentSelection;
//
//public class MOEADTournamentSelection extends TournamentSelection {
//
//	private static final long serialVersionUID = 1L;
//
//	@Override
//	public int produce(final int min, final int max, final int start, final int subpopulation, final Individual[] inds,
//			final EvolutionState state, final int thread) {
//		int n = 1;
//
//		inds[start] = state.population.subpops[subpopulation].individuals[produceMOEAD(start, subpopulation, state,
//				thread)];
//		return n;
//	}
//
//	public int produceMOEAD(final int start, final int subpopulation, final EvolutionState state, final int thread) {
//		Individual[] oldinds = state.population.subpops[subpopulation].individuals;
//		int best = getRandomIndividual(start, subpopulation, state, thread);
//
//		int s = getTournamentSizeToUse(state.random[thread]);
//
//		if (pickWorst)
//			for (int x = 1; x < s; x++) {
//				int j = getRandomIndividual(start, subpopulation, state, thread);
//				if (!betterThan(start, oldinds[j], oldinds[best], subpopulation, state, thread)) // j is at least as bad
//																									// as best
//					best = j;
//			}
//		else
//			for (int x = 1; x < s; x++) {
//				int j = getRandomIndividual(start, subpopulation, state, thread);
//				if (betterThan(start, oldinds[j], oldinds[best], subpopulation, state, thread)) // j is better than best
//					best = j;
//			}
//
//		return best;
//	}
//
//	public int getRandomIndividual(int subproblem, int subpopulation, EvolutionState state, int thread) {
//		MOEADInitializer init = (MOEADInitializer) state.initializer;
//		int neighbourIndex = init.random.nextInt(MOEADInitializer.numNeighbours);
//		int populationIndex = init.neighbourhood[subproblem][neighbourIndex];
//		return populationIndex;
//	}
//
//	// public int getRandomIndividual(int number, int subpopulation, EvolutionState
//	// state, int thread)
//	// {
//	// Individual[] oldinds = state.population.subpops[subpopulation].individuals;
//	// return state.random[thread].nextInt(oldinds.length);
//	// }
//
//	/**
//	 * Returns true if *first* is a better (fitter, whatever) individual than
//	 * *second*.
//	 */
//	public boolean betterThan(int subproblem, Individual first, Individual second, int subpopulation,
//			EvolutionState state, int thread) {
//		int index = subproblem;
//
//		MOEADInitializer init = (MOEADInitializer) state.initializer;
//		double firstScore;
//		double secondScore;
//
//		if (MOEADInitializer.tchebycheff) {
//			firstScore = init.calculateTchebycheffScore(first, index);
//			secondScore = init.calculateTchebycheffScore(second, index);
//		} else {
//			firstScore = init.calculateScore(first, index);
//			secondScore = init.calculateScore(second, index);
//		}
//		boolean betterThan = firstScore < secondScore;
//		return betterThan;
//	}
//
//	// public boolean betterThan(int subproblem, Individual first, Individual
//	// second, int subpopulation, EvolutionState state, int thread)
//	// {
//	// return first.fitness.betterThan(second.fitness);
//	// }
//}