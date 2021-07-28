//package ec.multiobjective.MOEAD;
//
//import java.util.Collections;
//import java.util.LinkedList;
//import java.util.Queue;
//
//
//import ec.EvolutionState;
//import ec.Individual;
//import ec.gp.GPDefaults;
//import ec.gp.GPInitializer;
//import ec.multiobjective.MultiObjectiveFitness;
//import ec.util.Parameter;
//
//public class MOEADInitializer extends GPInitializer {
//
//	// settings for MOEAD
//	public double idealPoint[];
//	public double[][] weights;
//	public int[][] neighbourhood;
//	public static boolean tchebycheff;
//	public static int numObjectives;
//	public static int popSize;
//	public static int numNeighbours;
//	public static int numLocalSearchTries;
//	public static int localSearchBound;
//
//	public void setup(final EvolutionState state, final Parameter base) {
//		super.setup(state, base);
//
//		Parameter tchebycheffParam = new Parameter("tchebycheff");
//		Parameter numObjectivesParam = new Parameter("eval.problem.eval-model.objectives");
//		Parameter popSizeParam = new Parameter("pop.subpop.0.size");
//		Parameter numNeighboursParam = new Parameter("numNeighbours");
//		Parameter numLocalSearchTriesParam = new Parameter("numLocalSearchTries");
//		Parameter localSearchBoundParam = new Parameter("localSearchBound");
//
//		// Initializations for MOEAD settings
//		tchebycheff = state.parameters.getBoolean(tchebycheffParam, null, false);
//		numObjectives = state.parameters.getInt(numObjectivesParam, null);
//		popSize = state.parameters.getInt(popSizeParam, null);
//		numNeighbours = state.parameters.getInt(numNeighboursParam, null);
//		numLocalSearchTries = state.parameters.getInt(numLocalSearchTriesParam, null);
//		localSearchBound = state.parameters.getInt(localSearchBoundParam, null);
//
//		// Initialise the reference point
//		if (tchebycheff)
//			initIdealPoint();
//		// Create a set of uniformly spread weight vectors
//		weights = new double[popSize][numObjectives];
//		initWeights_new();
//		// Identify the neighboring weights for each vector
//		neighbourhood = new int[popSize][numNeighbours];
//		identifyNeighbourWeights();
//	}
//
//	public double calculateTchebycheffScore(Individual ind, int problemIndex) {
//		double[] problemWeights = weights[problemIndex];
//		double max_fun = -1 * Double.MAX_VALUE;
//
//		MultiObjectiveFitness fit = (MultiObjectiveFitness) ind.fitness;
//
//		for (int i = 0; i < numObjectives; i++) {
//			double diff = abs(fit.getObjectives()[i] - idealPoint[i]);
//			double feval;
//			if (problemWeights[i] == 0)
//				feval = 0.00001 * diff;
//			else
//				feval = problemWeights[i] * diff;
//			if (feval > max_fun)
//				max_fun = feval;
//		}
//		return max_fun;
//	}
//
//	/**
//	 * Calculates the problem score for a given individual, using a given set of
//	 * weights.
//	 *
//	 * @param ind
//	 * @param problemIndex
//	 *            - for retrieving weights
//	 * @return score
//	 */
//	public double calculateScore(Individual ind, int problemIndex) {
//		double[] problemWeights = weights[problemIndex];
//		MultiObjectiveFitness fit = (MultiObjectiveFitness) ind.fitness;
//
//		double sum = 0;
//		for (int i = 0; i < numObjectives; i++)
//			sum += (problemWeights[i]) * fit.getObjectives()[i];
//		return sum;
//	}
//
//	/**
//	 * Create a neighborhood for each weight vector, based on the Euclidean distance
//	 * between each two vectors.
//	 */
//	private void identifyNeighbourWeights() {
//		// Calculate distance between vectors
//		double[][] distanceMatrix = new double[popSize][popSize];
//
//		for (int i = 0; i < popSize; i++) {
//			for (int j = 0; j < popSize; j++) {
//				if (i != j)
//					distanceMatrix[i][j] = calculateDistance(weights[i], weights[j]);
//			}
//		}
//
//		// Use this information to build the neighborhood
//		for (int i = 0; i < popSize; i++) {
//			int[] neighbours = identifyNearestNeighbours(distanceMatrix[i], i);
//			neighbourhood[i] = neighbours;
//		}
//	}
//
//	/**
//	 * Returns the indices for the nearest neighbors, according to their distance
//	 * from the current vector.
//	 *
//	 * @param distances
//	 *            - a list of distances from the other vectors
//	 * @param currentIndex
//	 *            - the index of the current vector
//	 * @return indices of nearest neighbors
//	 */
//	private int[] identifyNearestNeighbours(double[] distances, int currentIndex) {
//		Queue<IndexDistancePair> indexDistancePairs = new LinkedList<IndexDistancePair>();
//
//		// Convert the vector of distances to a list of index-distance pairs.
//		for (int i = 0; i < distances.length; i++) {
//			indexDistancePairs.add(new IndexDistancePair(i, distances[i]));
//		}
//		// Sort the pairs according to the distance, from lowest to highest.
//		Collections.sort((LinkedList<IndexDistancePair>) indexDistancePairs);
//
//		// Get the indices for the required number of neighbours
//		int[] neighbours = new int[numNeighbours];
//
//		// Get the neighbors, including the vector itself
//		IndexDistancePair neighbourCandidate;
//		for (int i = 0; i < numNeighbours; i++) {
//			neighbourCandidate = indexDistancePairs.poll();
//			// Uncomment this if you want to exclude the vector itself from being considered
//			// as part of the neighbourhood
//			// while (neighbourCandidate.getIndex() == currentIndex)
//			// neighbourCandidate = indexDistancePairs.poll();
//			neighbours[i] = neighbourCandidate.getIndex();
//		}
//		return neighbours;
//	}
//
//	/**
//	 * Calculates the Euclidean distance between two weight vectors.
//	 *
//	 * @param vector1
//	 * @param vector2
//	 * @return distance
//	 */
//	private double calculateDistance(double[] vector1, double[] vector2) {
//		double sum = 0;
//		for (int i = 0; i < vector1.length; i++) {
//			sum += Math.pow((vector1[i] - vector2[i]), 2);
//		}
//		return Math.sqrt(sum);
//	}
//
//	/**
//	 * Initialize the ideal point used for the Tchebycheff calculation.
//	 */
//	private void initIdealPoint() {
//		idealPoint = new double[numObjectives];
//		for (int i = 0; i < numObjectives; i++) {
//			idealPoint[i] = 0.0;
//		}
//	}
//
//	/**
//	 * Initialize uniformely spread weight vectors. This code come from the authors'
//	 * original code base.
//	 */
//	private void initWeights_new() {
//
//		for (int i = 1; i <= popSize; i++) {
//			if (numObjectives == 2) {
//				double[] weightVector = new double[2];
//				weightVector[0] = (i - 1) / (double) (popSize - 1);
//				weightVector[1] = (popSize - i) / (double) (popSize - 1);
//				weights[i - 1] = weightVector;
//			} else {
//				throw new RuntimeException("Unsupported number of objectives. Should be 2 or 3.");
//			}
//		}
//	}
//
//	/**
//	 * Initialize uniformely spread weight vectors. This code come from the authors'
//	 * original code base.
//	 */
//	private void initWeights() {
//
//		double interval = (double) popSize / ((double) popSize - 1);
//		for (int i = 0; i < popSize; i++) {
//			if (numObjectives == 2) {
//				double[] weightVector = new double[2];
//				weightVector[0] = (i * interval) / (double) popSize;
//				weightVector[1] = (popSize - (i * interval)) / (double) popSize;
//				weights[i] = weightVector;
//			} else if (numObjectives == 3) {
//				for (int j = 0; j < popSize; j++) {
//					if (i + j < popSize) {
//						int k = popSize - i - j;
//						double[] weightVector = new double[3];
//						weightVector[0] = i / (double) popSize;
//						weightVector[1] = j / (double) popSize;
//						weightVector[2] = k / (double) popSize;
//						weights[i] = weightVector;
//					}
//				}
//			} else {
//				throw new RuntimeException("Unsupported number of objectives. Should be 2 or 3.");
//			}
//		}
//	}
//}
