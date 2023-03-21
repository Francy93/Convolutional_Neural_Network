import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import lib.Util;

public class DataSet {

    private final String	FILE_NAME;		// name of the file to be read
    private final double[]	CLASSES;		// number of possible classifications
	private final int[]		CLASS_AMOUNT;	// number of samples per class
	private final String	DELIMITER;		// string file delimiter
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
        this.FILE_NAME = F_N;
		this.DELIMITER = D;

		// file reader to initialsize the sample collection
        try {  this.samples	= this.fileReader();  }
		catch(IOException e) { throw new FileNotFoundException(); }

		this.CLASSES		= this.labelClasses();	// getting an array of labels
		this.CLASS_AMOUNT	= this.classAmount();	// getting the amount of samples per class
		this.classesToSamples();					// equipping every sample with a one-hot array
    }


    /**
	 * File reader
	 * @return array of samples
	 */
	private Sample[] fileReader() throws FileNotFoundException{
		final ArrayList<Sample> SAMPLES = new ArrayList<>();

        try{
			final BufferedReader SCANN = new BufferedReader(new FileReader(this.FILE_NAME));

			// cycling over the file rows
			for(String line = ""; (line = SCANN.readLine()) != null; line.trim()){
				if(!line.matches("^(\\d|["+this.DELIMITER+"])*$")) continue;

				// generating the sample obgect
				try{ SAMPLES.add(new Sample(line, this.DELIMITER)); }
				catch(Exception e) { }
			}
	
			SCANN.close();
			if(SAMPLES.size() < 1) throw new FileNotFoundException();
        }catch(IOException e){ throw new FileNotFoundException(); }
        
		return SAMPLES.toArray(new Sample[0]);
    }

	/**
	 * geting lable classes
	 * @return label classes
	 */
	private double[] labelClasses(){
		final HashSet<Double> CLASSES_SET = new HashSet<>();
		for(final Sample SAMPLE: this.samples) CLASSES_SET.add(SAMPLE.getLabel());

		final double[] CLASSES_ARRAY = new double[CLASSES_SET.size()];
		int iterator = 0;
		for(final double CLASS: CLASSES_SET) CLASSES_ARRAY[iterator++] = CLASS;

		Arrays.sort(CLASSES_ARRAY);
		return CLASSES_ARRAY;
	}

	private int[] classAmount(){
		final int[] CLASSES_ARRAY = new int[this.CLASSES.length];

		for(final Sample SAMPLE: this.samples){
			for(int i = 0; i < this.CLASSES.length; i++){
				if(SAMPLE.getLabel() == this.CLASSES[i]){
					CLASSES_ARRAY[i]++;
					break;
				}
			}
		}
		return CLASSES_ARRAY;
	}

	// providing every sample with a one-hot array
	private void classesToSamples(){
		for(final Sample SAMPLE: this.samples){
			// creating an array to store the class location
			final double[] LABEL_LOCATION = new double[this.CLASSES.length];

			for(int label=0; label < this.CLASSES.length; label++){
				// setting the class location
				LABEL_LOCATION[label] = SAMPLE.getLabel() == this.CLASSES[label]? 1.0: 0.0;
			}
			// storing the class location into the sample
			SAMPLE.setClassLocation(LABEL_LOCATION);
		}
	}

	// getting the max and min values of the dataset
	private void minmaxUpdate(){
		this.max = -Double.MAX_VALUE;	// max value of the samples
		this.min =	Double.MAX_VALUE;	// min value of the samples

		// getting the max and min values of the samples
		for(final Sample SAMPLE: this.samples){
			for(final double FEATURE: SAMPLE.getFeature1D()){
				if(FEATURE > this.max) this.max = FEATURE;
				if(FEATURE < this.min) this.min = FEATURE;
			}
		}
	}

	/*
	 * Normalization function
	 * @param VAl value to be normalized
	 * @param MIN min value of the dataset
	 * @param MAX max value of the dataset
	 * @return normalized value
	 */
	private double normalization(final double VAl){
		return VAl / Math.pow(10d, (Math.floor(Math.log10(Math.abs(VAl))) + 1d));
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
		this.minmaxUpdate();
		
		// avoiding division by zero
		final double MIN = this.min == 0?  -Double.MIN_VALUE:	this.min;
		final double MAX = this.max == 0?	Double.MIN_VALUE:	this.max;

		// normalizing the dataset
		for(final Sample SAMPLE: this.samples){
			for(int i = 0; i < SAMPLE.getFeature1D().length; i++){
				SAMPLE.setToken(i, this.normalization(SAMPLE.getToken1D(i), MIN, MAX));
			}
		}
	}

	/**
	 * adversarial sampling augmentation
	 * @param AMOUNT amount of adversarial samples to be created
	 * @param EPSILON the amount of noise to be added to the sample (from 0 to 1)
	 */
	public Sample[] adversarialSampling(){ return this.adversarialSampling(1); }
	public Sample[]  adversarialSampling(final int AMOUNT){
		this.minmaxUpdate();							// updating the max and min values of the dataset
		return this.adversarialSampling(AMOUNT, 1d/3d);	// creating the adversarial samples
	}
	public Sample[]  adversarialSampling(final int AMOUNT, double epsilon){
		epsilon = this.normalization(epsilon);												// normalizing the epsilon value
		final int FEATURES_AMOUNT	= this.samples[0].getFeature1D().length;				// amount of features
		final ArrayList<Sample> ADVERSARIAL_SAMPLES = new ArrayList<>();					// list of adversarial samples
		
		// creating the adversarial samples
		for(final Sample SAMPLE: this.samples){
			for(int i = 0; i < AMOUNT; i++){
				final double[] FEATURE	= new double[FEATURES_AMOUNT];						// creating the feature array

				for(int f = 0; f < FEATURES_AMOUNT; f++){
					final double TOKEN	= SAMPLE.getToken1D(f);								// getting the token value
					final double RANDOM	= Math.random();									// random variable

					if(RANDOM < epsilon){
						double rangeRandom = RANDOM * this.max;								// calculating the range of the random value
						rangeRandom = TOKEN + Util.rangeRandom(-rangeRandom, rangeRandom);	// adding the random value to the original value
						rangeRandom = rangeRandom > this.max? this.max: rangeRandom < this.min? this.min: rangeRandom;
						FEATURE[f]	= rangeRandom;											// setting the feature with the random value
					}else FEATURE[f]= TOKEN;												// setting the feature with the original value
				}
				ADVERSARIAL_SAMPLES.add(new Sample(FEATURE, SAMPLE.getLabel()));			// adding the adversarial sample to the dataset
				// setting the class location (oneHot)
				ADVERSARIAL_SAMPLES.get(ADVERSARIAL_SAMPLES.size()-1).setClassLocation(SAMPLE.getOneHot());
			}
		}

		// setting the adversarial samples and returning them
		final ArrayList<Sample> JOINT_ORIG_ADVER = new ArrayList<>(Arrays.asList(this.samples));
		JOINT_ORIG_ADVER.addAll(ADVERSARIAL_SAMPLES);										// joining the original dataset with the adversarial samples
		this.samples = JOINT_ORIG_ADVER.toArray(new Sample[0]);								// updating the dataset
		return ADVERSARIAL_SAMPLES.toArray(new Sample[0]);									// returning the adversarial samples
	}




	// ..................getters methods .................

	// getting the class index
	public int getLabelIndex(final double LABEL){
		for(int i = 0; i < this.CLASSES.length; i++){
			if(LABEL == this.CLASSES[i]){
				return i;
			}
		}
		return -1;
	}

	// getting the amount of samples per class
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

	public Sample getSample(final int INDEX){
		return this.samples[INDEX];
	}

	public Sample[] getDataSet(){
		return this.samples.clone();
	}
}
