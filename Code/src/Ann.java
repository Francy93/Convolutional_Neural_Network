import java.io.FileNotFoundException;
import lib.Util.AnsiColours;

public class Ann{
	private static			DataSet dataTrain;								// dataset used to perform the training		
	private static			DataSet dataValid;								// detaset used to perform the validation
	public	static 			Sample[] missclassified = new Sample[0];		// missclassified samples

	private static final	String	TRAINING_FILE	= "cw2DataSet1.csv";	// file name of the training dataset
	private static final	String	VALIDATE_FILE	= "cw2DataSet2.csv";	// file name of the validation dataset
	private static 			double	noise			= 60;					// dynamic noise (as a regularizator)
	private static final	int		BATCH_SIZE		= 8;					// number of samples processed before updating the weights
	public	static final	int		EPOCHS			= 100;					// number of dataset cycles
	private static final	double	LEARNING_RATE	= 0.0001;				// learning rate suggested is about 0.001
	



	// Model definition
	private static final Model MODEL = Model.Sequential(
		// starting the convolutional layers
		Layer.Conv2D(8	,	1, 1,	Layer.Activation.MISH),		// convolutional layer of 8   filters with a kernal of 1X1
		Layer.Conv2D(16	,	1, 1,	Layer.Activation.MISH),		// convolutional layer of 16  filters with a kernal of 1X1
		Layer.Conv2D(32	,	2, 2,	Layer.Activation.MISH),		// convolutional layer of 128 filters with a kernal of 2X2
		Layer.Conv2D(64	,	2, 2,	Layer.Activation.MISH),		// convolutional layer of 254 filters with a kernal of 2X2
		Layer.Conv2D(128,	3, 3,	Layer.Activation.MISH),		// convolutional layer of 254 filters with a kernal of 3X3
		Layer.Conv2D(256,	3, 3,	Layer.Activation.MISH),		// convolutional layer of 254 filters with a kernal of 3X3
		// starting the fully connected layers
		//Layer.Dense(128	,			Layer.Activation.MISH),		// dense layer of 128 nodes
		Layer.Dense(10	,			Layer.Activation.SOFTMAX)	// output layer of 10 classifications
	);




	// Loading the dataset and building the model
	public static void loadAndBuild() throws FileNotFoundException {
		System.out.print(" - Loading the datasets...\r");
		try{ // loading the datasets
			dataTrain = new DataSet(TRAINING_FILE, ",");	// loading the dataset 1
			dataValid = new DataSet(VALIDATE_FILE, ",");	// loading the dataset 2
		}catch(Exception e){ throw new FileNotFoundException(); }
		System.out.println(" - Loaded "+dataTrain.size()+" trainig samples and "+dataValid.size()+" validation samples");

		// normalising the datasets
		dataTrain.normalize();								// normalising the training dataset
		dataValid.normalize();								// normalising the validation dataset

		// getting the shape of the training dataset
		final int INPUT_SHAPE_Y	=	dataTrain.getSample(0).getFeature2D().length;		// getting the y shape of the training dataset
		final int INPUT_SHAPE_X	=	dataTrain.getSample(0).getFeature2D()[0].length;	// getting the x shape of the training dataset

		// initialising the model
		System.out.print(" - Building the model...\r");
		MODEL.buildStructure(
			INPUT_SHAPE_Y,				// y shape of the input
			INPUT_SHAPE_X,				// x shape of the input
			1,							// number of channels
			Model.Optimizer.ADAM,		// optimizer
			Model.Loss.CROSS_ENTROPY	// loss function
		);
		System.out.println(" - Built "+(MODEL.getModelDepth()+1)+" layers, "+MODEL.getNeuronsAmount()+" neurons and "+MODEL.getParametersAmount()+" parameters\n\n");
	}


	// running training and testing
	public static void trainAndTest(final int ITER){
		final Fitness	FITNESS		= noise > 0? new Ann.Noisify(MODEL, noise): new Ann.Smooth(MODEL);	// used to determine the noise
		final String[]	TITLES		= new String[]{"Accuracy", "Precision", "Recall", "F1Score"};		// titles of the metrics
		double[]		metrics		= new double[]{MODEL.getAccuracy(), MODEL.getPrecision(), MODEL.getRecall(), MODEL.getF1Score()};
		double			bestAccuracy= MODEL.getAccuracy();												// highest accuracy

		for(int epoch = 1+(ITER-1)*EPOCHS; epoch <= EPOCHS*ITER; epoch++){		// looping through the epochs
			FITNESS.printTitle(epoch);											// printing the epoch number
			FITNESS.train(dataTrain, BATCH_SIZE, 1, LEARNING_RATE, true);		// performing the training
			FITNESS.validate(dataValid, true);									// performing the validation

			if(MODEL.getAccuracy() > bestAccuracy){								// determining if this epoch has the best accruacy
				missclassified	= getMissclassified(dataValid);					// storing the missclassified samples if any improvement
				metrics			= new double[]{MODEL.getAccuracy(), MODEL.getPrecision(), MODEL.getRecall(), MODEL.getF1Score()};
				bestAccuracy	= MODEL.getAccuracy();							// storing the highest accuracy
			}
		}

		noise = FITNESS instanceof Noisify? FITNESS.getNoise() == 0? 0.001: FITNESS.getNoise(): noise;	// grantee next loop noise is made
		printScores(TITLES, metrics, bestAccuracy);														// printing the metrics
	}


	// storing the missclassified samples
	private static Sample[] getMissclassified(final DataSet DATASET){
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
	private static void printScores(final String[] TITLES, final double[] METRICS, final double BEST_ACCURACY){
		final lib.Util.AnsiColours COLORS = new AnsiColours();										// used to colour the output
		final float		ONE_THIRD 	= 1f/3f, 	TWO_THIRDS 	= 1f/1.5f;								// used to determine the output colour
		final String	RED 		= "red",	YELLOW 		= "yellow", GREEN = "green";			// colours for the output
		final int		DATA		= Math.min(TITLES.length, METRICS.length);						// number of metrics to print
		
		System.out.println(COLORS.colourText("\nHighest Accuracy: "+ BEST_ACCURACY*100d,"cyan"));	// printing the highest accuracy
		for(int i = 0; i < DATA; i++){
			final String COLOUR	= METRICS[i] <= ONE_THIRD || Double.isNaN(METRICS[i])? RED: METRICS[i] <= TWO_THIRDS? YELLOW: GREEN;
			System.out.println(TITLES[i] +" :\t"+ COLORS.colourText(lib.Util.round(METRICS[i]*100d, 2) +"%", COLOUR));
		}
		System.out.println();
	}

	// printing the misclassified samples
	public static void printMisclassified(){
		for(final Sample SAMPLE: missclassified){
			SAMPLE.print2D(dataValid.getMax(), 23, 1);
			System.out.println("Sample label: " + SAMPLE.getLabel() + "\tPrediction label: " + SAMPLE.getPred() + "\n\n");
		}
	}
	


	// Running the training and validation
	private static abstract class Fitness{
		protected	final 	lib.Util.AnsiColours COLOR = new AnsiColours();								// used to colour the output
		private 	final	float	ONE_THIRD 	= 1f/3f, 	TWO_THIRDS 	= 1f/1.5f;						// used to determine the output colour
		private		final 	String	RED 		= "red",	YELLOW 		= "yellow", GREEN = "green";	// colours for the output
		private		final 	String	U_RED		= this.COLOR.colourText("/\\", this.RED), U_GREEN = this.COLOR.colourText("/\\", this.GREEN);
		private		final 	String	D_RED		= this.COLOR.colourText("\\/", this.RED), D_GREEN = this.COLOR.colourText("\\/", this.GREEN);
		protected			double	noise		= 0;													// noise ammount
		protected 			String	message		= "";													// message to be printed
		protected	final 	Model	MODEL;																// used to store the model
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
		}

		public abstract void train(final DataSet DATASET, final int BATCH_SIZE, final int EPOCHS, final double LEARNING_RATE, final boolean PRINT);
		public abstract void validate(final DataSet DATASET, final boolean PRINT);
		
		// printing the metrics
		private void	printMetrics(final double ACCURACY, final double LOSS, final double PREV_ACC, final double PREV_LOSS){
			final String A_ARROW	= ACCURACY	< 	PREV_ACC	? this.D_RED: ACCURACY 	== PREV_ACC	? "--": this.U_GREEN;	// arrow accuracy
			final String L_ARROW	= LOSS		> 	PREV_LOSS	? this.U_RED: LOSS		== PREV_LOSS? "--": this.D_GREEN;	// arrow loss
			
			final String A_COLOUR	= ACCURACY	<=	this.ONE_THIRD	|| Double.isNaN(ACCURACY)	? this.RED: ACCURACY<= this.TWO_THIRDS?	this.YELLOW: this.GREEN;
			final String L_COLOUR	= LOSS 		>=	this.TWO_THIRDS	|| Double.isNaN(LOSS)		? this.RED: LOSS	>= this.ONE_THIRD?	this.YELLOW: this.GREEN;
			System.out.println("  "+A_ARROW+" Accuracy:\t" + this.COLOR.colourText(lib.Util.round(ACCURACY*100d,3)	+ " %", A_COLOUR));
			System.out.println("  "+L_ARROW+" Loss:    \t" + this.COLOR.colourText(lib.Util.round(LOSS*100d, 	3)	+ " %", L_COLOUR));
		}
		
		public void		printTitle(final int EPOCH){
			System.out.println(COLOR.colourText("\n\nEPOCH " +EPOCH+ this.message + "\n", "magenta"));	// printing the epoch number
		}
		public void		printTraining()		{ this.printMetrics(this.trainAcc, this.trainErr, this.prevTrainAcc, this.prevTrainErr); }
		public void		printValidation()	{ this.printMetrics(this.validAcc, this.validErr, this.prevValidAcc, this.prevValidErr); }
		public double	getNoise()			{ return this.noise; }
	}

	private static class Smooth extends Ann.Fitness{
		public Smooth(final Model MODEL){ super(MODEL); }

		@Override
		public void train(final DataSet DATASET, final int BATCH_SIZE, final int EPOCHS, final double LEARNING_RATE, final boolean PRINT){
			if(PRINT)	System.out.println(super.COLOR.colourText(" Training ...","blue"));	// training message

			super.MODEL.train(DATASET, BATCH_SIZE, EPOCHS, LEARNING_RATE);	// performing the training
			super.prevTrainErr	= super.trainErr;							// holding the previous error rate
			super.prevTrainAcc	= super.trainAcc;							// holding the previous accuracy
			super.trainErr		= super.MODEL.getError();					// storing the current error rate
			super.trainAcc		= super.MODEL.getAccuracy();				// storing the current accuracy

			if(PRINT)	super.printTraining();								// printing the metrics
		}
		@Override
		public void	validate(final DataSet DATASET, final boolean PRINT){
			if(PRINT)	System.out.println(super.COLOR.colourText(" Validating ...","yellow"));// validation message

			super.MODEL.validate(DATASET);									// performing the validation
			super.prevValidErr	= super.validErr;							// holding the previous error rate
			super.prevValidAcc	= super.validAcc;							// holding the previous accuracy
			super.validErr		= super.MODEL.getError();					// storing the current error rate
			super.validAcc		= super.MODEL.getAccuracy();				// storing the current accuracy

			if(PRINT)	super.printValidation();							// printing the metrics
		}
	}
	
	private static class Noisify extends Ann.Smooth{
		// current
		private double a_fit, e_fit, t_percent, v_percent, fitting;										// mean fitting
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
			final double ADD_NOISE	= (this.t_percent * (this.fitting + this.overfit)/2d) + ((1-this.v_percent) * this.v_improve);	// noise ammount
			super.noise				= Math.min(Math.max(super.noise + ADD_NOISE, 0), 1);		// prevents noise to be negative or greater than 1
		}

		// updating the parameters
		private void paramUpdate(){
			// current
			this.a_fit		= super.trainAcc		- super.validAcc;			// accuracy fitting
			this.e_fit		= (1-super.trainErr)	- (1-super.validErr);		// confidence fitting
			this.t_percent	= (super.trainAcc		+ (1-super.trainErr))/2d;	// training percentage
			this.v_percent	= (super.validAcc		+ (1-super.validErr))/2d;	// validation percentage
			this.fitting	= (this.a_fit			+ this.e_fit)/2d;			// mean fitting
			// previous
			this.ta_comp	= super.trainAcc		- super.prevTrainAcc;		// training accuracy comparison
			this.te_comp	= super.prevTrainErr	- super.trainErr;			// training error comparison		
			this.va_comp	= super.validAcc		- super.prevValidAcc;		// validation accuracy comparison
			this.ve_comp	= super.prevValidErr	- super.validErr;			// validation error comparison
			this.t_improve	= (this.ta_comp			+ this.te_comp)/2d;			// average training improvement
			this.v_improve	= (this.va_comp			+ this.ve_comp)/2d;			// average validation improvement
			this.overfit	= this.t_improve		- this.v_improve;			// overfitting

			this.isOverfit		= this.va_comp*(-1d) >= 0.0001 && this.te_comp>0 && this.ve_comp<0;
			this.overfitNoise	= Math.min(this.va_comp*(-1d)*100d, 0.4);		// overfitting noise ammount
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
			if		(this.isOverfit	)	dataset.adversarialSampling(1,  this.overfitNoise);				// fixing overfitting
			else if	(super.noise > 0)	dataset.setDataSet(dataset.adversarialSampling(1, super.noise));// full noise data alteration
			return	dataset;																			// no noise
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
