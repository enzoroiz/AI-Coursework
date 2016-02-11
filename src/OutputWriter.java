import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that writes a .dat file
 * 
 * @author enzoroiz
 *
 */
public class OutputWriter {
	/**
	 * Instance fields
	 */
	BufferedWriter outputFile;
	private final String OUTPUT = "assets/Signal Information.dat";

	/**
	 * Constructor
	 */
	public OutputWriter() {
		// Create a file
		try {
			File file = new File(OUTPUT);
			FileWriter fileWriter;
			fileWriter = new FileWriter(file);
			this.outputFile = new BufferedWriter(fileWriter);
			this.outputFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Write the information of the signals in the file
	 * 
	 * @param toWrite
	 *            array with SignalInfo objects
	 */
	public void writeFile(ArrayList<SignalInfo> toWrite) {
		for (int i = 0; i < toWrite.size(); i++) {
			try {
				this.outputFile.write(toWrite.get(i).toString() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Write the string in the file
	 * 
	 * @param toWrite
	 */
	public void writeFile(String toWrite) {
		try {
			String message = (String) toWrite;
			this.outputFile.write(message);

		} catch (Exception e) {// Catch exception if any
			e.printStackTrace();
		}
	}

	public void finishWriting() {
		// Closes the file
		try {
			this.outputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
