import java.io.FileNotFoundException;

public class Ann{
	private static			DataSet dataTrain;							// dataset used to perform the training		
	private static			DataSet dataValid;							// detaset used to perform the validation
	public	static			Regularize.Fitness fitness;					// used to determine the regularizer
	private static			lib.Chart chart;							// chart used to plot the metrics

	private static final	String	TRAINING_FILE	= "dataSet1.csv";	// file name of the training dataset
	private static final	String	VALIDATE_FILE	= "dataSet2.csv";	// file name of the validation dataset
	private static final	String	DELIMITER		= ",";				// delimiter of the dataset
	private static final	boolean	LABEL_LOCATION	= false;			// label location (true = first column, false = last column)
	private static final	boolean	CHART_STATE		= true;				// chart state (true = on, false = off)
	private static final	double	NOISE			= 0;				// dynamic noise (as a regularizator)
	private static final	int		BATCH_SIZE		= 1;				// number of samples processed before updating the weights
	public	static final	int		EPOCHS			= 100;				// number of dataset cycles
	private static final	double	LEARNING_RATE	= 0.0001;			// learning rate suggested is about 0.001
	



	// Model definition
	private static final Model MODEL = Model.Sequential(
		// starting the convolutional layers
		Layer.Conv2D(8	,	1, 1,	Layer.Activation.TANH),		// convolutional layer of 8   filters with a kernel of 1X1
		Layer.Conv2D(16	,	1, 1,	Layer.Activation.LRELU),	// convolutional layer of 16  filters with a kernel of 1X1
		Layer.Conv2D(32	,	2, 2,	Layer.Activation.SELU),		// convolutional layer of 32  filters with a kernel of 2X2
		Layer.Conv2D(64	,	2, 2,	Layer.Activation.GELU),		// convolutional layer of 64  filters with a kernel of 2X2
		Layer.Conv2D(128,	3, 3,	Layer.Activation.SWISH),	// convolutional layer of 128 filters with a kernel of 3X3
		Layer.Conv2D(256,	3, 3,	Layer.Activation.MISH),		// convolutional layer of 254 filters with a kernel of 3X3
		// starting the fully connected layers
		Layer.Dense(128	,			Layer.Activation.MISH),		// dense layer of 128 nodes
		Layer.Dense(10	,			Layer.Activation.SOFTMAX)	// output layer of 10 classifications
	);




	// Loading the dataset and building the model
	public static void loadAndBuild() throws FileNotFoundException {
		System.out.print(" - Loading the datasets...\r");
		try{ // loading the datasets
			dataTrain = new DataSet(TRAINING_FILE, DELIMITER, LABEL_LOCATION);			// loading the dataset 1
			dataValid = new DataSet(VALIDATE_FILE, DELIMITER, LABEL_LOCATION);			// loading the dataset 2
		}catch(Exception e){ throw new FileNotFoundException(); }
		System.out.println(" - Loaded "+dataTrain.size()+" trainig samples and "+dataValid.size()+" validation samples");

		// normalising the datasets
		dataTrain.normalize();															// normalising the training dataset
		dataValid.normalize();															// normalising the validation dataset

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
		System.out.println(" - Built "+(MODEL.getModelDepth()+1)+" layers, "+MODEL.getNeuronsAmount()+" neurons and "+MODEL.getParametersAmount()+" parameters");
	
		fitness	= NOISE > 0? new Regularize.Noisify(MODEL, NOISE): new Regularize.Smooth(MODEL);	// used to determine the noise
		try{if(CHART_STATE){
				chart	= new lib.Chart(new String[]{"Training Accuracy", "Training Loss", "Validation Accuracy", "Validation Loss"},
										new String[]{"Accuracy", "Loss", "Accuracy", "Loss"},
										new String[]{"Epoch", "Epoch", "Epoch", "Epoch"},
										new String[]{"BLUE", "RED", "GREEN", "ORANGE"});
			}
			System.out.println(" - GUI Chart status: Supported!\n\n");
		}catch(Exception e){ System.out.println(" - GUI Chart status: Unsupported!\n\n"); }
	}


	// running training and testing
	public static void trainAndTest(final int ITER){
		
		for(int epoch = 1+(ITER-1)*EPOCHS; epoch <= EPOCHS*ITER; epoch++){	// looping through the epochs
			fitness.printTitle(epoch);										// printing the epoch number
			// training
			fitness.train(dataTrain, BATCH_SIZE, 1, LEARNING_RATE, true);	// performing the training
			if (chart != null){
				chart.addData(0, fitness.getTrainAcc()*100d, epoch, true);	// adding the training accuracy to the chart
				chart.addData(1, fitness.getTrainErr()*100d, epoch, true);	// adding the training loss to the chart
			}
			// testing
			fitness.validate(dataValid, true);								// performing the validation
			if (chart != null){
				chart.addData(2, fitness.getValidAcc()*100d, epoch, true);	// adding the validation accuracy to the chart
				chart.addData(3, fitness.getValidErr()*100d, epoch, true);	// adding the validation loss to the chart
			}
		}

		fitness.printScores();												// printing the metrics
	}
	
}
