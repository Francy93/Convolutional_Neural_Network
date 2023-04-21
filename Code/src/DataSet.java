import java.util.stream.IntStream;		// used to convert the array to a stream
import java.util.stream.Collectors;		// used to convert the stream to a list
import java.io.BufferedReader;			// used to read the file
import java.io.File;
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
	 * Dataset constructor from a file
	 * @param F_N File name
	 * @param D Data Delimiter
	 * @throws FileNotFoundException
	 */
    public DataSet(final String F_N, final String D) throws FileNotFoundException{
		// file reader to initialsize the sample collection
        try {  this.samples	= this.fileReader(this.findFile(F_N), D);  }
		catch(IOException e) { throw new FileNotFoundException(); }

		this.CLASSES		= this.labelClasses();	// getting an array of labels
		this.CLASS_AMOUNT	= this.classAmount();	// getting the amount of samples per class
		this.classesToSamples();					// equipping every sample with a one-hot array
		this.minmaxUpdate();						// updating the max and min values of the dataset
    }
	/**
	 * Dataset constructor from an array of samples
	 * @param S array of samples
	 */
	public DataSet(final Sample[] S){
		this.samples		= S.clone();			// getting the samples
		this.CLASSES		= this.labelClasses();	// getting an array of labels
		this.CLASS_AMOUNT	= this.classAmount();	// getting the amount of samples per class
		this.classesToSamples();					// equipping every sample with a one-hot array
		this.minmaxUpdate();						// updating the max and min values of the dataset
	}
	/**
	 * Dataset constructor from another dataset
	 * @param DS dataset
	 */
	private DataSet(DataSet DS){
		this.samples		= DS.samples.clone();		// getting the samples
		this.CLASSES		= DS.CLASSES.clone();		// getting an array of labels
		this.CLASS_AMOUNT	= DS.CLASS_AMOUNT.clone();	// getting the amount of samples per class
		this.min			= DS.min;					// getting the min value of the dataset
		this.max			= DS.max;					// getting the max value of the dataset
	}

    /**
	 * File reader
	 * @return array of samples
	 */
	private Sample[] fileReader(final String FILE_NAME, final String DELIMITER) throws FileNotFoundException {
		// reading the file
		try (final BufferedReader SCANN = new BufferedReader(new FileReader(FILE_NAME))) {
			return SCANN.lines().parallel()									// reading the file line by line
				.filter(line -> line.matches("^(\\d|["+DELIMITER+"])*$"))	// filtering out the lines that are not numbers
				.map(line -> {												// converting the line to a sample
					try { return new Sample(line, DELIMITER);				// creating a sample
					}catch (Exception e) { return null; }					// if the line is not a sample, return null
				})
				.filter(sample -> sample != null)							// filtering out the null samples
				.collect(Collectors.toList())								// collecting the samples into a list
				.toArray(Sample[]::new);									// converting the list to an array
		} catch (IOException e) { throw new FileNotFoundException(); }		// if the file is not found, throw an exception
	}

	/**
	 * Finding a file Breadth First Search way (BFS)
	 * @param FILE_NAME file name
	 * @param dirs directories to search in
	 * @return file path
	 */
	private String findFile(final String FILE_NAME, File ... dirs) {
		dirs = dirs.length == 0 ? new File[] { new File(".") } : dirs;				// if no directories are provided, use the current directory
		final ArrayList<File> SUB_DIRS = new ArrayList<>();							// list of sub directories

		for(final File DIR: dirs){													// for each directory
			final File[] FILES =  DIR.listFiles((d, n) -> n.matches(FILE_NAME));	// getting the files that match the file name
			if(FILES.length > 0) return FILES[0].getPath();							// if a file is found, return it
			else SUB_DIRS.addAll(Arrays.asList(DIR.listFiles(File::isDirectory)));	// if no file is found, add the sub directories to the list
		}
		// if no file is found, search in the sub directories
		if(SUB_DIRS.size() > 0) return this.findFile(FILE_NAME, SUB_DIRS.toArray(new File[SUB_DIRS.size()]));
		else					return FILE_NAME;									// if no file is found, return the original file name
	}


	/**
	 * geting lable classes
	 * @return label classes
	 */
	private double[] labelClasses(){
		return Arrays.stream(samples)					// converting the samples array to a stream
            .mapToDouble(Sample::getLabel)				// getting the label of each sample
            .distinct()									// removing duplicates
            .sorted()									// sorting the labels
            .toArray();									// converting the stream to an array
	}

	private int[] classAmount() {
		return Arrays.stream(this.CLASSES)							// converting the classes array to a stream
			.mapToInt(CLASS -> (int) Arrays.stream(this.samples)	// converting the samples array to a stream
				.parallel()											// using parallel processing
				.filter(sample -> sample.getLabel() == CLASS)		// filtering out the samples that are not of the current class
				.count())											// counting the samples
			.toArray();												// converting the stream to an array
	}

	// providing every sample with a one-hot array
	public void classesToSamples(){
		Arrays.stream(this.samples).parallel().forEach(SAMPLE -> {					// cycling through the samples
			SAMPLE.setClassLocation(IntStream.range(0, this.CLASSES.length)			// creating a stream of indexes
                .mapToDouble(i -> SAMPLE.getLabel() == this.CLASSES[i] ? 1.0 : 0.0)	// creating a one-hot array
                .toArray()															// converting the stream to an array
			);
		});
	}

	// getting the max and min values of the dataset
	private void minmaxUpdate(){
		this.max = -Double.MAX_VALUE;	// max value of the samples
		this.min =	Double.MAX_VALUE;	// min value of the samples

		// getting the max and min values of the samples
		Arrays.stream(this.samples).forEach(SAMPLE -> {			// cycling through the samples	
			for(final double FEATURE: SAMPLE.getFeature1D()){	// cycling through the features
				if(FEATURE > this.max) this.max = FEATURE;		// updating the max value
				if(FEATURE < this.min) this.min = FEATURE;		// updating the min value
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
			final int NEW_INDEX		= (int) Math.round(Util.rangeRandom(0, RANGE));	// getting a random index

			final Sample TEMP		= this.samples[old_index];						// storing temporary the second sample
			this.samples[old_index]	= this.samples[NEW_INDEX];						// swapping the first sample
			this.samples[NEW_INDEX]	= TEMP;											// swapping the second sample
		}
	}

	// normalizing the dataset
	public void normalize(){
		this.minmaxUpdate(); // updating the max and min values of the dataset
		
		// normalizing the samples
		this.samples = Arrays.stream(this.samples).parallel().map(sample -> {
			final Sample SAMPLE = new Sample(sample.getFeature1D(), sample.getLabel() );
			for(int i = 0; i < SAMPLE.getFeature1D().length; i++){
				SAMPLE.setToken(i, this.normalization(SAMPLE.getToken1D(i), this.min, this.max));
			}
			SAMPLE.setClassLocation(sample.getOneHot().clone());// setting the one-hot array
			return SAMPLE;										// returning the sample
		}).toArray(Sample[]::new);								// converting the stream to an array

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
							double range = E * this.max;				// calculating the range
							range = Util.rangeRandom(-range, range);	// getting a random number in the range
							if(Math.random() < E) return range*10;		// adding noise to the sample
							else return Math.min(Math.max(TOKEN+range, this.min), this.max);
						}).toArray(),									// converting the stream to an array
						sample.getLabel()								// setting the label
					);
					SAMPLE.setClassLocation(sample.getOneHot().clone());// setting the one-hot array
					return SAMPLE;										// returning the sample
				})
			).toList()													// converting the stream to a list
		);
		// creating an array list to store the adversarial samples
		final ArrayList<Sample> JOINT = new ArrayList<>(Arrays.asList(this.samples));	
		JOINT.addAll(ADVERSARIAL);										// adding the adversarial samples to the array list
		this.samples = JOINT.toArray(Sample[]::new);					// converting the array list to an array
		return ADVERSARIAL.toArray(Sample[]::new);						// returning the adversarial samples
	}




	// ..................getters methods .................

	// printing the dataset
	public void print1D(){
		Arrays.stream(this.samples).parallel().forEach(SAMPLE -> SAMPLE.print1D());
	}

	/**
	 * printing the image of the dataset samples
	 * @param SCGS	sample color grid scale
	 * @param ACGS	ascii color grid scale
	 * @param SIZE	size of the sample
	 */
	public void print2D(){ this.print2D(this.max, 23, 1); }
	public void print2D(final double SCGS, final int ACGS, final int SIZE){
		Arrays.stream(this.samples).parallel().forEach(SAMPLE -> {			// cycling through the samples
			SAMPLE.print2D(SCGS, ACGS, SIZE);								// printing the sample
		});
	}

	/**
	 * Get the index of the label
	 * @param LABEL label to be searched
	 * @return		index of the label
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

	/**
	 * Get a sample from the dataset
	 * @param INDEX	index of the sample
	 * @return		sample
	 */
	public Sample getSample(final int INDEX){
		return this.samples[INDEX];
	}
	
	public DataSet	clone()			{ return new DataSet(this);		}	// clone dataset
	public Sample[] getDataSet()	{ return this.samples.clone();	}	// get dataset
	public double[] getClasses()	{ return this.CLASSES; 			}	// get the classes
	public int		classesAmount()	{ return this.CLASSES.length;	}	// get the amount of classes
	public int		size()			{ return this.samples.length;	}	// get dataset size
	public double	getMin()		{ return this.min; 				}	// get dataset min value
	public double	getMax()		{ return this.max; 				}	// get dataset max value
}
