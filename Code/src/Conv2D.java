
public class Conv2D extends Layer{

	/**
	 * Layer constructos
	 * @param NODES_AMOUNT Number of nodes
	 * @param KY Kernel Y size
	 * @param KX Kernel X size
	 * @param ACTIVATION Activation function
	 */
	public Conv2D(final int NODES_AMOUNT, final int KY, final int KX, final Activation ACTIVATION){
		super(NODES_AMOUNT, ACTIVATION, KY, KX);
	}


	// --------------------- initialising -------------------------




	/**
	 * Hidden lyer initialiser
	 * @param OPT   optimizer 
	 * @param INPUTS previous layer output
	 */
	public void layerInit(final lib.Optimizer OPT, final Node ... INPUTS){
		super.inputs    = INPUTS;
		super.optimizer = OPT;

		this.sizesInit();				// initialising this layer sizes
		super.nodesInit();				// initialising this layer nodes
		super.weightsInit();			// initialising all this layer weights
		super.flatOutInit();			// flattening the output
		super.kernelRelationsInit();	// initialising this layer kernel relations
		super.sequencesInit();			// initialising this layer sequences
	}

	/**
	 * First Convolutional layer initialiser
	 * @param OPT   optimizer 
	 * @param SAMPLE 
	 */
	public void firstLayerInit(final lib.Optimizer OPT, final Sample SAMPLE){
		final Node[] NODE	= {new Node(SAMPLE.getFeature2D(), OPT)}; 
		super.isFirstLayer	= true;

		this.layerInit(OPT, NODE);
	}
	/**
	 * First Densenet layer initialiser
	 * @param OPT   optimizer 
	 * @param SAMPLE 
	 */
	public void firstLayerInit(final lib.Optimizer OPT, final int SHAPE_Y, final int SHAPE_X, final int CHANNEL){
		final Node[] NODE	= {new Node(SHAPE_Y, SHAPE_X, CHANNEL, OPT)};
		super.isFirstLayer	= true;

		this.layerInit(OPT, NODE);
	}


	// initialising this layer sizes
	protected void sizesInit(){
		// i need to know the size of this layer input / matrix
		final Node.Relation[][] PREV_LYER_OUTPUT = super.inputs[0].getOutput();
		super.inputSizeY = PREV_LYER_OUTPUT.length;
		super.inputSizeX = PREV_LYER_OUTPUT[0].length;


		// i need to calculate how big will be this layer output / matrix
		super.outputSizeY = Math.abs(super.inputSizeY - super.KERNEL_Y) +1;
		super.outputSizeX = Math.abs(super.inputSizeX - super.KERNEL_X) +1;
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

			// cycling over the activation map of this layer nodes
			for(int out_y=0; out_y < super.outputSizeY; out_y++){
				for(int out_x=0; out_x < super.outputSizeX; out_x++){
					// loading the sample pixel as it were an output of an ipothtical previous layer
					OUTPUT[out_y][out_x].setOutput(SMAPLE.getToken2D(out_y, out_x));
				}
			}
		}
	}




}
