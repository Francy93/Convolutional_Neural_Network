import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import lib.Util;

public class DataSet {

    private final String	FILE_NAME;	// name of the file to be read
    private final int[]		CLASSES;	// number of possible classifications
    private final Sample[]	SAMPLES;	// samples collection
	private final String	DELIMITER;

    public DataSet(final String F_N, final String D) throws FileNotFoundException{
        FILE_NAME = F_N;
		DELIMITER = D;

        try {  SAMPLES	= fileReader();  }
		catch(IOException e) { throw new FileNotFoundException(); }

		CLASSES = labelClasses();
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
				if(!line.matches("^\\s*([+-]?\\d+([.]\\d+)?\\s+){2}([+-]?\\d+([.]\\d+)?)\\s*$")) continue;

				try{ samples.add(new Sample(line, this.DELIMITER)); }
				catch(Exception e) { }
			}
	
			scann.close();
        }catch(IOException e){ throw new FileNotFoundException(); }
        
		return samples.toArray(new Sample[0]);
    }

	/**
	 * geting lable classes
	 * @return label classes
	 */
	private int[] labelClasses(){
		final HashSet<Integer> CLASSES_SET = new HashSet<>();
		for(final Sample SAMPLE: this.SAMPLES) CLASSES_SET.add(SAMPLE.getLabel());

		int[] classesArray = new int[CLASSES_SET.size()];
		int iterator = 0;
		for(final int CLASS: CLASSES_SET) classesArray[iterator++] = CLASS;

		return classesArray;
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

	public int[] getClasses(){
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
