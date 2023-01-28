
public class Model {

	private			double		accuracy 	= 0;	// to store the accuracy result
	private			double		precision	= 0;	// to store the precision result
	private			double		recall		= 0;	// to store the recall result
	private			double		f1Score 	= 0;	// to store the f1score result
	private			DataSet 	trainData;			// datastructure of training samples
	private			DataSet 	validateData;		// datastructure of validation samples
	private final	Layer[]		LAYERS;				// array containing all the layers
	private 		Loss		loss;				// loss operations
	private 		Sample		sample;				// index of the current iterated sample
	private			lib.Optimizer optimizer;		// Learning (gradient descent) optimizer


	// collection of loss functions
	public enum Loss {
		CROSS_ENTROPY( lib.Loss.CROSS_ENTROPY ),	// Cross Entropy
		KULLBACK( lib.Loss.KULLBACK ),				// Kullback Leibler Divergence
		HUBER( lib.Loss.HUBER ),					// Huber Loss
		MSE( lib.Loss.MSE ),						// Mean Squared Error
		MAE( lib.Loss.MAE );						// Mean Absolute Error

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
		private void setIntoNode(final double RESULT, final Node.Relation ... RELATION){
			for(final Node.Relation REL: RELATION)	REL.addToChainRuleSum(RESULT);
		}

		/**
		 * Loss function derivative
		 * @param LAYER	layer object
		 * @param SAMPLE Sample object
		 */
		public void derivative(final Layer LAYER, final Sample SAMPLE){
			final Node.Relation[] FLAT_OUTPUT	= LAYER.getFlatOutput();
			final double[] ONE_HOT				= SAMPLE.getLabelLocation();

			for(int classs=0; classs < FLAT_OUTPUT.length; classs++){
				setIntoNode(this.LOSS.derivative(FLAT_OUTPUT[classs].getOutput(), ONE_HOT[classs]), FLAT_OUTPUT[classs]);
			}
		}
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
	public static Model Sequential(final Layer ... L){
		return new Model(L);
	}





	/**
	 * Building the Neural Network structure
	 * @param DATA_TRAIN
	 * @param DATA_VALID
	 * @param OPT
	 * @param L
	 */
	public void buildStructure(final DataSet DATA_TRAIN, final DataSet DATA_VALID, final lib.Optimizer OPT, final Loss L){
		this.trainData		= DATA_TRAIN;
		this.validateData	= DATA_VALID;
		this.loss			= L;
		this.optimizer		= OPT;

		Layer prevLayer = this.LAYERS[0];						// previous layer
		prevLayer.firstLayerInit(OPT, DATA_TRAIN.getSample(0));	// initialising the input layer
							
		// cycling overt the rest of the layers initialising them
		for(int index=1; index < this.LAYERS.length; index++){
			this.LAYERS[index].layerInit(OPT, prevLayer.getNodes());
			prevLayer = this.LAYERS[index];
		}
	}



	
	/**
	 * Feed forward through every layer
	 */
	private void feedForward(){
		// loading the sample
		try{ this.LAYERS[0].sampleLoader(this.sample); }
		catch(Exception e){ System.err.println("Sample loaded into the wrong layer"); }

		// cycling over the layers
		for(final Layer LAYER: this.LAYERS)	LAYER.feedForward();
	}




	/**
	 * Performing the back propagation to every layer
	 */
	private void backPropagate(){
		this.loss.derivative(LAYERS[this.LAYERS.length-1], this.sample);
		for(int layer = this.LAYERS.length-1; layer >= 0; layer--)	this.LAYERS[layer].backPropagating();
	}




	// Updating the weights upon the Batch-size end
	private void weightsUpdate(){
		for(final Layer LAYER: this.LAYERS) LAYER.updateWeights();
	}




	/**
	 * Model training
	 * @param DATA dataset
	 * @param BATCH mini batch size
	 * @param EPOCHS cicles of entire dataset
	 * @param LEARNING_RATE learning rate
	 */
	public void train(final int BATCH, final int EPOCHS, final double LEARNING_RATE){ train( this.trainData, BATCH, EPOCHS, LEARNING_RATE); }
	public void train(final DataSet DATA, final int BATCH, final int EPOCHS, final double LEARNING_RATE){
		this.optimizer.setParam(LEARNING_RATE, BATCH);

		final int DATA_SIZE = DATA.getSize()-1;
		final lib.Util.Loading BAR = new lib.Util.Loading(DATA_SIZE);
		
		//cicling over the dataset samples for "EPOCHS" times
		for(int epoch=0; epoch < EPOCHS; epoch++){
			BAR.message("Epoch: "+ (epoch+1) + " / " + EPOCHS, "blue");
			DATA.shuffle();	// shuffeling the samples

			// cycling over the samples
			for(int sampleIndex=0, batch=0; sampleIndex <= DATA_SIZE; sampleIndex++, batch++){
				this.sample = DATA.getSample(sampleIndex);
				
				BAR.printNewBar();  // printing the loading bar

				feedForward();		// performing forward propagation for all the layers
				backPropagate();	// performing back propagation for all the layers

				// updating weights after a certain amount of samples (mini batch)
				if(batch >= BATCH || sampleIndex >= DATA_SIZE){
					weightsUpdate();
					batch = 0;
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
		final int[] FP	= new int[DATA.getClasses().length];
		final int[] TP	= new int[DATA.getClasses().length];
		int correct = 0;

		DATA.shuffle();

		for(final Sample SAMPLE: DATA.getDataSet()){
			this.sample = SAMPLE;

			feedForward();
			if (this.sample.getLabel() == this.getPredClass(DATA)){
				correct++;													// correct counter
				TP[DATA.getLabelIndex(getPredClass(DATA))]++;				// getting true positives
			}else	FP[DATA.getLabelIndex(getPredClass(DATA))]++;			// getting false positives
		}

		// storing the outcome data
		this.accuracy	= (double)correct * 100.00 / (double)DATA.getSize();
		this.precision	= this.getPrecision(TP, FP)*100;
		this.recall		= this.getRecall(TP, FP, DATA)*100;
		this.f1Score	= this.getF1Score(this.precision, this.recall);
		
		return this.accuracy;
	}


	/**
	 * Getting the predicted class
	 * @param DATA dataset
	 * @return predicted class
	 */
	private double getPredClass(final DataSet DATA){
		Node.Relation[] NODES = this.LAYERS[this.LAYERS.length-1].getFlatOutput();

		int answerIndex		= 0;
		double valHolder	= 0; 
		for(int node=0; node < NODES.length; node++){
			if(NODES[node].getOutput() > valHolder){
				answerIndex = node;
				valHolder	= NODES[node].getOutput();
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
		return 2 * (PRECISION * RECALL) / (PRECISION + RECALL);
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
		return precision / (double)TP.length;
	}

	/**
	 * Calculating the recall
	 * @param TP true positives
	 * @param FP false positives
	 * @return recall
	 */
	private double getRecall(final int[] TP, final int[] FP, final	DataSet DATA){
		double recall = 0;

		for(int label=0; label < TP.length; label++){
			final long FN = DATA.getLabelsAmount(label) - TP[label];
			final long TP_FN = TP[label] + FN;
			recall += (double)TP[label] / TP_FN;
		}

		return recall / (double)TP.length;
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
