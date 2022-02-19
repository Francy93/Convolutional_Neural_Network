
public class Conv2D extends Layer{

        
    public Conv2D(final int NODES_AMOUNT, final int KY, final int KX, final Activation ACTIVATION){
        super(NODES_AMOUNT, ACTIVATION, KY, KX);
    }


    // --------------------- initialising -------------------------



    
    // hidden lyer initialiser
    public void layerInit(final Node ... INPUTS){
        super.inputs = INPUTS;

        this.sizesInit();				// initialising this layer sizes
        this.nodesInit();				// initialising this layer nodes
        this.weightsInit();				// initialising all this layer weights
        super.flatOutInit();			// flattening the output
        this.kernelRelationsInit();		// initialising this layer kernel relations
        this.outputInputRelationsInit();// initialising this layer input and output relations
    }

    // first layer initialiser
    publi void layerInit(final Sample SAMPLE){
        final Node[] NODE	= {new Node(SAMPLE.getData2D())}; 
        super.isFirstLayer	= true;

        this.layerInit(NODE);
    }



    private void sizesInit(){
        // i need to know the size of this layer input / matrix
        final Node.Relation[][] PREV_LYER_OUTPUT = super.inputs[0].getOutput();
        super.inputSizeY = PREV_LYER_OUTPUT.length;
        super.inputSizeX = PREV_LYER_OUTPUT[0].length;


        // i need to calculate how big will be this layer output / matrix
        super.outputSizeY = Math.abs(super.inputSizeY - super.KERNEL_Y) +1;
        super.outputSizeX = Math.abs(super.inputSizeX - super.KERNEL_X) +1;
    }

    private void nodesInit(){
        for(int i=0; i< super.NODES_AMOUNT; i++){
            super.NODES[i] = new Node(super.inputs.length, super.KERNEL_Y, super.KERNEL_X, super.outputSizeY, super.outputSizeX);
        }
    }

	// initialising all the weights of this layer
	private void weightsInit(){
		for(int node=0; node < super.NODES_AMOUNT; node++){
			final Node NODE = super.NODES[node];

			// cycling over each channel / filter
			for(int filter=0; filter < super.inputs.length; filter++){
				// cycling over the filter weigths
				for(int kernel_y=0; kernel_y < super.NODES_AMOUNT; kernel_y++){
					for(int kernel_x=0; kernel_x < super.NODES_AMOUNT; kernel_x++){
						// setting the weigth
						NODE.setWeight(filter, kernel_y, kernel_x, super.ACTIVATION.randomWeight(this));
					}
				}
			}
		}
	}

    private void kernelRelationsInit(){
        // getting the size of an entire feature map output
        final int PATCH_SIZE	= super.outputSizeY * super.outputSizeX;
        super.kernelRelations 	= new Node.Relation[super.inputs.length][super.KERNEL_Y][super.KERNEL_X][PATCH_SIZE];

        // calculating for possible need for padding
        final int IMAGE_Y		= super.inputSizeY - super.KERNEL_Y;
        final int IMAGE_X		= super.inputSizeX - super.KERNEL_X;
        final int LEFT_IMAGE_Y	= IMAGE_Y>= 0? 0: IMAGE_Y;
        final int LEFT_IMAGE_X	= IMAGE_X>= 0? 0: IMAGE_X;
        final int RIGHT_IMAGE_Y = IMAGE_Y>= 0? IMAGE_Y + 1: 1;
        final int RIGHT_IMAGE_X = IMAGE_X>= 0? IMAGE_X + 1: 1;


        // cycling over the filters
        for(int filter=0; filter < super.inputs.length; filter++){
            final Node.Relation[][] INPUT_NODE = super.inputs[filter].getOutput();
            int relation = 0;

            // cycling over the input image pixels
            for(int image_y = LEFT_IMAGE_Y; image_y < RIGHT_IMAGE_Y; image_y++){
                for(int image_x = LEFT_IMAGE_X; image_x < RIGHT_IMAGE_X; image_x++){

                    // cycling over the kernal weights
                    for(int kernel_y=0; kernel_y < super.KERNEL_Y; kernel_y++){
                        for(int kernel_X=0; kernel_X < super.KERNEL_X; kernel_X++){

                            try{	// storing the relations between weigths and inputs
                                super.kernelRelations[filter][kernel_y][kernel_X][relation] = INPUT_NODE[image_y+kernel_y][image_x+kernel_X];
                            }catch(Exception e){}
                        }

                    }
                    relation++; // incrementing the relation iterator
                }
            }
        }
    }

    private void outputInputRelationsInit(){
        // getting the relations amount of a each output pixel
        final int RELATIONS_AMOUNT = super.KERNEL_Y * super.KERNEL_X * super.kernelRelations.length;
        super.outputInputRelations = new Node.Relation[super.outputSizeY][super.outputSizeX][RELATIONS_AMOUNT];

        int stride = 0;
        // cycling over the output matrix 
        for(int y=0; y < super.outputSizeY; y++){
            for(int x=0; x < super.outputSizeX; x++){
                int filterIndex = 0;	// counting the filter index
                
                // cycling over all the filters
                for(int filter=0; filter < super.kernelRelations.length; filter++){

                    // cycling over the relation set of each filter weigth
                    for(int kernel_y=0; kernel_y < super.kernelRelations[filter].length; kernel_y++){
                        for(int kernel_x=0; kernel_x < super.kernelRelations[filter][0].length; kernel_x++){

                            // storing the relation between input and output (patch) of every weight
                            super.outputInputRelations[y][x][filterIndex++] = super.kernelRelations[filter][kernel_y][kernel_x][stride];
                        }
                    }
                }
                stride++;				// Stride counter. Moving by 1 kernel sliding (next filter-inputImage relation)
            }
        }
    }
	




    // -------------------------- getters --------------------------------   
    
    /* public Node.Relation[] getOutInRelation(final int OUT_Y, final int OUT_X){

        return super.outputInputRelations[OUT_Y][OUT_X];
    }

    public Node.Relation[] getWeightInRelation(final int FILTER, final int KERNEL_Y, final int KERNEL_X){

        return super.kernelRelations[FILTER][KERNEL_Y][KERNEL_X];
    } */

    // --------------------- weigths init -------------------------




    // --------------------- feed forward -------------------------

    public void feedForward(){
		// cycling over all this layer nodes
		for(int node=0; node < super.NODES_AMOUNT; node++){
			final Node NODE = super.NODES[node];
			int strideCounter = 0;

			 // cycling over all the "pixels" of the output matrix
			 for(int map_y=0; map_y < super.outputSizeY; map_y++){
                for(int map_x=0; map_x < super.outputSizeX; map_x++){
					// getting the output of this activation map index
					Node.Relation SINGLE_OUTPUT = NODE.getOutput()[map_y][map_x];
                   
                    // cycling over the all the kernel weights
                    for(int filter=0; filter < super.inputs.length; filter++){

						// cycling over this entire filter
                        for(int kernel_y=0; kernel_y < super.KERNEL_Y; kernel_y++){
                            for(int kernel_x=0; kernel_x < super.KERNEL_X; kernel_x++){

								try{	// summing the input times the weight
									SINGLE_OUTPUT.addToLinearOutput(
										NODE.getWeight(filter, kernel_y, kernel_x) * super.kernelRelations[filter][kernel_y][kernel_x][strideCounter].getOutput()
									);
								} catch(NullPointerException e){}
                            }
                        }						
                    }
					// summming the bias
					SINGLE_OUTPUT.addToLinearOutput(NODE.getBias(map_y, map_x));
					// performing the activation function for this output / feature-map "pixel"
					super.ACTIVATION.function(SINGLE_OUTPUT, this);
                    strideCounter++;
                }
            }

		}
    }





    // --------------------- back propagation -------------------------

    // the back propagation method
    public void backPropagating(){
        // cycling overall the nodes
        for(int node=0; node < super.NODES_AMOUNT; node++){
            final Node				NODE		= super.NODES[node];
            final Node.Relation[][] NODE_OUTPUT	= NODE.getOutput();
            int strideCounter = 0;

            // cycling over all the "pixels" of the output matrix
            for(int map_y=0; map_y < super.outputSizeY; map_y++){
                for(int map_x=0; map_x < super.outputSizeX; map_x++){

                    // calculationthe derivative of the non-linear to linear operation
                    final double DERIV_SUM = calculateDerivative(NODE_OUTPUT[map_y][map_x]);
                    // storing the biases gradients
                    NODE.addBiasGradients(DERIV_SUM, map_y, map_x);

                    // cycling over the all the kernel weights
                    for(int filter=0; filter < super.inputs.length; filter++){
                        for(int kernel_y=0; kernel_y < super.KERNEL_Y; kernel_y++){
                            for(int kernel_x=0; kernel_x < super.KERNEL_X; kernel_x++){

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

    // main chain runle operations
    private void gradientAndPropagate(final Node NODE, final double DERIV_SUM, final int FILTER, final int KERNEL_Y, final int KERNEL_X, final int STRIDE){
        try{
            // --------- GRADIENT DISCENT OPERATION

            // storing the gradient into this layer node
            NODE.addToKernelGradients(
                // summing this output pixel derivative times all its inputs (find new weight gradient)
                DERIV_SUM * super.kernelRelations[FILTER][KERNEL_Y][KERNEL_X][STRIDE].getOutput(), 
                FILTER,
                KERNEL_Y,
                KERNEL_X
            );


            // --------- BACK PROPAGATION OPERATION
            
            // storing the sum into the next layer node in back propagation way
            super.kernelRelations[FILTER][KERNEL_Y][KERNEL_X][STRIDE].addToChainRuleSum(
                // summing this output pixel derivative times all its weights (find new input gradient)
                DERIV_SUM * NODE.getWeight(FILTER, KERNEL_Y, KERNEL_X)
            );

        }catch(NullPointerException e){}
    }

    // calculating the derivative of a single output
    private double calculateDerivative(final Node.Relation NODE_SINGLE_OUT){
        super.ACTIVATION.derivative(NODE_SINGLE_OUT, this);
        NODE_SINGLE_OUT.derivAndCRS_sum();

        return NODE_SINGLE_OUT.getDerivativeSum();
    }

/*         public void calculateGradients(){
        // cycling overall the nodes
        for(int node=0; node < super.NODES_AMOUNT; node++){
            final Node				NODE		= super.NODES[node];
            final Node.Relation[][] NODE_OUTPUT	= NODE.getOutput();
            int strideCounter = 0;

            // cycling over all the "pixels" of the output matrix
            for(int map_y=0; map_y < super.outputSizeY; map_y++){
                for(int map_x=0; map_x < super.outputSizeX; map_x++){

                    // cycling over the all the kernel weights
                    for(int filter=0; filter < super.inputs.length; filter++){
                        for(int kernel_y=0; kernel_y < super.KERNEL_Y; kernel_y++){
                            for(int kernel_x=0; kernel_x < super.KERNEL_X; kernel_x++){

                                try{
                                    NODE.addToKernelGradients(
                                        NODE_OUTPUT[map_y][map_x].getDerivativeSum() * super.kernelRelations[filter][kernel_y][kernel_x][strideCounter].getOutput(), 
                                        filter,
                                        kernel_y,
                                        kernel_x
                                    );
                                }catch(NullPointerException e){}
                                
                            }
                        }
                    }
                    strideCounter++;
                }
            }
        }
    }

    public void prevLayerPropagate(){
        if(super.isFirstLayer) return;

        // cycling overall the nodes
        for(int node=0; node < super.NODES_AMOUNT; node++){
            final Node				NODE		= super.NODES[node];
            final Node.Relation[][] NODE_OUTPUT	= NODE.getOutput();
            int strideCounter = 0;

            // cycling over all the "pixels" of the output matrix
            for(int map_y=0; map_y < super.outputSizeY; map_y++){
                for(int map_x=0; map_x < super.outputSizeX; map_x++){

                    // cycling over the all the kernel weights
                    for(int filter=0; filter < super.inputs.length; filter++){
                        for(int kernel_y=0; kernel_y < super.KERNEL_Y; kernel_y++){
                            for(int kernel_x=0; kernel_x < super.KERNEL_X; kernel_x++){

                                try{
                                    // storing the sum into the next layer node in back propagation way
                                    super.kernelRelations[filter][kernel_y][kernel_x][strideCounter].addToChainRuleSum(
                                        // summing this pixel of the output times all its weights
                                        NODE_OUTPUT[map_y][map_x].getDerivativeSum() * NODE.getWeight(filter, kernel_y, kernel_x)
                                    );
                                }catch(NullPointerException e){}
                                
                            }
                        }
                    }
                    // incrementing stride
                    strideCounter++;
                }
            }
            
        }
    } */

    public void updateWeights(){
        // feasible!
    }

    



}
