import java.util.ArrayList;

/**
 * Class used to process signals and extract its informations
 * 
 * @author enzoroiz
 *
 */
@SuppressWarnings("unchecked")
public class SignalProcessing {
	// Attributes
	private ArrayList<Double> sample;
	private int audioSizeInMS;

	/**
	 * @param sample
	 *            to analyse
	 * @param audioSizeInMS
	 */
	public SignalProcessing(ArrayList<Double> sample, int audioSizeInMS) {
		this.sample = sample;
		this.audioSizeInMS = audioSizeInMS;
	}

	/**
	 * @return the original signal normalized with the peak
	 */
	public ArrayList<Double> originalNormalized() {
		ArrayList<Double> originalNormalized = new ArrayList<Double>();
		originalNormalized = (ArrayList<Double>) sample.clone();
		return normalizeSignal(originalNormalized);
	}

	/**
	 * Ideal delay
	 * 
	 * @param delay
	 * @return samples shifted by delay
	 */
	public ArrayList<Double> idealDelay(int delay) {
		ArrayList<Double> shiftedSample = new ArrayList<Double>();
		shiftedSample = (ArrayList<Double>) sample.clone();

		int shiftSize = (sample.size() * delay) / audioSizeInMS;

		for (int i = 0; i < shiftSize; i++) {
			shiftedSample.add(0, 0.0);
			shiftedSample.remove(shiftedSample.size() - 1);
		}

		return normalizeSignal(shiftedSample);
	}

	/**
	 * Moving Average
	 * 
	 * @param limit
	 * @return the moving average sample
	 */
	public ArrayList<Double> movingAverage(int window) {
		ArrayList<Double> movingAverage = new ArrayList<Double>();

		int averageSize = (sample.size() * window) / audioSizeInMS;

		int i;
		double sampleWindowSum = 0;

		// Calculate moving average
		for (i = 0; i < averageSize / 2; i++) {
			sampleWindowSum += sample.get(i);
		}

		for (; i < averageSize; i++) {
			movingAverage.add(sampleWindowSum / averageSize);
			sampleWindowSum += sample.get(i);
		}

		for (; i < sample.size(); i++) {
			movingAverage.add(sampleWindowSum / averageSize);
			sampleWindowSum -= sample.get(i - averageSize);
			sampleWindowSum += sample.get(i);
		}

		for (int j = 0; j < averageSize / 2; j++) {
			movingAverage.add(sampleWindowSum / averageSize);
			sampleWindowSum -= sample.get(sample.size() + j - averageSize);
		}

		return normalizeSignal(movingAverage);
	}

	/**
	 * Convolution
	 * 
	 * @param window
	 * @return the convolved signal
	 */
	public ArrayList<Double> convolution(int window) {
		ArrayList<Double> convolution = new ArrayList<Double>();

		int windowSize = (sample.size() * window) / audioSizeInMS;

		int i;
		double sampleWindowSum = 0;

		// Calculate the convolution
		for (i = 0; i < windowSize; i++) {
			sampleWindowSum += (sample.get(i));
			convolution.add(sampleWindowSum);
		}

		for (; i < sample.size(); i++) {
			sampleWindowSum -= (sample.get(i - windowSize));
			sampleWindowSum += (sample.get(i));
			convolution.add(sampleWindowSum);
		}

		return normalizeSignal(convolution);
	}

	/**
	 * Energy
	 * 
	 * @param window
	 * @return the energy of the original signal
	 */
	public ArrayList<Double> energy(int window) {
		ArrayList<Double> energy = new ArrayList<Double>();

		int windowSize = (sample.size() * window) / audioSizeInMS;

		int i;
		double sampleWindowSum = 0;

		// Calculate energy
		for (i = 0; i < windowSize; i++) {
			sampleWindowSum += (Math.pow(sample.get(i), 2) / Math.pow(10, 4));
			energy.add(sampleWindowSum);
		}

		for (; i < sample.size(); i++) {
			sampleWindowSum -= (Math.pow(sample.get(i - windowSize), 2) / Math
					.pow(10, 4));
			sampleWindowSum += (Math.pow(sample.get(i), 2) / Math.pow(10, 4));
			energy.add(sampleWindowSum);
		}

		// return normalizeSignal(energy);
		return energy;
	}

	/**
	 * Magnitude
	 * 
	 * @param window
	 * @return the magnitude of the original signal
	 */
	public ArrayList<Double> magnitude(int window) {
		ArrayList<Double> magnitude = new ArrayList<Double>();

		int windowSize = (sample.size() * window) / audioSizeInMS;
		int i;
		double sampleWindowSum = 0;

		// Calculate magnitude
		for (i = 0; i < windowSize; i++) {
			sampleWindowSum += Math.abs(sample.get(i));
			magnitude.add(sampleWindowSum);
		}

		for (; i < sample.size(); i++) {
			sampleWindowSum -= Math.abs(sample.get(i - windowSize));
			sampleWindowSum += Math.abs(sample.get(i));
			magnitude.add(sampleWindowSum);
		}

		// return normalizeSignal(magnitude);
		return magnitude;
	}

	/**
	 * Zero Crossing Rate
	 * 
	 * @param window
	 * @return the ZCR
	 */
	public ArrayList<Double> zeroCrossingRate(int window) {
		ArrayList<Double> zcr = new ArrayList<Double>();
		ArrayList<Double> zcrAux = new ArrayList<Double>(30);

		int windowSize = (sample.size() * window) / audioSizeInMS;

		int i;
		double zcrSampleWindowSum = 0;
		boolean sampleBeforeIsPositive = true;
		boolean sampleNowIsPositive;

		// Calculate zero crossing rate
		for (i = 0; i < windowSize; i++) {
			if (sample.get(i) >= 0) {
				sampleNowIsPositive = true;
			} else {
				sampleNowIsPositive = false;
			}

			if (sampleNowIsPositive != sampleBeforeIsPositive) {
				zcrSampleWindowSum += 1;
				zcrAux.add(1.0);
			} else {
				zcrAux.add(0.0);
			}

			sampleBeforeIsPositive = sampleNowIsPositive;

			zcr.add(zcrSampleWindowSum/(2 * window));
		}

		for (; i < sample.size(); i++) {
			if (sample.get(i) >= 0) {
				sampleNowIsPositive = true;
			} else {
				sampleNowIsPositive = false;
			}

			if (sampleNowIsPositive != sampleBeforeIsPositive) {
				zcrSampleWindowSum += 1;
				zcrSampleWindowSum -= zcrAux.get(0);
				zcrAux.add(1.0);
				zcrAux.remove(0);
			} else {
				zcrSampleWindowSum -= zcrAux.get(0);
				zcrAux.add(0.0);
				zcrAux.remove(0);
			}

			sampleBeforeIsPositive = sampleNowIsPositive;

			zcr.add(zcrSampleWindowSum/(2 * window));
		}

		// return normalizeSignal(zcr);
		return zcr;
	}

	/**
	 * Normalize the signal by the maximum absolute value
	 * 
	 * @param arrayToNormalize
	 * @return a normalized array
	 */
	public ArrayList<Double> normalizeSignal(ArrayList<Double> arrayToNormalize) {
		int maxValueIndex = 0;
		double maxValue;
		double aux;

		// Get the max absolute value of the array
		for (int i = 0; i < arrayToNormalize.size(); i++) {
			if (Math.abs(arrayToNormalize.get(i)) > Math.abs(arrayToNormalize
					.get(maxValueIndex))) {
				maxValueIndex = i;
			}
		}

		maxValue = Math.abs(arrayToNormalize.get(maxValueIndex))
				/ Math.pow(10.0, 1.0);

		// Normalize the other values
		for (int i = 0; i < arrayToNormalize.size(); i++) {
			aux = arrayToNormalize.get(i) / maxValue;
			aux = Math.round(aux * 100000.0) / 100000.0;
			arrayToNormalize.set(i, aux);
		}

		return arrayToNormalize;

	}

	/**
	 * 
	 * @param samples
	 *            read from files
	 * @param audioSizeInMS
	 *            of the files
	 * @return a list containing log from the average of energy and magnitude
	 *         signals, and the average of zero crossing rate for each of the
	 *         signals
	 */
	public static ArrayList<SignalInfo> processSignals(
			ArrayList<ArrayList<Double>> samples, int audioSizeInMS,
			int windowSize) {
		ArrayList<SignalProcessing> signalProcessing = new ArrayList<>();
		ArrayList<SignalInfo> signalInfos = new ArrayList<>();

		// For each signal
		for (int i = 0; i < samples.size(); i++) {
			signalProcessing.add(new SignalProcessing(samples.get(i),
					audioSizeInMS));
			signalInfos.add(signalProcessing.get(i).getSignalInfos(
					signalProcessing.get(i), windowSize));
		}

		return signalInfos;
	}

	/**
	 * Create Stat objects to compute the log and the mean of the signals
	 * 
	 * @param signalProcessing
	 *            object
	 * @param windowSize
	 * @return SignalInfo containing log from the average of energy and
	 *         magnitude signals, and the average of zero crossing rate for each
	 *         of the signals
	 */
	private SignalInfo getSignalInfos(SignalProcessing signalProcessing,
			int windowSize) {
		double energyLog = new Stat(signalProcessing.energy(windowSize))
				.getLog();
		double magnitudeLog = new Stat(signalProcessing.magnitude(windowSize))
				.getLog();
		double zeroCrossingRateMean = new Stat(
				signalProcessing.zeroCrossingRate(windowSize)).getMean();
		SignalInfo signalInfo = new SignalInfo(energyLog, magnitudeLog,
				zeroCrossingRateMean);

		return signalInfo;
	}

}
