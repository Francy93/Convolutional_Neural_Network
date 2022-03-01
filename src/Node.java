
public class Node {

    
	private	final	Relation[][]	OUTPUT;				// Matrix output also known as activation map in CNNs
	private	final	double[][]		BIAS;				// Storage of biases assigned to avery index of the activation map matrix
	private			double[][]		biasGradients;		// Gradient accumulation for the biases
    private final	double[][][]	KERNEL;				// Kernel matrix
	private			double[][][]	kernelGradients;	// Gradient accumulation for the Kernel
	private	final	double[][][][]	KERNEL_MOMENTUM;	// Histoy of kernel gradients (momentum) for the Adam optimizer
	private	final	double[][][]	BIAS_MOMENTUM;		// Histoy of biases gradients (momentum) for the Adam optimizer
	private 		double			timeStep = 0;		// Adam TimeStep counter
	
	// Adam parameters
	private final double BETA1 = 0.9, BETA2 = 0.999, EPSILON = 1e-08;	
	
	private final	int				CHANNEL_AMOUNT;		// Number of kernel channel
	private	final	int				KERNEL_Y;			// Kernel Y size
	private	final	int				KERNEL_X;			// Kernel X size
	
	/**
	 * Node Constructor
	 * @param CA Channels amount
	 * @param KY Kernel y size
	 * @param KX Kernel x size
	 * @param OUTPUT_Y Activation map Y size
	 * @param OUTPUT_X Activation map X size
	 */
    public Node(final int CA, final int KY, final int KX, final int OUTPUT_Y, final int OUTPUT_X){
        KERNEL			= new double[CA][KY][KX];
		CHANNEL_AMOUNT	= CA;
		KERNEL_Y		= KY;
		KERNEL_X		= KX;
		OUTPUT			= outputInit(OUTPUT_Y, OUTPUT_X);
		BIAS			= new double[OUTPUT_Y][OUTPUT_X];
		kernelGradients	= new double[CA][KY][KX];
		biasGradients	= new double[OUTPUT_Y][OUTPUT_X];
		KERNEL_MOMENTUM = new double[CA][KY][KX][2];
		BIAS_MOMENTUM	= new double[OUTPUT_Y][OUTPUT_X][2];
    }
	// constructor for convnet first layer
	public Node(final double[][] INPUT){
        this(1, INPUT.length, INPUT[0].length, INPUT.length, INPUT[0].length);
		fillOutput(INPUT);
    }
	// constructor for densenet first layer
	public Node(final double[] INPUT){
        this(1, 1, INPUT.length, 1, INPUT.length);
		fillOutput(INPUT);
    }
    

	// activation map "pixels"
    public class Relation{

		private final	int		INDEX_Y;					// Index Y of this node into the Activation map
		private final	int		INDEX_X;					// Index X of this node into the Activation map
		private			double	frontLinearOutput	= 0;	// Linear output of the forward propagation
		private			double	backLinearOutput	= 0;	// Linear output kept after the forward propagation
		private			double	output				= 0;	// Actual value output
		private			double	chainRuleSum		= 0;	// Accumulation of chain rule derivatives
		private			double	derivative			= 0;	// This node Partial derivative
		private			double	derivativeSum		= 0;	// This node Partial derivative times chainRuleSum

		/**
		 * Activation map node ("pixel")
		 * @param Y Index Y of this node into the Activation map
		 * @param X Index X of this node into the Activation map
		 */
        public Relation(final int Y, final int X){
			INDEX_Y = Y;
			INDEX_X = X;
		}

		// ------------ setters ------------------

		// setting output and deleting the forward pripagation linear output
		public void setOutput(final double ACTIVATED){
			this.output = ACTIVATED;
			this.backLinearOutput = this.frontLinearOutput;
			this.frontLinearOutput = 0;
		}
		
		// summing the derivatives accumulation
		public void addToChainRuleSum(final double CRS){
			this.chainRuleSum += CRS;
		}

		// summing all weigths and inputs moltiplications
		public void addToLinearOutput(final double ADD){
			this.frontLinearOutput += ADD;
		}
		
		// setting this node partial derivative
		public void setDerivative(final double DERIV){
			this.derivative = DERIV;
			this.backLinearOutput = 0; // resetting the linearOutput attribute
		}

		// summing this node partial derivative times the error propagation 
		public void derivAndCRS_sum(){
			this.derivativeSum = this.derivative * this.chainRuleSum;
			this.chainRuleSum = 0; // resetting the chainRuleSum attribute
		}

		// ---------------- getters ---------------

		public double getOutput(){
			return this.output;
		}
		public double getBackLinearOutput(){
			return this.backLinearOutput;
		}
		public double getFrontLinearOutput(){
			return this.frontLinearOutput;
		}
		public double getDerivativeSum(){
			return this.derivativeSum;
		}

    }


	// fills the output with the values in "INPUT"
	private void fillOutput(final double[] ... INPUT){
		for(int y=0; y < INPUT.length; y++){
			for(int x=0; x < INPUT[0].length; x++){
				this.setOutput(y, x, INPUT[y][x]);
			}
		}
	}

	// make an output matrix made of relations
	private Relation[][] outputInit(final int SIZE_Y, final int SIZE_X){
		final Relation[][] INIT_REL = new Relation[SIZE_Y][SIZE_X];

		for(int y=0; y < SIZE_Y; y++){
			for(int x=0; x < SIZE_X; x++){
				INIT_REL[y][x] = this.new Relation(y, x);
			}
		}

		return INIT_REL;
	}



	
	// --------------------- setters -------------------

	/**
	 * Setting the weight of a specific index of the kernel
	 * @param CHANNEL
	 * @param Y
	 * @param X
	 * @param WEIGHT
	 */
	public void setWeight(final int CHANNEL, final int Y, final int X, final double WEIGHT){

		KERNEL[CHANNEL][Y][X] = WEIGHT;
	}

	/**
	 * Aggregate gradients of the bias weights
	 * @param GRADIENT
	 * @param Y
	 * @param X
	 */
	public void addBiasGradients(final double GRADIENT, final int Y, final int X){
		this.biasGradients[Y][X] += GRADIENT;
	}

	/**
	 * Aggregate gradients of the kernel weights
	 * @param GRADIENT
	 * @param CHANNEL
	 * @param Y
	 * @param X
	 */
	public void addToKernelGradients(final double GRADIENT, final int CHANNEL, final int Y, final int X){
		this.kernelGradients[CHANNEL][Y][X] += GRADIENT;
	}

	/**
	 * Setting the output of a specific activation map index
	 * @param Y
	 * @param X
	 * @param ACTIVATED	value from activation function
	 */
	public void setOutput(final int Y, final int X, final double ACTIVATED){
		this.OUTPUT[Y][X].setOutput(ACTIVATED);
	}

	/**
	 * Updating weights and biases
	 * @param BATCH_SIZE
	 * @param LEARNING_RATE
	 */
	public void update(final int BATCH_SIZE, final double LEARNING_RATE){
		this.timeStep++;
		weightsUpdate(BATCH_SIZE, LEARNING_RATE);
		biasUpdate(BATCH_SIZE, LEARNING_RATE);
	}

	// Weights update
	private void weightsUpdate(final int BATCH_SIZE, final double LEARNING_RATE){

		// cycling over the all the kernel weights
		for(int channel=0; channel < this.CHANNEL_AMOUNT; channel++){
			for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
				for(int kernel_x=0; kernel_x < this.KERNEL_X; kernel_x++){

					// updating every single weight dividing it by the mini batch to find its average
					//this.KERNEL[channel][kernel_y][kernel_x] -=  LEARNING_RATE * (this.kernelGradients[channel][kernel_y][kernel_x] / ((double)BATCH_SIZE));
					this.KERNEL[channel][kernel_y][kernel_x] -= LEARNING_RATE * adamOpt(this.KERNEL_MOMENTUM[channel][kernel_y][kernel_x], this.kernelGradients[channel][kernel_y][kernel_x] / ((double)BATCH_SIZE));
				}
			}
		}

		// resetting the gradinets storage
		this.kernelGradients = new double[this.CHANNEL_AMOUNT][this.KERNEL_Y][this.KERNEL_X];
	}

	// biases update
	private void biasUpdate(final int BATCH_SIZE, final double LEARNING_RATE){

		for(int bias_y=0; bias_y < this.BIAS.length; bias_y++){
			for(int bias_x=0; bias_x <  this.BIAS[0].length; bias_x++){

				// updating every single weight dividing it by the mini batch to find its average
				//this.BIAS[bias_y][bias_x] -=  LEARNING_RATE * (this.biasGradients[bias_y][bias_x] / ((double)BATCH_SIZE));
				this.BIAS[bias_y][bias_x] -= LEARNING_RATE * adamOpt(this.BIAS_MOMENTUM[bias_y][bias_x], this.biasGradients[bias_y][bias_x] / ((double)BATCH_SIZE));
			}
		}
		

		// resetting the gradinets storage
		this.biasGradients = new double[this.BIAS.length][this.BIAS[0].length];
	}

	/**
	 * Adam optimizer
	 * @param MOMENTUM
	 * @param GRAD non optimized gradient
	 * @return optimized gradient
	 */
	private double adamOpt(final double[] MOMENTUM, final double GRAD){
		double NORM1, NORM2;

		// compute the first moment
		MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + ((1.0 - this.BETA1) * GRAD); 

		// compute the second moment
		MOMENTUM[1] = (MOMENTUM[1] * this.BETA2) + ((1.0 - this.BETA2) * GRAD * GRAD); 

		// normalisation
		NORM1 = MOMENTUM[0] / (1.0 - Math.pow(this.BETA1, this.timeStep));
		NORM2 = MOMENTUM[1] / (1.0 - Math.pow(this.BETA2, this.timeStep));

		return NORM1 / ((Math.sqrt(NORM2) + this.EPSILON));
	}






	// -------------------- getters ----------------

	/**
	 * Getting a specific weight of the kernel given its index
	 * @param CHANNEL
	 * @param Y
	 * @param X
	 * @return weight
	 */
	public double getWeight(final int CHANNEL, final int Y, final int X){
		return KERNEL[CHANNEL][Y][X];
	}

	// get node relation
	public Relation[][] getOutput(){
		return this.OUTPUT;
	}

	/**
	 * Getting a specific weight of the bias given its index
	 * @param ACT_MAP_Y
	 * @param ACT_MAP_X
	 * @return bias of a specific index
	 */
	public double getBias(final int ACT_MAP_Y, final int ACT_MAP_X){
		return this.BIAS[ACT_MAP_Y][ACT_MAP_X];
	}

}
