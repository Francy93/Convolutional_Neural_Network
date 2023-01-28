package lib;

/**
 * Collection of Optimizers
 * @see inspired by https://github.com/pritishmishra703/Deep-Learning-From-Neurons-to-GANs/blob/master/Chapter4_Optimization.ipynb
 */
public enum Optimizer {
    /* SGD, MOMENTUM, RMSPROP, NESTEROV, ADAGRAD, ADADELTA, ADAM, ADAMAX, AMSGRAD, NADAM; */
  
    SGD{
        public void timeStepIncrease(){}
        public int momentNumber(){ return 0; }
        public double optimize(final double[] MOMENTUM, final double GRAD){
            // colculating the gradient descent
            return this.learningRate * (GRAD / this.batchSize);
        }
    },
    MOMENTUM{
        public void timeStepIncrease(){}
        public int momentNumber(){ return 1; }
        public double optimize(final double[] MOMENTUM, final double GRAD){
            // colculating the momentum
            MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + (this.learningRate * (GRAD / this.batchSize)); 
            return MOMENTUM[0];
        }
    },
    RMSPROP{
        public void timeStepIncrease(){}
        public int momentNumber(){ return 1; }
        public double optimize(final double[] MOMENTUM, double grad){
            grad		/= this.batchSize; // batch size average

            // colculating the momentum
            MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + ((1.0 - this.BETA1) * grad * grad);

            return (this.learningRate / Math.sqrt(MOMENTUM[0] + this.EPSILON)) * grad;
        }
    },
    NESTEROV{
        public void timeStepIncrease(){}
        public int momentNumber(){ return 1; }
        public double optimize(final double[] MOMENTUM, double grad){
            grad		/= this.batchSize; // batch size average

            // colculating the momentum
            MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + (this.learningRate * grad); 

            return (this.BETA1 * MOMENTUM[0]) + ((1.0 - this.BETA1) * this.learningRate * grad);
        }
    },
    ADAGRAD{
        public void timeStepIncrease(){}
        public int momentNumber(){ return 1; }
        public double optimize(final double[] MOMENTUM, double grad){
            grad		/= this.batchSize; // batch size average

            // colculating the momentum
            MOMENTUM[0] += grad * grad;

            return (this.learningRate / Math.sqrt(MOMENTUM[0] + this.EPSILON)) * grad;
        }
    },
    ADADELTA{
        public void timeStepIncrease(){}
        public int momentNumber(){ return 2; }
        public double optimize(final double[] MOMENTUM, double grad){
            grad		/= this.batchSize; // batch size average

            // compute the first moment
            MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + ((1.0 - this.BETA1) * grad * grad); 

            // compute the second moment
            this.optGrad 	= (Math.sqrt(MOMENTUM[1] + this.EPSILON) / Math.sqrt(MOMENTUM[0] + this.EPSILON)) * grad;

            // storing the updated second moment value
            MOMENTUM[1] = (MOMENTUM[1] * this.BETA1) + ((1.0 - this.BETA1) * this.optGrad * this.optGrad);

            return optGrad;
        }
    },
    ADAM{
        public void timeStepIncrease(){ this.timeStep++; }
        public int momentNumber(){ return 2; }
        public double optimize(final double[] MOMENTUM, double grad){
            grad		/= this.batchSize; // batch size average

            // compute the first moment
            MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + ((1.0 - this.BETA1) * grad); 

            // compute the second moment
            MOMENTUM[1] = (MOMENTUM[1] * this.BETA2) + ((1.0 - this.BETA2) * grad * grad); 

            // normalisation
            this.norm1	= MOMENTUM[0] / (1.0 - Math.pow(this.BETA1, this.timeStep));
            this.norm2	= MOMENTUM[1] / (1.0 - Math.pow(this.BETA2, this.timeStep));

            return this.learningRate * this.norm1 / (Math.sqrt(this.norm2) + this.EPSILON);
        }
    },
    ADAMAX{
        public void timeStepIncrease(){ this.timeStep++; }
        public int momentNumber(){ return 2; }
        public double optimize(final double[] MOMENTUM, double grad){
            grad		/= this.batchSize; // batch size average

            // compute the first moment
            MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + ((1.0 - this.BETA1) * grad); 

            // compute the second moment
            MOMENTUM[1] = Math.max(this.BETA2 * MOMENTUM[1], Math.abs(grad));

            // normalisation
            this.norm1	= MOMENTUM[0] / (1.0 - Math.pow(this.BETA1, this.timeStep));

            return this.learningRate * this.norm1 / (MOMENTUM[1] + this.EPSILON);
        }
    },
    NADAM{
        public void timeStepIncrease(){ this.timeStep++; }
        public int momentNumber(){ return 2; }
        public double optimize(final double[] MOMENTUM, double grad){
            grad		/= this.batchSize; // batch size average

            // compute the first moment
            MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + ((1.0 - this.BETA1) * grad); 

            // compute the second moment
            MOMENTUM[1] = (MOMENTUM[1] * this.BETA2) + ((1.0 - this.BETA2) * grad * grad); 

            // normalisation
            this.norm1	= MOMENTUM[0] / (1.0 - Math.pow(this.BETA1, this.timeStep));
            this.norm2	= MOMENTUM[1] / (1.0 - Math.pow(this.BETA2, this.timeStep));

            return this.learningRate * (this.BETA1 * this.norm1 + (1.0 - this.BETA1) * grad / (1.0 - Math.pow(this.BETA1, this.timeStep))) / (Math.sqrt(this.norm2) + this.EPSILON);
        }
    },
    AMSGRAD{
        public void timeStepIncrease(){ this.timeStep++; }
        public int momentNumber(){ return 2; }
        public double optimize(final double[] MOMENTUM, double grad){
            grad		/= this.batchSize; // batch size average

            // compute the first moment
            MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + ((1.0 - this.BETA1) * grad); 

            // compute the second moment
            MOMENTUM[1] = Math.max((MOMENTUM[1] * this.BETA2), (grad * grad)); 

            // normalisation
            this.norm1	= MOMENTUM[0] / (1.0 - Math.pow(this.BETA1, this.timeStep));

            return this.learningRate * this.norm1 / (Math.sqrt(MOMENTUM[1]) + this.EPSILON);
        }
    };

    // optimizers parameters
    protected final double BETA1 = 0.9, BETA2 = 0.999, EPSILON = 1e-08;
    protected double norm1, norm2, optGrad;	    // temporary normalizers
    protected double timeStep = 0;			    // updates counter
    
    // custom paramenters
    protected double    learningRate;		    // learning rate
    protected double    batchSize;			    // batch size

    /**
     * Optimizer method
     * @param MOMENTUM array of momentums
     * @param grad  gradient
     * @return optimized gradient
     */
    public abstract double optimize(final double[] MOMENTUM, double grad);
    public abstract int momentNumber();         // get the momentums amount
    public abstract void timeStepIncrease();    // increase the time step
    
    /**
     * setting the optimizer parameters
     * @param LR Learning rate
     * @param BS Batch site
     */
    public void setParam(final double LR, final double BS){
        this.learningRate   = LR;
        this.batchSize      = BS;
    }

}
