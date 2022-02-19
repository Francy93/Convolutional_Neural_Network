
public abstract class Layer {

	protected			boolean 	isFirstLayer = false;
    private 	final	Node[]		NODES;
    protected       	Node[]		inputs;
	protected       	int			outputSizeY;
	protected       	int			outputSizeX;
	protected       	int			inputSizeY;
	protected       	int			inputSizeX;
    private 	final 	int			NODES_AMOUNT;
    private 	final 	Activation	ACTIVATION;
	protected			int     	KERNEL_Y;
	protected			int     	KERNEL_X;
	private	Node.Relation[]			falt_output;
	private	Node.Relation[][][][] 	kernelRelations;
	//private	Node.Relation[][][] 	outputInputRelations;

	public static enum Activation{
		LINEAR{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Linear.function(REL.getLinearOutput(), 0)); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Linear.derivative(REL.getLinearOutput(), REL.getOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Linear.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		}, 
		BINARY{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Binary.function(REL.getLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Binary.derivative(REL.getLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Binary.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		SIGMOID{
			public void function(final Node.Relation REL, final Layer LAYER){  REL.setOutput(lib.Activation.Sigmoid.function(REL.getLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Sigmoid.derivative(REL.getOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Sigmoid.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		TANH{
			public void function(final Node.Relation REL, final Layer LAYER){  REL.setOutput(lib.Activation.Tanh.function(REL.getLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Tanh.derivative(REL.getOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Tanh.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		SWISH{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Swish.function(REL.getLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Swish.derivative(REL.getLinearOutput(), REL.getOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Swish.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		RELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Relu.function(REL.getLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Relu.derivative(REL.getLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Relu.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		LRELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Lrelu.function(REL.getLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Lrelu.derivative(REL.getLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Lrelu.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		GELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Gelu.function(REL.getLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Gelu.derivative(REL.getLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Gelu.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		SELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Selu.function(REL.getLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Selu.derivative(REL.getLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Selu.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		PRELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Prelu.function(REL.getLinearOutput(), 0)); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Prelu.derivative(REL.getLinearOutput(), 0)); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Prelu.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		ELU{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Elu.function(REL.getLinearOutput(), 0)); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Elu.derivative(REL.getLinearOutput(), 0, REL.getOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Elu.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		SOFTPLUS{
			public void function(final Node.Relation REL, final Layer LAYER){ REL.setOutput(lib.Activation.Softplus.function(REL.getLinearOutput())); }
			public void derivative(final Node.Relation REL, final Layer LAYER){ REL.setDerivative(lib.Activation.Softplus.derivative(REL.getLinearOutput())); }
			public double randomWeight(final Layer LAYER){ return lib.Activation.Softplus.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		},
		SOFTMAX{
			public void function(final Node.Relation REL, final Layer LAYER){ 
				double sum = 0; 
				final double L_O = Math.exp(REL.getLinearOutput()); // getting linear output
		
				for(int index = 0 ; index < LAYER.falt_output.length; index++){
					sum += Math.exp(LAYER.falt_output[index].getLinearOutput());
				}

				REL.setOutput(L_O/sum);
			}
			public void derivative(final Node.Relation REL, final Layer LAYER){ 
				double sum = 0;
				final double N_L_O =  REL.getOutput(); // getting non linear output

				for(int index = 0 ; index < LAYER.falt_output.length; index++){
					sum += REL == LAYER.falt_output[index]? N_L_O * (1 - N_L_O): -LAYER.falt_output[index].getOutput() * N_L_O;
				}

				REL.setDerivative(sum);
			}
			public double randomWeight(final Layer LAYER){ return lib.Activation.Softmax.randomWeight(LAYER.NODES_AMOUNT * LAYER.KERNEL_Y * LAYER.KERNEL_X); }
		};

		// abstract
		public abstract void function(final Node.Relation REL, final Layer LAYER);

		public abstract void derivative(final Node.Relation REL, final Layer LAYER);
		
		public abstract double randomWeight(final Layer LAYER);

	}




    protected Layer(final int N, final Activation A, final int KY, final int KX){
        NODES_AMOUNT	= N;
        ACTIVATION		= A;
		KERNEL_Y		= KY < 1? 1: KY;
        KERNEL_X		= KX < 1? 1: KX;
		NODES			= new Node[NODES_AMOUNT];
    }

    


    // static constructors


	public static Conv2D Conv2D(final int NODES_AMOUNT, final int KY, final int KX, final Activation ACTIVATION){
		return new Conv2D(NODES_AMOUNT, KY, KX, ACTIVATION);
	}

	public static Dense Dense(final int NODES_AMOUNT, final Activation ACTIVATION){
		return new Dense(NODES_AMOUNT, ACTIVATION);
	}














	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////// INITIALISERS //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	protected void nodesInit(){
        for(int i=0; i< this.NODES_AMOUNT; i++){
            this.NODES[i] = new Node(this.inputs.length, this.KERNEL_Y, this.KERNEL_X, this.outputSizeY, this.outputSizeX);
        }
    }

	// initialising all the weights of this layer
	protected void weightsInit(){
		for(int node=0; node < this.NODES_AMOUNT; node++){
			final Node NODE = this.NODES[node];

			// cycling over each channel / filter
			for(int filter=0; filter < this.inputs.length; filter++){
				// cycling over the filter weigths
				for(int kernel_y=0; kernel_y < this.NODES_AMOUNT; kernel_y++){
					for(int kernel_x=0; kernel_x < this.NODES_AMOUNT; kernel_x++){
						// setting the weigth
						NODE.setWeight(filter, kernel_y, kernel_x, this.ACTIVATION.randomWeight(this));
					}
				}
			}
		}
	}

	// flattening this layer output
	protected void flatOutInit(){
		this.falt_output = new Node.Relation[this.outputSizeY * this.outputSizeX * this.NODES_AMOUNT];
		int flt_out_iter = 0;

		// cycling over all this layer node
		for(int node=0; node < this.NODES_AMOUNT; node++){
			final Node.Relation[][] OUTPUT_MATRIX = this.NODES[node].getOutput();

			// cycling over all the node output values
			for(int outY=0; outY < this.outputSizeY; outY++){
				for(int outX=0; outX < this.outputSizeY; outX++){

					// flattening the node values
					this.falt_output[flt_out_iter++] = OUTPUT_MATRIX[outY][outX];
				}
			}
		}
	}

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


        // cycling over the filters
        for(int filter=0; filter < this.inputs.length; filter++){
            final Node.Relation[][] INPUT_NODE = this.inputs[filter].getOutput();
            int relation = 0;

            // cycling over the input image pixels
            for(int image_y = LEFT_IMAGE_Y; image_y < RIGHT_IMAGE_Y; image_y++){
                for(int image_x = LEFT_IMAGE_X; image_x < RIGHT_IMAGE_X; image_x++){

                    // cycling over the kernal weights
                    for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
                        for(int kernel_X=0; kernel_X < this.KERNEL_X; kernel_X++){

                            try{	// storing the relations between weigths and inputs
                                this.kernelRelations[filter][kernel_y][kernel_X][relation] = INPUT_NODE[image_y+kernel_y][image_x+kernel_X];
                            }catch(Exception e){}
                        }

                    }
                    relation++; // incrementing the relation iterator
                }
            }
        }
    }

    /* protected void outputInputRelationsInit(){
        // getting the relations amount of a each output pixel
        final int RELATIONS_AMOUNT = this.KERNEL_Y * this.KERNEL_X * this.kernelRelations.length;
        this.outputInputRelations = new Node.Relation[this.outputSizeY][this.outputSizeX][RELATIONS_AMOUNT];

        int stride = 0;
        // cycling over the output matrix 
        for(int y=0; y < this.outputSizeY; y++){
            for(int x=0; x < this.outputSizeX; x++){
                int filterIndex = 0;	// counting the filter index
                
                // cycling over all the filters
                for(int filter=0; filter < this.kernelRelations.length; filter++){

                    // cycling over the relation set of each filter weigth
                    for(int kernel_y=0; kernel_y < this.kernelRelations[filter].length; kernel_y++){
                        for(int kernel_x=0; kernel_x < this.kernelRelations[filter][0].length; kernel_x++){

                            // storing the relation between input and output (patch) of every weight
                            this.outputInputRelations[y][x][filterIndex++] = this.kernelRelations[filter][kernel_y][kernel_x][stride];
                        }
                    }
                }
                stride++;				// Stride counter. Moving by 1 kernel sliding (next filter-inputImage relation)
            }
        }
    } */
	















	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////// PRINCIPAL AND PUBLIC METHODS ////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////






    // --------------------- feed forward -------------------------

    public void feedForward(){
		// cycling over all this layer nodes
		for(int node=0; node < this.NODES_AMOUNT; node++){
			final Node NODE = this.NODES[node];
			int strideCounter = 0;

			 // cycling over all the "pixels" of the output matrix
			 for(int map_y=0; map_y < this.outputSizeY; map_y++){
                for(int map_x=0; map_x < this.outputSizeX; map_x++){
					// getting the output of this activation map index
					Node.Relation SINGLE_OUTPUT = NODE.getOutput()[map_y][map_x];
                   
                    // cycling over the all the kernel weights
                    for(int filter=0; filter < this.inputs.length; filter++){

						// cycling over this entire filter
                        for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
                            for(int kernel_x=0; kernel_x < this.KERNEL_X; kernel_x++){

								try{	// summing the input times the weight
									SINGLE_OUTPUT.addToLinearOutput(
										NODE.getWeight(filter, kernel_y, kernel_x) * this.kernelRelations[filter][kernel_y][kernel_x][strideCounter].getOutput()
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
        for(int node=0; node < this.NODES_AMOUNT; node++){
            final Node				NODE		= this.NODES[node];
            final Node.Relation[][] NODE_OUTPUT	= NODE.getOutput();
            int strideCounter = 0;

            // cycling over all the "pixels" of the output matrix
            for(int map_y=0; map_y < this.outputSizeY; map_y++){
                for(int map_x=0; map_x < this.outputSizeX; map_x++){

                    // calculationthe derivative of the non-linear to linear operation
                    final double DERIV_SUM = calculateDerivative(NODE_OUTPUT[map_y][map_x]);
                    // storing the biases gradients
                    NODE.addBiasGradients(DERIV_SUM, map_y, map_x);

                    // cycling over the all the kernel weights
                    for(int filter=0; filter < this.inputs.length; filter++){
                        for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
                            for(int kernel_x=0; kernel_x < this.KERNEL_X; kernel_x++){

								// performing the chain runle operations
                                this.gradientAndPropagate(NODE, DERIV_SUM, filter, kernel_y, kernel_x, strideCounter);
                                                            
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
	 * @param NODE
	 * @param DERIV_SUM
	 * @param FILTER
	 * @param KERNEL_Y
	 * @param KERNEL_X
	 * @param STRIDE
	 */
    private void gradientAndPropagate(final Node NODE, final double DERIV_SUM, final int FILTER, final int KERNEL_Y, final int KERNEL_X, final int STRIDE){
        try{
            // --------- GRADIENT DISCENT OPERATION

            // storing the gradient into this layer node
            NODE.addToKernelGradients(
                // summing this output pixel derivative times all its inputs (find new weight gradient)
                DERIV_SUM * this.kernelRelations[FILTER][KERNEL_Y][KERNEL_X][STRIDE].getOutput(), 
                FILTER,
                KERNEL_Y,
                KERNEL_X
            );


            // --------- BACK PROPAGATION OPERATION
            
            // storing the sum into the next layer node in back propagation way
            this.kernelRelations[FILTER][KERNEL_Y][KERNEL_X][STRIDE].addToChainRuleSum(
                // summing this output pixel derivative times all its weights (find new input gradient)
                DERIV_SUM * NODE.getWeight(FILTER, KERNEL_Y, KERNEL_X)
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

	/**
	 * Weights Update
	 * @param BATCH_SIZE
	 * @param LEARNING_RATE
	 */
    public void updateWeights(final int BATCH_SIZE, final double LEARNING_RATE){

		// cycling overall the nodes
		for(int node=0; node < this.NODES_AMOUNT; node++){
			final Node NODE = this.NODES[node];

			// updating both weights and biases 
			NODE.weightsUpdate(BATCH_SIZE, LEARNING_RATE);
			NODE.biasUpdate(BATCH_SIZE, LEARNING_RATE);

		}
    }







	// --------------------- getter methods -------------------------


	// getting the whole nodes Array
	public Node[] getNodes(){
		return this.NODES;
	}



	// ------------------------ abstracts ---------------------------

	// initialising sizes
	protected abstract void sizesInit();

	/**
	 * Initialising this layer
	 * @param INPUTS
	 */
	public abstract void layerInit(final Node ... INPUTS);
	/**
	 * Initialising the first layer
	 * @param SMAPLE
	 */
    public abstract void firstLayerInit(final Sample SMAPLE);

	/**
	 * Samples Loader
	 * @param SMAPLE
	 * @throws Exception
	 */
	public abstract void sampleLoader(final Sample SMAPLE) throws Exception;

}
