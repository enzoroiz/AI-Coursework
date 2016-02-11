import java.util.ArrayList;
import java.util.Collections;

/**
 * Class that computes the accuracy of predicting either speech or silence
 * signal by doing a kfold validation approach, applying the Naive Bayes
 * Classifier
 * 
 * @author enzoroiz
 *
 */
public class Classifier {
	/**
	 * Final fields
	 */
	// Probability Speech or Silence
	private final double PSILENCE = 0.5;
	private final double PSPEECH = 0.5;

	/**
	 * Instance fields
	 */
	private ArrayList<SignalInfo> silenceProcessedSignals;
	private ArrayList<SignalInfo> speechProcessedSignals;
	private ArrayList<ArrayList<Stat>> statsSilence;
	private ArrayList<ArrayList<Stat>> statsSpeech;
	private int processedSignalsSize;
	private int signalInfosSize;
	private int kFold;

	/**
	 * Constructor
	 * 
	 * @param silenceProcessedSignals
	 * @param speechProcessedSignals
	 * @param kFold
	 *            times
	 */
	public Classifier(ArrayList<SignalInfo> silenceProcessedSignals,
			ArrayList<SignalInfo> speechProcessedSignals, int kFold) {
		this.silenceProcessedSignals = silenceProcessedSignals;
		this.speechProcessedSignals = speechProcessedSignals;
		this.processedSignalsSize = silenceProcessedSignals.size(); // 50
		this.signalInfosSize = SignalInfo.NUM_INFO; // 3
		this.kFold = kFold;

		// Initialise Stats Arrays
		this.statsSilence = new ArrayList<>();
		this.statsSpeech = new ArrayList<>();
		for (int i = 0; i < signalInfosSize; i++) {
			this.statsSilence.add(new ArrayList<Stat>());
			this.statsSpeech.add(new ArrayList<Stat>());
		}
	}

	/**
	 * Realise the Kfold cross validation by splitting the data between speech
	 * and silence signals For each one of the Kfold iteration keeps a
	 * k/processedSignalsSize as test set and the remaining as training set for
	 * both speech and silence samples. Computes the statistics for the training
	 * set and put it in an array list
	 */
	private void kFold() {
		int lowerBound;
		int upperBound;

		ArrayList<Double> silenceInfo;
		ArrayList<Double> speechInfo;

		// For each type of information: E - M - Z //3
		for (int i = 0; i < signalInfosSize; i++) {
			// For each folding //10
			for (int fold = 0; fold < kFold; fold++) {
				silenceInfo = new ArrayList<>();
				speechInfo = new ArrayList<>();
				lowerBound = fold * (processedSignalsSize / kFold);
				upperBound = (fold + 1) * (processedSignalsSize / kFold);

				// For each information of the processed signal //50
				for (int j = 0; j < processedSignalsSize; j++) {
					if (!(j >= lowerBound && j < upperBound)) { // Adding
																// training sets
						silenceInfo.add(silenceProcessedSignals.get(j).get(i));
						speechInfo.add(speechProcessedSignals.get(j).get(i));
					}
				}
				
				this.statsSilence.get(i).add(new Stat(silenceInfo));
				this.statsSpeech.get(i).add(new Stat(speechInfo));
			}
		}
	}

	/**
	 * For each of the Kfold iteration uses the test set in order to compute the
	 * accuracy in predicting if the signal analysed is either from a speech or
	 * silence sample. Do it by applying the Naive Bayes approach to the
	 * corresponding training set. Compute the accuracy by measuring how many
	 * times the classification was right.
	 */
	public void test() {
		double posteriorSilenceForSilenceSignal = PSILENCE;
		double posteriorSpeechForSilenceSignal = PSPEECH;
		double posteriorSilenceForSpeechSignal = PSILENCE;
		double posteriorSpeechForSpeechSignal = PSPEECH;
		double evidenceSilenceSignal;
		double evidenceSpeechSignal;
		double right;
		int lowerBound;
		int upperBound;

		ArrayList<Double> guessed = new ArrayList<>();

		kFold();

		// For each fold iteration
		for (int i = 0; i < kFold; i++) {// 10
			right = 0;
			lowerBound = i * (processedSignalsSize / kFold);
			upperBound = (i + 1) * (processedSignalsSize / kFold);

			// For each subset
			for (int j = lowerBound; j < upperBound; j++) {// 5
				posteriorSilenceForSilenceSignal = PSILENCE;
				posteriorSpeechForSilenceSignal = PSPEECH;
				posteriorSilenceForSpeechSignal = PSILENCE;
				posteriorSpeechForSpeechSignal = PSPEECH;

				// For each type of information
				for (int m = 0; m < signalInfosSize; m++) {// 3
					// Calculate the posterior for silence signals
					posteriorSilenceForSilenceSignal *= statsSilence
							.get(m)
							.get(i)
							.getNormalDistribution(
									silenceProcessedSignals.get(j).get(m));
					posteriorSpeechForSilenceSignal *= statsSpeech
							.get(m)
							.get(i)
							.getNormalDistribution(
									silenceProcessedSignals.get(j).get(m));

					// Calculate the posterior for speech signals
					posteriorSilenceForSpeechSignal *= statsSilence
							.get(m)
							.get(i)
							.getNormalDistribution(
									speechProcessedSignals.get(j).get(m));
					posteriorSpeechForSpeechSignal *= statsSpeech
							.get(m)
							.get(i)
							.getNormalDistribution(
									speechProcessedSignals.get(j).get(m));
				}

				// Calculate the evidences
				evidenceSilenceSignal = posteriorSilenceForSilenceSignal
						+ posteriorSpeechForSilenceSignal;
				evidenceSpeechSignal = posteriorSilenceForSpeechSignal
						+ posteriorSpeechForSpeechSignal;

				// Calculate the posterior for silence signals in %
				posteriorSilenceForSilenceSignal = posteriorSilenceForSilenceSignal
						/ evidenceSilenceSignal * 100;
				posteriorSpeechForSilenceSignal = posteriorSpeechForSilenceSignal
						/ evidenceSilenceSignal * 100;

				// Calculate the posterior for speech signals in %
				posteriorSilenceForSpeechSignal = posteriorSilenceForSpeechSignal
						/ evidenceSpeechSignal * 100;
				posteriorSpeechForSpeechSignal = posteriorSpeechForSpeechSignal
						/ evidenceSpeechSignal * 100;

				if (posteriorSilenceForSilenceSignal > posteriorSpeechForSilenceSignal) {
					right++;
				}

				if (posteriorSpeechForSpeechSignal > posteriorSilenceForSpeechSignal) {
					right++;
				}
			}

			guessed.add(right);
		}

		outputResults(guessed);
	}

	/**
	 * Prints the accuracy got in the test
	 * 
	 * @param guessed
	 */
	private void outputResults(ArrayList<Double> guessed) {
		System.out.println("The results for iteration were: " + guessed.toString());
		System.out.println("The accuracy for the test was: "
				+ Stat.round(new Stat(guessed).getMean() * 10) + "%");
	}

	/**
	 * test the accuracy for "n" times shuffling the processed signals
	 * 
	 * @param times
	 *            to shuffle and test compute the accuracy
	 */
	public void shuffledSampleTest(int times) {
		for (int i = 0; i < times; i++) {
			shuffle();
			test();
		}

	}

	/**
	 * Shuffle the processed Signals in order to produce different training and
	 * test sets
	 */
	private void shuffle() {
		ArrayList<SignalInfo> silenceTemp = new ArrayList<>(
				silenceProcessedSignals);
		ArrayList<SignalInfo> speechTemp = new ArrayList<>(
				speechProcessedSignals);

		ArrayList<Integer> index = new ArrayList<>();
		for (int i = 0; i < processedSignalsSize; i++) {
			index.add(i);
		}

		Collections.shuffle(index);

		for (int i = 0; i < processedSignalsSize; i++) {
			silenceTemp.set(i, silenceProcessedSignals.get(index.get(i)));
			speechTemp.set(i, speechProcessedSignals.get(index.get(i)));
		}

		this.silenceProcessedSignals = silenceTemp;
		this.speechProcessedSignals = speechTemp;

	}
}
