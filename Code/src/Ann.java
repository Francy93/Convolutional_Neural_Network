import java.io.FileNotFoundException;
import lib.Util.AnsiColours;

public class Ann{
	public static 			Sample[] missclassified = new Sample[0];		// missclassified samples
	private static			DataSet dataTrain;								//	dataset used to perform the training		
	private static			DataSet dataValid;								//	detaset used to perform the validation

    private static final	String	TRAINING_FILE	= "cw2DataSet1.csv";	//	file name of the training dataset
	private static final	String	VALIDATE_FILE	= "cw2DataSet2.csv";	//	file name of the validation dataset
	private static final	double	NOISE_STEP		= 0;					// 	noise step used to determine if the model is overfitting
	private static final	int		BATCH_SIZE		= 8;					//	number of samples processed before updating the weights
	public	static final	int		EPOCHS			= 100;					//	number of dataset cycles
	private static final	double	LEARNING_RATE	= 0.0001;				//	learning rate suggested is about 0.001
	



	// Model definition
	private static final Model MODEL = Model.Sequential(
		// starting the convolutional layers
		Layer.Conv2D(8	,	1, 1,	Layer.Activation.MISH),
		Layer.Conv2D(32	,	2, 2,	Layer.Activation.MISH),		// convolutional layer of 32 filters with a kernal of 2X2
		Layer.Conv2D(64	,	2, 2,	Layer.Activation.MISH),		// convolutional layer of 64 filters with a kernal of 2X2
		Layer.Conv2D(128,	3, 3,	Layer.Activation.MISH),		// convolutional layer of 128 filters with a kernal of 3X3
		Layer.Conv2D(256,	3, 3,	Layer.Activation.MISH),		// convolutional layer of 256 filters with a kernal of 3X3
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
	public static void trainAndTest(){
		final AnsiColours COLOR = new AnsiColours();	// used to colour the output
		final DataSet ORIGINAL	= dataTrain.clone();	// original training dataset
		double bestAccuracy		= MODEL.getAccuracy();	// highest accuracy used to determine if the model is overfitting
		double overfitting		= 0;					// used to determine if the model is overfitting
		double trainCompare		= 0, trainError = 0;
		double validCompare		= 0, validError	= 0;

		for(int epoch = 1; epoch <= EPOCHS; epoch++){											// looping through the epochs
			String message	= "Validating epoch ";												// validation message
			
			dataTrain 		= ORIGINAL.clone();													// resetting the training dataset
			message			= fullNoise(overfitting, NOISE_STEP, message);						// performing the noise augmentation
			message			= adversarial(overfitting, message) + epoch;						// performing the adversarial augmentation
			
			// trainig the model
			System.out.println("Training epoch "+epoch+" ...");									// training message
			MODEL.train(dataTrain, BATCH_SIZE, 1, LEARNING_RATE);								// performing the training
			trainCompare	= trainError - MODEL.getError();									// check confidence improvements
			trainError		= MODEL.getError();													// storing the current error rate
			System.out.println("Accuracy:\t"+MODEL.getAccuracy()+"\nLoss:\t"+MODEL.getError());	// printing the metrics
			
			// validate the model
			System.out.println(COLOR.colourText("\r\n"+message+" ...","yellow"));				// validation message
			MODEL.validate(dataValid);															// performing the validation
			validCompare	= validError - MODEL.getError();									// check confidence improvements
			validError		= MODEL.getError();													// storing the current error rate
			printMetrics();																		// printing the metrics

			overfitting		= trainCompare>0 && validCompare<0? bestAccuracy - MODEL.getAccuracy(): 0;// used to determine if the model is overfitting
			
			if(MODEL.getAccuracy() > bestAccuracy) getMissclassified(dataValid);				// storing the missclassified samples if any improvement
			bestAccuracy	= Math.max(MODEL.getAccuracy(), bestAccuracy);						// storing the highest accuracy
		}
		System.out.println(COLOR.colourText("\nHighest Accuracy: "+ bestAccuracy*100,"cyan"));	// printing the highest accuracy
	}

	/**
	 * Full noise data augmentation
	 * @param OVERFITTING
	 * @param MESSAGE
	 * @return message
	 */
	private static String fullNoise(final double OVERFITTING, final double NOISE_STEP, final String MESSAGE){
		if(NOISE_STEP == 0) return MESSAGE;
		else if (MODEL.getAccuracy() < 0.95){

			final double HALF_NS	= NOISE_STEP/2d;	// half of the noise step
			double noise			= 5;				// noise used to augment the training dataset

			if(OVERFITTING>0 && noise>2) noise -= noise==(int)noise? NOISE_STEP: HALF_NS; 	// reducing the noise if the model is overfitting
			else if(OVERFITTING+5 < 0 && noise+HALF_NS < 10) noise += HALF_NS;				// increasing the noise if the model is underfitting

			dataTrain.setDataSet(dataTrain.adversarialSampling(1, noise));					// replacing the training dataset with noise
			return "Validating noise (" + lib.Util.round(dataTrain.normalization(noise),2)*100 + "%) at epoch: ";	// validation message
		}
		return MESSAGE;
	}
	 /**
	  * Adversarial data augmentation
	  * @param OVERFITTING
	  * @param MESSAGE
	  * @return message
	  */
	private static String adversarial(final double OVERFITTING, final String MESSAGE){
		if((OVERFITTING >= 0.01) || (OVERFITTING <= 0.004 && OVERFITTING >= 0.001)){		// if the model is overfitting
			dataTrain.adversarialSampling(1, OVERFITTING);									// augmenting the training dataset with noise
			return "Validating FIX (" +lib.Util.round(dataTrain.normalization(OVERFITTING),2)*100 + "%) at epoch: ";	// validation message																			// reducing the epochs counter
		}
		return MESSAGE;
	}


	// printing the metrics
	private static void printMetrics(){
		final lib.Util.AnsiColours COLOURS = new AnsiColours();									// used to colour the output
		
		// setting the colours according to the metrics
		final float	 ONE_THIRD 			= 1f/3f, 	TWO_THIRDS 	= 1f/1.5f;						// used to determine the output colour
		final String RED 				= "red",	YELLOW 		= "yellow", GREEN = "green";	// colours for the output
		final String ACCURACY_COLOUR	= MODEL.getAccuracy()	<= ONE_THIRD? RED: MODEL.getAccuracy()	<= TWO_THIRDS? YELLOW: GREEN;
		final String PRECISION_COLOUR	= MODEL.getPrecision()	<= ONE_THIRD? RED: MODEL.getPrecision()	<= TWO_THIRDS? YELLOW: GREEN;
		final String RECALL_COLOUR		= MODEL.getRecall()		<= ONE_THIRD? RED: MODEL.getRecall()	<= TWO_THIRDS? YELLOW: GREEN;
		final String F1_COLOUR			= MODEL.getF1Score()	<= ONE_THIRD? RED: MODEL.getF1Score()	<= TWO_THIRDS? YELLOW: GREEN;
		
		// printing the metrics
		System.out.println("Accuracy:\t"	+ COLOURS.colourText(lib.Util.round(MODEL.getAccuracy()		*100d, 2)	+ " %"	, ACCURACY_COLOUR	));
		System.out.println("Precision:\t"	+ COLOURS.colourText(lib.Util.round(MODEL.getPrecision()	*100d, 2)	+ " %"	, PRECISION_COLOUR	));
		System.out.println("Recall:\t\t"	+ COLOURS.colourText(lib.Util.round(MODEL.getRecall()		*100d, 2)	+ " %"	, RECALL_COLOUR		));
		System.out.println("F1Score:\t"		+ COLOURS.colourText(lib.Util.round(MODEL.getF1Score()		*100d, 2)	+ " %\n", F1_COLOUR			));
	}


	// storing the missclassified samples
	private static void getMissclassified(final DataSet DATASET){
		final Sample[] SAMPLES = new Sample[DATASET.size()];				// used to store the missclassified samples
		
		int missed = 0;
		for(final Sample SAMPLE: DATASET.getDataSet()){						// cycling through the samples
			if(!SAMPLE.isPredCorrect()) SAMPLES[missed++] = SAMPLE;			// storing the missclassified samples
		}
		
		final Sample[] MISSCLASSIFIED = new Sample[missed];					// resizing the array
		for(int i=0; i<missed; i++) MISSCLASSIFIED[i] = SAMPLES[i].clone();	// copying the missclassified samples

		missclassified = MISSCLASSIFIED;									// storing the missclassified samples
	}


	// printing the misclassified samples
	public static void printMisclassified(){
		for(final Sample SAMPLE: missclassified){
			SAMPLE.print2D(dataValid.getMax(), 23, 1);
			System.out.println("Sample label: " + SAMPLE.getLabel() + "\tPrediction label: " + SAMPLE.getPred() + "\n\n");
		}
	}	

}
