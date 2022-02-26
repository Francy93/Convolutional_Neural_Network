//import lib.Util;

public class Node {

    
	private	final	Relation[][]	OUTPUT;
	private	final	double[][]		BIAS;
	private			double[][]		biasGradients;
    private final	double[][][]	KERNEL;
	private			double[][][]	kernelGradients;
	
	private final	int				FILTER_AMOUNT;
	private	final	int				FILTER_Y;
	private	final	int				FILTER_X;
	
    public Node(final int FA, final int FY, final int FX, final int OUTPUT_Y, final int OUTPUT_X){
        KERNEL			= new double[FA][FY][FX];
		FILTER_AMOUNT	= FA;
		FILTER_Y		= FY;
		FILTER_X		= FX;
		OUTPUT			= outputInit(OUTPUT_Y, OUTPUT_X);
		BIAS			= new double[OUTPUT_Y][OUTPUT_X];
		kernelGradients	= new double[FA][FY][FX];
		biasGradients	= new double[OUTPUT_Y][OUTPUT_X];
		//kernelGradientsInit();
		//fillBiasGradients();
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

		/* public int getCoordY(){
			return this.INDEX_Y;
		}
		public int getCoordX(){
			return this.INDEX_X;
		} */

		public double getOutput(){
			return this.output;
		}
		public double getBackLinearOutput(){
			return this.backLinearOutput;
		}
		public double getFrontLinearOutput(){
			return this.frontLinearOutput;
		}
		/* public double getChainRuleSum(){
			return this.chainRuleSum;
		} */
		/* public double getDerivative(){
			return this.derivative;
		} */
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


	public void setWeight(final int FILTER, final int Y, final int X, final double WEIGHT){

		KERNEL[FILTER][Y][X] = WEIGHT;
	}

	public void addBiasGradients(final double GRADIENT, final int Y, final int X){
		this.biasGradients[Y][X] += GRADIENT;
	}

	public void addToKernelGradients(final double GRADIENT, final int FILTER, final int Y, final int X){
		this.kernelGradients[FILTER][Y][X] += GRADIENT;
	}


	public void setOutput(final int Y, final int X, final double ACTIVATED){
		this.OUTPUT[Y][X].setOutput(ACTIVATED);
	}




	// -------------------- getters ----------------


	public double getWeight(final int FILTER, final int Y, final int X){
		return KERNEL[FILTER][Y][X];
	}

	public Relation[][] getOutput(){
		return this.OUTPUT;
	}

	public double getBias(final int ACT_MAP_Y, final int ACT_MAP_X){
		return this.BIAS[ACT_MAP_Y][ACT_MAP_X];
	}


	public void weightsUpdate(final int BATCH_SIZE, final double LEARNING_RATE){		
		
		// cycling over the all the kernel weights
		for(int filter=0; filter < this.FILTER_AMOUNT; filter++){
			for(int kernel_y=0; kernel_y < this.FILTER_Y; kernel_y++){
				for(int kernel_x=0; kernel_x < this.FILTER_X; kernel_x++){

					// updating every single weight dividing it by the mini batch to find its average
					this.KERNEL[filter][kernel_y][kernel_x] -=  LEARNING_RATE * (this.kernelGradients[filter][kernel_y][kernel_x] / ((double)BATCH_SIZE));
					
				}
			}
		}

		// resetting the gradinets storage
		this.kernelGradients = new double[this.FILTER_AMOUNT][this.FILTER_Y][this.FILTER_X];
	}

	public void biasUpdate(final int BATCH_SIZE, final double LEARNING_RATE){		
		

		for(int bias_y=0; bias_y < this.BIAS.length; bias_y++){
			for(int bias_x=0; bias_x <  this.BIAS[0].length; bias_x++){

				// updating every single weight dividing it by the mini batch to find its average
				this.BIAS[bias_y][bias_x] -=  LEARNING_RATE * (this.biasGradients[bias_y][bias_x] / ((double)BATCH_SIZE));

			}
		}

		// resetting the gradinets storage
		this.biasGradients = new double[this.BIAS.length][this.BIAS[0].length];
	}



}
