import java.io.BufferedReader;			// used to read the file
import java.util.stream.IntStream;		// used to convert the array to a stream
import java.util.stream.Collectors;		// used to convert the stream to a list
import java.io.FileReader;				// used to read the file
import java.io.IOException;				// used to throw an exception
import java.io.FileNotFoundException;	// used to throw an exception
import java.util.ArrayList;				// used to store the samples
import java.util.Arrays;				// used to convert the array to a list
import lib.Util;						// multiple utility functions

public class DataSet {
    private final double[]	CLASSES;		// number of possible classifications
	private final int[]		CLASS_AMOUNT;	// number of samples per class
    private Sample[]		samples;		// samples collection
	private double			max;			// max value of the dataset
	private double			min;			// min value of the dataset

	/**
	 * Dataset constructor
	 * @param F_N File name
	 * @param D Data Delimiter
	 * @throws FileNotFoundException
	 */
    public DataSet(final String F_N, final String D) throws FileNotFoundException{
		// file reader to initialsize the sample collection
        try {  this.samples	= this.fileReader(F_N, D);  }
		catch(IOException e) { throw new FileNotFoundException(); }

		this.CLASSES		= this.labelClasses();	// getting an array of labels
		this.CLASS_AMOUNT	= this.classAmount();	// getting the amount of samples per class
		this.classesToSamples();					// equipping every sample with a one-hot array
		this.minmaxUpdate();						// updating the max and min values of the dataset
    }
	public DataSet(final Sample[] S){
		this.samples		= S.clone();			// getting the samples
		this.CLASSES		= this.labelClasses();	// getting an array of labels
		this.CLASS_AMOUNT	= this.classAmount();	// getting the amount of samples per class
		this.classesToSamples();					// equipping every sample with a one-hot array
		this.minmaxUpdate();						// updating the max and min values of the dataset
	}

    /**
	 * File reader
	 * @return array of samples
	 */
	private Sample[] fileReader(final String FILE_NAME, final String DELIMITER) throws FileNotFoundException {
		// reading the file
		try (final BufferedReader SCANN = new BufferedReader(new FileReader(FILE_NAME))) {
			return SCANN.lines().parallel()										// reading the file line by line
				.filter(line -> line.matches("^(\\d|["+DELIMITER+"])*$"))	// filtering out the lines that are not numbers
				.map(line -> {													// converting the line to a sample
					try { return new Sample(line, DELIMITER);				// creating a sample
					}catch (Exception e) { return null; }						// if the line is not a sample, return null
				})
				.filter(sample -> sample != null)								// filtering out the null samples
				.collect(Collectors.toList())
				.toArray(new Sample[0]);										// collecting the samples into a list
		} catch (IOException e) { throw new FileNotFoundException(); }			// if the file is not found, throw an exception

		// updating the max and min values of the dataset
	}


	/**
	 * geting lable classes
	 * @return label classes
	 */
	private double[] labelClasses(){
		return Arrays.stream(samples)	// converting the samples array to a stream
            .mapToDouble(Sample::getLabel)				// getting the label of each sample
            .distinct()									// removing duplicates
            .sorted()									// sorting the labels
            .toArray();									// converting the stream to an array
	}

	private int[] classAmount(){
		int[] classCount = new int[CLASSES.length];					// creating an array to store the amount of samples per class

		Arrays.stream(this.samples).parallel().forEach(S -> {		// cycling through the samples
			int index = Arrays.binarySearch(CLASSES, S.getLabel());	// getting the class index
			if (index >= 0)  classCount[index]++;					// incrementing the class count
		});
		return classCount;
	}

	// providing every sample with a one-hot array
	public void classesToSamples(){
		Arrays.stream(this.samples).parallel().forEach(SAMPLE -> {				// cycling through the samples
			double[] labelLocation = IntStream.range(0, CLASSES.length)			// creating a stream of indexes
                .mapToDouble(i -> SAMPLE.getLabel() == CLASSES[i] ? 1.0 : 0.0)	// creating a one-hot array
                .toArray();														// converting the stream to an array
			SAMPLE.setClassLocation(labelLocation);								// setting the one-hot array
		});
	}

	// getting the max and min values of the dataset
	private void minmaxUpdate(){
		this.max = -Double.MAX_VALUE;	// max value of the samples
		this.min =	Double.MAX_VALUE;	// min value of the samples

		// getting the max and min values of the samples
		Arrays.stream(this.samples).parallel().forEach(SAMPLE -> {	// cycling through the samples	
			for(final double FEATURE: SAMPLE.getFeature1D()){		// cycling through the features
				if(FEATURE > this.max) this.max = FEATURE;			// updating the max value
				if(FEATURE < this.min) this.min = FEATURE;			// updating the min value
			}
		});
	}

	/*
	 * Normalization function
	 * @param VAl value to be normalized
	 * @param MIN min value of the dataset
	 * @param MAX max value of the dataset
	 * @return normalized value
	 */
	public double normalization(final double VAL){
		return VAL == 0? 0: VAL / Math.pow(10d, (Math.floor(Math.log10(Math.abs(VAL))) + 1d));
	}
	private double normalization(final double VAl, final double MIN, final double MAX){
		return (VAl - MIN) / (MAX - MIN);	// normalization formula
	}




	//  ..................setter methods .................

	/**
	 * Set dataset samples
	 * @param SAMPLES array of samples
	 */
	public void setDataSet(final Sample[] SAMPLES){
		this.samples = SAMPLES;
		this.minmaxUpdate();
	}

	// set dataset max value
	public void shuffle(){
		final int RANGE = this.samples.length-1;

		for (int old_index=0; old_index<=RANGE; old_index++) {
			final int NEW_INDEX		= (int) Math.round(Util.rangeRandom(0, RANGE));

			final Sample TEMP		= this.samples[old_index];
			this.samples[old_index]	= this.samples[NEW_INDEX];
			this.samples[NEW_INDEX]	= TEMP;
		}
	}

	// normalizing the dataset
	public void normalize(){
		this.minmaxUpdate(); // updating the max and min values of the dataset
		
		// avoiding division by zero
		final double MIN = this.min == 0?	-1d/this.max:	this.min;
		final double MAX = this.max == 0?	1d/this.min:	this.max;

		// normalizing the dataset
		for(final Sample SAMPLE: this.samples){
			for(int i = 0; i < SAMPLE.getFeature1D().length; i++){
				SAMPLE.setToken(i, this.normalization(SAMPLE.getToken1D(i), MIN, MAX));
			}
		}
		// updating the max and min values of the dataset
		this.minmaxUpdate();

	}

	/**
	 * adversarial sampling augmentation
	 * @param AMOUNT amount of adversarial samples to be created
	 * @param EPSILON the amount of noise to be added to the sample (from 0 to 1)
	 */
	public Sample[] adversarialSampling(){ return this.adversarialSampling(1); }
	public Sample[] adversarialSampling(final int AMOUNT){ return this.adversarialSampling(AMOUNT, 1d/3d); }
	public Sample[] adversarialSampling(final int AMOUNT, final double EPSILON){
		this.minmaxUpdate();											// updating the max and min values of the dataset
		final double E = normalization(EPSILON);						// normalizing the epsilon value
	
		final ArrayList<Sample> ADVERSARIAL = new ArrayList<>(Arrays.stream(this.samples).parallel()
			.flatMap(sample -> IntStream.range(0, AMOUNT)				// creating a stream of indexes
				.mapToObj(i -> {										// creating a stream of samples
					final Sample SAMPLE = new Sample(Arrays.stream(sample.getFeature1D())
						.map(TOKEN -> {									// creating a stream of features
							final double RANDOM = Math.random();		// getting a random number
							double range = E * max;						// calculating the range
							range = Util.rangeRandom(-range, range);	// getting a random number in the range
							return Math.min(Math.max(RANDOM <= E? range: TOKEN+range, this.min), this.max);
						}).toArray(),									// converting the stream to an array
						sample.getLabel()
					);
					SAMPLE.setClassLocation(sample.getOneHot());		// setting the one-hot array
					return SAMPLE;										// returning the sample
				})
			).toList()													// converting the stream to a list
		);
		
		final ArrayList<Sample> JOINT = new ArrayList<>(Arrays.asList(this.samples));	// creating an array list to store the adversarial samples
		JOINT.addAll(ADVERSARIAL);										// adding the adversarial samples to the array list
		this.samples = JOINT.toArray(new Sample[0]);					// converting the array list to an array
		return ADVERSARIAL.toArray(new Sample[0]);						// returning the adversarial samples
	}




	// ..................getters methods .................

	/**
	 * Get dataset samples
	 * @param SAMPLE array of samples
	 */
	public void print1D(final Sample SAMPLE){
		System.out.println(
			Arrays.stream(SAMPLE.getFeature1D())
				.mapToObj(String::valueOf)
				.collect(Collectors.joining(", "))
			+ "\b\b"
		);
	}

	// printing the dataset
	public void print1D(){
		Arrays.stream(this.samples).parallel().forEach(S -> this.print1D(S));
	}

	/**
	 * Print the dataset in 2D
	 * @param SAMPLE sample to be printed
	 * @param SCGS	 sample color gradient scale
	 * @param ACGS	 ascii color gradient scale
	 * @param SIZE	 size of the ascii character
	 */
	public void print2D(final Sample SAMPLE, final double SCGS, final int ACGS, final int SIZE){
		StringBuilder sb = new StringBuilder();
			for (double[] feature : SAMPLE.getFeature2D()) {
				for (double val : feature) {
					final float SP = (float) val * 100f / (float) SCGS; // sample percentage
					final float AP = SP / 100f * (float) ACGS; // ascii percentage
					final int COL = 232 + Math.round(AP); // color index
					for (int i = 0; i < SIZE; i++) {
						sb.append("\033[38;5;"+COL+";48;5;"+COL+"m███\033[0m");
					}
				}
				sb.append("\r\n");
			}
       		System.out.println(sb.toString());
	}

	/**
	 * printing the image of the dataset samples
	 * @param SCGS	sample color grid scale
	 * @param ACGS	ascii color grid scale
	 * @param SIZE	size of the sample
	 */
	public void print2D(){ this.print2D(this.max, 23, 1); }
	public void print2D(final double SCGS, final int ACGS, final int SIZE){
		Arrays.stream(this.samples).parallel().forEach(S -> {			// cycling through the samples
			this.print2D(S, SCGS, ACGS, SIZE);							// printing the sample
		});
	}

	/**
	 * Get the index of the label
	 * @param LABEL label to be searched
	 */
	public int getLabelIndex(final double LABEL){
		for(int i = 0; i < this.CLASSES.length; i++){
			if(LABEL == this.CLASSES[i]) return i;
		}
		return -1;
	}

	/**
	 * Get the amount of labels
	 * @param LABEL	label to be searched
	 * @return		amount of labels
	 */
	public int getLabelsAmount(final double LABEL){
		return this.CLASS_AMOUNT[this.getLabelIndex(LABEL)];
	}

	public double[] getClasses(){
		return this.CLASSES;
	}

	public int classesAmount(){
		return this.CLASSES.length;
	}

	public int getSize(){
		return this.samples.length;
	}

	/**
	 * Get a sample from the dataset
	 * @param INDEX	index of the sample
	 * @return		sample
	 */
	public Sample getSample(final int INDEX){
		return this.samples[INDEX];
	}

	public Sample[] getDataSet(){
		return this.samples.clone();
	}
}
