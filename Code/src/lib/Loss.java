package lib;

public class Loss {

    /* MSE, MAE, CROSS_ENTROPY, HUBER, KULLBACK; */
    
    
    public static class MSE{

    	/**
		 * Mean Squared Error function
		 * @param classes_pred the output predicted from the model 
         * @param classes_act the output which should have been predicted
		 * @return double
		 */
        public static double function(final double[] classes_pred, final double[] classes_act){
            double sum = 0.0;

        	for(int index = 0 ; index < classes_pred.length; index++){
				sum += Math.pow((classes_pred[index] - classes_act[index]), 2.0);
			}
            return sum/classes_pred.length;
        }
        /**
		 * Mean Squared Error derivative
		 * @param F predicted output from the previous Function
         * @param T actual target
		 * @return
		 */
        public static double derivative(final double F, final double T){ return 2.0 * (F - T); }
    }
    
    public static class MAE{

        /**
		 * Mean Average Error function
		 * @param classes_pred the output predicted from the model 
         * @param classes_act the output which should have been predicted
		 * @return double
		 */
        public static double function(final double[] classes_pred, final double[] classes_act){
            double sum =0.0;

            for(int index = 0 ; index < classes_pred.length; index++){
                sum += Math.abs((classes_pred[index] - classes_act[index]));
            }

            return sum/classes_pred.length;
        }
        /**
		 * Mean Average Error derivative
	     * @param y_hat the output predicted from the model 
         * @param T the output (target) which should have been predicted
		 * @return double
		 */
        public static double derivative(final double y_hat, final double T){ return y_hat > T ? 1.0 : -1.0; }
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
         * @param classes_pred the output predicted from the model 
         * @param classes_act the output which should have been predicted
		 * @return double
		 */
        public static double function(final double[] classes_pred, final double[] classes_act){
            double sum =0.0; 

            for(int index = 0 ; index < classes_pred.length; index++){
				sum += classes_pred[index] * (Math.log((classes_pred[index]/classes_act[index])));
			}

            return sum;
        }
        public static double derivative(final double PREV_Y, final double T){return 0.0;} // TO DO
    }
}