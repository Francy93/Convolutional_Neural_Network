import java.io.FileNotFoundException;

public class Ann{
    
	private static			DataSet dataTrain;								//	dataset used to perform the training		
	private static			DataSet dataValid;								//	detaset used to perform the validation
	private static final	double	LEARNING_RATE	= 0.001;				//	learning rate suggested is about 0.01
	private static final	int		BATCH_SIZE		= 8;					//	number of samples processed before updating the weights
	private static final	int		EPOCHS			= 5;					//	number of dataset cycles
	private static final	String	TRAINING_FILE	= "cw2DataSet1.csv";	//	file name of the training dataset
	private static final	String	VALIDATE_FILE	= "cw2DataSet2.csv";	//	file name of the validation dataset



	// Model definition
	private static final Model MODEL = Model.Sequential(
		Layer.Conv2D(16, 2, 2,	Layer.Activation.SWISH),
		Layer.Conv2D(32, 3, 3,	Layer.Activation.SWISH),
		//Layer.Dense(256,		Layer.Activation.MISH),
		//Layer.Dense(128,		Layer.Activation.MISH),
		Layer.Dense(64 ,		Layer.Activation.SWISH),
		Layer.Dense(10 ,		Layer.Activation.SOFTMAX)
    );




	// running the model
	public static void setModel() throws FileNotFoundException {
		try{
			dataTrain = new DataSet(TRAINING_FILE, ",");
			dataValid = new DataSet(VALIDATE_FILE, ",");
		}catch(Exception e){ throw new FileNotFoundException(); }

		// initialising the model
		MODEL.buildStructure(dataTrain, dataValid, Model.Loss.CROSS_ENTROPY);
		
	}

	public static void testModel(){
		MODEL.train(BATCH_SIZE, EPOCHS, LEARNING_RATE);
		System.out.println("\r\nValidating...");
		MODEL.validate();
		System.out.println("Accuracy: " + MODEL.getAccuracy() + "%\r\n");
	}
		// Convolutional layer 1
		/* layer.Conv2D(64, 3, 3, activation.relu, input_shape=(28, 28, 1)),
		//layer.MaxPooling2D(2, 2),
		// Convolutional layer 2
		layer.Conv2D(64, 3, 3, activation.relu),
		//layer.MaxPooling2D(2, 2),
		// Final layers
		//layer.Flatten(),
		layer.Dense(512, activation.relu),
		layer.Dense(10, activation.softmax) */
       //]);



	// Model compiler
	/* model.compile(optimizer='adam',
		loss='sparse_categorical_crossentropy',
		metrics=['accuracy']) */

	// Generators
/* 	train_gen = ImageDataGenerator().flow(train_images, train_labels_split, batch_size=32)
	validation_gen = ImageDataGenerator().flow(validation_images, validation_labels, batch_size=32)

	// Model fit
	history = model.fit(train_gen,
						validation_data = validation_gen,
						steps_per_epoch = len(train_images) / 32,
						validation_steps = len(validation_images) / 32,    
						epochs=10, 
						callbacks=[myCallback()]) */

}
