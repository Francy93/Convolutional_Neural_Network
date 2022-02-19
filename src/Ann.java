import java.io.FileNotFoundException;

import lib.Loss;

public class Ann{
    

	private static DataSet DATA_TRAIN;
	private static DataSet DATA_VALID;



	// Model definition
	private static final Model MODEL = Model.Sequential(
		Layer.Conv2D(32, 3, 3,	Layer.Activation.RELU),
		Layer.Conv2D(64, 3, 3,	Layer.Activation.RELU),
		Layer.Dense(512,		Layer.Activation.RELU),
		Layer.Dense(10 ,		Layer.Activation.SOFTMAX)
    );





	public static void runModel() throws FileNotFoundException {
		try{
			DATA_TRAIN = new DataSet("cw2DataSet1.csv", ",");
			DATA_VALID = new DataSet("cw2DataSet2.csv", ",");
		}catch(Exception e){ throw new FileNotFoundException(); }

		final int BATCH_SIZE = 32, EPOCHS = 10;


		MODEL.buildStructure(DATA_TRAIN, Loss.CROSS_ENTROPY);
		MODEL.train(DATA_TRAIN, BATCH_SIZE, EPOCHS);
		MODEL.validate(DATA_VALID);
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
