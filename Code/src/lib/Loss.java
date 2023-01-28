package lib;

/**
 * Collection of Loss functions
 */
public enum Loss {
    /* MSE, MAE, CROSS_ENTROPY, HUBER, KULLBACK; */

    MSE{
    	/**
		 * Mean Squared Error function
		 * @param PRED the output predicted from the model 
         * @param TARGET the output which should have been predicted
		 * @return double
		 */
        public double function(final double PRED, final double TARGET){ return Math.pow(TARGET - PRED, 2.0); }
        public double function(final double[] PRED, final double[] TARGET){
            double sum = 0.0;

        	for(int index = 0 ; index < PRED.length; index++){ sum += this.function(PRED[index], TARGET[index]); }
            return sum / PRED.length;
        }
        /**
		 * Mean Squared Error derivative
		 * @param P predicted output from the previous Function
         * @param T actual target
		 * @return
		 */
        public double derivative(final double P, final double T){ return 2.0 * (P - T); }
        public double derivative(final double[] P, final double[] T){ 
            double sum = 0.0;

            for(int index = 0 ; index < P.length; index++){ sum += P[index] - T[index]; }
            return (sum * 2.0 ) / P.length;
        }
    },
    
    MAE{
        /**
		 * Mean Absolute Error function
		 * @param PRED the output predicted from the model 
         * @param TARGET the output which should have been predicted
		 * @return double
		 */
        public double function(final double PRED, final double TARGET){ return Math.abs(TARGET - PRED); }
        public double function(final double[] PRED, final double[] TARGET){
            double sum = 0.0;

            for(int index = 0 ; index < PRED.length; index++){ sum += this.function(PRED[index], TARGET[index]); }
            return sum / PRED.length;
        }
        /**
		 * Mean Absolute Error derivative
	     * @param P the output predicted from the model 
         * @param T the output (target) which should have been predicted
		 * @return double P > T ? 1.0 : (P < T ? -1.0: 0.0);
		 */
        public double derivative(final double P, final double T){ return Math.signum(P - T); }
        public double derivative(final double[] P, final double[] T){ 
            double sum = 0.0;

            for(int index = 0 ; index < P.length; index++){ sum += this.derivative(P[index], T[index]); }
            return sum / P.length;
        }
    },

    CROSS_ENTROPY{
        /**
		 * Cross entropy function
         * @param PRED the output predicted from the model 
         * @param TARGET the output (target) which should have been predicted
		 * @return function
		 */
        public double function(final double PRED, final double TARGET){ return -Math.log(TARGET == 1 ? PRED : 1 - PRED); }
        public double function(final double[] PRED, final double[] TARGET){
            double sum = 0.0;

            for(int index = 0 ; index < PRED.length; index++){ sum += this.function(PRED[index], TARGET[index]); }
            return sum / PRED.length;
        }
        /**
         * Cross Entropy derivative
         * @param P output of the previous neuron (softmax output)
         * @param T target value
         * @return derivative
         */
        public double derivative(final double P, final double T){ return P - T; }
        public double derivative(final double[] P, final double[] T){ 
            double sum = 0.0;

            for(int index = 0 ; index < P.length; index++){ sum += this.derivative(P[index], T[index]); }
            return sum / P.length;
        }
    },

    HUBER{
        /**
		 * Huber loss function
         * @param PRED the output predicted from the model 
         * @param TARGET the output (target) which should have been predicted
		 * @return double
		 */
        public double function(final double PRED, final double TARGET){
            final double DIFF = Loss.MAE.function(PRED, TARGET);
            if(DIFF <= delta)   return 0.5 * Math.pow(DIFF, 2.0);
            else                return delta * DIFF - 0.5 * Math.pow(delta, 2.0);
        }
        public double function(final double[] PRED, final double[] TARGET){
            double sum = 0.0;

            for(int index = 0 ; index < PRED.length; index++){ sum += this.function(PRED[index], TARGET[index]); }
            return sum / PRED.length;
        }
        /**
         * Huber loss derivative
         * @param P output of the previous neuron (softmax output)
         * @param T target value
         * @return derivative
         */
        public double derivative(final double P, final double T) {
            final double DIFF = T - P;
            return Math.abs(DIFF) <= delta? DIFF: (DIFF > 0? -delta: delta);
        }
        public double derivative(final double[] P, final double[] T){ 
            double sum = 0.0;

            for(int index = 0 ; index < P.length; index++){ sum += this.derivative(P[index], T[index]); }
            return sum / P.length;
        }
    },

    KULLBACK{
        /**
		 * 
         * @param PRED the output predicted from the model 
         * @param TARGET the output which should have been predicted
		 * @return double
		 */
        public double function(final double PRED, final double TARGET){ return PRED * Math.log((PRED / TARGET)); }
        public double function(final double[] PRED, final double[] TARGET){
            double sum =0.0; 

            for(int index = 0 ; index < PRED.length; index++){
				if (PRED[index] != 0) sum += this.function(PRED[index], TARGET[index]);
			}

            return sum;
        }
        /**
         * As the derivative of the Kullback-Leibler divergence is not defined, we use the derivative of the Cross Entropy function
         * @param P output of the previous neuron (softmax output)
         * @param T target value
         * @return derivative
         */
        public double derivative(final double P, final double T){ return Loss.CROSS_ENTROPY.derivative(P, T); }
        public double derivative(final double[] P, final double[] T){ return Loss.CROSS_ENTROPY.derivative(P, T); }
    };

    // Abstract methods
    public abstract double function(final double PREDICTD, final double TERGAET);
    public abstract double function(final double PREDICTD[], final double TERGAET[]);
    public abstract double derivative(final double PREDICTD, final double TERGAET);
    public abstract double derivative(final double PREDICTD[], final double TERGAET[]);

    protected double delta = 1.0; // Delta value for Huber loss function  
    /**
     * Set the delta value for the Huber loss function
     * @param DELTA
     */
    public void setDelta(final double DELTA){ this.delta = DELTA; }
}