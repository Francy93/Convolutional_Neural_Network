import java.io.FileNotFoundException;
import lib.Util.AnsiColours;

public class Ann{
	private static			DataSet dataTrain;								// dataset used to perform the training		
	private static			DataSet dataValid;								// detaset used to perform the validation
	public static 			Sample[] missclassified = new Sample[0];		// missclassified samples

    private static final	String	TRAINING_FILE	= "cw2DataSet1.csv";	// file name of the training dataset
	private static final	String	VALIDATE_FILE	= "cw2DataSet2.csv";	// file name of the validation dataset
	private static 			double	noise			= 60;					// initial noise
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
		final String[] TITLES	= new String[]{"Accuracy", "Precision", "Recall", "F1Score"};
		double[] metrics		= new double[]{MODEL.getAccuracy(), MODEL.getPrecision(), MODEL.getRecall(), MODEL.getF1Score()};
		final AnsiColours COLOR = new AnsiColours();		// used to colour the output
		final DataSet ORIGINAL	= dataTrain.clone();		// original training dataset
		double bestAccuracy		= MODEL.getAccuracy();		// highest accuracy used to determine if the model is overfitting
		final boolean MAKE_NOISE= noise > 0;				// if the noise should be made

		double trainErrPrev		= MODEL.getError(), trainAccPrev = bestAccuracy, trainErr	= MODEL.getError(), trainAcc = bestAccuracy;	// training loss
		double validErrPrev		= MODEL.getError(), validAccPrev = bestAccuracy, validErr	= MODEL.getError(), validAcc = bestAccuracy;	// testing loss
		noise					= noise>1? noise/100d: noise;						// noise

		for(int epoch = 1+(ITER-1)*EPOCHS; epoch <= EPOCHS*ITER; epoch++){			// looping through the epochs
			String message	= "";													// validation message
			
			if(MAKE_NOISE){
				dataTrain 	= ORIGINAL.clone();										// resetting the training dataset
				// current
				final double A_FIT		= trainAcc - validAcc, E_FIT = (1-trainErr) - (1-validErr);			// accuracy and error fitting
				final double T_PERCENT	= (trainAcc+(1-trainErr))/2d, V_PERCENT	=(validAcc+(1-validErr))/2d;// validation percentage
				final double FITTING	= (A_FIT + E_FIT)/2d;												// mean fitting
				// previous
				final double TA_COMP	= trainAcc - trainAccPrev,	TE_COMP		= trainErrPrev - trainErr;	// training accuracy and error comparison		
				final double VA_COMP	= validAcc - validAccPrev, 	VE_COMP		= validErrPrev - validErr;	// validation accuracy and error comparison
				final double T_IMPROVE	= (TA_COMP + TE_COMP)/2d,	V_IMPROVE	= (VA_COMP + VE_COMP)/2d;	// average improvement
				final double OVERFIT	= T_IMPROVE - V_IMPROVE;											// overfitting

				message		= adversarial(TE_COMP>0 && VE_COMP<0? validAccPrev - validAcc: 0);				// both original and noisy dataset
				noise		= message == ""? fullNoise(T_PERCENT, V_PERCENT, FITTING, V_IMPROVE, OVERFIT, noise): noise;	// make dataset noisy
				message		= message == "" && noise > 0? " noisiness (" + lib.Util.round(noise*100d, 2) + "%)": message;	// setting the message
			}								
			
			System.out.println(COLOR.colourText("\n\nEPOCH " +epoch+ message + "\n", "magenta"));			// printing the epoch number

			// trainig the model
			System.out.println(COLOR.colourText(" Training ...","blue"));		// training message
			MODEL.train(dataTrain, BATCH_SIZE, 1, LEARNING_RATE);				// performing the training
			trainErrPrev	= trainErr;											// holding the previous error rate
			trainAccPrev	= trainAcc;											// holding the previous accuracy
			trainErr		= MODEL.getError();									// storing the current error rate
			trainAcc		= MODEL.getAccuracy();								// storing the current accuracy
			printMetrics(MODEL, trainAccPrev, trainErrPrev);					// printing the metrics
			
			// validate the model
			System.out.println(COLOR.colourText(" Validating ...","yellow"));	// validation message
			MODEL.validate(dataValid);											// performing the validation
			validErrPrev	= validErr;											// holding the previous error rate
			validAccPrev	= validAcc;											// holding the previous accuracy
			validErr		= MODEL.getError();									// storing the current error rate
			validAcc		= MODEL.getAccuracy();								// storing the current accuracy
			printMetrics(MODEL, validAccPrev, validErrPrev);					// printing the metrics

			
			if(MODEL.getAccuracy() > bestAccuracy){								// determining if this epoch has the best accruacy
				missclassified	= getMissclassified(dataValid);					// storing the missclassified samples if any improvement
				metrics			= new double[]{MODEL.getAccuracy(), MODEL.getPrecision(), MODEL.getRecall(), MODEL.getF1Score()};
			}
			bestAccuracy		= Math.max(MODEL.getAccuracy(), bestAccuracy);	// storing the highest accuracy
		}
		noise += MAKE_NOISE && noise==0? 0.001: 0;								// grantee next loop noise is made
		System.out.println(COLOR.colourText("\nHighest Accuracy: "+ bestAccuracy*100d,"cyan"));	// printing the highest accuracy
		printScores(TITLES, metrics);
	}

	/**
	 * Full noise data augmentation
	 * @param T_P			train percentage
	 * @param V_P			validation percentage
	 * @param AE_NOT_FIT	accuracy and error not fitting
	 * @param V_IMPROVE		validation improvement
	 * @param OVERFIT		overfitting
	 * @param noise			current noise
	 * @return				updated noise
	 */
	private static double fullNoise(final double T_P, final double V_P, final double AE_NOT_FIT, final double V_IMPROVE, final double OVERFIT, double noise){
		final double ADD_NOISE	= (T_P * (AE_NOT_FIT + OVERFIT)/2d) + ((1-V_P) * V_IMPROVE);// noise ammount

		noise = Math.min(Math.max(noise + ADD_NOISE, 0), 1);								// prevents noise to be negative or greater than 1
		if(noise > 0) dataTrain.setDataSet(dataTrain.adversarialSampling(1, noise));		// replacing the training dataset with noise
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
		return " FIX (" +lib.Util.round(noise*100d, 2) + "%)";	// message	
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
	private static void printMetrics(final Model MODEL,final double P_A,final double P_L){printMetrics(MODEL.getAccuracy(),MODEL.getError(),P_A,P_L);}
	private static void printMetrics(final double ACCURACY, final double LOSS, final double PREV_ACC, final double PREV_LOSS){
		final lib.Util.AnsiColours COLOURS = new AnsiColours();							// used to colour the output

		final String RED 		= "red",	YELLOW 		= "yellow", GREEN = "green";	// colours for the output
		final String D_RED		= COLOURS.colourText("\\/", RED), D_GREEN = COLOURS.colourText("\\/", GREEN);	// down arrow
		final String U_RED		= COLOURS.colourText("/\\", RED), U_GREEN = COLOURS.colourText("/\\", GREEN);	// up arrow
		final String A_ARROW	= ACCURACY	< PREV_ACC	? D_RED: ACCURACY 	== PREV_ACC	? "--": U_GREEN;		// arrow accuracy
		final String L_ARROW	= LOSS		> PREV_LOSS	? U_RED: LOSS		== PREV_LOSS? "--": D_GREEN;		// arrow loss
		
		final float	 ONE_THIRD 	= 1f/3f, 	TWO_THIRDS 	= 1f/1.5f;						// used to determine the output colour
		final String A_COLOUR	= ACCURACY	<=	ONE_THIRD	|| Double.isNaN(ACCURACY)	? RED: ACCURACY	<= TWO_THIRDS?	YELLOW: GREEN;
		final String L_COLOUR	= LOSS 		>=	TWO_THIRDS	|| Double.isNaN(LOSS)		? RED: LOSS		>= ONE_THIRD?	YELLOW: GREEN;
		System.out.println("  "+A_ARROW+" Accuracy:\t" + COLOURS.colourText(lib.Util.round(ACCURACY*100d,	3)	+ " %", A_COLOUR));
		System.out.println("  "+L_ARROW+" Loss:    \t" + COLOURS.colourText(lib.Util.round(LOSS*100d, 		3)	+ " %", L_COLOUR));
	}


	// printing the misclassified samples
	public static void printMisclassified(){
		for(final Sample SAMPLE: missclassified){
			SAMPLE.print2D(dataValid.getMax(), 23, 1);
			System.out.println("Sample label: " + SAMPLE.getLabel() + "\tPrediction label: " + SAMPLE.getPred() + "\n\n");
		}
	}	

}
