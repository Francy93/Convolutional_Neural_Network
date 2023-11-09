import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import lib.Util;

public class Augment {

	private	final DataSet	DATASET;	// the data set to be augmented
	public	final Getter	GET;		// getter object
	public	final Setter	SET;		// setter object
	public	final Adder		ADD;		// adder object

	/**
	 * constructor
	 * @param DATASET the data set to be augmented
	 */
	public Augment(final DataSet DATASET){ 
		this.DATASET = DATASET; 
		GET = new Getter(this);
		SET = new Setter(this);
		ADD = new Adder(this);
	}

		
	/**
	 * Adversarial sampling augmentation
	 * @param AMOUNT amount of adversarial samples to be created
	 * @param EPSILON the amount of noise to be added to the sample (from 0 to 1)
	 */
	private Sample[] adversarial(final int AMOUNT, final double EPSILON){

		final ArrayList<Sample> ADVERSARIAL = new ArrayList<>(Arrays.stream(this.DATASET.getSamples()).parallel()
			.flatMap(sample -> IntStream.range(0, AMOUNT)					// creating a stream of indexes
				.mapToObj(i -> {											// creating a stream of samples
					final Sample SAMPLE = new Sample(Arrays.stream(sample.getFeature1D())
						.map(TOKEN -> {										// creating a stream of features
							double range = EPSILON * this.DATASET.getMax();	// calculating the range
							range = Util.rangeRandom(-range, range);		// getting a random number in the range
							if(Math.random() < EPSILON) return range * 10d;	// adding noise to the sample (salt-and-pepper noise)
							else return Math.min(Math.max(TOKEN+range, this.DATASET.getMin()), this.DATASET.getMax()); // additive white gaussian noise
						}).toArray(),										// converting the stream to an array
						sample.getLabel()									// setting the label
					);
					SAMPLE.setOneHot(sample.getOneHot().clone());			// setting the one-hot array
					return SAMPLE;											// returning the sample
				})
			).toList()														// converting the stream to a list
		);

		return ADVERSARIAL.toArray(Sample[]::new);							// returning the adversarial samples
	}


	/**
	 * Negative sampling augmentation
	 * @return the negative samples
	 */
	private Sample[] negative(){
		return Arrays.stream(this.DATASET.getSamples()).parallel()	// creating a stream of samples
			.map(sample -> {										// mapping the samples
				final Sample SAMPLE = new Sample(Arrays.stream(sample.getFeature1D())
					.map(TOKEN -> this.DATASET.getMax() - TOKEN + this.DATASET.getMin())
					.toArray(),										// converting the stream to an array
					sample.getLabel());								// setting the label
				SAMPLE.setOneHot(sample.getOneHot().clone());		// setting the one-hot array
				return SAMPLE;										// returning the sample
			})
			.toArray(Sample[]::new);								// converting the stream to an array
	}



	//................................. GETTERS .................................

	public class Getter {

		private final Augment SUPER;	// the augment object
			
		/**
		* getter constructor
		*/
		public Getter(final Augment A){ this.SUPER = A; }


		/**
		 * Adversarial sampling augmentation
		 * @param AMOUNT amount of adversarial samples to be created
		 * @param EPSILON the amount of noise to be added to the sample (from 0 to 1)
		 * @return the adversarial samples
		 */
		public Sample[] adversarial(){ return this.adversarial(1); }
		public Sample[] adversarial(final int AMOUNT){ return this.adversarial(AMOUNT, 1d/3d); }
		public Sample[] adversarial(final int AMOUNT, final double EPSILON){ return SUPER.adversarial(AMOUNT, EPSILON); }


		/**
		 * Getting negative samples
		 * @return the negative samples
		 */
		public Sample[] negative(){ return SUPER.negative(); }
	}



	//................................. SETTERS .................................

	public class Setter {
			
		private final Augment SUPER;	// the augment object
			
		/**
		* setter constructor
		*/
		public Setter(final Augment A){ this.SUPER = A; }


		/**
		 * Adversarial sampling augmentation
		 * @param AMOUNT amount of adversarial samples to be created
		 * @param EPSILON the amount of noise to be added to the sample (from 0 to 1)
		 */
		public void adversarial(){ this.adversarial(1); }
		public void adversarial(final int AMOUNT){ this.adversarial(AMOUNT, 1d/3d); }
		public void adversarial(final int AMOUNT, final double EPSILON){
			DATASET.setDataSet(SUPER.adversarial(AMOUNT, EPSILON));			// converting the array list to an array   
		}
		

		/**
		 * Setting negative samples
		 */
		public void negative(){  DATASET.setDataSet(SUPER.negative()); }
	}



	//................................. ADDERS ..................................

	public class Adder {
			
		private final Augment SUPER;	// the augment object
			
		/**
		* adder constructor
		*/
		public Adder(final Augment A){ this.SUPER = A; }

		/**
		 * Joins two arrays of samples
		 * @param SAMPLES1 the first array of samples
		 * @param SAMPLES2 the second array of samples
		 * @return the joint array
		 */
		private Sample[] join(final Sample[] SAMPLES1, final Sample[] SAMPLES2){
			final Sample[] JOINT = new Sample[SAMPLES1.length + SAMPLES2.length];	// creating the joint array
			System.arraycopy(SAMPLES1, 0, JOINT, 0, SAMPLES1.length);				// copying the original samples to the joint array
			System.arraycopy(SAMPLES2, 0, JOINT, SAMPLES1.length, SAMPLES2.length);	// copying the adversarial samples to the joint array
			return JOINT;
		}


		/**
		 * Adversarial sampling augmentation
		 * @param AMOUNT amount of adversarial samples to be created
		 * @param EPSILON the amount of noise to be added to the sample (from 0 to 1)
		 */
		public void adversarial(){ this.adversarial(1); }
		public void adversarial(final int AMOUNT){ this.adversarial(AMOUNT, 1d/3d); }
		public void adversarial(final int AMOUNT, final double EPSILON){
			DATASET.setDataSet(this.join(DATASET.getSamples(), SUPER.adversarial(AMOUNT, EPSILON)));	// converting the array list to an array
		}


		/**
		 * Adding negative samples
		 */
		public void negative(){ SUPER.negative(); }
	}


}