
public abstract class Layer {

	protected			boolean 	isFirstLayer = false;	//  marking this layer as first layer or a hidden one
    private 	final	Node[]		NODES;					//	this layer nodes container
    protected       	Node[]		inputs;					//	this layer inputs container
	protected       	int			outputSizeY;			//	this layer activation map Y size
	protected       	int			outputSizeX;			//	this layer activation map X size
	protected       	int			inputSizeY;				//	this layer input y size
	protected       	int			inputSizeX;				//	this layer input X size
    private 	final 	int			NODES_AMOUNT;			//	this layer nodes total
    private 	final 	Activation	ACTIVATION;				//	container of activation function and related differentiation
	protected			int     	KERNEL_Y;				//	Size Y of this layer kernel
	protected			int     	KERNEL_X;				//	Size X of this layer kernel
	private	Node.Relation[]			flat_output;			//	array of nodes outputs
	private	Node.Relation[][][][] 	kernelRelations;		//	array of relations between this layer weigths and inputs
	protected	lib.Optimizer		optimizer;			//	learning optimizer

	// activation functions
	public static enum Activation{
		LINEAR{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Linear.function(REL.getFrontLinearOutput(), 1)); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Linear.derivative(REL.getBackLinearOutput(), REL.getOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Linear.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X, 0); }
		}, 
		BINARY{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Binary.function(REL.getFrontLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Binary.derivative(REL.getBackLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Binary.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X, 0); }
		},
		SIGMOID{
			public void function(final Node.Relation REL, final Layer LAYER){  REL.setOutput(lib.Activation.Sigmoid.function(REL.getFrontLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Sigmoid.derivative(REL.getOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Sigmoid.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X, 0); }
		},
		TANH{
			public void function(final Node.Relation REL, final Layer LAYER){  REL.setOutput(lib.Activation.Tanh.function(REL.getFrontLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Tanh.derivative(REL.getOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Tanh.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X, 0); }
		},
		SWISH{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Swish.function(REL.getFrontLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Swish.derivative(REL.getBackLinearOutput(), REL.getOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Swish.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		MISH{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Mish.function(REL.getFrontLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Mish.derivative(REL.getBackLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Mish.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		RELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Relu.function(REL.getFrontLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Relu.derivative(REL.getBackLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Relu.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		LRELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Lrelu.function(REL.getFrontLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Lrelu.derivative(REL.getBackLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Lrelu.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		GELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Gelu.function(REL.getFrontLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Gelu.derivative(REL.getBackLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Gelu.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		SELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Selu.function(REL.getFrontLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Selu.derivative(REL.getBackLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Selu.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		PRELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Prelu.function(REL.getFrontLinearOutput(), 1)); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Prelu.derivative(REL.getBackLinearOutput(), 1)); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Prelu.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		ELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Elu.function(REL.getFrontLinearOutput(), 1)); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Elu.derivative(REL.getBackLinearOutput(), 1, REL.getOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Elu.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		SOFTPLUS{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Softplus.function(REL.getFrontLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Softplus.derivative(REL.getBackLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Softplus.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		SOFTMAX{
			public void function(final Node.Relation REL, final Layer LAYER){ 
				REL.setOutput(lib.Activation.Softmax.function(REL, LAYER.getFlatOutput(), (node) -> node.getFrontLinearOutput()));
			}
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Softmax.derivative()); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Softmax.weightsInit(LAYER.inputs.length * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		};

		// abstract
		public abstract void function(final Node.Relation REL, final Layer LAYER);
		public abstract void derivative(final Node.Relation REL, final Layer LAYER);
		public abstract double randomWeight(final Layer LAYER);

	}



	/**
	 * General onstructor for both Conv2D and Dense
	 * @param N NODES_AMOUNT
	 * @param A ACTIVATION
	 * @param KY KERNEL_Y
	 * @param KX KERNEL_X
	 */
    protected Layer(final int N, final Activation A, final int KY, final int KX){
        NODES_AMOUNT	= N;
        ACTIVATION		= A;
		KERNEL_Y		= KY < 1? 1: KY;
        KERNEL_X		= KX < 1? 1: KX;
		NODES			= new Node[NODES_AMOUNT];
    }

    


    // static constructors

	/**
	 * Conv2D constructor
	 * @param NODES_AMOUNT Quantity of this layer nodes
	 * @param KY Kernel Y size
	 * @param KX Kernel X size
	 * @param ACTIVATION Activation function
	 * @return Layer object
	 */
	public static Conv2D Conv2D(final int NODES_AMOUNT, final int KY, final int KX, final Activation ACTIVATION){
		return new Conv2D(NODES_AMOUNT, KY, KX, ACTIVATION);
	}
	/**
	 * Dense constructor
	 * @param NODES_AMOUNT Quantity of this layer nodes
	 * @param ACTIVATION Activation function
	 * @return Layer object
	 */
	public static Dense Dense(final int NODES_AMOUNT, final Activation ACTIVATION){
		return new Dense(NODES_AMOUNT, ACTIVATION);
	}














	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////// INITIALISERS //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// initialising this layer nodes
	protected void nodesInit(){
        for(int i=0; i< this.NODES_AMOUNT; i++){
            this.NODES[i] = new Node(this.inputs.length, this.KERNEL_Y, this.KERNEL_X, this.outputSizeY, this.outputSizeX, this.optimizer);
        }
    }

	// initialising all the weights of this layer
	protected void weightsInit(){
		for(final Node NODE: this.NODES){

			// cycling over each channel / channel
			for(int channel=0; channel < this.inputs.length; channel++){
				// cycling over the channel weigths
				for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
					for(int kernel_x=0; kernel_x < this.KERNEL_X; kernel_x++){
						// setting the weigth
						NODE.setWeight(channel, kernel_y, kernel_x, this.ACTIVATION.randomWeight(this));
					}
				}
			}
		}
	}

	// flattening this layer output
	protected void flatOutInit(){
		this.flat_output = new Node.Relation[this.outputSizeY * this.outputSizeX * this.NODES_AMOUNT];
		int flt_out_iter = 0;

		// cycling over all this layer node
		for(final Node NODE: this.NODES){
			final Node.Relation[][] OUTPUT_MATRIX = NODE.getOutput();

			// cycling over all the node output values
			for(int outY=0; outY < this.outputSizeY; outY++){
				for(int outX=0; outX < this.outputSizeX; outX++){

					// flattening the node values
					this.flat_output[flt_out_iter++] = OUTPUT_MATRIX[outY][outX];
				}
			}
		}
	}

	// initilising the array of relations between weights and inputs
    protected void kernelRelationsInit(){
        // getting the size of an entire feature map output
        final int PATCH_SIZE	= this.outputSizeY * this.outputSizeX;
        this.kernelRelations 	= new Node.Relation[this.inputs.length][this.KERNEL_Y][this.KERNEL_X][PATCH_SIZE];

        // calculating for possible need for padding
        final int IMAGE_Y		= this.inputSizeY - this.KERNEL_Y;
        final int IMAGE_X		= this.inputSizeX - this.KERNEL_X;
        final int LEFT_IMAGE_Y	= IMAGE_Y>= 0? 0: IMAGE_Y;
        final int LEFT_IMAGE_X	= IMAGE_X>= 0? 0: IMAGE_X;
        final int RIGHT_IMAGE_Y = IMAGE_Y>= 0? IMAGE_Y + 1: 1;
        final int RIGHT_IMAGE_X = IMAGE_X>= 0? IMAGE_X + 1: 1;


        // cycling over the channels
        for(int channel=0; channel < this.inputs.length; channel++){
            final Node.Relation[][] INPUT_NODE = this.inputs[channel].getOutput();
            int relation = 0;

            // cycling over the input image pixels
            for(int image_y = LEFT_IMAGE_Y; image_y < RIGHT_IMAGE_Y; image_y++){
                for(int image_x = LEFT_IMAGE_X; image_x < RIGHT_IMAGE_X; image_x++){

                    // cycling over the kernal weights
                    for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
                        for(int kernel_X=0; kernel_X < this.KERNEL_X; kernel_X++){

                            try{	// storing the relations between weigths and inputs
                                this.kernelRelations[channel][kernel_y][kernel_X][relation] = INPUT_NODE[image_y+kernel_y][image_x+kernel_X];
                            }catch(ArrayIndexOutOfBoundsException e){}
                        }

                    }
                    relation++; // incrementing the relation iterator
                }
            }
        }
    }















	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////// PRINCIPAL AND PUBLIC METHODS ////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////






    // --------------------- feed forward -------------------------

    public void feedForward(){
		// cycling over all this layer nodes
		for(final Node NODE: this.NODES){
			int strideCounter = 0;
			
			 // cycling over all the "pixels" of the output matrix
			 for(int map_y=0; map_y < this.outputSizeY; map_y++){
                for(int map_x=0; map_x < this.outputSizeX; map_x++){
					// getting the output of this activation map index
					final Node.Relation SINGLE_OUTPUT = NODE.getOutput()[map_y][map_x];
                   
                    // cycling over the all the kernel weights
                    for(int channel=0; channel < this.inputs.length; channel++){

						// cycling over this entire channel
                        for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
                            for(int kernel_x=0; kernel_x < this.KERNEL_X; kernel_x++){

								try{	// summing the input times the weight
									SINGLE_OUTPUT.addToLinearOutput(
										NODE.getWeight(channel, kernel_y, kernel_x) * this.kernelRelations[channel][kernel_y][kernel_x][strideCounter].getOutput()
									);
								} catch(NullPointerException e){}
                            }
                        }						
                    }
					// summming the bias
					SINGLE_OUTPUT.addToLinearOutput(NODE.getBias(map_y, map_x));
					// performing the activation function for this output / feature-map "pixel"
					this.ACTIVATION.function(SINGLE_OUTPUT, this);
                    strideCounter++;
                }
            }
		}
    }





    // --------------------- back propagation -------------------------

    // the back propagation method
    public void backPropagating(){
        // cycling overall the nodes
        for(final Node NODE: this.NODES){
            final Node.Relation[][] NODE_OUTPUT		= NODE.getOutput();
            int						strideCounter	= 0;

            // cycling over all the "pixels" of the output matrix
            for(int map_y=0; map_y < this.outputSizeY; map_y++){
                for(int map_x=0; map_x < this.outputSizeX; map_x++){

                    // calculationthe derivative of the non-linear to linear operation
                    final double DERIV_SUM = calculateDerivative(NODE_OUTPUT[map_y][map_x]);
                    // storing the biases gradients
                    NODE.addBiasGradients(DERIV_SUM, map_y, map_x);

                    // cycling over the all the kernel weights
                    for(int channel=0; channel < this.inputs.length; channel++){
                        for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
                            for(int kernel_x=0; kernel_x < this.KERNEL_X; kernel_x++){

								// performing the chain runle operations
                                this.gradientAndPropagate(NODE, DERIV_SUM, channel, kernel_y, kernel_x, strideCounter);
                                                            
                            }
                        }
                    }
                    strideCounter++;
                }
            }
        }
    }

	/**
	 * main chain runle operations
	 * @param NODE Current node to be processed
	 * @param DERIV_SUM Sum of current activation map derivative
	 * @param CHANNEL Current channel to be processed
	 * @param KERNEL_Y Kenrel y position
	 * @param KERNEL_X kernel x position
	 * @param STRIDE Kernel movement
	 */
    private void gradientAndPropagate(final Node NODE, final double DERIV_SUM, final int CHANNEL, final int KERNEL_Y, final int KERNEL_X, final int STRIDE){
        try{
            // --------- GRADIENT DESCENT OPERATION

            // storing the gradient into this layer node
            NODE.addToKernelGradients(
                // summing this output pixel derivative times all its inputs (find new weight gradient)
                DERIV_SUM * this.kernelRelations[CHANNEL][KERNEL_Y][KERNEL_X][STRIDE].getOutput(), 
                CHANNEL,
                KERNEL_Y,
                KERNEL_X
            );


            // --------- BACK PROPAGATION OPERATION
            
            // storing the sum into the next layer node in back propagation way
            this.kernelRelations[CHANNEL][KERNEL_Y][KERNEL_X][STRIDE].addToChainRuleSum(
                // summing this output pixel derivative times all its weights (find new input gradient)
                DERIV_SUM * NODE.getWeight(CHANNEL, KERNEL_Y, KERNEL_X)
            );

        }catch(NullPointerException e){}
    }

    // calculating the derivative of a single output
    private double calculateDerivative(final Node.Relation NODE_SINGLE_OUT){
	    this.ACTIVATION.derivative(NODE_SINGLE_OUT, this);
        NODE_SINGLE_OUT.derivAndCRS_sum();

        return NODE_SINGLE_OUT.getDerivativeSum();
    }





	// --------------------- final update -------------------------

	//Weights Update
    public void updateWeights(){

		// cycling overall the nodes
		for(final Node NODE: this.NODES)	NODE.update(); // updating both weights and biases 	
    }







	// --------------------- getter methods -------------------------


	// getting the whole nodes Array
	public Node[] getNodes(){
		return this.NODES;
	}

	public Node.Relation[] getFlatOutput(){
		return this.flat_output;
	}






	// ------------------------ abstracts ---------------------------


	// initialising sizes
	protected abstract void sizesInit();

	/**
	 * Initialising this layer
	 * @param INPUTS
	 */
	public abstract void layerInit(final lib.Optimizer OPT, final Node ... INPUTS);
	/**
	 * Initialising the first layer
	 * @param SMAPLE
	 */
    public abstract void firstLayerInit(final lib.Optimizer OPT, final Sample SMAPLE);

	/**
	 * Samples Loader
	 * @param SMAPLE
	 * @throws Exception
	 */
	public abstract void sampleLoader(final Sample SMAPLE) throws Exception;

}
