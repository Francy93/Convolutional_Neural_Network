
 public class Dense extends Layer{

	/**
	 * Constructor method
	 * @param NODES_AMOUNT
	 * @param ACTIVATION
	 */
    public Dense(final int NODES_AMOUNT, final Activation ACTIVATION){
        super(NODES_AMOUNT, ACTIVATION, 0, 0);
    }



    
	/**
	 * Hidden lyer initialiser
	 * @param OPT   optimizer 
	 * @param INPUTS 
	 */
    public void layerInit(final lib.Optimizer OPT, final Node ... INPUTS){
        super.inputs	= INPUTS;
		super.optimizer = OPT;

        this.sizesInit();				// initialising this layer sizes
        super.nodesInit();				// initialising this layer nodes
        super.weightsInit();			// initialising all this layer weights
        super.flatOutInit();			// flattening the output
        super.kernelRelationsInit();	// initialising this layer kernel relations
		super.sequencesInit();			// initialising this layer sequences
    }

	/**
	 * First Densenet layer initialiser
	 * @param OPT   optimizer 
	 * @param SAMPLE 
	 */
    public void firstLayerInit(final lib.Optimizer OPT, final Sample SAMPLE){
        final Node[] NODE	= {new Node(SAMPLE.getFeature1D(), OPT)};
        super.isFirstLayer	= true;

        this.layerInit(OPT, NODE);
    }
	/**
	 * First Densenet layer initialiser
	 * @param OPT   optimizer 
	 * @param SAMPLE 
	 */
    public void firstLayerInit(final lib.Optimizer OPT, final int SHAPE_Y, final int SHAPE_X, final int CHANNEL){
        final Node[] NODE	= {new Node(1, SHAPE_Y*SHAPE_X, CHANNEL, OPT)};
        super.isFirstLayer	= true;

        this.layerInit(OPT, NODE);
    }


    protected void sizesInit(){
        // i need to know the size of this layer input / matrix
        final Node.Relation[][] PREV_LYER_OUTPUT = super.inputs[0].getOutput();
        super.inputSizeY	= PREV_LYER_OUTPUT.length;
        super.inputSizeX	= PREV_LYER_OUTPUT[0].length;

        // storing the kernel size
        super.KERNEL_Y		= super.inputSizeY;
        super.KERNEL_X		= super.inputSizeX;

        // i need to calculate how big will be this layer output / matrix
        super.outputSizeY	= 1;
        super.outputSizeX	= 1;
    }


	/**
	 * Samples Loader
	 * @param SMAPLE
	 * @throws Exception
	 */
	public void sampleLoader(final Sample SMAPLE) throws Exception{
		if(!super.isFirstLayer) throw new Exception("SampleLoader() can only be used with the first layer");

		// cycling over this layer inputs array
		for(int node=0; node < super.inputs.length; node++){
			Node.Relation[][] OUTPUT = super.inputs[node].getOutput();
			int sample1D_iterator = 0;

			// cycling over the activation map of this layer nodes
			for(int out_y=0; out_y < OUTPUT.length; out_y++){
				for(int out_x=0; out_x < OUTPUT[0].length; out_x++){
					// loading the sample pixel as it were an output of an ipothtical previous layer
					OUTPUT[out_y][out_x].setOutput(SMAPLE.getToken1D(sample1D_iterator++));
				}
			}
		}
	}
}
