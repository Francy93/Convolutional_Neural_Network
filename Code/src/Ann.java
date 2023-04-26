import java.io.FileNotFoundException;
import lib.Util.AnsiColours;

public class Ann{
	private static			DataSet dataTrain;								// dataset used to perform the training		
	private static			DataSet dataValid;								// detaset used to perform the validation
	public static 			Sample[] missclassified = new Sample[0];		// missclassified samples

    private static final	String	TRAINING_FILE	= "cw2DataSet1.csv";	// file name of the training dataset
	private static final	String	VALIDATE_FILE	= "cw2DataSet2.csv";	// file name of the validation dataset
	private static final	double	NOISE			= 100;					// initial noise
	private static final	int		BATCH_SIZE		= 8;					// number of samples processed before updating the weights
	public	static final	int		EPOCHS			= 100;					// number of dataset cycles
	private static final	double	LEARNING_RATE	= 0.0001;				// learning rate suggested is about 0.001
	



	// Model definition
	private static final Model MODEL = Model.Sequential(
		// starting the convolutional layers
		Layer.Conv2D(16	,	1, 1,	Layer.Activation.MISH),		// convolutional layer of 8  filters with a kernal of 1X1
		Layer.Conv2D(23	,	2, 2,	Layer.Activation.MISH),		// convolutional layer of 16 filters with a kernal of 1X1
		Layer.Conv2D(64	,	3, 3,	Layer.Activation.MISH),		// convolutional layer of 32 filters with a kernal of 2X2
		Layer.Conv2D(128,	4, 4,	Layer.Activation.MISH),		// convolutional layer of 32 filters with a kernal of 2X2
		/* Layer.Conv2D(128,	3, 3,	Layer.Activation.MISH),		// convolutional layer of 64 filters with a kernal of 3X3
		Layer.Conv2D(254,	3, 3,	Layer.Activation.MISH), */		// convolutional layer of 64 filters with a kernal of 3X3
		// starting the fully connected layers
		//Layer.Dense(128	,			Layer.Activation.MISH),		// dense layer of 128 nodes
		Layer.Dense(10	,			Layer.Activation.SOFTMAX)	// output layer of 10 classifications
    );




	// Loading the dataset and building the model
	public static void loadAndBuild() throws FileNotFoundException {
		try{ // loading the datasets
			dataTrain = new DataSet(TRAINING_FILE, ",");	// loading the dataset 1
			dataValid = new DataSet(VALIDATE_FILE, ",");	// loading the dataset 2
		}catch(Exception e){ throw new FileNotFoundException(); }

		// normalising the datasets
		dataTrain.normalize();								// normalising the training dataset
		dataValid.normalize();								// normalising the validation dataset

		// getting the shape of the training dataset
		final int INPUT_SHAPE_Y	=	dataTrain.getSample(0).getFeature2D().length;		// getting the y shape of the training dataset
		final int INPUT_SHAPE_X	=	dataTrain.getSample(0).getFeature2D()[0].length;	// getting the x shape of the training dataset

		// initialising the model
		MODEL.buildStructure(
			INPUT_SHAPE_Y,				// y shape of the input
			INPUT_SHAPE_X,				// x shape of the input
			1,							// number of channels
			Model.Optimizer.ADAM,		// optimizer
			Model.Loss.CROSS_ENTROPY	// loss function
		);
	}


	// running training and testing
	public static void trainAndTest(final int ITER){
		final String[] TITLES	= new String[]{"Accuracy", "Precision", "Recall", "F1Score"};
		double[] metrics		= new double[]{MODEL.getAccuracy(), MODEL.getPrecision(), MODEL.getRecall(), MODEL.getF1Score()};
		final AnsiColours COLOR = new AnsiColours();		// used to colour the output
		final DataSet ORIGINAL	= dataTrain.clone();		// original training dataset
		double bestAccuracy		= MODEL.getAccuracy();		// highest accuracy used to determine if the model is overfitting

		double trainCompare		= 0, trainError	= 0, trainAccuracy = 0;			// training loss
		double validCompare		= 0, validError	= 0, validAccuracy = 0;			// testing loss
		double validPrevAcc		= 0, noise		= ITER > 1? 0: NOISE/100d;		// noise

		for(int epoch = 1+(ITER-1)*EPOCHS; epoch <= EPOCHS*ITER; epoch++){		// looping through the epochs
			String message	= "";												// validation message
			
			if(NOISE > 0){
				dataTrain 	= ORIGINAL.clone();									// resetting the training dataset
				message		= adversarial(trainCompare>0 && validCompare<0? validPrevAcc - MODEL.getAccuracy(): 0);			// augmentation
				noise		= message == ""? fullNoise(trainAccuracy - validAccuracy, (1-trainError) - (1-validError), noise): noise;	// altering the training dataset with noise
				message		= message == "" && noise > 0? " noisiness (" + lib.Util.round(noise*100d, 2) + "%)": message;	// setting the message
				validPrevAcc= MODEL.getAccuracy();								// storing the current accuracy
			}								
			
			System.out.println(COLOR.colourText("\n\nEPOCH " +epoch+ message + "\n", "magenta"));	// printing the epoch number

			// trainig the model
			System.out.println(COLOR.colourText(" Training ...","blue"));		// training message
			MODEL.train(dataTrain, BATCH_SIZE, 1, LEARNING_RATE);				// performing the training
			trainCompare	= trainError - MODEL.getError();					// check confidence improvements
			trainError		= MODEL.getError();									// storing the current error rate
			trainAccuracy	= MODEL.getAccuracy();			// storing the current accuracy
			printMetrics(MODEL);												// printing the metrics
			
			// validate the model
			System.out.println(COLOR.colourText(" Validating ...","yellow"));	// validation message
			MODEL.validate(dataValid);											// performing the validation
			validCompare	= validError - MODEL.getError();					// check confidence improvements
			validError		= MODEL.getError();									// storing the current error rate
			validAccuracy	= MODEL.getAccuracy();			// storing the current accuracy
			printMetrics(MODEL);												// printing the metrics																	

			
			if(MODEL.getAccuracy() > bestAccuracy){				// determining if this epoch has the best accruacy
				missclassified	= getMissclassified(dataValid);	// storing the missclassified samples if any improvement
				metrics			= new double[]{MODEL.getAccuracy(), MODEL.getPrecision(), MODEL.getRecall(), MODEL.getF1Score()};
			}
			bestAccuracy		= Math.max(MODEL.getAccuracy(), bestAccuracy);						// storing the highest accuracy
		}
		System.out.println(COLOR.colourText("\nHighest Accuracy: "+ bestAccuracy*100d,"cyan"));	// printing the highest accuracy
		printScores(TITLES, metrics);
	}

	/**
	 * Full noise data augmentation
	 * @param A_FIT	accuracy
	 * @param E_FIT	error
	 * @param noise	noise
	 * @return noise ammount
	 */
	private static double fullNoise(final double A_FIT, final double E_FIT, double noise){
		final double MEAN = (A_FIT + E_FIT)/2d;													// mean of the accuracy and error
		final double OVERFITTING = (MEAN * Math.abs(A_FIT - E_FIT)) + Math.min(A_FIT, E_FIT);	// overfitting ammount
		noise = Math.min(Math.max(noise + OVERFITTING, 0), 1);									// prevents noise to be negative
		if(noise > 0) dataTrain.setDataSet(dataTrain.adversarialSampling(1, noise));			// replacing the training dataset with noise
		return noise;
	}
	 /**
	  * Adversarial data augmentation
	  * @param OVERFITTING
	  * @return message
	  */
	private static String adversarial(final double OVERFITTING){
		if(OVERFITTING < 0.0001) return "";						// if the model is overfitting
		final double noise = Math.min(OVERFITTING*100d, 0.4);	// noise ammount
		dataTrain.adversarialSampling(1,  noise);				// augmenting the training dataset with noise
		return " FIX (" +lib.Util.round(noise, 2) + "%)";		// message	
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

		return MISSCLASSIFIED;									// storing the missclassified samples
	}
	
	
	// printing the metrics
	private static void printScores(final String[] TITLES, final double[] METRICS){
		final lib.Util.AnsiColours COLOURS = new AnsiColours();							// used to colour the output
		
		final float		ONE_THIRD 	= 1f/3f, 	TWO_THIRDS 	= 1f/1.5f;					// used to determine the output colour
		final String	RED 		= "red",	YELLOW 		= "yellow", GREEN = "green";// colours for the output
		final int		DATA		= Math.min(TITLES.length, METRICS.length);			// used to determine the number of metrics to print

		for(int i = 0; i < DATA; i++){
			final String COLOUR	= METRICS[i] <= ONE_THIRD || Double.isNaN(METRICS[i])? RED: METRICS[i] <= TWO_THIRDS? YELLOW: GREEN;
			System.out.println(TITLES[i] +" :\t"+ COLOURS.colourText(lib.Util.round(METRICS[i]*100d, 2) +"%", COLOUR));
		}
		System.out.println();
	}

	// printing the metrics
	private static void printMetrics(final Model MODEL){ printMetrics(MODEL.getAccuracy(), MODEL.getError()); }
	private static void printMetrics(final double ACCURACY, final double LOSS){
		final lib.Util.AnsiColours COLOURS = new AnsiColours();							// used to colour the output
		final float	 ONE_THIRD 	= 1f/3f, 	TWO_THIRDS 	= 1f/1.5f;						// used to determine the output colour
		final String RED 		= "red",	YELLOW 		= "yellow", GREEN = "green";	// colours for the output

		final String A_COLOUR	= ACCURACY	<=	ONE_THIRD	|| Double.isNaN(ACCURACY)	? RED: ACCURACY	<= TWO_THIRDS?	YELLOW: GREEN;
		final String L_COLOUR	= LOSS 		>=	TWO_THIRDS	|| Double.isNaN(LOSS)		? RED: LOSS		>= ONE_THIRD?	YELLOW: GREEN;
		System.out.println("  Accuracy:\t" + COLOURS.colourText(lib.Util.round(ACCURACY*100d, 3)+ " %", A_COLOUR));
		System.out.println("  Loss:    \t" + COLOURS.colourText(lib.Util.round(LOSS*100d, 	3)	+ " %", L_COLOUR));
	}


	// printing the misclassified samples
	public static void printMisclassified(){
		for(final Sample SAMPLE: missclassified){
			SAMPLE.print2D(dataValid.getMax(), 23, 1);
			System.out.println("Sample label: " + SAMPLE.getLabel() + "\tPrediction label: " + SAMPLE.getPred() + "\n\n");
		}
	}	

}
