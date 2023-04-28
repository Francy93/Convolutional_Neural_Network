
import java.util.Arrays;				// used to execute in parallel
import java.util.ArrayList;				// used to convert the array to a list


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
	protected			DotProd[]	forwardSequence;		//	sequence of nodes forward propagation
	protected			DotProd[]	backwardSequence;		//	sequence of nodes backward propagation
	protected			DotProd[]	derivativeSequence;		//	sequence of nodes backward propagation
	protected		lib.Optimizer	optimizer;				//	learning optimizer
	
	// collection of activation functions
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
		this.NODES_AMOUNT	= N;
        this.ACTIVATION		= A;
		this.KERNEL_Y		= KY < 1? 1: KY;
        this.KERNEL_X		= KX < 1? 1: KX;
		this.NODES			= new Node[NODES_AMOUNT];
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


	// DotProd class
	public abstract class DotProd{
		protected Node.Relation[]	inputs;	// input node relations
		protected Node.Parameter[]	param;	// node weights
		protected Layer				layer;	// layer object
		public abstract void propagate();	// update the parameters
	}
	// Forward class
	public class Forward extends DotProd{
		private final Node.Relation 	OUTPUT;	// output node relation
		private final Node.Parameter	BIAS;	// node bias

		public Forward(final Node.Relation OUTPUT, final Node.Relation[] INPUTS, final Node.Parameter[] PARAM, final Node.Parameter BIAS, final Layer LAYER){
			this.OUTPUT		= OUTPUT;	// output node relation
			super.inputs	= INPUTS;	// input node relations
			super.param		= PARAM;	// node weights
			this.BIAS		= BIAS;		// node bias
			super.layer		= LAYER;	// layer object
		}

		@Override
		public void propagate(){
			for(int i=0; i < super.inputs.length; i++){
				this.OUTPUT.addToLinearOutput(super.inputs[i].getOutput() * super.param[i].getWeight());
			}
			this.OUTPUT.addToLinearOutput(this.BIAS.getWeight());		// add bias
			super.layer.ACTIVATION.function(this.OUTPUT, super.layer);	// calculating the activation function
		}
	}
	// Backward class
	public class Backward extends DotProd{
		private final Node.Relation[] 	OUTPUTS;	// output node relation

		public Backward(final Node.Relation[] OUTPUTS, final Node.Relation[] INPUTS, final Node.Parameter[] PARAM, final Layer LAYER){
			this.OUTPUTS	= OUTPUTS;	// output node relations
			super.inputs	= INPUTS;	// input node relations
			super.param		= PARAM;	// node weights
			super.layer		= LAYER;	// layer object
		}

		@Override
		public void propagate(){
			for(int i=0; i < super.inputs.length; i++){
				// --------- BACK PROPAGATION OPERATION
				super.inputs[i].addToChainRuleSum(this.OUTPUTS[i].getDerivativeSum() * super.param[i].getWeight());
				// --------- GRADIENT DESCENT OPERATION
				super.param[i].addGradient(this.OUTPUTS[i].getDerivativeSum() * super.inputs[i].getOutput());
			}
		}
	}
	// Derivative class
	public class Derivative extends DotProd{
		private final Node.Relation 	OUTPUT;	// output node relation
		private final Node.Parameter	BIAS;	// node bias

		public Derivative(final Node.Relation OUTPUT, final Node.Parameter PARAM, final Layer LAYER){
			this.OUTPUT	= OUTPUT;	// output node relationss
			this.BIAS	= PARAM;	// node weights
			super.layer	= LAYER;	// layer object
		}

		@Override
		public void propagate(){ 
			this.BIAS.addGradient(this.calculateDerivative(this.OUTPUT));
		}

		/**
		 * Calculating the derivative of a single output
		 * @param NODE_SINGLE_OUT 	the output to calculate the derivative of
		 * @return the derivative of the output
		 */
		private double calculateDerivative(final Node.Relation NODE_SINGLE_OUT){
			super.layer.ACTIVATION.derivative(NODE_SINGLE_OUT, super.layer);	// calculating the derivative of the activation function
			NODE_SINGLE_OUT.derivAndCRS_sum();									// calculating the derivative of the non-linear to linear operation

			return NODE_SINGLE_OUT.getDerivativeSum();
		}
	}












	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////// INITIALISERS //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// initialising this layer nodes
	protected void nodesInit(){
        for(int index=0; index<this.NODES_AMOUNT; index++){
            this.NODES[index] = new Node(this.inputs.length, this.KERNEL_Y, this.KERNEL_X, this.outputSizeY, this.outputSizeX, this.optimizer);
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

        // calculating the possible need for padding
        final int IMAGE_Y		= this.inputSizeY - this.KERNEL_Y;	// if negative, padding is needed
        final int IMAGE_X		= this.inputSizeX - this.KERNEL_X;	// if negative, padding is needed
        final int LEFT_IMAGE_Y	= IMAGE_Y>= 0? 0: IMAGE_Y;			// if Y is greater than 0, no padding is needed
        final int LEFT_IMAGE_X	= IMAGE_X>= 0? 0: IMAGE_X;			// if X is greater than 0, no padding is needed
        final int RIGHT_IMAGE_Y = IMAGE_Y>= 0? IMAGE_Y + 1: 1;		// if Y is greater than 0, no padding is needed
        final int RIGHT_IMAGE_X = IMAGE_X>= 0? IMAGE_X + 1: 1;		// if X is greater than 0, no padding is needed


        // cycling over the channels
        for(int channel=0; channel < this.inputs.length; channel++){
            final Node.Relation[][] INPUT_NODE = this.inputs[channel].getOutput();
            int relation = 0;

            // cycling over the input image pixels
            for(int image_y = LEFT_IMAGE_Y; image_y < RIGHT_IMAGE_Y; image_y++){
                for(int image_x = LEFT_IMAGE_X; image_x < RIGHT_IMAGE_X; image_x++){

                    // cycling over the kernal weights
                    for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
                        for(int kernel_x=0; kernel_x < this.KERNEL_X; kernel_x++){

                            try{	// storing the relations between weigths and inputs
                                this.kernelRelations[channel][kernel_y][kernel_x][relation] = INPUT_NODE[image_y+kernel_y][image_x+kernel_x];
                            }catch(ArrayIndexOutOfBoundsException e){}
                        }

                    }
                    relation++; // incrementing the relation iterator
                }
            }
        }
    }

	// initialising the array of relations between weights and inputs
	public void forwardSequence(){
		final int OUTPUT_SIZE =	this.outputSizeY*this.outputSizeX;
		// cycling over all this layer nodes
		for(int nodeIndex=0; nodeIndex < this.NODES_AMOUNT; nodeIndex++){
			final Node NODE = this.NODES[nodeIndex];
			int strideCounter = 0;
			
			// cycling over all the "pixels" of the output matrix
			for(int map_y=0; map_y < this.outputSizeY; map_y++){
				for(int map_x=0; map_x < this.outputSizeX; map_x++){
					// getting the output of this activation map index
					final ArrayList<Node.Relation> INPUTS = new ArrayList<>();
					final ArrayList<Node.Parameter> PARAM = new ArrayList<>();
					// cycling over the all the kernel weights
					for(int channel=0; channel < this.inputs.length; channel++){
						// cycling over this entire channel
						for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
							for(int kernel_x=0; kernel_x < this.KERNEL_X; kernel_x++){
								try{
									if (this.kernelRelations[channel][kernel_y][kernel_x][strideCounter] == null) continue;
									// collecting the relations and parameters for the dot product
									INPUTS.add(this.kernelRelations[channel][kernel_y][kernel_x][strideCounter]);
									PARAM.add(NODE.getWeight(channel, kernel_y, kernel_x));
								}catch(NullPointerException e){}
							}
						}						
					}
					// storing the sequence of relations
					this.forwardSequence[nodeIndex*OUTPUT_SIZE+strideCounter++] = new Forward(
						NODE.getOutput()[map_y][map_x],
						INPUTS.toArray(new Node.Relation[INPUTS.size()]),
						PARAM.toArray(new Node.Parameter[PARAM.size()]),
						NODE.getBias(map_y, map_x),
						this
					);
				}
			}
		}
	}
	
	// initialising the array of derivatives and relations between weights and inputs
	public void backwardSequence(){
		// storing the sequence of derivatives
		this.derivativeSequence = Arrays.stream(this.NODES).map(node -> {
				final Derivative[] DERIVATIVE = new Derivative[this.outputSizeY*this.outputSizeX];
				for(int map_y=0; map_y < this.outputSizeY; map_y++){
					for(int map_x=0; map_x < this.outputSizeX; map_x++){
						DERIVATIVE[map_y*this.outputSizeY+map_x] = new Derivative(node.getOutput()[map_y][map_x], node.getBias(map_y, map_x), this);
					}
				}return DERIVATIVE;
			}).flatMap(Arrays::stream).toArray(Derivative[]::new);

		// getting the gradient descent sequence
		for(int nodeIndex=0; nodeIndex < this.NODES_AMOUNT; nodeIndex++){
			final Node NODE = this.NODES[nodeIndex];
			int	strideCounter	= 0;
			final ArrayList<Node.Relation> INPUTS	= new ArrayList<>();	// inputs
			final ArrayList<Node.Relation> OUTPUTS	= new ArrayList<>();	// output
			final ArrayList<Node.Parameter> PARAM	= new ArrayList<>();	// weight

			// cycling over all the "pixels" of the output matrix
			for(int map_y=0; map_y < this.outputSizeY; map_y++){
			  	for(int map_x=0; map_x < this.outputSizeX; map_x++){
	
					// cycling over the all the kernel weights
					for(int channel=0; channel < this.inputs.length; channel++){
						for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
							for(int kernel_x=0; kernel_x < this.KERNEL_X; kernel_x++){
								try{
									if (this.kernelRelations[channel][kernel_y][kernel_x][strideCounter] == null) continue;
									// collecting the relations and parameters for the dot product
									INPUTS.add(this.kernelRelations[channel][kernel_y][kernel_x][strideCounter]);	// inputs	
									OUTPUTS.add(NODE.getOutput()[map_y][map_x]);									// output
									PARAM.add(NODE.getWeight(channel, kernel_y, kernel_x));							// weight
								}catch(NullPointerException e){}		
							}
						}
					}
					strideCounter++;
				}
			}
			// storing the sequence of relations
			this.backwardSequence[nodeIndex] = new Backward(
				OUTPUTS.toArray(new Node.Relation[OUTPUTS.size()]),	// output
				INPUTS.toArray(new Node.Relation[INPUTS.size()]),	// inputs
				PARAM.toArray(new Node.Parameter[PARAM.size()]),	// weight
				this												// layer
		  	);
		}
	}

	// initialising the array of sequences of relations
	protected void sequencesInit(){
		final int FORWARD_SIZE	= this.NODES_AMOUNT * this.outputSizeY * this.outputSizeX;
		final int BACKWARD_SIZE	= this.NODES_AMOUNT;
		this.forwardSequence	= new DotProd[FORWARD_SIZE];		// storing the Forward sequence
		this.backwardSequence	= new DotProd[BACKWARD_SIZE];		// storing the Backward sequence

		this.forwardSequence();										// initialising the forward sequence
		this.backwardSequence();									// initialising the backward sequence
	}















	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////// PRINCIPAL AND PUBLIC METHODS ////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////






    // --------------------- feed forward -------------------------

	public void feedForward(){
		// cycling over all this layer nodes
		Arrays.stream(this.forwardSequence).parallel().forEach(DotProd::propagate);		// calculating the dot product
	}

	
	// --------------------- back propagation -------------------------

	public void backPropagating(){
		// cycling over this layer outputs
		Arrays.stream(this.derivativeSequence).parallel().forEach(DotProd::propagate);	// calculating the derivative
		// cycling over the inputs relations
		Arrays.stream(this.backwardSequence).parallel().forEach(DotProd::propagate);	// calculating the dot product
	}	

	
	// --------------------- final update -------------------------

	// updating the weights and biases
	public void updateWeights(){
		// cycling over all the nodes
		Arrays.stream(this.NODES).parallel().forEach(Node::update);						// updating both weights and biases
	}



	









	// --------------------- getter methods -------------------------


	// getting the whole nodes Array
	public Node[] getNodes(){ return this.NODES; }
	// getting the flattened output
	public Node.Relation[] getFlatOutput(){ return this.flat_output; }
	// getting inputs
	public Node[] getInputs(){ return this.inputs; }






	// ------------------------ abstracts ---------------------------


	// initialising sizes
	protected abstract void sizesInit();

	/**
	 * Initialising this layer
	 * @param OPT		Optimizer of this layer
	 * @param INPUTS	Inputs of this layer
	 */
	public abstract void layerInit(final lib.Optimizer OPT, final Node ... INPUTS);
	/**
	 * Initialising the first layer
	 * @param OPT		Optimizer of this layer
	 * @param SMAPLE	Sample to be loaded
	 */
    public abstract void firstLayerInit(final lib.Optimizer OPT, final Sample SMAPLE);

	/**
	 * Initialising the first layer
	 * @param SHAPE_Y	size y of the input
	 * @param SHAPE_X	size x of the input	
	 * @param CHANNELS	channels of the input
	 */
	public abstract void firstLayerInit(final lib.Optimizer OPT, final int SHAPE_Y, final int SHAPE_X, final int CHANNELS);

	/**
	 * Samples Loader
	 * @param SMAPLE
	 * @throws Exception
	 */
	public abstract void sampleLoader(final Sample SMAPLE) throws Exception;

}
