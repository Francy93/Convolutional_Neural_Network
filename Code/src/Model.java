
public class Model {

	private			double		accuracy 	= 0;	// to store the accuracy result
	private			double		precision	= 0;	// to store the precision result
	private			double		recall		= 0;	// to store the recall result
	private			double		f1Score 	= 0;	// to store the f1score result
	private			DataSet 	trainData;			// datastructure of training samples
	private			DataSet 	validateData;		// datastructure of validation samples
	private final	Layer[]		LAYERS;				// array containing all the layers
	private 		Sample		sample;				// index of the current iterated sample
	private 		Loss		loss;				// loss operations
	private			Optimizer	optimizer;			// Learning (gradient descent) optimizer


	// Collection of loss functions
	public enum Loss {
		CROSS_ENTROPY(	lib.Loss.CROSS_ENTROPY	),	// Cross Entropy
		KULLBACK( 		lib.Loss.KULLBACK		),	// Kullback Leibler Divergence
		HUBER( 			lib.Loss.HUBER			),	// Huber Loss
		MSE( 			lib.Loss.MSE			),	// Mean Squared Error
		MAE( 			lib.Loss.MAE			);	// Mean Absolute Error

		// variables
		private final lib.Loss LOSS;				// loss function

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
		 * Loss function derivative
		 * @param LAYER	layer object
		 * @param SAMPLE Sample object
		 */
		public void derivative(final Layer LAYER, final Sample SAMPLE){
			final Node.Relation[] FLAT_OUTPUT	= LAYER.getFlatOutput();		// getting the output of the layer	
			final double[] ONE_HOT				= SAMPLE.getLabelLocation();	// getting the label of the sample

			for(int classs=0; classs < FLAT_OUTPUT.length; classs++){			// cycling over the classes
				// setting the result into the node
				this.setIntoNode(this.LOSS.derivative(FLAT_OUTPUT[classs].getOutput(), ONE_HOT[classs]), FLAT_OUTPUT[classs]);
			}
		}
	}

	
	// Collection of Optimizers
	public enum Optimizer {
		SGD(		lib.Optimizer.SGD		),	// Stochastic Gradient Descent
		MOMENTUM(	lib.Optimizer.MOMENTUM	),	// Momentum
		RMSPROP(	lib.Optimizer.RMSPROP	),	// RMSProp
		NESTEROV(	lib.Optimizer.NESTEROV	),	// Nesterov Accelerated Gradient
		ADAGRAD(	lib.Optimizer.ADAGRAD	),	// Adaptive Gradient
		ADADELTA(	lib.Optimizer.ADADELTA	),	// Adaptive Delta
		ADAM(		lib.Optimizer.ADAM		),	// Adaptive Moment Estimation
		ADAMAX(		lib.Optimizer.ADAMAX	),	// Adaptive Moment Estimation (max)
		AMSGRAD(	lib.Optimizer.AMSGRAD	),	// Adaptive Moment Estimation (max)
		NADAM(		lib.Optimizer.NADAM		);	// Nesterov Adaptive Moment Estimation

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
		LAYERS = L;
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
	public void buildStructure(final DataSet DATA_TRAIN, final DataSet DATA_VALID, final Optimizer OPT, final Loss L){
		this.trainData		= DATA_TRAIN;	// training data
		this.validateData	= DATA_VALID;	// validation data
		this.loss			= L;			// loss function
		this.optimizer		= OPT;			// optimizer

		Layer prevLayer = this.LAYERS[0];								// previous layer
		prevLayer.firstLayerInit(OPT.OPT, DATA_TRAIN.getSample(0));		// initialising the input layer
							
		// cycling overt the rest of the layers initialising them
		for(int index=1; index < this.LAYERS.length; index++){
			this.LAYERS[index].layerInit(OPT.OPT, prevLayer.getNodes());// initialising the layer
			prevLayer = this.LAYERS[index];								// updating the previous layer
		}
	}



	
	/**
	 * Feed forward through every layer
	 */
	private void feedForward(){
		// loading the sample
	try{ this.LAYERS[0].sampleLoader(this.sample); }									// loading the sample into the input layer
		catch(Exception e){ System.err.println("Sample loaded into the wrong layer"); }	// error handling

		// cycling over the layers
		for(final Layer LAYER: this.LAYERS)	LAYER.feedForward();
	}




	/**
	 * Performing the back propagation to every layer
	 */
	private void backPropagate(){
		this.loss.derivative(LAYERS[this.LAYERS.length-1], this.sample);									// getting the loss function derivative
		for(int layer = this.LAYERS.length-1; layer >= 0; layer--)	this.LAYERS[layer].backPropagating();	// cycling over the layers
	}




	// Updating the weights upon the Batch-size end
	private void weightsUpdate(){
		this.optimizer.OPT.timeStepIncrease();						// increasing the time step of the optimizer
		for(final Layer LAYER: this.LAYERS) LAYER.updateWeights();	// updating the weights
	}




	/**
	 * Model training
	 * @param DATA dataset
	 * @param BATCH mini batch size
	 * @param EPOCHS cicles of entire dataset
	 * @param LEARNING_RATE learning rate
	 */
	public void train(final int BATCH, final int EPOCHS, final double LEARNING_RATE){ this.train( this.trainData, BATCH, EPOCHS, LEARNING_RATE); }
	public void train(final DataSet DATA, final int BATCH, final int EPOCHS, final double LEARNING_RATE){
		this.optimizer.OPT.setParam(LEARNING_RATE, BATCH);				// setting the learning rate and the batch size

		final int DATA_SIZE = DATA.getSize()-1;							// getting the size of the dataset
		final lib.Util.Loading BAR = new lib.Util.Loading(DATA_SIZE);	// loading bar
		
		//cicling over the dataset samples for "EPOCHS" times
		for(int epoch=0; epoch < EPOCHS; epoch++){
			BAR.message("Epoch: "+ (epoch+1) + " / " + EPOCHS, "blue");	// printing the epoch number
			DATA.shuffle();												// shuffeling the samples

			// cycling over the samples
			for(int sampleIndex=0, nextBatch=BATCH-1; sampleIndex <= DATA_SIZE; sampleIndex++){
				this.sample = DATA.getSample(sampleIndex);				// getting the sample
				
				BAR.printNewBar();  									// printing the loading bar

				this.feedForward();										// performing forward propagation for all the layers
				this.backPropagate();									// performing back propagation for all the layers

				// updating weights after a certain amount of samples (mini batch)
				if(sampleIndex == nextBatch || sampleIndex == DATA_SIZE){
					this.weightsUpdate();								// updating the weights
					nextBatch += BATCH;									// updating the next batch
				}
			}
		}
	}


	/**
	 * Validating / testing the model
	 * @param DATA dataset
	 * @return accuracy
	 */
	public double validate(){ return validate(this.validateData); }
    public double validate(final DataSet DATA){
		final int[] FP	= new int[DATA.getClasses().length];			// false positives
		final int[] TP	= new int[DATA.getClasses().length];			// true positives
		int correct = 0;												// correct counter

		DATA.shuffle();													// shuffeling the samples

		// cycling over the samples
		for(final Sample SAMPLE: DATA.getDataSet()){
			this.sample = SAMPLE;										// loading the sample

			this.feedForward();											// performing forward propagation for all the layers
			if (this.sample.getLabel() == this.getPredClass(DATA)){		// checking if the prediction is correct
				correct++;												// correct counter
				TP[DATA.getLabelIndex(this.getPredClass(DATA))]++;		// getting true positives
			}else	FP[DATA.getLabelIndex(this.getPredClass(DATA))]++;	// getting false positives
		}

		// storing the outcome data
		this.accuracy	= (double)correct * 100.00 / (double)DATA.getSize();	// accuracy
		this.precision	= this.getPrecision(TP, FP) * 100;						// precision
		this.recall		= this.getRecall(TP, FP, DATA) * 100;					// recall
		this.f1Score	= this.getF1Score(this.precision, this.recall);			// f1Score
		
		return this.accuracy;
	}


	/**
	 * Getting the predicted class
	 * @param DATA dataset
	 * @return predicted class
	 */
	private double getPredClass(final DataSet DATA){
		Node.Relation[] NODES = this.LAYERS[this.LAYERS.length-1].getFlatOutput();

		int answerIndex		= 0;						// index of the answer
		double valHolder	= 0; 						// value holder
		for(int node=0; node < NODES.length; node++){	// cycling over the nodes
			if(NODES[node].getOutput() > valHolder){	// checking if the node output is greater than the value holder
				answerIndex = node;						// setting the answer index
				valHolder	= NODES[node].getOutput();	// setting the value holder
			}
		}
		return DATA.getClasses()[answerIndex];
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
		return lib.Util.round(this.accuracy, 2);
	}

	// getting the model precision
	public double getPrecision(){
		return lib.Util.round(this.precision, 2);
	}

	// getting the model recall
	public double getRecall(){
		return lib.Util.round(this.recall, 2);
	}

	// getting the model F1Score
	public double getF1Score(){
		return lib.Util.round(this.f1Score, 2);
	}


}
