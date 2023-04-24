
public class Model {
	
	private			double		error 		= 0;	// to store the loss result
	private			double		accuracy 	= 0;	// to store the accuracy result
	private			double		precision	= 0;	// to store the precision result
	private			double		recall		= 0;	// to store the recall result
	private			double		f1Score		= 0;	// to store the f1score result
	private	final	Layer[]		LAYERS;				// array containing all the layers
	private			Sample		sample;				// index of the current iterated sample
	private			Loss		loss;				// loss operations
	private			Optimizer	optimizer;			// Learning (gradient descent) optimizer


	// Collection of loss functions
	public enum Loss {
		CROSS_ENTROPY	(	lib.Loss.CROSS_ENTROPY	),	// Cross Entropy
		KULLBACK		(	lib.Loss.KULLBACK		),	// Kullback Leibler Divergence
		HUBER			(	lib.Loss.HUBER			),	// Huber Loss
		MSE				(	lib.Loss.MSE			),	// Mean Squared Error
		MAE				(	lib.Loss.MAE			);	// Mean Absolute Error

		// variables
		private final lib.Loss LOSS;					// loss function
		public double accuracy	= 0;					// accuracy
		public double error		= 0;					// loss value

		// constructor
		private Loss(final lib.Loss LOSS){ this.LOSS = LOSS; }

		// methods
		/**
		 * Setting the result into the node
		 * @param RESULT
		 * @param RELATION
		 */
		private void setIntoNode(final double RESULT, final Node.Relation ... RELATION){	// setting the result into the node
			for(final Node.Relation REL: RELATION)	REL.addToChainRuleSum(RESULT);
		}

		/**
		 * Loss derivative
		 * @param LAYER	layer object
		 * @param SAMPLE Sample object
		 */
		public void derivative(final Layer LAYER, final Sample SAMPLE){
			final Node.Relation[] FLAT_OUTPUT	= LAYER.getFlatOutput();		// getting the output of the layer	
			final double[] ONE_HOT				= SAMPLE.getOneHot();			// getting the label of the sample

			for(int classs=0; classs < FLAT_OUTPUT.length; classs++){			// cycling over the classes
				// setting the result into the node
				this.setIntoNode(this.LOSS.derivative(FLAT_OUTPUT[classs].getOutput(), ONE_HOT[classs]), FLAT_OUTPUT[classs]);
			}
		}

		/**
		 * Loss function
		 * @param OUTPUT output of the layer
		 * @param LABEL label of the sample
		 */
		public int function(final Layer LAYER, final Sample SAMPLE){
			final Node.Relation[] FLAT_OUTPUT	= LAYER.getFlatOutput();		// getting the output of the layer	
			final double[] ONE_HOT				= SAMPLE.getOneHot();			// getting the label of the sample
			double sum		= 0, prevMax = 0;
			int maxIndex	= 0;


            for(int index = 0; index < FLAT_OUTPUT.length; index++){
				if(FLAT_OUTPUT[index].getOutput() > prevMax){
					prevMax = FLAT_OUTPUT[index].getOutput();
					maxIndex = index;
				}
				sum += this.LOSS.function(FLAT_OUTPUT[index].getOutput(), ONE_HOT[index]);
			}
			this.accuracy	+= ONE_HOT[maxIndex];
            this.error		+= sum / FLAT_OUTPUT.length;
			return maxIndex;
		}
	}

	
	// Collection of Optimizers
	public enum Optimizer {
		SGD		(	lib.Optimizer.SGD		),	// Stochastic Gradient Descent
		MOMENTUM(	lib.Optimizer.MOMENTUM	),	// Momentum
		RMSPROP	(	lib.Optimizer.RMSPROP	),	// RMSProp
		NESTEROV(	lib.Optimizer.NESTEROV	),	// Nesterov Accelerated Gradient
		ADAGRAD	(	lib.Optimizer.ADAGRAD	),	// Adaptive Gradient
		ADADELTA(	lib.Optimizer.ADADELTA	),	// Adaptive Delta
		ADAM	(	lib.Optimizer.ADAM		),	// Adaptive Moment Estimation
		ADAMAX	(	lib.Optimizer.ADAMAX	),	// Adaptive Moment Estimation (max)
		AMSGRAD	(	lib.Optimizer.AMSGRAD	),	// Adaptive Moment Estimation (max)
		NADAM	(	lib.Optimizer.NADAM		);	// Nesterov Adaptive Moment Estimation

		// variables
		public final lib.Optimizer OPT;	// optimizer

		// constructor
		private Optimizer(final lib.Optimizer OPT){ this.OPT = OPT; }
	}


	/**
	 * Constructor method
	 * @param L Layers
	 */
	private Model(final Layer[] L){
		this.LAYERS = L;
	}

	/**
	 * Getting the model object
	 * @param L array of layers
	 * @return model object
	 */
	public static Model Sequential(final Layer ... L){	// getting the model object
		return new Model(L);							// returning the model object
	}








	/**
	 * Building the Neural Network structure
	 * @param DATA_TRAIN
	 * @param DATA_VALID
	 * @param OPT
	 * @param L
	 */
	public void buildStructure(final int SHAPE_Y, final int SHAPE_X, final int CHANNELS, final Optimizer OPT, final Loss L){
		this.loss			= L;			// loss function
		this.optimizer		= OPT;			// optimizer

		Layer prevLayer = this.LAYERS[0];									// previous layer
		prevLayer.firstLayerInit(OPT.OPT, SHAPE_Y, SHAPE_X, CHANNELS);		// initialising the input layer
							
		// cycling overt the rest of the layers initialising them
		for(int index=1; index < this.LAYERS.length; index++){
			this.LAYERS[index].layerInit(OPT.OPT, prevLayer.getNodes());	// initialising the layer
			prevLayer = this.LAYERS[index];									// updating the previous layer
		}
	}



	
	/**
	 * Feed forward through every layer
	 */
	private int feedForward(){
		// loading the sample
		try{ this.LAYERS[0].sampleLoader(this.sample); }								// loading the sample into the input layer
		catch(Exception e){ System.err.println("Sample loaded into the wrong layer"); }	// error handling

		// cycling over the layers
		for(final Layer LAYER: this.LAYERS)	LAYER.feedForward();
		return this.loss.function(LAYERS[this.LAYERS.length-1], this.sample);			// get the loss and return the predicted class
	}




	/**
	 * Performing the back propagation to every layer
	 */
	private void backPropagate(){
		this.loss.derivative(LAYERS[this.LAYERS.length-1], this.sample);									// getting the loss derivative
		for(int layer = this.LAYERS.length-1; layer >= 0; layer--)	this.LAYERS[layer].backPropagating();	// cycling over the layers
	}




	// Updating the weights upon the Batch-size end
	private void weightsUpdate(){
		for(final Layer LAYER: this.LAYERS) LAYER.updateWeights();	// updating the weights
		this.optimizer.OPT.timeStepIncrease();						// increasing the time step of the optimizer
	}




	/**
	 * Model training
	 * @param DATA dataset
	 * @param batch mini batch size
	 * @param EPOCHS cicles of entire dataset
	 * @param LEARNING_RATE learning rate
	 */
	public void train(final DataSet DATA, int batch, final int EPOCHS, final double LEARNING_RATE){
		batch = Math.max(batch, 1);												// setting the batch size to 1 if it is less than 1
		final lib.Util.Loading BAR = new lib.Util.Loading(DATA.size()-1);		// loading bar
		this.optimizer.OPT.setParam(LEARNING_RATE, batch);						// setting the learning rate and the batch size
		
		//cicling over the dataset samples for "EPOCHS" times
		for(int epoch = 1; epoch <= EPOCHS; epoch++){
			DATA.shuffle();														// shuffeling the samples
			String epochMessage = "";											// epoch message
			if(EPOCHS > 1){
				epochMessage = "Epoch: " + epoch + " / " + EPOCHS;				// generate epoch message
				BAR.message(epochMessage, "blue");								// printing the epoch number
			}
			
			// cycling over the samples
			for(int startBatch = 0; startBatch < DATA.size(); startBatch += batch){
				final int END_BATCH = Math.min(startBatch+batch,DATA.size());	// getting the end of the batch

				for(int sampleIndex=startBatch; sampleIndex < END_BATCH; sampleIndex++){
					this.sample = DATA.getSample(sampleIndex);					// getting the sample
					
					BAR.printNewBar();  										// printing the loading bar
					this.feedForward();											// performing forward propagation for all the layers
					this.backPropagate();										// performing back propagation for all the layers
				}
				this.weightsUpdate();											// updating the weights
				BAR.message(
					epochMessage + 
					" | Loss: "		+	this.loss.error		/ (double)END_BATCH	+ 
					" | Accuracy: " +	this.loss.accuracy	/ (double)END_BATCH, "blue"
				);
			}
			this.error			= this.loss.error		/ (double)DATA.size();	// setting this epoch error
			this.accuracy		= this.loss.accuracy	/ (double)DATA.size();	// setting this epoch accuracy
			this.loss.error		= 0;											// resetting the loss error
			this.loss.accuracy	= 0;											// resetting the accuracy
		}
	}


	/**
	 * Validating / testing the model
	 * @param DATA dataset
	 * @return accuracy
	 */
    public void validate(final DataSet DATA){
		final int[] FP	= new int[DATA.getClasses().length];			// false positives
		final int[] TP	= new int[DATA.getClasses().length];			// true positives

		DATA.shuffle();													// shuffeling the samples

		// cycling over the samples
		for(final Sample SAMPLE: DATA.getDataSet()){
			this.sample = SAMPLE;											// loading the sample

			final int PREDICTED = this.feedForward();						// performing forward propagation for all the layers
			this.sample.setPred(DATA.getClasses()[PREDICTED]);				// getting the prediction
			final int L_INDEX = DATA.getLabelIndex(this.sample.getPred());	// getting the label index
			if (this.sample.getLabel() == this.sample.getPred()){			// checking if the prediction is correct
				TP[L_INDEX]++;												// getting true positives
			}else	FP[L_INDEX]++;											// getting false positives
		}

		// storing the outcome data
		this.error		= this.loss.error		/ (double)DATA.size();	// setting error
		this.accuracy	= this.loss.accuracy	/ (double)DATA.size();	// setting accuracy
		this.precision	= this.getPrecision(TP, FP);					// setting precision
		this.recall		= this.getRecall(TP, FP, DATA);					// setting recall
		this.f1Score	= this.getF1Score(this.precision, this.recall);	// setting f1Score

		this.loss.error		= 0;										// resetting the loss error
		this.loss.accuracy	= 0;										// resetting the accuracy
	}



	/**
	 * Calculating the F1Score
	 * @param PRECISION 
	 * @param RECALL 
	 * @return F1Score
	 */
	private double getF1Score(final double PRECISION, final double RECALL){
		return 2 * (PRECISION * RECALL) / (PRECISION + RECALL);	// F1Score
	}

	/**
	 * Calculating the precision
	 * @param TP true positives
	 * @param FP false positives
	 * @return precision
	 */
	private double getPrecision(final int[] TP, final int[] FP){
		double precision = 0;
		for(int label=0; label < TP.length; label++)	precision += (double)TP[label] / (double)(TP[label] + FP[label]);
		return precision / (double)TP.length;	// precision
	}

	/**
	 * Calculating the recall
	 * @param TP true positives
	 * @param FP false positives
	 * @return recall
	 */
	private double getRecall(final int[] TP, final int[] FP, final	DataSet DATA){
		double recall = 0;

		for(int label=0; label < TP.length; label++){					// cycling over the labels
			final long FN = DATA.getLabelsAmount(label) - TP[label];	// getting the false negatives
			final long TP_FN = TP[label] + FN;							// getting the true positives + false negatives
			recall += (double)TP[label] / TP_FN;						// calculating the recall
		}

		return recall / (double)TP.length;								// recall
	}
	


	// ------------------ getter methods ------------------------
	
	// getting the model accuracy
	public double getAccuracy(){
		return this.accuracy;
	}

	// getting the model precision
	public double getPrecision(){
		return this.precision;
	}

	// getting the model recall
	public double getRecall(){
		return this.recall;
	}

	// getting the model F1Score
	public double getF1Score(){
		return this.f1Score;
	}

	// getting the model error
	public double getError(){
		return this.error;
	}

}
