
public class Node {

	// Node's attributes
	private	final	Relation[][]	OUTPUT;				// Matrix output also known as activation map in CNNs
	private	final	Parameter[][]	BIAS;				// Storage of biases assigned to avery index of the activation map matrix
	private final	Parameter[][][]	KERNEL;				// Kernel matrix
	private			lib.Optimizer	OPTIMIZER;			// gradients optimizer
	
	// Kernel sizes
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
	 * @param OPT optimizer
	 */
	public Node(final int CA, final int KY, final int KX, final int OUTPUT_Y, final int OUTPUT_X, final lib.Optimizer OPT){
		this.CHANNEL_AMOUNT	= CA;
		this.KERNEL_Y		= KY;
		this.KERNEL_X		= KX;
		this.OPTIMIZER		= OPT;
		this.OUTPUT			= this.outputInit(OUTPUT_Y, OUTPUT_X);
		this.KERNEL			= new Parameter[CA][KY][KX];
		this.BIAS			= new Parameter[OUTPUT_Y][OUTPUT_X];
		this.kernelInit(OPT.momentNumber());
		this.biasInit(OPT.momentNumber());
	}
	// constructor for convnet first layer
	public Node(final double[][] INPUT, final lib.Optimizer OPT){
		this(1, INPUT.length, INPUT[0].length, INPUT.length, INPUT[0].length, OPT);
		fillOutput(INPUT);
	}
	// constructor for densenet first layer
	public Node(final double[] INPUT, final lib.Optimizer OPT){
		this(1, 1, INPUT.length, 1, INPUT.length, OPT);
		fillOutput(INPUT);
	}
	// constructor for all the first layers
	public Node(final int SY, final int SX, final int CHANNELS, final lib.Optimizer OPT){
		this(CHANNELS, SY, SX, SY, SX, OPT);
	}
	
	// Parameter class
	public class Parameter{
		private double weight	= 0;
		private double gradient	= 0;
		final public double[] MOMENTUM;

		public Parameter(final double WEIGHT, final double GRADIENT, final double[] MOMENTUM){
			this.weight		= WEIGHT;
			this.gradient	= GRADIENT;
			this.MOMENTUM	= MOMENTUM;
		}
		public Parameter(final int NUM_MOMENTUM){
			this.MOMENTUM	= new double[NUM_MOMENTUM];
		}


		// ------------ setters ------------------
		public void setWeight(final double WEIGHT){ this.weight = WEIGHT; }
		public void addWeight(final double WEIGHT){ this.weight += WEIGHT; }
		public void subWeight(final double WEIGHT){ this.weight -= WEIGHT; }
		public void setGradient(final double GRADIENT){ this.gradient = GRADIENT; }
		public void addGradient(final double GRADIENT){ this.gradient += GRADIENT; }
		public void subGradient(final double GRADIENT){ this.gradient -= GRADIENT; }

		// ------------ getters ------------------
		public double getWeight(){ return this.weight; }
		public double getGradient(){ return this.gradient; }
	}	

	// activation map "pixels"
	public class Relation{

		// Relation attributes
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
			this.INDEX_Y = Y;
			this.INDEX_X = X;
		}

		// ------------ setters ------------------

		// setting output and deleting the forward pripagation linear output
		public void setOutput(final double ACTIVATED){
			this.output				= ACTIVATED;
			this.backLinearOutput	= this.frontLinearOutput;
			this.frontLinearOutput	= 0;
		}
		
		// summing the derivatives accumulation
		public void addToChainRuleSum(final double CRS){
			this.chainRuleSum 		+= CRS;
		}

		// summing all weigths and inputs moltiplications
		public void addToLinearOutput(final double FLO){
			this.frontLinearOutput	+= FLO;
		}
		
		// setting this node partial derivative
		public void setDerivative(final double DERIV){
			this.derivative			= DERIV;
			this.backLinearOutput	= 0; // resetting the linearOutput attribute
		}

		// summing this node partial derivative times the error propagation 
		public void derivAndCRS_sum(){
			this.derivativeSum		= this.derivative * this.chainRuleSum;
			this.chainRuleSum		= 0; // resetting the chainRuleSum attribute
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

		// getting the index Y of this output 
		public double getIndexY(){
			return this.INDEX_Y;
		}
		// getting the index X of this output 
		public double getIndexX(){
			return this.INDEX_X;
		}

	}

	/**
	 * initializes the kernel
	 * @param MOMENTUM_AMOUNT
	 */
	private void kernelInit(final int MOMENTUM_AMOUNT){
		for(final Parameter[][] CH : this.KERNEL){
			for(final Parameter[] Y : CH){
				for(int x=0; x < Y.length; x++){
					Y[x] = new Parameter(MOMENTUM_AMOUNT);
				}
			}
		}
	}

	/**
	 * initializes the bias
	 * @param MOMENTUM_AMOUNT
	 */
	private void biasInit(final int MOMENTUM_AMOUNT){
		for(final Parameter[] Y : this.BIAS){
			for(int x=0; x < Y.length; x++){
				Y[x] = new Parameter(MOMENTUM_AMOUNT);
			}
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
		this.KERNEL[CHANNEL][Y][X].setWeight(WEIGHT);
	}

	/**
	 * Aggregate gradients of the bias weights
	 * @param GRADIENT
	 * @param Y
	 * @param X
	 */
	public void addToBiasGradients(final int Y, final int X){
		this.BIAS[Y][X].addGradient(this.OUTPUT[Y][X].getDerivativeSum());
	}

	/**
	 * Aggregate gradients of the kernel weights
	 * @param GRADIENT
	 * @param CHANNEL
	 * @param Y
	 * @param X
	 */
	public void addToKernelGradients(final double GRADIENT, final int CHANNEL, final int Y, final int X){
		this.KERNEL[CHANNEL][Y][X].addGradient(GRADIENT);
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

	//Updating weights and biases
	public void update(){
		this.weightsUpdate();	// updating the weights
		this.biasUpdate();		// updating the biases
	}

	//Weights update
	private void weightsUpdate(){

		// cycling over the all the kernel weights
		for(int channel=0; channel < this.CHANNEL_AMOUNT; channel++){
			for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
				for(int kernel_x=0; kernel_x < this.KERNEL_X; kernel_x++){
					
					// updating every single weight dividing it by the mini batch to find its average
					this.KERNEL[channel][kernel_y][kernel_x].subWeight(this.OPTIMIZER.optimize(
						this.KERNEL[channel][kernel_y][kernel_x].MOMENTUM, 
						this.KERNEL[channel][kernel_y][kernel_x].getGradient()
					));
					// resetting the gradient
					this.KERNEL[channel][kernel_y][kernel_x].setGradient(0);
				}
			}
		}
	}

	// Biases update
	private void biasUpdate(){

		// cycling over the all the bias weights
		for(int bias_y=0; bias_y < this.BIAS.length; bias_y++){
			for(int bias_x=0; bias_x <  this.BIAS[0].length; bias_x++){

				// updating every single weight dividing it by the mini batch to find its average
				this.BIAS[bias_y][bias_x].subWeight(this.OPTIMIZER.optimize(
					this.BIAS[bias_y][bias_x].MOMENTUM, 
					this.BIAS[bias_y][bias_x].getGradient()
				));
				// resetting the gradient
				this.BIAS[bias_y][bias_x].setGradient(0);
			}
		}
	}








	// -------------------- getters ----------------

	/**
	 * Getting a specific weight of the kernel given its index
	 * @param CHANNEL
	 * @param Y
	 * @param X
	 * @return weight
	 */
	public Parameter getWeight(final int CHANNEL, final int Y, final int X){
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
	public Parameter getBias(final int ACT_MAP_Y, final int ACT_MAP_X){
		return this.BIAS[ACT_MAP_Y][ACT_MAP_X];
	}

}
