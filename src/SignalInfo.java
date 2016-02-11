/**
 * Class that stores informations extracted from a read signal
 * 
 * @author enzoroiz
 */
public class SignalInfo {
	/**
	 * Instance fields
	 */
	public static int NUM_INFO = 3;
	public static String ENERGY = "ENERGY";
	public static String MAGNITUDE = "MAGNITUDE";
	public static String ZCR = "ZERO CROSSING RATE";
	private double energy;
	private double magnitude;
	private double zeroCrossingRate;

	/**
	 * Constructor passing as parameter
	 * 
	 * @param energyLog
	 * @param magnitudeLog
	 * @param zeroCrossingRateAverage
	 */
	public SignalInfo(double energy, double magnitude, double zeroCrossingRate) {
		this.energy = energy;
		this.magnitude = magnitude;
		this.zeroCrossingRate = zeroCrossingRate;
	}

	/**
	 * Getter to return the specified information 0 if energyLog 1 if
	 * magnitudeLog 2 if zeroCrossingRateAverage
	 * 
	 * @param index
	 * @return the information passed as index
	 */
	public double get(int index) {
		switch (index) {
		case 0:
			return energy;
		case 1:
			return magnitude;
		case 2:
			return zeroCrossingRate;
		default:
			return Double.NaN;
		}
	}

	@Override
	public String toString() {
		return ("[" + energy + ", " + magnitude + ", " + zeroCrossingRate + "]");
	}

	@Override
	public boolean equals(Object obj) {
		SignalInfo signalInfo = (SignalInfo) obj;
		return (signalInfo.get(0) == energy && signalInfo.get(1) == magnitude && signalInfo
				.get(2) == zeroCrossingRate);
	}

}
