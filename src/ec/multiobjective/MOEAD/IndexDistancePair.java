package ec.multiobjective.MOEAD;

public class IndexDistancePair implements Comparable<IndexDistancePair>{
	private int index;
	private double distance;

	public IndexDistancePair(int i, double d) {
		index = i;
		distance = d;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public int compareTo(IndexDistancePair o) {
		if (distance < o.distance)
			return -1;
		else if (distance > o.distance)
			return 1;
		else
			return 0;
	}

	public int getIndex() {
		return index;
	}

	public double getDistance() {
		return distance;
	}

}
