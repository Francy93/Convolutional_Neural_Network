
public class Node {

    
	private	final	Relation[][]	OUTPUT;
	private	final	double[][]		BIAS;
	private			double[][]		biasGradients;
    private final	double[][][]	KERNEL;
	private			double[][][]	kernelGradients;
	private	final	double[][][][]	KERNEL_MOMENTUM;
	private	final	double[][][]	BIAS_MOMENTUM;
	private 		double			timeStep = 0;
		
	private final double BETA1 = 0.9, BETA2 = 0.999, EPSILON = 1e-07;	
	
	private final	int				CHANNEL_AMOUNT;
	private	final	int				KERNEL_Y;
	private	final	int				KERNEL_X;
	
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
		KERNEL_MOMENTUM = new double[2][CA][KY][KX];
		BIAS_MOMENTUM	= new double[2][OUTPUT_Y][OUTPUT_X];
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
    


    public class Relation{

		private final	int		INDEX_Y;
		private final	int		INDEX_X;
		private			double	frontLinearOutput	= 0;	// to be resetted
		private			double	backLinearOutput	= 0;	// to be resetted
		private			double	output				= 0;
		private			double	chainRuleSum		= 0;	// to be resetted
		private			double	derivative			= 0;
		private			double	derivativeSum		= 0;	// derivate * chainRuleSum

        public Relation(final int Y, final int X){
			INDEX_Y = Y;
			INDEX_X = X;
		}

		// ------------ setters ------------------

		public void setOutput(final double ACTIVATED){
			this.output = ACTIVATED;
			this.backLinearOutput = this.frontLinearOutput;
			this.frontLinearOutput = 0;
		}
		
		public void addToChainRuleSum(final double CRS){
			this.chainRuleSum += CRS;
		}

		public void addToLinearOutput(final double ADD){
			this.frontLinearOutput += ADD;
		}
		
		public void setDerivative(final double DERIV){
			this.derivative = DERIV;
			this.backLinearOutput = 0; // resetting the linearOutput attribute
		}
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


	public void setWeight(final int CHANNEL, final int Y, final int X, final double WEIGHT){

		KERNEL[CHANNEL][Y][X] = WEIGHT;
	}

	public void addBiasGradients(final double GRADIENT, final int Y, final int X){
		this.biasGradients[Y][X] += GRADIENT;
	}

	public void addToKernelGradients(final double GRADIENT, final int CHANNEL, final int Y, final int X){
		this.kernelGradients[CHANNEL][Y][X] += GRADIENT;
	}


	public void setOutput(final int Y, final int X, final double ACTIVATED){
		this.OUTPUT[Y][X].setOutput(ACTIVATED);
	}


	// Weights update
	public void weightsUpdate(final int BATCH_SIZE, final double LEARNING_RATE){
		double m, v, mk, vk, grad;
		this.timeStep++;

		// cycling over the all the kernel weights
		for(int channel=0; channel < this.CHANNEL_AMOUNT; channel++){
			for(int kernel_y=0; kernel_y < this.KERNEL_Y; kernel_y++){
				for(int kernel_x=0; kernel_x < this.KERNEL_X; kernel_x++){

					// updating every single weight dividing it by the mini batch to find its average
					//this.KERNEL[channel][kernel_y][kernel_x] -=  LEARNING_RATE * (this.kernelGradients[channel][kernel_y][kernel_x] / ((double)BATCH_SIZE));
					grad = (this.kernelGradients[channel][kernel_y][kernel_x] / ((double)BATCH_SIZE));

					this.KERNEL_MOMENTUM[0][channel][kernel_y][kernel_x] *= BETA1; 
					this.KERNEL_MOMENTUM[0][channel][kernel_y][kernel_x] += (1.0 - BETA1) * grad;
					m = this.KERNEL_MOMENTUM[0][channel][kernel_y][kernel_x];

					this.KERNEL_MOMENTUM[1][channel][kernel_y][kernel_x] *= BETA2;
					this.KERNEL_MOMENTUM[1][channel][kernel_y][kernel_x] += (1.0 - BETA2) * grad * grad;
					v = this.KERNEL_MOMENTUM[1][channel][kernel_y][kernel_x];

					mk = m / (1.0 - Math.pow(BETA1, this.timeStep));
					vk = v / (1.0 - Math.pow(BETA2, this.timeStep));

					this.KERNEL[channel][kernel_y][kernel_x] -= LEARNING_RATE * mk / (Math.sqrt(vk) + EPSILON);

					
				}
			}
		}

		// resetting the gradinets storage
		this.kernelGradients = new double[this.CHANNEL_AMOUNT][this.KERNEL_Y][this.KERNEL_X];
	}

	// biases update
	public void biasUpdate(final int BATCH_SIZE, final double LEARNING_RATE){
		double m, v, mk, vk, grad;

		for(int bias_y=0; bias_y < this.BIAS.length; bias_y++){
			for(int bias_x=0; bias_x <  this.BIAS[0].length; bias_x++){

				// updating every single weight dividing it by the mini batch to find its average
				//this.BIAS[bias_y][bias_x] -=  LEARNING_RATE * (this.biasGradients[bias_y][bias_x] / ((double)BATCH_SIZE));


				grad = this.biasGradients[bias_y][bias_x] / ((double)BATCH_SIZE);

				this.BIAS_MOMENTUM[0][bias_y][bias_x] *= BETA1; 
				this.BIAS_MOMENTUM[0][bias_y][bias_x] += (1.0 - BETA1) * grad;
				m = this.BIAS_MOMENTUM[0][bias_y][bias_x];

				this.BIAS_MOMENTUM[1][bias_y][bias_x] *= BETA2;
				this.BIAS_MOMENTUM[1][bias_y][bias_x] += (1.0 - BETA2) * grad * grad;
				v = this.BIAS_MOMENTUM[1][bias_y][bias_x];

				mk = m / (1.0 - Math.pow(BETA1, this.timeStep));
				vk = v / (1.0 - Math.pow(BETA2, this.timeStep));

				this.BIAS[bias_y][bias_x] -= LEARNING_RATE * mk / (Math.sqrt(vk) + EPSILON);

			}
		}
		

		// resetting the gradinets storage
		this.biasGradients = new double[this.BIAS.length][this.BIAS[0].length];
	}






	// -------------------- getters ----------------


	public double getWeight(final int CHANNEL, final int Y, final int X){
		return KERNEL[CHANNEL][Y][X];
	}

	public Relation[][] getOutput(){
		return this.OUTPUT;
	}

	public double getBias(final int ACT_MAP_Y, final int ACT_MAP_X){
		return this.BIAS[ACT_MAP_Y][ACT_MAP_X];
	}

}
