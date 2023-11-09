public class Regularize{

// Running the training and validation
	public static abstract class Fitness{
		protected	final 	lib.Util.AnsiColours COLORS	= new lib.Util.AnsiColours();					// used to colour the output
		private		final	String[] TITLES				= new String[]{"Accuracy", "Precision", "Recall", "F1Score"};	// titles of the metrics
		protected 			Sample[] missclassified		= new Sample[0];								// missclassified samples
		protected			double[] metrics	;														// metrics
		protected			double	bestAccuracy;														// highest accuracy	
		protected			double	maxFeatures	;														// highest value of the features
		private 	final	float	ONE_THIRD 	= 1f/3f, 	TWO_THIRDS 	= 1f/1.5f;						// used to determine the output colour
		private		final 	String	RED 		= "red",	YELLOW 		= "yellow", GREEN = "green";	// colours for the output
		protected	final 	Model	MODEL		;														// used to store the model
		protected 			String	message		= "";													// message to be printed
		protected			double	noise		= 0	;													// noise ammount
		protected			double	prevTrainErr, prevTrainAcc, trainErr, trainAcc;						// training metrics
		protected			double	prevValidErr, prevValidAcc, validErr, validAcc;						// testing metrics

		private Fitness(final Model MODEL){
			this.MODEL = MODEL;	
			
			this.prevTrainErr	= this.MODEL.getError();
			this.prevTrainAcc	= this.MODEL.getAccuracy();
			this.trainErr		= this.MODEL.getError();
			this.trainAcc		= this.MODEL.getAccuracy();

			this.prevValidErr	= this.MODEL.getError();
			this.prevValidAcc	= this.MODEL.getAccuracy();
			this.validErr		= this.MODEL.getError();
			this.validAcc		= this.MODEL.getAccuracy();

			this.bestAccuracy	= this.MODEL.getAccuracy();
			this.metrics		= new double[]{this.MODEL.getAccuracy(), this.MODEL.getPrecision(), this.MODEL.getRecall(), this.MODEL.getF1Score()};
		}
		
		/**
		 * Training the model
		 * @param DATASET
		 * @param BATCH_SIZE
		 * @param EPOCHS
		 * @param LEARNING_RATE
		 * @param PRINT
		 */
		public abstract void train(final DataSet DATASET, final int BATCH_SIZE, final int EPOCHS, final double LEARNING_RATE, final boolean PRINT);
		public abstract void validate(final DataSet DATASET, final boolean PRINT);
		
		/**
		 * Printing the metrics
		 * @param ACCURACY
		 * @param LOSS
		 * @param PREV_ACC
		 * @param PREV_LOSS
		 */
		private void	printMetrics(final double ACCURACY, final double LOSS, final double PREV_ACC, final double PREV_LOSS){
			final String U_RED		= this.COLORS.colourText("/\\", this.RED), U_GREEN = this.COLORS.colourText("/\\", this.GREEN);
			final String D_RED		= this.COLORS.colourText("\\/", this.RED), D_GREEN = this.COLORS.colourText("\\/", this.GREEN);

			final String A_ARROW	= ACCURACY	< 	PREV_ACC	? D_RED: ACCURACY 	== PREV_ACC	? "--": U_GREEN;	// arrow accuracy
			final String L_ARROW	= LOSS		> 	PREV_LOSS	? U_RED: LOSS		== PREV_LOSS? "--": D_GREEN;	// arrow loss
			
			final String A_COLOUR	= ACCURACY	<=	this.ONE_THIRD	|| Double.isNaN(ACCURACY)	? this.RED: ACCURACY<= this.TWO_THIRDS?	this.YELLOW: this.GREEN;
			final String L_COLOUR	= LOSS 		>=	this.TWO_THIRDS	|| Double.isNaN(LOSS)		? this.RED: LOSS	>= this.ONE_THIRD?	this.YELLOW: this.GREEN;
			System.out.println("  "	+ A_ARROW +" Accuracy:\t" + this.COLORS.colourText(lib.Util.round(ACCURACY*100d,	3)	+ " %", A_COLOUR));
			System.out.println("  "	+ L_ARROW +" Loss:    \t" + this.COLORS.colourText(lib.Util.round(LOSS*100d,		3)	+ " %", L_COLOUR));
		}

		/**
		 * Storing the missclassified samples
		 * @param DATASET
		 * @return
		 */
		public Sample[] getMissclassified(final DataSet DATASET){
			final Sample[] SAMPLES = new Sample[DATASET.size()];				// used to store the missclassified samples
			
			int missed = 0;
			for(final Sample SAMPLE: DATASET.getDataSet()){						// cycling through the samples
				if(!SAMPLE.isPredCorrect()) SAMPLES[missed++] = SAMPLE;			// storing the missclassified samples
			}
			
			final Sample[] MISSCLASSIFIED = new Sample[missed];					// resizing the array
			for(int i=0; i<missed; i++) MISSCLASSIFIED[i] = SAMPLES[i].clone();	// copying the missclassified samples

			return MISSCLASSIFIED;												// storing the missclassified samples
		}
		
		// printing the metrics
		public void printScores(){
			final int DATA	= Math.min(this.TITLES.length, this.metrics.length);								// number of metrics to print
			
			System.out.println(this.COLORS.colourText("\nHighest Accuracy: "+ this.bestAccuracy*100d,"cyan"));	// printing the highest accuracy
			for(int i = 0; i < DATA; i++){
				final String COLOR	= this.metrics[i] <= this.ONE_THIRD || Double.isNaN(this.metrics[i])? this.RED: this.metrics[i] <= this.TWO_THIRDS? this.YELLOW: GREEN;
				System.out.println(this.TITLES[i] +" :\t"+ COLORS.colourText(lib.Util.round(this.metrics[i]*100d, 2) +"%", COLOR));
			}
			System.out.println();
		}

		// printing the misclassified samples
		public void printMisclassified(){
			for(final Sample SAMPLE: this.missclassified){
				SAMPLE.print2D(this.maxFeatures, 23, 1);
				System.out.println("Sample label: " + SAMPLE.getLabel() + "\tPrediction label: " + SAMPLE.getPred() + "\n\n");
			}
		}
		
		public void		printTitle(final int EPOCH){
			System.out.println(COLORS.colourText("\n\nEPOCH " +EPOCH+ this.message + "\n", "magenta"));	// printing the epoch number
		}
		public void		printTraining()		{ this.printMetrics(this.trainAcc, this.trainErr, this.prevTrainAcc, this.prevTrainErr); }
		public void		printValidation()	{ this.printMetrics(this.validAcc, this.validErr, this.prevValidAcc, this.prevValidErr); }
		public double	getNoise()			{ return this.noise; }
		public double 	getTrainErr()		{ return this.trainErr; }
		public double 	getTrainAcc()		{ return this.trainAcc; }
		public double 	getValidErr()		{ return this.validErr; }
		public double 	getValidAcc()		{ return this.validAcc; }
		public Sample[]	getMissclassified()	{ return this.missclassified; }
	}

	public static class Smooth extends Fitness{
		public Smooth(final Model MODEL){ super(MODEL); }

		@Override
		public void train(final DataSet DATASET, final int BATCH_SIZE, final int EPOCHS, final double LEARNING_RATE, final boolean PRINT){
			if(PRINT)	System.out.println(super.COLORS.colourText(" Training ...","blue"));	// training message

			super.MODEL.train(DATASET, BATCH_SIZE, EPOCHS, LEARNING_RATE);	// performing the training
			super.prevTrainErr	= super.trainErr;							// holding the previous error rate
			super.prevTrainAcc	= super.trainAcc;							// holding the previous accuracy
			super.trainErr		= super.MODEL.getError();					// storing the current error rate
			super.trainAcc		= super.MODEL.getAccuracy();				// storing the current accuracy

			if(PRINT)	super.printTraining();								// printing the metrics
		}
		@Override
		public void	validate(final DataSet DATASET, final boolean PRINT){
			if(PRINT)	System.out.println(super.COLORS.colourText(" Validating ...","yellow"));// validation message

			super.MODEL.validate(DATASET);									// performing the validation
			super.prevValidErr	= super.validErr;							// holding the previous error rate
			super.prevValidAcc	= super.validAcc;							// holding the previous accuracy
			super.validErr		= super.MODEL.getError();					// storing the current error rate
			super.validAcc		= super.MODEL.getAccuracy();				// storing the current accuracy

			if(MODEL.getAccuracy() > super.bestAccuracy){					// determining if this epoch has the best accruacy
				super.missclassified	= super.getMissclassified(DATASET);	// storing the missclassified samples if any improvement
				super.metrics			= new double[]{MODEL.getAccuracy(), MODEL.getPrecision(), MODEL.getRecall(), MODEL.getF1Score()};
				super.bestAccuracy		= super.MODEL.getAccuracy();		// storing the highest accuracy
				super.maxFeatures		= DATASET.getMax();					// storing the highest accuracy
			}

			if(PRINT)	super.printValidation();							// printing the metrics
		}
	}
	
	public static class Noisify extends Smooth{
		// current
		private double a_fit, e_fit, fitting;															// mean fitting
		// previous
		private double ta_comp, te_comp, va_comp, ve_comp, t_improve, v_improve, overfit, overfitNoise;	// comparison
		private boolean isOverfit = false;																// overfitting
		/**
		 * @param MODEL
		 * @param NOISE
		 */
		public Noisify(final Model MODEL, final double NOISE){
			super(MODEL);
			super.noise	=  NOISE>1? NOISE/100d: NOISE;
			this.paramUpdate();
			this.messageUpdate();
		}

		private void noiseUpdate(){
			final double ADD_NOISE	= (this.v_improve * (1d-super.trainAcc)) + (((this.fitting + this.overfit)/2d) * super.trainAcc);	// noise ammount
			super.noise				= Math.min(Math.max(super.noise + ADD_NOISE, 0), 1);		// prevents noise to be negative or greater than 1
		}

		// updating the parameters
		private void paramUpdate(){
			
			// current
			this.a_fit		= super.trainAcc		- super.validAcc;				// accuracy fitting
			this.e_fit		= (super.validErr		- super.trainErr)	/ Math.max(Math.max(Math.abs(super.validErr), Math.abs(super.trainErr)), 1);		// confidence fitting
			this.fitting	= (this.a_fit			+ this.e_fit)		/ 2d;		// mean fitting
			// previous
			this.ta_comp	= super.trainAcc		- super.prevTrainAcc;			// training accuracy comparison
			this.va_comp	= super.validAcc		- super.prevValidAcc;			// validation accuracy comparison
			this.te_comp	= (super.prevTrainErr	- super.trainErr) 	/ Math.max(Math.max(Math.abs(super.prevTrainErr), Math.abs(super.trainErr)), 1);	// training error comparison		
			this.ve_comp	= (super.prevValidErr	- super.validErr)	/ Math.max(Math.max(Math.abs(super.prevValidErr), Math.abs(super.validErr)), 1);	// validation error comparison
			this.t_improve	= (this.ta_comp			+ this.te_comp)		/ 2d;		// average training improvement
			this.v_improve	= (this.va_comp			+ this.ve_comp)		/ 2d;		// average validation improvement
			this.overfit	= this.t_improve		- this.v_improve;				// overfitting

			this.isOverfit		= this.overfit > 0 && this.fitting > 0;				// condition for overfitting
			this.overfitNoise	= Math.min((this.fitting + this.overfit) * 5d, 1);	// overfitting noise ammount
		}

		// updating the message
		private void messageUpdate(){
			if		(this.isOverfit)	super.message = " FIX (" + lib.Util.round(this.overfitNoise*100d,	2) + "%)";	// overfitting
			else if	(super.noise > 0)	super.message = " noisiness (" + lib.Util.round(super.noise*100d,	2) + "%)";	// noise
			else						super.message = " no noise";													// no noise
		}

		/**
		 * Making noise
		 * @param dataset
		 * @return dataset
		 */
		private DataSet makeNoise(DataSet dataset){
			if		(this.isOverfit	)	dataset.AUGMENT.ADD.adversarial(1,	this.overfitNoise);	// fixing overfitting
			else if	(super.noise > 0)	dataset.AUGMENT.SET.adversarial(1,	super.noise);		// full noise data alteration
			return	dataset;																	// no noise
		}

		/**
		 * Training the model
		 * @param DATASET		- dataset
		 * @param BATCH_SIZE	- batch size
		 * @param EPOCHS		- epochs
		 * @param LEARNING_RATE	- learning rate
		 * @param PRINT			- print the training
		 */
		@Override
		public void train(final DataSet DATASET, final int BATCH_SIZE, final int EPOCHS, final double LEARNING_RATE, final boolean PRINT){
			super.train(this.makeNoise(DATASET.clone()), BATCH_SIZE, EPOCHS, LEARNING_RATE, PRINT);
		}
		/**
		 * Validating the model
		 * @param DATASET	- dataset
		 * @param PRINT		- print the validation
		 */
		@Override
		public void validate(final DataSet DATASET, final boolean PRINT){
			super.validate(DATASET, PRINT);	// validating the model
			this.paramUpdate();				// updating the parameters
			this.noiseUpdate();				// updating the noise
			this.messageUpdate();			// updating the message
		}
	}
	
}
