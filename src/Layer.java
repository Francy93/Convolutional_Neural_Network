
public abstract class Layer {
	protected			boolean 	isFirstLayer = false;
    protected 	final	Node[]		NODES;
    protected       	Node[]		inputs;
	protected       	int			outputSizeY;
	protected       	int			outputSizeX;
	protected       	int			inputSizeY;
	protected       	int			inputSizeX;
    protected 	final 	int			NODES_AMOUNT;
    protected 	final 	Activation	ACTIVATION;
	protected			int     	KERNEL_Y;
	protected			int     	KERNEL_X;
	protected	Node.Relation[]		falt_output;
	protected	Node.Relation[][][][] 	kernelRelations;
	protected	Node.Relation[][][] 	outputInputRelations;

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

    
    


	public static Conv2D Conv2D(final int NODES_AMOUNT, final int KY, final int KX, final Activation ACTIVATION){
		return new Conv2D(NODES_AMOUNT, KY, KX, ACTIVATION);
	}

	public static Dense Dense(final int NODES_AMOUNT, final Activation ACTIVATION){
		return new Dense(NODES_AMOUNT, ACTIVATION);
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


	public Node[] getNodes(){
		return this.NODES;
	}



	private abstract double calculateDerivative(final Node.Relation NODE_SINGLE_OUT);

	//public abstract void prevLayerPropagate();

    public abstract void updateWeights();


	public abstract void layerInit(final Node ... INPUTS);
    public abstract void layerInit(final Sample SMAPLE);


    


}
