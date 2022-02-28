import java.io.BufferedReader;
//import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import lib.Util;

public class DataSet {

    private final String	FILE_NAME;	// name of the file to be read
    private final double[]	CLASSES;	// number of possible classifications
    private final Sample[]	SAMPLES;	// samples collection
	private final String	DELIMITER;	// string file delimiter

	/**
	 * 
	 * @param F_N File name
	 * @param D Data Delimiter
	 * @throws FileNotFoundException
	 */
    public DataSet(final String F_N, final String D) throws FileNotFoundException{
        FILE_NAME = F_N;
		DELIMITER = D;

        try {  SAMPLES	= fileReader();  }
		catch(IOException e) { throw new FileNotFoundException(); }

		CLASSES = labelClasses();
		classesToSamples();
    }


    /**
	 * Txt file reader
	 * @return array of samples
	 */
	private Sample[] fileReader() throws FileNotFoundException{
		ArrayList<Sample> samples = new ArrayList<>();

        try{
			BufferedReader scann = new BufferedReader(new FileReader(this.FILE_NAME));

			for(String line = ""; (line = scann.readLine()) != null; line.trim()){
				if(!line.matches("^(\\d|[,])*$")) continue;

				try{ samples.add(new Sample(line, this.DELIMITER)); }
				catch(Exception e) { }
			}
	
			scann.close();
			if(samples.size() < 1) throw new FileNotFoundException();
        }catch(IOException e){ throw new FileNotFoundException(); }
        
		return samples.toArray(new Sample[0]);
    }

	/**
	 * geting lable classes
	 * @return label classes
	 */
	private double[] labelClasses(){
		final HashSet<Double> CLASSES_SET = new HashSet<>();
		for(final Sample SAMPLE: this.SAMPLES) CLASSES_SET.add(SAMPLE.getLabel());

		double[] classesArray = new double[CLASSES_SET.size()];
		int iterator = 0;
		for(final double CLASS: CLASSES_SET) classesArray[iterator++] = CLASS;

		Arrays.sort(classesArray);
		return classesArray;
	}

	private void classesToSamples(){
		for(final Sample SAMPLE: this.SAMPLES){
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




	//  ..................setter methods .................


	public void shuffle(){
		final int LENGTH = this.SAMPLES.length;

		for (int i = 0; i < LENGTH; i++) {
			final int INDEX		= (int) Util.rangeRandom(0, LENGTH-1);

			Sample temp			= this.SAMPLES[i];
			this.SAMPLES[i]		= this.SAMPLES[INDEX];
			this.SAMPLES[INDEX] = temp;
		}
	}




	// ..................getters methods .................

	public double[] getClasses(){
		return CLASSES;
	}

	public int classesAmount(){
		return CLASSES.length;
	}
	
	public Sample[] getSamples(){
		return SAMPLES;
	}

	public int getSize(){
		return SAMPLES.length;
	}

	public Sample getSample(int index){
		return SAMPLES[index];
	}
}
