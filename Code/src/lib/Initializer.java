package lib;

public class Initializer{

    private static final double ZERO		= 0;
    private static final double ONE			= 1;
    private static final double LECUM_NORM	= 1;
    private static final double LECUM_UNIF	= 3;
    private static final double XAVIER_NORM	= 2;
    private static final double XAVIER_UNIF	= 6;
    private static final double HE_NORM		= 2;
    private static final double HE_UNIF		= 6;


    public static class Constant{

        /**
         * Zero Weight initialization
         * @return	Constant customized double
         */
        public static double zero(){
            return custom(ZERO);
        }

        /**
         * One Weight initialization
         * @return	Constant customized double
         */
        public static double one(){
            return custom(ONE);
        }

        /**
         * Constant Weight initialization
         * @param PARAM constant output
         * @return	Constant customized double
         */
        public static double custom(final double PARAM){
            return PARAM;
        }
    }

    public static class Lecun{

         /**
         * LeCun Weight initialization
         * @param N_INPUTS umber of inputs of the current node
         * @return	Random double with a uniform probability distribution
         */
        public static double normal(final int N_INPUTS){
            return randomGen( LECUM_NORM, N_INPUTS );
        }

        /**
         * Normalized LeCun Weight initialization
         * @param N_INPUTS number of inputs of the current node
         * @return Random double with a uniform probability distribution
         */
        public static double uniform(final int N_INPUTS){
            return randomGen( LECUM_UNIF, N_INPUTS );
        } 
    }

    public static class Xavier{

        /**
         * Xavier Weight initialization
         * @param N_INPUTS umber of inputs of the current node
         * @return	Random double with a uniform probability distribution
         */
        public static double normal(final int N_INPUTS, final int N_OUPUTS){
            return randomGen( XAVIER_NORM, N_INPUTS + N_OUPUTS );
        }

        /**
         * Normalized Xavier Weight initialization
         * @param N_INPUTS number of inputs of the current node
         * @param N_OUPUTS number of output of the current node
         * @return Random double with a uniform probability distribution
         */
        public static double uniform(final int N_INPUTS, final int N_OUPUTS){
            return randomGen( XAVIER_UNIF, N_INPUTS + N_OUPUTS );
        }
    }

    public static class He{

        /**
         * He Weight Initialization
         * @param N_INPUTS number of inputs of the current node
         * @return random double with a Gaussian probability
         */
        public static double normal(final int N_INPUTS){
            return randomGen( HE_NORM, N_INPUTS );
        }
        /**
         * Normalized He Weight Initialization
         * @param N_INPUTS number of inputs of the current node
         * @param N_OUPUTS number of output of the current node
         * @return Random double with a uniform probability distribution
         */
        public static double uniform(final int N_INPUTS){
            return randomGen( HE_UNIF, N_INPUTS );
        }
    }


    /**
     * Get weight value
     * @param PARAM 
     * @param FAN number of inputs and outputs
     * @return random initializer
     */
    private static double randomGen(final double PARAM, final double FAN){
        return randomize( Math.sqrt( PARAM / FAN ) );
    }

    /**
	 * Calculating the initial random weight value
	 * @param FORMULA
	 * @return random weight
	 */
	private static double randomize(final double FORMULA){
		return Util.rangeRandom(-FORMULA, FORMULA);
	}
}