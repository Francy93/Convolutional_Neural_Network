
import lib.Util;

public class Sample {

    private final 	double[]	TOKENS;			// array1D of sample data/input
	private final 	double[][]	MATRIX;			// array2D of sample data/input
    private final 	double		LABEL;			// class of sample
	private			double[]	labelLocation;	// to store the class location

	/**
	 * 
	 * @param p array of sample data
	 * @throws ExceptionInInitializerError
	 */
    public Sample(final String S, final String D) throws ExceptionInInitializerError{
        double[] SAMPLE_DATA;

		try { SAMPLE_DATA = validator(S, D); }
		catch(Exception e){ throw new ExceptionInInitializerError(); }

        TOKENS	= tokenFilter(SAMPLE_DATA);
        LABEL	= labelFilter(SAMPLE_DATA);
		MATRIX	= matrixInit();
    }



    private double[] validator(final String str, final String DELIMITER){
		final String[]	DATA_STRING = str.split(DELIMITER);
		final int		DATA_LENGTH = DATA_STRING.length;
		final double	SQUARE_ROOT = Math.sqrt( DATA_STRING.length -1 );

		if( SQUARE_ROOT != (int) SQUARE_ROOT ) throw new ExceptionInInitializerError();

		double[] DATA = new double[DATA_LENGTH];

		for(int i=0; i < DATA_LENGTH; i++){
			if(Util.isNumeric(DATA_STRING[i])) DATA[i] = Double.parseDouble(DATA_STRING[i]);
			else throw new ExceptionInInitializerError();
		}

        return DATA;
    }

    private double[] tokenFilter(double[] array){
		final int 		TK_LENGTH	= array.length-1;
		final double[]	TKS			= new double[TK_LENGTH];

		// getting just pixel data except the label data
		for(int i=0; i < TK_LENGTH; i++) TKS[i] = array[i];

		return TKS;
    }

    private double labelFilter(double[] array){
		return array[array.length - 1];
    }

	private double[][] matrixInit(){
		final int MATRIX_SIZE = (int) Math.sqrt( TOKENS.length );
		final double[][] ARRAY_2D = new double[MATRIX_SIZE][MATRIX_SIZE];

		for(int x=0; x < MATRIX_SIZE; x++){
			for(int y=0; y < MATRIX_SIZE; y++){
				ARRAY_2D[x][y] = this.TOKENS[(y*MATRIX_SIZE) + x]; 
			}
		}

		return ARRAY_2D;
	}



	// ..................setter method .................


	public void setClassLocation(final double[] LABEL_LOCATION){
		labelLocation = LABEL_LOCATION;
	}




	// ..................getters method .................

	
	public double getLabel(){
		return this.LABEL;
	}

	public double[] getData1D(){
		return this.TOKENS;
	}

	public double[][] getData2D(){
		return this.MATRIX;
	}

	public double getToken1D(final int INDEX){
		return this.TOKENS[INDEX];
	}

	public double getToken2D(final int X, final int Y){
		return this.MATRIX[X][Y];
	}

	public double[] getLabelLocation(){
		return this.labelLocation;
	}
}
