package lib;

public class Loss {

    /* MSE, MAE, CROSS_ENTROPY, HUBER, KULLBACK; */
    
    
    public static class MSE{

    	/**
		 * Mean Squared Error function
		 * @param PRED the output predicted from the model 
         * @param TARGET the output which should have been predicted
		 * @return double
		 */
        public static double function(final double PRED, final double TARGET){ return Math.pow(PRED - TARGET, 2.0); }
        public static double function(final double[] PRED, final double[] TARGET){
            double sum = 0.0;

        	for(int index = 0 ; index < PRED.length; index++){
				sum += Loss.MSE.function(PRED[index], TARGET[index]);
			}
            return sum/PRED.length;
        }
        /**
		 * Mean Squared Error derivative
		 * @param P predicted output from the previous Function
         * @param T actual target
		 * @return
		 */
        public static double derivative(final double P, final double T){ return 2.0 * (P - T); }
    }
    
    public static class MAE{

        /**
		 * Mean Absolute Error function
		 * @param PRED the output predicted from the model 
         * @param TARGET the output which should have been predicted
		 * @return double
		 */
        public static double function(final double PRED, final double TARGET){ return Math.abs(PRED - TARGET); }
        public static double function(final double[] PRED, final double[] TARGET){
            double sum =0.0;

            for(int index = 0 ; index < PRED.length; index++){
                sum += Loss.MAE.function(PRED[index], TARGET[index]);
            }

            return sum/PRED.length;
        }
        /**
		 * Mean Absolute Error derivative
	     * @param P the output predicted from the model 
         * @param T the output (target) which should have been predicted
		 * @return double (P-T)/Math.abs(P-T)
		 */
        public static double derivative(final double P, final double T){ return P > T ? 1.0 : -1.0; }
    }

    public static class Cross_Entropy{
        /**
		 * Cross entropy function
         * @param PRED the output predicted from the model 
         * @param T the output (target) which should have been predicted
		 * @return function
		 */
        public static double function(final double PRED, final double T){  
            if(T == 1.0)	return -(Math.log(PRED));
            else			return -(Math.log(1.0 - PRED));
        }
        /**
         * Cross Entropy derivative
         * @param PRED output of the previous neuron (softmax output)
         * @param T target value
         * @return derivative
         */
        public static double derivative(final double PRED, final double T){ return PRED - T; }
    }


    public static class Huber{
        /**
		 * 
         * @param PRED the output predicted from the model 
         * @param T the output (target) which should have been predicted
		 * @return double
		 */
        public static double function(final double PRED, final double T){
            final double DELTA = 1.0;
            return Math.abs(PRED - T) < DELTA ?  Math.pow((0.5 * (T - PRED)), 2.0) : DELTA * (Math.abs(T - PRED) -0.5 * DELTA);
        }
        public static double derivative(final double PREV_Y, final double T){return 0.0;} // TO DO
    }

    public static class Kullback{
        /**
		 * 
         * @param PRED the output predicted from the model 
         * @param TARGET the output which should have been predicted
		 * @return double
		 */
        public static double function(final double PRED, final double TARGET){ return PRED * Math.log((PRED / TARGET)); }
        public static double function(final double[] PRED, final double[] TARGET){
            double sum =0.0; 

            for(int index = 0 ; index < PRED.length; index++){
				sum += Loss.Kullback.function(PRED[index], TARGET[index]);
			}

            return sum;
        }
        public static double derivative(final double PREV_Y, final double T){ return T * Math.log((T / PREV_Y)); } // TO DO
    }
}