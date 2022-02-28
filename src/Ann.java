import java.io.FileNotFoundException;

public class Ann{
    

	private static			DataSet dataTrain;
	private static			DataSet dataValid;
	private static final	double	LEARNING_RATE	= 0.03;
	private static final	int		BATCH_SIZE		= 1;
	private static final	int		EPOCHS			= 5;
	private static final	String	TRAINING_FILE	= "cw2DataSet1.csv";
	private static final	String	VALIDATE_FILE	= "cw2DataSet2.csv";



	// Model definition
	private static final Model MODEL = Model.Sequential(
		Layer.Conv2D(16, 2, 2,	Layer.Activation.SWISH),
		Layer.Conv2D(16, 2, 2,	Layer.Activation.SWISH),
		Layer.Dense(128,		Layer.Activation.SWISH),
		Layer.Dense(128,		Layer.Activation.SWISH),
		Layer.Dense(10 ,		Layer.Activation.SOFTMAX)
    );




	// running the model
	public static void runModel() throws FileNotFoundException {
		try{
			dataTrain = new DataSet(TRAINING_FILE, ",");
			dataValid = new DataSet(VALIDATE_FILE, ",");
		}catch(Exception e){ throw new FileNotFoundException(); }


		MODEL.buildStructure(dataTrain, Model.Loss.CROSS_ENTROPY);
		MODEL.train(dataTrain, BATCH_SIZE, EPOCHS, LEARNING_RATE);
		MODEL.validate(dataValid);
		System.out.println("Accuracy: " + MODEL.getAccuracy() + "%");
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
