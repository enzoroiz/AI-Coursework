import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class that helps in reading files.
 * 
 * @author enzoroiz
 *
 */
public class FileReader {
	/**
	 * Instance fields
	 */
	private File filesPath;
	private File[] filesToRead;

	/**
	 * Construtor informing the entryPath List the files of the given path or
	 * throws an error if it is not a directory
	 * 
	 * @param entryPath
	 */
	public FileReader(String entryPath) {
		filesPath = new File(entryPath);
		try {
			if (!filesPath.isDirectory()) {
				throw new IOException("The path is not valid. ");
			} else {
				filesToRead = new File(entryPath).listFiles();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Read the files listed in the constructor
	 * 
	 * @return the samples to be processed as an matrix
	 */
	public ArrayList<ArrayList<Double>> readAllFiles() {
		// Read the silence ones first
		ArrayList<ArrayList<Double>> samples = new ArrayList<>();
		for (File file : filesToRead) {
			if (!file.isDirectory()
					&& file.getName().toLowerCase().contains("silence")) {
				samples.add(readFile(file));
			}
		}

		// And then the speech ones
		for (File file : filesToRead) {
			if (!file.isDirectory()
					&& file.getName().toLowerCase().contains("speech")) {
				samples.add(readFile(file));
			}
		}

		return samples;
	}

	/**
	 * Read the file passed as parameter and store the information in an array
	 * list
	 * 
	 * @param file
	 * @return array list containing information read
	 */
	private ArrayList<Double> readFile(File file) {
		ArrayList<Double> sample = new ArrayList<Double>();

		try {
			double x;
			Scanner scanner = new Scanner(file);
			sample.add(Double.valueOf(String.valueOf(scanner.nextLine())));
			while (scanner.hasNextLine()) {
				x = Double.valueOf(String.valueOf(scanner.nextLine()));
				sample.add(x);
			}

			scanner.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sample;
	}

}