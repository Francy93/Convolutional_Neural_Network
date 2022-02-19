
 public class Dense extends Layer{


    public Dense(final int NODES_AMOUNT, final Activation ACTIVATION){
        super(NODES_AMOUNT, ACTIVATION, 1, 0);
    }



    

    public void layerInit(final Node ... INPUTS){
        super.inputs	= INPUTS;

        this.sizesInit();				// initialising this layer sizes
        this.nodesInit();				// initialising this layer nodes
        super.flatOutInit();            // flattening the output
        this.kernelRelationsInit();		// initialising this layer kernel relations
        this.outputInputRelationsInit();// initialising this layer input and output relations
    }
    // first layer initialiser
    public void layerInit(final Sample SAMPLE){
        final Node[] NODE	= {new Node(SAMPLE.getData1D())};
        super.isFirstLayer	= true;

        layerInit(NODE);
    }


    private void sizesInit(){
        // i need to know the size of this layer input / matrix
        final Node.Relation[][] PREV_LYER_OUTPUT = super.inputs[0].getOutput();
        super.inputSizeY	= PREV_LYER_OUTPUT.length;
        super.inputSizeX	= PREV_LYER_OUTPUT[0].length;

        // calculating the kernel size
        super.KERNEL_X		= super.inputSizeY * super.inputSizeX * super.inputs.length;

        // i need to calculate how big will be this layer output / matrix
        super.outputSizeY	= 1;
        super.outputSizeX	= 1;
    }

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
        
    }

    public void prevLayerPropagate(){
        if(super.isFirstLayer) return;
    }

    public void updateWeights(){}

    // ------------------- getter methods ---------------------

    /* public Node getNode(final int INDEX){
        return NODES[INDEX];
    } */
}
