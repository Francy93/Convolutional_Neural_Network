
public class Model {

	private			double		accuracy = 0;	// to store the accuracy result
	private			DataSet 	trainData;		// datastructure of training samples
	private			DataSet 	validateData;	// datastructure of validation samples
	private final	Layer[]		LAYERS;			// array containing all the layers
	private 		Loss		loss;			// loss operations
	private 		Sample		sample;			// index of the current iterated sample
	private			lib.Optimizer optimizer;	// Learning (gradient descent) optimizer


	// collection of loss functions
	public static enum Loss {
		MSE{
			public void derivative(final Layer LAYER, final Sample SAMPLE){
				Node.Relation[] FLAT_OUTPUT = LAYER.getFlatOutput();
				final double[] LABEL_LOCATION = SAMPLE.getLabelLocation();

				for(int classs=0; classs < FLAT_OUTPUT.length; classs++){
					setIntoNode(lib.Loss.MSE.derivative(FLAT_OUTPUT[classs].getOutput(), LABEL_LOCATION[classs]) / FLAT_OUTPUT.length, FLAT_OUTPUT[classs]);
				}
			}
		},
		MAE{
			public void derivative(final Layer LAYER, final Sample SAMPLE){
				Node.Relation[] FLAT_OUTPUT = LAYER.getFlatOutput();
				final double[] LABEL_LOCATION = SAMPLE.getLabelLocation();

				for(int classs=0; classs < FLAT_OUTPUT.length; classs++){
					setIntoNode(lib.Loss.MAE.derivative(FLAT_OUTPUT[classs].getOutput(), LABEL_LOCATION[classs]), FLAT_OUTPUT[classs]);
				}
			}
		},
		CROSS_ENTROPY{
			public void derivative(final Layer LAYER, final Sample SAMPLE){
				Node.Relation[] FLAT_OUTPUT = LAYER.getFlatOutput();
				final double[] LABEL_LOCATION = SAMPLE.getLabelLocation();

				//double meanError = 0;
				for(int classs=0; classs < FLAT_OUTPUT.length; classs++){
					setIntoNode(lib.Loss.Cross_Entropy.derivative(FLAT_OUTPUT[classs].getOutput(), LABEL_LOCATION[classs]), FLAT_OUTPUT[classs]);
				}
			}
		},
		HUBER{
			public void derivative(final Layer LAYER, final Sample SAMPLE){
				Node.Relation[] FLAT_OUTPUT = LAYER.getFlatOutput();
				final double[] LABEL_LOCATION = SAMPLE.getLabelLocation();

				for(int classs=0; classs < FLAT_OUTPUT.length; classs++){
					setIntoNode(lib.Loss.Huber.derivative(FLAT_OUTPUT[classs].getOutput(), LABEL_LOCATION[classs]), FLAT_OUTPUT[classs]);
				}
			}
		},
		KULLBACK{
			public void derivative(final Layer LAYER, final Sample SAMPLE){
				Node.Relation[] FLAT_OUTPUT = LAYER.getFlatOutput();
				final double[] LABEL_LOCATION = SAMPLE.getLabelLocation();

				for(int classs=0; classs < FLAT_OUTPUT.length; classs++){
					setIntoNode(lib.Loss.Kullback.derivative(FLAT_OUTPUT[classs].getOutput(), LABEL_LOCATION[classs]), FLAT_OUTPUT[classs]);
				}
			}
		};

		// abstracts
		/**
		 * Loss function derivative
		 * @param LAYER	layer object
		 * @param SAMPLE Sample object
		 */
		public abstract void derivative(final Layer LAYER, final Sample SAMPLE);
		
		// setters
		/**
		 * Setting the result into the nodes
		 * @param RESULT
		 * @param RELATION
		 */
		private static void setIntoNode(final double RESULT, final Node.Relation ... RELATION){
			for(final Node.Relation REL: RELATION)	REL.addToChainRuleSum(RESULT);
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
		for(int i=1; i < this.LAYERS.length; i++){
			this.LAYERS[i].layerInit(OPT, prevLayer.getNodes());
			prevLayer = this.LAYERS[i];
		}
	}



	
	/**
	 * Feed forward through every layer
	 * @return boolean wether the prediction is correct
	 */
	private void feedForward(){
		// loading the sample
		try{ this.LAYERS[0].sampleLoader(this.sample); }
		catch(Exception e){ System.err.println("Sample loaded into the wrong layer"); }


		for(final Layer LAYER: this.LAYERS) LAYER.feedForward();
	}





	// Performing the back propagation to every layer
	private void backPropagate(){
		this.loss.derivative(LAYERS[this.LAYERS.length-1], this.sample);
		for(int layer = this.LAYERS.length-1; layer >= 0; layer--) this.LAYERS[layer].backPropagating();
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
		
		//cicling over the dataset samples for "EPOCHS" times
		for(int e=0; e < EPOCHS; e++){
			lib.Util.Loading bar = new lib.Util.Loading(DATA_SIZE);
			DATA.shuffle();	// shuffeling the samples

			// cycling over the samples
			for(int sampleIndex=0, batch=0; sampleIndex <= DATA_SIZE; sampleIndex++, batch++){
				this.sample = DATA.getSample(sampleIndex);
				bar.loading("Epoch: "+ (e+1) + " / " + EPOCHS);

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
	 */
	public double validate(){ return validate(this.validateData); }
    public double validate(final DataSet DATA){

		DATA.shuffle();
		int correct = 0;

		for(int sampleIndex=0; sampleIndex < DATA.getSize(); sampleIndex++){
			this.sample = DATA.getSample(sampleIndex);

			feedForward();
			correct += accuracyCheck(DATA)? 1: 0;
		}

		// storing the accuracy percentage
		this.accuracy = (double)correct * 100.00 / (double)DATA.getSize();
		return this.accuracy;
	}


	// checking this sample prediction correctness
	private boolean accuracyCheck(final DataSet DATA){
		
		Node.Relation[] NODES = this.LAYERS[this.LAYERS.length-1].getFlatOutput();

		int answerIndex		= 0;
		double valHolder	= 0; 
		for(int node=0; node < NODES.length; node++){
			if(NODES[node].getOutput() > valHolder){
				answerIndex = node;
				valHolder	= NODES[node].getOutput();
			}
		}

		return this.sample.getLabel() == DATA.getClasses()[answerIndex];
	}






	// ------------------ getter methods ------------------------
	// getting the model accuracy
	public double getAccuracy(){
		return lib.Util.round(this.accuracy, 2);
		}

}
