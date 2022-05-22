import java.io.FileNotFoundException;

public class Ann{

    private static final	String	TRAINING_FILE	= "cw2DataSet1.csv";	//	file name of the training dataset
	private static final	String	VALIDATE_FILE	= "cw2DataSet2.csv";	//	file name of the validation dataset
	private static			DataSet dataTrain;								//	dataset used to perform the training		
	private static			DataSet dataValid;								//	detaset used to perform the validation
	private static final	double	LEARNING_RATE	= 0.001;				//	learning rate suggested is about 0.001
	private static final	int		BATCH_SIZE		= 4;					//	number of samples processed before updating the weights
	private static final	int		EPOCHS			= 5;					//	number of dataset cycles
	



	// Model definition
	private static final Model MODEL = Model.Sequential(
		Layer.Conv2D(32, 2, 2,	Layer.Activation.MISH),		// convolutional layer of 32 filters with a kernal of 2X2
		Layer.Conv2D(64, 2, 2,	Layer.Activation.MISH),		// convolutional layer of 64 filters with a kernal of 2X2
		// starting the fully connected layers
		Layer.Dense(128,		Layer.Activation.MISH),		// dense layer of 128 nodes
		Layer.Dense(10 ,		Layer.Activation.SOFTMAX)	// output layer of 10 classifications
    );




	// building the model
	public static void setModel() throws FileNotFoundException {
		try{
			dataTrain = new DataSet(TRAINING_FILE, ",");	// loading the dataset 1
			dataValid = new DataSet(VALIDATE_FILE, ",");	// loading the dataset 2
		}catch(Exception e){ throw new FileNotFoundException(); }

		// initialising the model
		MODEL.buildStructure(dataTrain, dataValid, lib.Optimizer.ADAM, Model.Loss.CROSS_ENTROPY);
	}

	// running tests and validations
	public static void testModel(){
		MODEL.train(BATCH_SIZE, EPOCHS, LEARNING_RATE);
		System.out.println("\r\nValidating...");

		MODEL.validate();
		System.out.println("Accuracy: " + MODEL.getAccuracy() + "%\r\n");
	}

}
