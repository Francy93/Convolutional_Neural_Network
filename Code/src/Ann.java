import java.io.FileNotFoundException;

public class Ann{

    private static final	String	TRAINING_FILE	= "cw2DataSet1.csv";	//	file name of the training dataset
	private static final	String	VALIDATE_FILE	= "cw2DataSet2.csv";	//	file name of the validation dataset
	private static			DataSet dataTrain;								//	dataset used to perform the training		
	private static			DataSet dataValid;								//	detaset used to perform the validation
	private static final	double	LEARNING_RATE	= 0.0001;				//	learning rate suggested is about 0.001
	private static final	int		BATCH_SIZE		= 4;					//	number of samples processed before updating the weights
	public	static final	int		EPOCHS			= 50;					//	number of dataset cycles
	



	// Model definition
	private static final Model MODEL = Model.Sequential(
		// starting the convolutional layers
		Layer.Conv2D(32, 2, 2,	Layer.Activation.MISH),		// convolutional layer of 32 filters with a kernal of 2X2
		Layer.Conv2D(64, 2, 2,	Layer.Activation.MISH),		// convolutional layer of 64 filters with a kernal of 2X2
		// starting the fully connected layers
		Layer.Dense(128,		Layer.Activation.MISH),		// dense layer of 128 nodes
		Layer.Dense(10 ,		Layer.Activation.SOFTMAX)	// output layer of 10 classifications
    );




	// building the model
	public static void setModel() throws FileNotFoundException {
		try{ // loading the datasets
			dataTrain = new DataSet(TRAINING_FILE, ",");	// loading the dataset 1
			dataValid = new DataSet(VALIDATE_FILE, ",");	// loading the dataset 2
		}catch(Exception e){ throw new FileNotFoundException(); }

		// normalising the datasets
		dataTrain.normalize();	// normalising the training dataset
		dataValid.normalize();	// normalising the validation dataset

		// initialising the model
		MODEL.buildStructure(dataTrain, dataValid, Model.Optimizer.ADAM, Model.Loss.CROSS_ENTROPY);
	}

	// running tests and validations
	public static void testModel(){
		MODEL.train(BATCH_SIZE, EPOCHS, LEARNING_RATE);											//	performing the training
		
		final lib.Util.AnsiColours COLOURS = new lib.Util.AnsiColours();						//	used to colour the output
		System.out.println(COLOURS.colourText("\r\nValidating...", "yellow"));					//	validation message

		MODEL.validate();																		//	performing the validation
		final float	 ONE_THIRD 			= 100f/3f, 	TWO_THIRDS 	= 100f/1.5f;					//	used to determine the output colour
		final String RED 				= "red",	YELLOW 		= "yellow", GREEN = "green";	//	colours for the output
		final String ACCURACY_COLOUR	= MODEL.getAccuracy()	<= ONE_THIRD? RED: MODEL.getAccuracy()	<= TWO_THIRDS? YELLOW: GREEN;
		final String PRECISION_COLOUR	= MODEL.getPrecision()	<= ONE_THIRD? RED: MODEL.getPrecision()	<= TWO_THIRDS? YELLOW: GREEN;
		final String RECALL_COLOUR		= MODEL.getRecall()		<= ONE_THIRD? RED: MODEL.getRecall()	<= TWO_THIRDS? YELLOW: GREEN;
		final String F1_COLOUR			= MODEL.getF1Score()	<= ONE_THIRD? RED: MODEL.getF1Score()	<= TWO_THIRDS? YELLOW: GREEN;
		
		System.out.println("Accuracy:\t"	+ COLOURS.colourText(MODEL.getAccuracy()	+ "%"	, ACCURACY_COLOUR	));
		System.out.println("Precision:\t"	+ COLOURS.colourText(MODEL.getPrecision()	+ "%"	, PRECISION_COLOUR	));
		System.out.println("Recall:\t\t"	+ COLOURS.colourText(MODEL.getRecall()		+ "%"	, RECALL_COLOUR		));
		System.out.println("F1Score:\t"		+ COLOURS.colourText(MODEL.getF1Score()		+ "%\n"	, F1_COLOUR			));
	}

}
