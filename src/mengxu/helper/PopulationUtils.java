package mengxu.helper;

import ec.Individual;
import ec.Population;
import ec.Subpopulation;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

import java.io.*;
import java.util.*;

public class PopulationUtils
{
	/**
	 * Sorts individuals based on their fitness. The method iterates over all subpopulations
	 * and sorts the array of individuals in them based on their fitness so that the first
	 * individual has the best (i.e. least) fitness.
	 *
	 * @param pop a population to sort. Can't be {@code null}.
	 * @return the given pop with individuals sorted.
	 */
	public static Population sort(Population pop)
	{
		Comparator<Individual> comp = (Individual o1, Individual o2) ->
		{
			if(o1.fitness.fitness() < o2.fitness.fitness())
				return -1;
			if(o1.fitness.fitness() == o2.fitness.fitness())
				return 0;

			return 1;
		};

		for(Subpopulation subpop : pop.subpops)
			Arrays.sort(subpop.individuals, comp);

		return pop;
	}


	//2020.2.10 sort the individuals in the arraylist
	public static Individual[] sortInds(ArrayList<Individual> individuals) {
		Comparator<Individual> comp = (Individual o1, Individual o2) ->
		{
			if (o1.fitness.fitness() < o2.fitness.fitness())
				return -1;
			if (o1.fitness.fitness() == o2.fitness.fitness())
				return 0;

			return 1;
		};
		//convert arraylist to array
		Individual[] inds = individuals.toArray(new Individual[individuals.size()]);
		Arrays.sort(inds, comp);
		return inds;
	}

	//2021.5.8 sort the individuals in the list
	public static Individual[] sortInds(List<Individual> individuals) {
		Comparator<Individual> comp = (Individual o1, Individual o2) ->
		{
			if (o1.fitness.fitness() < o2.fitness.fitness())
				return -1;
			if (o1.fitness.fitness() == o2.fitness.fitness())
				return 0;

			return 1;
		};
		//convert arraylist to array
		Individual[] inds = individuals.toArray(new Individual[individuals.size()]);
		Arrays.sort(inds, comp);
		return inds;
	}

//	static void Frequency(TerminalsStats stats, GPNode node) {
//		if (node == null) {
//			return;
//		}
//
//		if (node.children == null || node.children.length == 0) {  //1. a node does not have child is a terminal
//			                                                       //2. the length of node's child is 0 (empty array)---it is a terminal
//			stats.update(((TerminalERCUniform)node).getTerminal().name()); //read terminals
//			return;
//		}
//
//		for(GPNode child : node.children) //repeat to check the terminals
//			Frequency(stats, child);
//	}

//	public static ArrayList<HashMap<String,Integer>> Frequency(Population pop, int topInds) {
//		sort(pop); //sort the subpop separately
//
//		ArrayList<HashMap<String,Integer>> retval = new ArrayList<HashMap<String,Integer>>();
//		for(Subpopulation subpop : pop.subpops)
//			for (int i = 0; i < topInds; i++) {
//				TerminalsStats stats = new TerminalsStats();
//				Frequency(stats, ((GPIndividual)(subpop.individuals[i])).trees[0].child);
//				retval.add(stats.getStats());
//		}
//		return retval;
//	}

	public static void sort(Individual[] ind)
	{
		Comparator<Individual> comp = (Individual o1, Individual o2) ->
		{
			if(o1.fitness.fitness() < o2.fitness.fitness())
				return -1;
			if(o1.fitness.fitness() == o2.fitness.fitness())
				return 0;

			return 1;
		};

		Arrays.sort(ind, comp);
	}




	public static void savePopulation(Population pop, String fileName)
			throws FileNotFoundException, IOException
	{
		File file = new File(fileName);
		if(file.exists())
			file.delete();
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file)))
		{
			int nSubPops = pop.subpops.length;
			oos.writeInt(nSubPops);
			for(Subpopulation subpop : pop.subpops)
			{
				int nInds = subpop.individuals.length;
				oos.writeInt(nInds);
				for(Individual ind : subpop.individuals)
				{
					oos.writeObject(ind);
				}
			}
		}
	}

	public static Population loadPopulation(File file)
			throws FileNotFoundException, IOException, ClassNotFoundException, InvalidObjectException
	{
		Population retval = new Population();
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
		{
			int numSub = ois.readInt();
			retval.subpops = new Subpopulation[numSub];
			for(int subInd = 0; subInd < numSub; subInd++)
			{
				int numInd = ois.readInt();
				retval.subpops[subInd] = new Subpopulation();
				retval.subpops[subInd].individuals = new Individual[numInd];
				for(int indIndex = 0; indIndex < numInd; indIndex++)
				{
					Object ind = ois.readObject();
					if(!(ind instanceof Individual))
						throw new InvalidObjectException("The file contains an object that is not "
								+ "instance of Individual: " + ind.getClass().toString());
					retval.subpops[subInd].individuals[indIndex] = (Individual)ind;
				}
			}
		}

		return retval;
	}

	public static Population loadPopulation(String fileName)
			throws FileNotFoundException, IOException, ClassNotFoundException, InvalidObjectException
	{
		File file = new File(fileName);
		return loadPopulation(file);
	}


	//fzhang 2019.6.6 get the index of best individuals
	public static int  getIndexOfbestInds(Population pop, int numSubPop){
		{
			int best_index = 0;
			double best_fitness = pop.subpops[numSubPop].individuals[0].fitness.fitness();
			for(int ind = 0; ind < pop.subpops[numSubPop].individuals.length; ind++){
				if(pop.subpops[numSubPop].individuals[ind].fitness.fitness() < best_fitness){
					best_fitness = pop.subpops[numSubPop].individuals[ind].fitness.fitness();
					best_index = ind;
				}
			}
            return best_index;
		}
	}

}


class TerminalsStats {
	private HashMap<String, Integer> stats = new HashMap<>();

	public void update(String nodeName) {
		if (stats.containsKey(nodeName) == false) {
			stats.put(nodeName, 0); // put: set the value
		}

		stats.put(nodeName, stats.get(nodeName) + 1);
	}

	public HashMap<String, Integer> getStats() {
		return stats;
	}
}


