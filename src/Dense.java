
 public class Dense extends Layer{


    public Dense(final int NODES_AMOUNT, final Activation ACTIVATION){
        super(NODES_AMOUNT, ACTIVATION, 0, 0);
    }



    

    public void layerInit(final Node ... INPUTS){
        super.inputs	= INPUTS;

        this.sizesInit();					// initialising this layer sizes
        super.nodesInit();					// initialising this layer nodes
        super.weightsInit();				// initialising all this layer weights
        super.flatOutInit();				// flattening the output
        super.kernelRelationsInit();		// initialising this layer kernel relations
        //super.outputInputRelationsInit();	// initialising this layer input and output relations
    }
    // first layer initialiser
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

        // calculating the kernel size
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
/* 
    private void nodesInit(){

        final int CHANNELS = 1;
        final int KERNEL_Y = 1;
        for(int i=0; i< super.NODES_AMOUNT; i++){
            super.NODES[i] = new Node(CHANNELS, KERNEL_Y, super.KERNEL_X, super.outputSizeY, super.outputSizeX);
        }
    }

    private void kernelRelationsInit(){
        // initialising the kernelRelations for densenet
        super.kernelRelations 	= new Node.Relation[1][1][super.KERNEL_X][1];
        
        int kernelIndex = 0;
        // cycling over all the inputs
        for(int node=0; node < super.inputs.length; node++){

            // current node
            final Node.Relation[][] NODE_REL = super.inputs[node].getOutput();

            // cycling over each input pixel
            for(int y=0; y < NODE_REL.length; y++){
                for(int x=0; x < NODE_REL[0].length; x++){
                    super.kernelRelations[0][0][kernelIndex++][0] = NODE_REL[y][x];
                }
            }
        }
    }

    private void outputInputRelationsInit(){
        super.outputInputRelations = new Node.Relation[super.outputSizeY][super.outputSizeX][super.KERNEL_X];

        for(int i=0; i < super.KERNEL_X; i++){
            super.outputInputRelations[0][0][i] = super.kernelRelations[0][0][i][0];
        }
    }
        


    // ------------------- setter methods ---------------------

    private double calculateDerivative(final Node.Relation NODE_SINGLE_OUT){
        
    } */

    /* public void prevLayerPropagate(){
        if(super.isFirstLayer) return;
    }

    public void updateWeights(){} */

    // ------------------- getter methods ---------------------

    /* public Node getNode(final int INDEX){
        return NODES[INDEX];
    } */
}
