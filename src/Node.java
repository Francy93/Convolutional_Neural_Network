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
        this(0, 0, 0, INPUT.length, INPUT[0].length);
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
		private			double	linearOutput	= 0;	// to be resetted
		private			double	output			= 0;
		private			double	chainRuleSum	= 0;	// to be resetted
		private			double	derivative		= 0;
		private			double	derivativeSum	= 0;	// derivate * chainRuleSum

        public Relation(final int Y, final int X){
			INDEX_Y = Y;
			INDEX_X = X;
		}

		// ------------ setters ------------------

		public void setOutput(final double ACTIVATED){
			this.output = ACTIVATED;
		}
		
		public void addToChainRuleSum(final double CRS){
			this.chainRuleSum += CRS;
		}

		public void addToLinearOutput(final double ADD){
			this.linearOutput += ADD;
		}
		
		public void setDerivative(final double DERIV){
			this.derivative = DERIV;
			this.linearOutput = 0; // resetting the linearOutput attribute
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
		public double getLinearOutput(){
			return this.linearOutput;
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

	
	/* private void biasGradientsInit(){
		this.biasGradients		= new double[this.BIAS.length][this.BIAS[0].length];
	} */
	/* private void kernelGradientsInit(){
        this.kernelGradients	= new double[FILTER_AMOUNT][FILTER_Y][FILTER_X];
	} */

	// fills the output with the values in "INPUT"
	private void fillOutput(final double[] ... INPUT){
		for(int y=0; y < INPUT.length; y++){
			for(int x=0; x < INPUT[0].length; x++){
				this.setOutput(y, x, INPUT[y][x]);
			}
		}
	}

	// fills the bias array with 0
	/* private void fillBiasGradients(){
		for(int y = 0; y < this.biasGradients.length; y++){
			for(int x = 0; x < this.biasGradients[0].length; x++){
				this.biasGradients[y][x] = 0;
			}
		}
	} */

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
		

		/*for(int fa=0; fa < this.filterAmount; fa++){
			for(int y=0; y < this.filterY; y++){
				for(int x=0; x < this.filterX; x++){
					this.gradients[fa][y][x] = Util.rangeRandom(0, 1);
				}
			}
		} */
	}

	/* public void setRelation(final int Y, final int X, final Node NODE){
		this.OUTPUT[Y][X].addRelation(NODE);
	} */

	public void addBiasGradients(final double GRADIENT, final int Y, final int X){
		this.biasGradients[Y][X] += GRADIENT;

		/* for(boolean exit = false, error = false; !exit; exit = error){
			try{
				this.biasGradients[Y][X] = GRADIENT;
				exit = true;
			}
			catch(Exception e){
				if(error)	throw new ArrayIndexOutOfBoundsException();
				else		this.kernelGradientsInit(); 
				
				error = true;
			}
		} */
	}

	public void addToKernelGradients(final double GRADIENT, final int FILTER, final int Y, final int X){
		this.kernelGradients[FILTER][Y][X] += GRADIENT;
			/* try{
				this.kernelGradients[FILTER][Y][X] = GRADIENT;
			} */
			/* catch(Exception e){
				this.kernelGradientsInit(); 
				this.kernelGradients[FILTER][Y][X] = GRADIENT;
			} */
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



}
