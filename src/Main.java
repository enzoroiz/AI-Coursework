import java.io.IOException;
import java.util.ArrayList;

/**
 * Main class of the program Initialise and executes everything needed to write
 * an excel file containing informations about the signals read from a file
 * write a .dat file with the information of the extracted signals and computes
 * the accuracy on predicting sample silence or speech
 * 
 * @author enzoroiz
 */
public class Main {
	private final static int k = 10;

	public static void main(String[] args) {
		String entry;

		// Receive command line parameters
		try {
			entry = args[0];
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Parameters problem");
			System.exit(0);
			return;
		}

		// Read all files in the specified directory and receive a matrix of the
		// samples read as a return
		FileReader fileReader = new FileReader(entry);
		ArrayList<ArrayList<Double>> samples = fileReader.readAllFiles();

		// Split the data into silence and speech signals
		ArrayList<ArrayList<Double>> silenceSignals = new ArrayList<>();
		ArrayList<ArrayList<Double>> speechSignals = new ArrayList<>();
		silenceSignals.addAll(samples.subList(0, samples.size() / 2));
		speechSignals
				.addAll(samples.subList(samples.size() / 2, samples.size()));

		// Process the signal information for all the read files
		ArrayList<SignalInfo> silenceSignalsInfo = SignalProcessing
				.processSignals(silenceSignals, 300, 30);
		ArrayList<SignalInfo> speechSignalsInfo = SignalProcessing
				.processSignals(speechSignals, 300, 30);

		// Write an Excel sheet containing the information extracted from the
		// given signals
		WriteExcel excelWriter = new WriteExcel();
		try {
			Integer column = null;
			column = excelWriter.writeDataInColumns(silenceSignalsInfo, column);
			excelWriter.writeDataInColumns(speechSignalsInfo, column);
			excelWriter.writeWorkbook();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		// Write a file .dat containing the information extracted from the given
		// signals
		OutputWriter outputWriter = new OutputWriter();
		outputWriter.writeFile("SILENCE SIGNALS\n");
		outputWriter.writeFile(silenceSignalsInfo);
		outputWriter.writeFile("\nSPEECH SIGNALS\n");
		outputWriter.writeFile(speechSignalsInfo);
		outputWriter.finishWriting();

		// Compute the acuuracy in a cross-validation fashion, using K-fold
		// validation with K=10
		Classifier classifier = new Classifier(silenceSignalsInfo,
				speechSignalsInfo, k);
		//classifier.test();

		// Do the same described above, but shuffle the samples and test it "n"
		// times in order to have different sets
		 classifier.test();

	}
}