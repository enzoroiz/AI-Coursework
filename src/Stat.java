import java.util.ArrayList;

/**
 * Class that computes statistics
 * 
 * @author enzoroiz
 *
 */
public class Stat {
	/**
	 * instance fields
	 */
	private ArrayList<Double> signalNormalized;
	private double mean;
	private double log10;
	private double variance;

	/**
	 * Constructor given
	 * 
	 * @param signalNormalized
	 *            array list of double
	 */
	@SuppressWarnings("unchecked")
	public Stat(ArrayList<Double> signalNormalized) {
		this.signalNormalized = (ArrayList<Double>) signalNormalized.clone();
		this.mean = mean();
		this.log10 = log(mean);
	}

	/**
	 * @return the mean of the numbers in the array list
	 */
	private double mean() {
		double sum = 0;
		for (int i = 0; i < signalNormalized.size(); i++) {
			sum += signalNormalized.get(i);
		}

		return Math.round(sum / signalNormalized.size() * 10000.0) / 10000.0;
	}

	/**
	 * 
	 * @param mean
	 * @return the of the mean
	 */
	private double log(double mean) {
		return Math.round(Math.log10(mean) * 10000.0) / 10000.0;
	}

	/**
	 * @return the variance of the numbers in the array list given the mean
	 */
	public double getVariance() {
		double sum = 0;
		for (int i = 0; i < signalNormalized.size(); i++) {
			sum += Math.pow(signalNormalized.get(i) - mean, 2.0);
		}

		return Math.round(sum / signalNormalized.size() * 10000.0) / 10000.0;
	}

	/**
	 * @param parameter
	 *            to be analysed
	 * @return the normal or the Gaussian distribution given the mean and the
	 *         variance
	 */
	public double getNormalDistribution(double parameter) {
		this.variance = getVariance();
		double firstPart = (1 / Math.sqrt(2 * Math.PI * variance));
		double secondPart = -Math.pow(parameter - mean, 2) / (2 * variance);

		return firstPart * Math.pow(Math.E, secondPart);
	}
	
	/**
	 * @param parameter
	 *            to be analysed
	 * @param mean
	 * @param variance
	 * @return the normal or the Gaussian distribution given the mean and the
	 *         variance
	 */
	public static double getNormalDistribution(double parameter, double mean, double variance) {
		double firstPart = (1 / Math.sqrt(2 * Math.PI * variance));
		double secondPart = -Math.pow(parameter - mean, 2) / (2 * variance);

		return firstPart * Math.pow(Math.E, secondPart);
	}

	/**
	 * @return the mean
	 */
	public double getMean() {
		return this.mean;
	}

	/**
	 * @return the log in the basis of ten
	 */
	public double getLog() {
		return this.log10;
	}

	/**
	 * @param number
	 *            to round
	 * @return a number rounded up to 4 decimal places
	 */
	public static double round(double number) {
		return Math.round(number * 100000.0) / 100000.0;
	}

	@Override
	public String toString() {
		return ("Mean: " + mean + " Log 10: " + log10 + " Variance " + getVariance());
	}
}
