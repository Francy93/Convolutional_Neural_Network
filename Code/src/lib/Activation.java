package lib;

public class Activation{

	/* LINEAR, BINARY, SIGMOID, TANH, SWISH, MISH, RELU, LRELU, GELU, SELU, PRELU, ELU, SOFTPLUS, SOFTMAX; */

	public static class Linear {

		/**
		 * 
		 * @param X linear input
		 * @param A parameter
		 * @return function
		 */
		public static double function(final double X, final double A){ return A*X; }
		/**
		 * 
		 * @param X linear input
		 * @param F non linear output
		 * @return derivative
		 */
		public static double derivative(final double X, final double F){ return F/X; }
		/**
		 * Xavier Weight initialization
		 * @param N_INPUTS umber of inputs of the current node
		 * @return	Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( 1.0 / Math.sqrt(N_INPUTS) );
		}
		/**
		 * Normalized Xavier Weight initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( 6.0 / Math.sqrt(N_INPUTS + N_OUPUTS) );
		}
	}

	public static class Binary {

		/**
		 * 
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return X < 0.0? 0.0: 1.0; }
		/**
		 * 
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){ return X != 0.0? 0.0: 1.0; }
		/**
		* Xavier Weight initialization
		* @param N_INPUTS umber of inputs of the current node
		* @return	Random double with a uniform probability distribution
		*/
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( 1.0 / Math.sqrt(N_INPUTS) );
	   }
	   /**
		 * Normalized Xavier Weight initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( 6.0 / Math.sqrt(N_INPUTS + N_OUPUTS) );
		}
	}

	public static class Sigmoid {

		/**
		 * 
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return (1.0/(1.0 + Math.exp(-X))); }
		/**
		 * 
		 * @param F non linear output
		 * @return derivative
		 */
		public static double derivative(final double F){ return F*(1.0-F); }

		/**
		 * Xavier Weight initialization
		 * @param N_INPUTS umber of inputs of the current node
		 * @return	Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( 1.0 / Math.sqrt(N_INPUTS) );
		}
		/**
		 * Normalized Xavier Weight initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( 6.0 / Math.sqrt(N_INPUTS + N_OUPUTS) );
		}
	}

	public static class Tanh {

		/**
		 * 
		 * @param X linear input
		 * @return function
		 */
		//public static double function(final double X){ return (2/(1 + Math.exp(-2.0*X))) -1.0; }
		public static double function(final double X){ return Math.tanh(X); }
		/**
		 * 
		 * @param F non linear output
		 * @return derivative
		 */
		public static double derivative(final double F){ return 1.0 - Math.pow(F, 2.0); }

		/**
		 * Xavier Weight initialization
		 * @param N_INPUTS umber of inputs of the current node
		 * @return	Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( 1.0 / Math.sqrt(N_INPUTS) );
		}
		/**
		 * Normalized Xavier Weight initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( 6.0 / Math.sqrt(N_INPUTS + N_OUPUTS) );
		}
	}

	public static class Swish {

		/**
		 * 
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return X * Sigmoid.function(X); }
		/**
		 * 
		 * @param X linear input
		 * @param F non linear output
		 * @return derivative
		 */
		public static double derivative(final double X, final double F){ return F + Sigmoid.function(X) * (1.0-F); }
		/**
		 * He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS)) );
		}
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS * (double)N_OUPUTS)) );
		}
	}

	public static class Mish {
		/**
		 * 
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return X * Tanh.function(Softplus.function(X)); }

		/**
		 * 
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){
			final double OMEGA = 4.0 * (1.0 + X) + 4.0 * Math.exp(2.0 * X) + Math.exp(3.0 * X) +  Math.exp(X) * (4.0 * X + 6.0);
			//final double DELTA = 1.0 + Math.pow(Math.exp(X) + 1.0, 2); // Light version
			final double DELTA = 2.0 * Math.exp(X) + Math.exp(2.0 * X) + 2.0;

			return  Math.exp(X) * OMEGA / Math.pow(DELTA, 2);
		}
		/**
		 * He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS)) );
		}
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS * (double)N_OUPUTS)) );
		}

	}

	public static class Relu{

		/**
		 * 
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return X < 0.0? 0.0: X; }
		/**
		 * 
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){ return X > 0.0? 1.0 : 0.0; }
		/**
		 * He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS)) );
		}
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS * (double)N_OUPUTS)) );
		}
	}

	public static class Lrelu {

		/**
		 * 
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return Prelu.function(X, 0.01); }
		/**
		 * 
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){ return Prelu.derivative(X, 0.01); }
		/**
		 * He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS)) );
		}
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS * (double)N_OUPUTS)) );
		}
	}

	public static class Gelu {

		/**
		 * 
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){
			return X * (0.5 * (1.0 + erf(X / Math.sqrt(2.0))));
		}
		/**
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){
			return 0.5 * Math.tanh(0.0356774 * X * 3.0 + 0.797885 * X) + (0.0535161 * X * 3.0 + 0.398942 * X) * sech(0.0356774 * X * 3.0 + 0.797885 * X) + 0.5;
		}
		/**
		 * He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS)) );
		}
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS * (double)N_OUPUTS)) );
		}
	}

	public static class Selu {
		private static final double LAMBDA = 1.05070098, ALPHA = 1.67326324;

		/**
		 * 
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){
			return X < 0? LAMBDA * (ALPHA * (Math.exp(X) - 1.0)): LAMBDA * X;
		}
		/**
		 * 
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){
			return X < 0? LAMBDA * (ALPHA * Math.exp(X)): LAMBDA;
		}
		/**
		 * He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS)) );
		}
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS * (double)N_OUPUTS)) );
		}
	}

	public static class Prelu {

		/**
		 * 
		 * @param X linear input
		 * @param A parameter
		 * @return function
		 */
		public static double function(final double X, final double A){ return X < 0.0? A*X: X; }
		/**
		 * 
		 * @param X linear input
		 * @param A parameter
		 * @return derivative
		 */
		public static double derivative(final double X, final double A){ return X < 0.0? A: 1.0; }
		/**
		 * He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS)) );
		}
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS * (double)N_OUPUTS)) );
		}
	}


	public static class Elu {

		/**
		 * 
		 * @param X linear input
		 * @param A parameter
		 * @return function
		 */
		public static double function(final double X, final double A){ return X < 0.0? A*(Math.exp(X)-1.0): X; }
		/**
		 * 
		 * @param X linear input
		 * @param A parameter
		 * @param F non linear output
		 * @return derivative
		 */
		public static double derivative(final double X, final double A, final double F){ return X < 0.0? F+A: 1.0; }
		/**
		 * He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS)) );
		}
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS * (double)N_OUPUTS)) );
		}
	}

	public static class Softplus {

		/**
		 * 
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return Math.log(Math.exp(X) + 1.0); }
		/**
		 * 
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){ return 1.0 / (1.0 + Math.exp(-X)); }
		/**
		 * He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS)) );
		}
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( Math.sqrt(2.0 / ((double)N_INPUTS * (double)N_OUPUTS)) );
		}
	}

	public static class Softmax {

		/**
		 * 
		 * @param I linear output
		 * @param classes linear output
		 * @return function
		 */
		public static double function(final int I, final double[] CLASSES){
			double sum = 0; 
			final double F = Math.exp(CLASSES[I]);
	
			for(int index = 0 ; index < CLASSES.length; index++){
				sum += Math.exp(CLASSES[index]);
			}

			return F/sum;
		}
		/**
		 * 
		 * @param I index of non-linear output from softmax interested class
		 * @param F_CLASSES non-linear output from softmax all classes
		 * @return derivative
		 */
		public static double derivative(final int I, final double[] F_CLASSES){
			double sum = 0;
			final double F = F_CLASSES[I];

			for(int index = 0 ; index < F_CLASSES.length; index++){
				sum += I == index? F * (1.0 - F): -F_CLASSES[index] * F;
			}

			return sum;
		}
		/**
		 * Xavier Weight initialization
		 * @param N_INPUTS umber of inputs of the current node
		 * @return	Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS){
			return weightCalc( 1.0 / Math.sqrt(N_INPUTS) );
		}
		/**
		 * Normalized Xavier Weight initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double randomWeight(final int N_INPUTS, final int N_OUPUTS){
			return weightCalc( 6.0 / Math.sqrt(N_INPUTS + N_OUPUTS) );
		}
	}

	/**
	 * Calculating the initial random weight value
	 * @param FORMULA
	 * @return random weight
	 */
	private static double weightCalc(final double FORMULA){
		return (-FORMULA) + Math.random() * (FORMULA - (-FORMULA));
	}
  
	/**
	 * Gauss error function
	 * @param z subject to catastrophic cancellation when z in very close to 0
	 * @return Gauss error
	 */
	private static double erf(final double z) {
		double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

		// use Horner's method
		double ans = 1.0 - t * Math.exp( -z	* -   1.26551223 +
											t * ( 1.00002368 +
											t * ( 0.37409196 + 
											t * ( 0.09678418 + 
											t * (-0.18628806 + 
											t * ( 0.27886807 + 
											t * (-1.13520398 + 
											t * ( 1.48851587 + 
											t * (-0.82215223 + 
											t * ( 0.17087277))))))))));
		return z >= 0.0? ans: -ans;
	}

	public static double sech(double a) {
		return 1.0D / Math.cosh(a);
	}
}