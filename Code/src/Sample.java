
import lib.Util;

public class Sample {

    private final 	double[]	TOKENS;			// array1D of sample data/input
	private final 	double[][]	MATRIX;			// array2D of sample data/input
    private final 	double		LABEL;			// class of sample
	private			double[]	labelLocation;	// to store the class location

	/**
	 * Sample constructor
	 * @param S sample data
	 * @param D	data delimiter
	 * @throws ExceptionInInitializerError
	 */
	public Sample(final String S, final String D) throws ExceptionInInitializerError{
        double[] SAMPLE_DATA;

		// getting validated sample data
		try { SAMPLE_DATA = this.validator(S, D); }
		catch(Exception e){ throw new ExceptionInInitializerError(); }

        this.TOKENS	= this.tokenFilter(SAMPLE_DATA);	// extract this sample pixels only
        this.LABEL	= this.labelFilter(SAMPLE_DATA);	// extract this sample label only
		this.MATRIX	= this.matrixInit();				// initialising this sample image matrix
    }
	public Sample(final double[] SAMPLE_DATA, final double L){
        this.TOKENS	= SAMPLE_DATA.clone();
        this.LABEL	= L;
		this.MATRIX	= this.matrixInit();		// initialising this sample image matrix
    }


	/**
	 * Verifying the data validity
	 * @param str
	 * @param DELIMITER
	 * @return
	 */
    private double[] validator(final String STR, final String DELIMITER){
		final String[]	DATA_STRING = STR.split(DELIMITER);
		final int		DATA_LENGTH = DATA_STRING.length;
		final double	SQUARE_ROOT = Math.sqrt( DATA_STRING.length -1 );

		if( SQUARE_ROOT != (int) SQUARE_ROOT ) throw new ExceptionInInitializerError();

		double[] DATA = new double[DATA_LENGTH];

		// cycling over the pixels
		for(int i=0; i < DATA_LENGTH; i++){
			if(Util.isNumeric(DATA_STRING[i])) DATA[i] = Double.parseDouble(DATA_STRING[i]);
			else throw new ExceptionInInitializerError();
		}

        return DATA;
    }

	// getting this sample pixels only
    private double[] tokenFilter(double[] array){
		final int 		TK_LENGTH	= array.length-1;
		final double[]	TKS			= new double[TK_LENGTH];

		// getting just pixel data except the label data
		for(int i=0; i < TK_LENGTH; i++) TKS[i] = array[i];

		return TKS;
    }

	// getting this sample label only
    private double labelFilter(double[] array){
		return array[array.length - 1];
    }

	// initialising this sample image matrix
	private double[][] matrixInit(){
		final int MATRIX_SIZE = (int) Math.sqrt( this.TOKENS.length );
		final double[][] ARRAY_2D = new double[MATRIX_SIZE][MATRIX_SIZE];

		for(int y=0; y < MATRIX_SIZE; y++){
			for(int x=0; x < MATRIX_SIZE; x++){ 
				ARRAY_2D[y][x] = this.TOKENS[(y*MATRIX_SIZE) + x]; 
			}
		}

		return ARRAY_2D;
	}



	// ..................setter method .................

	/**
	 * Setting the "one-hot" encoder array
	 * @param LABEL_LOCATION
	 */
	public void setClassLocation(final double[] LABEL_LOCATION){
		this.labelLocation = LABEL_LOCATION;
	}

	/**
	 * Setting the pixel of a specific indx of both array and matrix
	 * @param INDEX the index of the pixel
	 * @param VALUE the value of the pixel
	 */
	public void setToken(final int INDEX, final double VALUE){
		this.TOKENS[INDEX] = VALUE;
		this.MATRIX[(int) INDEX / (int) Math.sqrt(this.TOKENS.length)][INDEX % (int) Math.sqrt(this.TOKENS.length)] = VALUE;
	}
	




	// ..................getters method .................

	/**
	 * Get dataset samples
	 * @param SAMPLE array of samples
	 */
	public void print1D(){
		StringBuilder sb = new StringBuilder();
		for (double val : this.getFeature1D()) sb.append(val + " ");
		sb.append("\r\n");
		System.out.println(sb.toString());
	}

	/**
	 * Print the dataset in 2D
	 * @param SAMPLE sample to be printed
	 * @param SCGS	 sample color gradient scale
	 * @param ACGS	 ascii color gradient scale
	 * @param SIZE	 size of the ascii character
	 */
	public void print2D(final double SCGS, final int ACGS, final int SIZE){
		StringBuilder sb = new StringBuilder();
			for (double[] feature : this.getFeature2D()) {
				for (double val : feature) {
					final float SP	= (float) val * 100f / (float) SCGS; // sample percentage
					final float AP	= SP / 100f * (float) ACGS; // ascii percentage
					final int	COL = 232 + Math.round(AP); // color index
					for (int i = 0; i < SIZE; i++) {
						sb.append("\033[38;5;"+COL+";48;5;"+COL+"m███\033[0m");
					}
				}
				sb.append("\r\n");
			}
       		System.out.println(sb.toString());
	}

	// getting this sample label
	public double getLabel(){
		return this.LABEL;
	}

	public double[] getFeature1D(){
		return this.TOKENS;
	}

	public double[][] getFeature2D(){
		return this.MATRIX;
	}
	// get the pixel of a specific array index
	public double getToken1D(final int INDEX){
		return this.TOKENS[INDEX];
	}

	// get the pixel of a specific matrix index
	public double getToken2D(final int Y, final int X){
		return this.MATRIX[Y][X];
	}

	// getting the one-hot array
	public double[] getOneHot(){
		return this.labelLocation;
	}
}
