package lib;

public enum Optimizer {
  
    SGD{
        public double optimize(final double[] MOMENTUM, final double GRAD){
            // colculating the gradient descent
            return this.learningRate * (GRAD / this.batchSize);
        }
    },
    MOMENTUM{
        public double optimize(final double[] MOMENTUM, final double GRAD){
            // colculating the momentum
            MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + (this.learningRate * (GRAD / this.batchSize)); 
            return MOMENTUM[0];
        }
    },
    ADAGRAD{
        public double optimize(final double[] MOMENTUM, double grad){
            grad /= this.batchSize; // batch size average

            MOMENTUM[0] += grad * grad;

            return (this.learningRate / Math.sqrt(MOMENTUM[0] + this.EPSILON)) * grad;
        }
    },
    ADADELTA{
        public double optimize(final double[] MOMENTUM, double grad){
            grad /= this.batchSize; // batch size average

            // compute the first moment
            MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + ((1.0 - this.BETA1) * grad * grad); 

            // compute the first moment
            optGrad = ( Math.sqrt(MOMENTUM[1] + this.EPSILON) / Math.sqrt(MOMENTUM[0] + this.EPSILON)) * grad;

            // storing the updated second moment value
            MOMENTUM[1] = (MOMENTUM[1] * this.BETA1) + ((1.0 - this.BETA1) * optGrad * optGrad);

            return optGrad;
        }
    },
    ADAM{
        public double optimize(final double[] MOMENTUM, double grad){
            grad /= this.batchSize; // batch size average

            // compute the first moment
            MOMENTUM[0] = (MOMENTUM[0] * this.BETA1) + ((1.0 - this.BETA1) * grad); 

            // compute the second moment
            MOMENTUM[1] = (MOMENTUM[1] * this.BETA2) + ((1.0 - this.BETA2) * grad * grad); 

            // normalisation
            this.norm1 = MOMENTUM[0] / (1.0 - Math.pow(this.BETA1, this.timeStep));
            this.norm2 = MOMENTUM[1] / (1.0 - Math.pow(this.BETA2, this.timeStep));

            return this.learningRate * this.norm1 / (Math.sqrt(this.norm2) + this.EPSILON);
        }
    };

    // Adam optimizer parameters
    protected final double BETA1 = 0.9, BETA2 = 0.999, EPSILON = 1e-08;
    protected double norm1, norm2, optGrad; // normalizers
    protected double timeStep = 0; // updates counter
    
    // custom paramenters
    protected double    learningRate;   // learning rate
    protected double    batchSize;      // batch size

    /**
     * Optimizer method
     * @param MOMENTUM array of momentums
     * @param grad  gradient
     * @return optimized gradient
     */
    public abstract double optimize(final double[] MOMENTUM, double grad);

    /**
     * setting the optimizer parameters
     * @param LR Learning rate
     * @param BS Batch site
     */
    public void setParam(final double LR, final double BS){
        learningRate   = LR;
        batchSize      = BS;
    }

    // increase the time step
    public void timeStepIncrease(){
        this.timeStep++;
    }

}
