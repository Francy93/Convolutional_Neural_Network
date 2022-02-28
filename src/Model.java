
public class Model {

	private			double		accuracy = 0;	// to store the accuracy result
	private			DataSet 	trainData;		// datastructure of training samples
	private			DataSet 	validateData;	// datastructure of validation samples
	private final	Layer[]		LAYERS;			// array containing all the layers
	private 		int			miniBatch;		// size of mini batch
	private 		double		learningRate;	// learning rate value
	private 		Loss		loss;			// loss operations
	private 		Sample		sample;			// index of the current iterated sample

	public static enum Loss {
		MSE{
			public void derivative(final Layer LAYER, final Sample SAMPLE){
				Node.Relation[] FLAT_OUTPUT = LAYER.getFlatOutput();
				final double[] OUTPUT_VALUES = getOutputValues(FLAT_OUTPUT);
				final double[] LABEL_LOCATION = SAMPLE.getLabelLocation();

				for(int val=0; val < OUTPUT_VALUES.length; val++){
					setIntoNode(lib.Loss.MSE.derivative(OUTPUT_VALUES[val], LABEL_LOCATION[val]) / FLAT_OUTPUT.length, FLAT_OUTPUT[val]);
				}
			}
		},
		MAE{
			public void derivative(final Layer LAYER, final Sample SAMPLE){
				Node.Relation[] FLAT_OUTPUT = LAYER.getFlatOutput();
				final double[] OUTPUT_VALUES = getOutputValues(FLAT_OUTPUT);
				final double[] LABEL_LOCATION = SAMPLE.getLabelLocation();

				for(int val=0; val < OUTPUT_VALUES.length; val++){
					setIntoNode(lib.Loss.MAE.derivative(OUTPUT_VALUES[val], LABEL_LOCATION[val]), FLAT_OUTPUT[val]);
				}
			}
		},
		CROSS_ENTROPY{
			public void derivative(final Layer LAYER, final Sample SAMPLE){
				Node.Relation[] FLAT_OUTPUT = LAYER.getFlatOutput();
				//final double[] OUTPUT_VALUES = getOutputValues(FLAT_OUTPUT);
				final double[] LABEL_LOCATION = SAMPLE.getLabelLocation();

				//double meanError = 0;
				for(int classs=0; classs < FLAT_OUTPUT.length; classs++){
					setIntoNode(lib.Loss.Cross_Entropy.derivative(FLAT_OUTPUT[classs].getOutput(), LABEL_LOCATION[classs]), FLAT_OUTPUT[classs]);
					//setIntoNode(lib.Loss.Cross_Entropy.derivative(lib.Loss.Cross_Entropy.function(FLAT_OUTPUT[classs].getOutput(), LABEL_LOCATION[classs]), LABEL_LOCATION[classs]));
					//meanError += lib.Loss.Cross_Entropy.function(FLAT_OUTPUT[classs].getOutput(), LABEL_LOCATION[classs]);
					//System.out.print( lib.Util.round(FLAT_OUTPUT[classs].getBackLinearOutput(), 5) + "	" );
				}
				//System.out.println(meanError/FLAT_OUTPUT.length);
				//System.out.println();
				/* int val = 4;
				if(SAMPLE.getLabel() == val){
					
					for(int classs=0; classs < FLAT_OUTPUT.length; classs++){
						String str = "";
						if(val == classs) str = lib.Util.color("yellow");
						str +="C" + classs + " = " + lib.Util.round(FLAT_OUTPUT[classs].getBackLinearOutput(), 5) + "	";
						//str +="C" + classs + " = " + lib.Util.round(lib.Loss.Cross_Entropy.function(FLAT_OUTPUT[classs].getOutput(), LABEL_LOCATION[classs]), 5) + "	";
						if(val == classs) str += lib.Util.color("reset");
						System.out.print(str);
					}
					System.out.println();
					//System.out.print("C: " + classs + "	" + lib.Util.round(lib.Loss.Cross_Entropy.function(FLAT_OUTPUT[classs].getOutput(), LABEL_LOCATION[classs]), 5) + "	");
					//System.out.println(lib.Loss.Cross_Entropy.function(FLAT_OUTPUT[val].getOutput(), LABEL_LOCATION[val]));
					//System.out.println(lib.Loss.Cross_Entropy.derivative(lib.Loss.Cross_Entropy.function(FLAT_OUTPUT[val].getOutput(), LABEL_LOCATION[val]), LABEL_LOCATION[val]) + " <--	predicted | target	--> " + LABEL_LOCATION[val]);
					//System.out.println(lib.Loss.Cross_Entropy.derivative(FLAT_OUTPUT[5].getOutput(), LABEL_LOCATION[5]));
				} */
			}
		},
		HUBER{
			public void derivative(final Layer LAYER, final Sample SAMPLE){
				Node.Relation[] FLAT_OUTPUT = LAYER.getFlatOutput();
				final double[] OUTPUT_VALUES = getOutputValues(FLAT_OUTPUT);
				final double[] LABEL_LOCATION = SAMPLE.getLabelLocation();

				for(int val=0; val < OUTPUT_VALUES.length; val++){
					setIntoNode(lib.Loss.Huber.derivative(OUTPUT_VALUES[val], LABEL_LOCATION[val]), FLAT_OUTPUT[val]);
				}
			}
		},
		KULLBACK{
			public void derivative(final Layer LAYER, final Sample SAMPLE){
				Node.Relation[] FLAT_OUTPUT = LAYER.getFlatOutput();
				final double[] OUTPUT_VALUES = getOutputValues(FLAT_OUTPUT);
				final double[] LABEL_LOCATION = SAMPLE.getLabelLocation();

				for(int val=0; val < OUTPUT_VALUES.length; val++){
					setIntoNode(lib.Loss.Kullback.derivative(OUTPUT_VALUES[val], LABEL_LOCATION[val]), FLAT_OUTPUT[val]);
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
		
		// getters 

		/**
		 * getting an array of nodes output
		 * @param FLAT_OUTPUT
		 * @return values of the outputs
		 */
		private static double[] getOutputValues(final Node.Relation[] FLAT_OUTPUT){
				final double[] OUTPUT_VALUES = new double[FLAT_OUTPUT.length];

				for(int output=0; output < FLAT_OUTPUT.length; output++){
					OUTPUT_VALUES[output] = FLAT_OUTPUT[output].getOutput();
				}
			return OUTPUT_VALUES;
		}

		// setters

		/**
		 * Setting the result into the nodes
		 * @param RESULT
		 * @param RELATION
		 */
		private static void setIntoNode(final double RESULT, final Node.Relation ... RELATION){
			for(final Node.Relation REL: RELATION){
				REL.addToChainRuleSum(RESULT);
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

	public static Model Sequential(final Layer ... L){
		return new Model(L);
	}





	/**
	 * Building the Neural Network structure
	 * @param DATA Dataset
	 * @param L loss function
	 */
	public void buildStructure(final DataSet DATA, final Loss L){
		this.loss = L;

		Layer prevLayer = this.LAYERS[0];				// previous layer
		prevLayer.firstLayerInit(DATA.getSample(0));	// initialising the input layer
							

		for(int i=1; i < this.LAYERS.length; i++){
			this.LAYERS[i].layerInit(prevLayer.getNodes());
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
		for(final Layer LAYER: this.LAYERS) LAYER.updateWeights(this.miniBatch, this.learningRate);
	}




	/**
	 * Model training
	 * @param DATA dataset
	 * @param BATCH mini batch size
	 * @param EPOCHS cicles of entire dataset
	 * @param LEARNING_RATE learning rate
	 */
	public void train(final DataSet DATA,final int BATCH, final int EPOCHS, final double LEARNING_RATE){
		this.trainData		= DATA;
		this.miniBatch		= BATCH;
		this.learningRate	= LEARNING_RATE;

		final int DATA_SIZE = this.trainData.getSize()-1;
		
		//cicling over the dataset samples for "EPOCHS" times
		for(int e=0; e < EPOCHS; e++){
			this.trainData.shuffle();	// shuffeling the samples
			lib.Util.Loading bar = new lib.Util.Loading();
			System.out.println("Epoch: "+ (e+1));

			for(int sampleIndex=0, batch=0; sampleIndex <= DATA_SIZE; sampleIndex++, batch++){
				this.sample = DATA.getSample(sampleIndex);
				bar.loading(DATA_SIZE, sampleIndex);

				feedForward();
				backPropagate();

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
    public double validate(final DataSet DATA){
		this.validateData = DATA;
		this.validateData.shuffle();
		int correct = 0;

		for(int sampleIndex=0; sampleIndex < this.validateData.getSize(); sampleIndex++){
			this.sample = this.validateData.getSample(sampleIndex);

			feedForward();
			correct += accuracyCheck(this.validateData)? 1: 0;
		}

		// storing the accuracy percentage
		this.accuracy = (double)correct * 100.00 / (double)validateData.getSize();
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
		
		/* String str = "";
		
		if(DATA.getClasses()[answerIndex] == this.sample.getLabel()) str = lib.Util.color("green");
		else str = lib.Util.color("red");

		str += "Predicted: " + DATA.getClasses()[answerIndex] + "	Target: " + this.sample.getLabel();
		System.out.println(str + lib.Util.color("reset")); */
		// checking if the prediction matches the actual label
		return this.sample.getLabel() == DATA.getClasses()[answerIndex];
	}






	// ------------------ getter methods ------------------------
	// getting the model accuracy
	public double getAccuracy(){
		return lib.Util.round(this.accuracy, 2);
		}

}
