
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
	 * @param INPUTS 
	 */
    public void layerInit(final Node ... INPUTS){
        super.inputs	= INPUTS;

        this.sizesInit();					// initialising this layer sizes
        super.nodesInit();					// initialising this layer nodes
        super.weightsInit();				// initialising all this layer weights
        super.flatOutInit();				// flattening the output
        super.kernelRelationsInit();		// initialising this layer kernel relations
        //super.outputInputRelationsInit();	// initialising this layer input and output relations
    }

	/**
	 * First Densenet layer initialiser
	 * @param SAMPLE 
	 */
    public void firstLayerInit(final Sample SAMPLE){
        final Node[] NODE	= {new Node(SAMPLE.getData1D())};
        super.isFirstLayer	= true;

        layerInit(NODE);
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
		if(!this.isFirstLayer) throw new Exception("SampleLoader() can only be used with the first layer");

		for(int node=0; node < this.inputs.length; node++){
			Node.Relation[][] OUTPUT = this.inputs[node].getOutput();
			int sample1D_iterator = 0;

			for(int out_y=0; out_y < OUTPUT.length; out_y++){
				for(int out_x=0; out_x < OUTPUT[0].length; out_x++){
					OUTPUT[out_y][out_x].setOutput(SMAPLE.getToken1D(sample1D_iterator++));
				}
			}
		}
	}
}
