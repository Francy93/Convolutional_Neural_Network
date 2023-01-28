package lib;

/**
 * Collection of Activation functions
 */
public class Activation {
	/* LINEAR, BINARY, SIGMOID, TANH, SWISH, MISH, RELU, LRELU, GELU, SELU, PRELU, ELU, SOFTPLUS, SOFTMAX; */

	public static class Linear {

		/**
		 * Linear function
		 * @param X linear input
		 * @param A parameter
		 * @return function
		 */
		public static double function(final double X, final double A){ return A*X; }
		/**
		 * Derivative of the linear function
		 * @param X linear input
		 * @param F non linear output
		 * @return derivative
		 */
		public static double derivative(final double X, final double F){ return F/X; }
		
		/**
		 * Normalized Xavier Weight initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double weightsInit(final int N_INPUTS, final int N_OUTPUTS){
			return Initializer.Xavier.uniform( N_INPUTS, N_OUTPUTS );
		}
	}

	public static class Binary {

		/**
		 * Binary step function
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return X < 0.0? 0.0: 1.0; }
		/**
		 * Derivative of the binary step function
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){ return X != 0.0? 0.0: 1.0; }
		
		/**
		 * Normalized Xavier Weight initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double weightsInit(final int N_INPUTS, final int N_OUTPUTS){
			return Initializer.Xavier.uniform( N_INPUTS, N_OUTPUTS );
		}
	}

	public static class Sigmoid {

		/**
		 * Sigmoid function
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return (1.0 / (1.0 + Math.exp(-X))); }
		/**
		 * Derivative of the sigmoid function
		 * @param F non linear output
		 * @return derivative
		 */
		public static double derivative(final double F){ return F * (1.0 - F); }
		/**
		 * Inverse of the sigmoid function
		 * @param F non linear output
		 * @return inverse
		 */
		public static double inverse(final double F){ return -Math.log(-((F - 1.0) / F)); }

		/**
		 * Normalized Xavier Weight initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double weightsInit(final int N_INPUTS, final int N_OUTPUTS){
			return Initializer.Xavier.uniform( N_INPUTS, N_OUTPUTS );
		}
	}

	public static class Tanh {

		/**
		 * Tanh function
		 * @param X linear input
		 * @return function
		 */
		//public static double function(final double X){ return (2/(1 + Math.exp(-2.0*X))) -1.0; }
		public static double function(final double X){ return Math.tanh(X); }
		/**
		 * Derivative of the tanh function
		 * @param F non linear output
		 * @return derivative
		 */
		public static double derivative(final double F){ return 1.0 - Math.pow(F, 2.0); }
		/**
		 * Inverse of the tanh function
		 * @param F non linear output
		 * @return inverse
		 */
		public static double inverse(final double F){ return 0.5 * Math.log((1.0 + F) / (1.0 - F)); }

		/**
		 * Normalized Xavier Weight initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @param N_OUPUTS number of output of the current node
		 * @return Random double with a uniform probability distribution
		 */
		public static double weightsInit(final int N_INPUTS, final int N_OUTPUTS){
			return Initializer.Xavier.uniform( N_INPUTS, N_OUTPUTS );
		}
	}

	public static class Swish {

		/**
		 * Swish function
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return X * Sigmoid.function(X); }
		/**
		 * Derivative of the swish function
		 * @param X linear input
		 * @param F non linear output
		 * @return derivative
		 */
		public static double derivative(final double X, final double F){ return F + Sigmoid.function(X) * (1.0 - F); }
		/**
		 * Inverse of the swish function
		 * @param F non linear output
		 * @return inverse
		 */
		public static double inverse(final double F){ return Math.log(F / (1.0 - F)); }
		
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double weightsInit(final int N_INPUTS){
			return Initializer.He.uniform( N_INPUTS );
		}
	}

	public static class Mish {

		/**
		 * Mish function
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return X * Tanh.function(Softplus.function(X)); }
		/**
		 * Derivative of the mish function
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){
			final double EXP_X	= Math.exp(X);
			final double EXP_2X	= Math.exp(2.0 * X);
			final double OMEGA	= 4.0 * (1.0 + X) + 4.0 * EXP_2X + Math.exp(3.0 * X) +  EXP_X * (4.0 * X + 6.0);
			final double DELTA	= 2.0 * EXP_X + EXP_2X + 2.0;
			//final double DELTA = 1.0 + Math.pow(Math.exp(X) + 1.0, 2); // Light version
			return  EXP_X * OMEGA / Math.pow(DELTA, 2);
		}
		/**
		 * Inverse of the mish function
		 * @param F non linear output
		 * @return inverse
		 */
		public static double inverse(double F) {
			double a = -10, b = 10, x = 0, fx = 0, eps = 1e-10;
			
			while (b - a > eps) {
				x = (a + b) / 2;
				fx = Mish.function(x) - F;
				if (fx > 0)	b = x;
				else 		a = x;
			}
			return x;
		}
		
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double weightsInit(final int N_INPUTS){
			return Initializer.He.uniform( N_INPUTS );
		}

	}

	public static class Relu{

		/**
		 * Relu function
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return X < 0.0? 0.0: X; }
		/**
		 * Derivative of the relu function
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){ return X > 0.0? 1.0 : 0.0; }
		/**
		 * Inverse of the relu function
		 * @param F non linear output
		 * @return inverse
		 */
		public static double inverse(final double F){ return F; }
		
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double weightsInit(final int N_INPUTS){
			return Initializer.He.uniform( N_INPUTS );
		}
	}

	public static class Lrelu {
		private static final double ALPHA = 0.01;

		/**
		 * Lrelu function
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return Prelu.function(X, ALPHA); }
		/**
		 * Derivative of the lrelu function
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){ return Prelu.derivative(X, ALPHA); }
		/**
		 * Inverse of the lrelu function
		 * @param F non linear output
		 * @return inverse
		 */
		public static double inverse(final double F){ return Prelu.inverse(F, ALPHA); }
		
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double weightsInit(final int N_INPUTS){
			return Initializer.He.uniform( N_INPUTS );
		}
	}

	public static class Gelu {

		/**
		 * Gelu function
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){
			return X * (0.5 * (1.0 + Activation.erf(X / Math.sqrt(2.0))));
			//return 0.5 * X * (1.0 + Tanh.function(Math.sqrt(2.0 / 3.14) * (X + 0.044715 * Math.pow(X, 3.0))));	// Light and approximate version
		}
		/**
		 * Derivative of the gelu function
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){
			final double X_POW = Math.pow(X, 3.0);
			return	0.5 * Math.tanh(0.0356774 * X_POW + 0.797885 * X) + 
					(0.0535161 * X_POW + 0.398942 * X) * 
					Math.pow(sech(0.0356774 * X_POW + 0.797885 * X), 2.0) + 0.5;
		}
		/**
		 * Inverse of the gelu function
		 * @param F non linear output
		 * @return inverse
		 */
		public static double inverse(final double F){
			return Math.sqrt(2.0) * Activation.erfInv(2.0 * F - 1.0);
		}
		
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double weightsInit(final int N_INPUTS){
			return Initializer.He.uniform( N_INPUTS );
		}
	}

	public static class Selu {
		private static final double LAMBDA = 1.05070098, ALPHA = 1.67326324;

		/**
		 * Selu function
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){
			return X < 0? LAMBDA * (ALPHA * (Math.exp(X) - 1.0)): LAMBDA * X;
		}
		/**
		 * Derivative of the selu function
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){
			return X < 0? LAMBDA * (ALPHA * Math.exp(X)): LAMBDA;
		}
		/**
		 * Inverse of the selu function
		 * @param F non linear output
		 * @return inverse
		 */
		public static double inverse(final double F){
			return (F < 0.0)? Math.log( (F / (LAMBDA * ALPHA)) + 1.0 ): F / LAMBDA;
		}

		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double weightsInit(final int N_INPUTS){
			return Initializer.He.uniform( N_INPUTS );
		}
	}

	public static class Prelu {

		/**
		 * Prelu function
		 * @param X linear input
		 * @param A parameter
		 * @return function
		 */
		public static double function(final double X, final double A){ return X < 0.0? A*X: X; }
		/**
		 * Derivative of the prelu function
		 * @param X linear input
		 * @param A parameter
		 * @return derivative
		 */
		public static double derivative(final double X, final double A){ return X < 0.0? A: 1.0; }
		/**
		 * Inverse of the prelu function
		 * @param F non linear output
		 * @param A parameter
		 * @return inverse
		 */
		public static double inverse(final double F, final double A){ return F < 0.0? F / A: F; }
		
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double weightsInit(final int N_INPUTS){
			return Initializer.He.uniform( N_INPUTS );
		}
	}


	public static class Elu {

		/**
		 * Elu function
		 * @param X linear input
		 * @param A parameter
		 * @return function
		 */
		public static double function(final double X, final double A){ return X < 0.0? A * (Math.exp(X) - 1.0): X; }
		/**
		 * Derivative of the elu function
		 * @param X linear input
		 * @param A parameter
		 * @param F non linear output
		 * @return derivative
		 */
		public static double derivative(final double X, final double A, final double F){ return X < 0.0? F + A: 1.0; }
		/**
		 * Inverse of the elu function
		 * @param F non linear output
		 * @param A parameter
		 * @return inverse
		 */
		public static double inverse(final double F, final double A){ return F < 0.0? Math.log( F / A + 1.0 ): F; }
		
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double weightsInit(final int N_INPUTS){
			return Initializer.He.uniform( N_INPUTS );
		}
	}

	public static class Softplus {

		/**
		 * Softplus function
		 * @param X linear input
		 * @return function
		 */
		public static double function(final double X){ return Math.log(Math.exp(X) + 1.0); }
		/**
		 * Derivative of the softplus function
		 * @param X linear input
		 * @return derivative
		 */
		public static double derivative(final double X){ return 1.0 / (1.0 + Math.exp(-X)); }
		/**
		 * Inverse of the softplus function
		 * @param F non linear output
		 * @return inverse
		 */
		public static double inverse(final double F){ return Math.log(Math.exp(F) - 1.0); }
		
		/**
		 * Normalized He Weight Initialization
		 * @param N_INPUTS number of inputs of the current node
		 * @return random double with a Gaussian probability
		 */
		public static double weightsInit(final int N_INPUTS){
			return Initializer.He.uniform( N_INPUTS );
		}
	}

	public static class Softmax {

		/**
		 * Lambda function with just a parameter
		 */ 
		public static interface Lambda1<T, V>{
			public V getVal(T a);
		}

		/**
		 * Softmax function
		 * @param I linear output
		 * @param classes linear output
		 * @return function
		 */
		public static <T> double function(final T CURRENT_CLASS, final T[] CLASSES, final Lambda1<T, Double> OPE){ 
			double sum = 0;
			double biggestValue = OPE.getVal(CLASSES[0]);

			// performing the normalization
			for(final T ITER_CLASS: CLASSES){
				biggestValue = Math.max(OPE.getVal(ITER_CLASS), biggestValue);
			}

			// getting the normalized current linear output
			final double L_O = Math.exp(OPE.getVal(CURRENT_CLASS) - biggestValue);
	
			for(final T ITER_CLASS: CLASSES){
				sum += Math.exp(OPE.getVal(ITER_CLASS) - biggestValue);
			}

			return L_O / sum;
		}
		/**
		 * Softmax derivative
		 * @return derivative
		 */
		public static double derivative(){ return 1.0; }
		

		/**
		 * Xavier Weight initialization
		 * @param N_INPUTS umber of inputs of the current node
		 * @return	Random double with a uniform probability distribution
		 */
		public static double weightsInit(final int N_INPUTS){
			return Initializer.Xavier.uniform( N_INPUTS, 1 );
		}
	}
  
	/**
	 * Gauss error function
	 * @param z subject to catastrophic cancellation when z in very close to 0
	 * @return Gauss error
	 */
	private static double erf(final double z) {
		double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

		// use Horner's method
		double ans = 1.0 - t * Math.exp( -z*z -   1.26551223 +
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
	/**
	 * Gauss error function inverse
	 * @param F Gauss error
	 * @return Gauss error inverse
	 */
	private static double erfInv(final double F) {
		double x, t, z=Math.abs(F);

		if (z > 1.0)	return Double.NaN;
		if (z == 0.0)  	return 0.0;
		if (z == 1.0)	return F * Double.POSITIVE_INFINITY;

		if (z > 0.7) {
			// erf(x) = sign(x) * (1 - erf(abs(x)))
			t = 0.5 * (1.0 - z);
			t = Math.sqrt(-2.0 * Math.log(t));
			x = -0.70711 * ((2.30753 + t * 0.27061) / (1.0 + t * (0.99229 + t * 0.04481)) - t);
			if (F < 0.0)	x = -x;
		} else {
			t = 0.5 * F * F;
			x = F * (((((0.00443205 + t * 0.0936242) + t * 0.00053929) - t * 0.00609359) + t * 0.00583892) - t * 0.000345372);
		}
		// two steps of Newton-Raphson correction
		x = x - (erf(x) - F) / (2.0 / Math.sqrt(Math.PI) * Math.exp(-x * x));
		x = x - (erf(x) - F) / (2.0 / Math.sqrt(Math.PI) * Math.exp(-x * x));
		return x;
	}
	
	/**
	 * Hyperbolic secant
	 * @param a
	 * @return
	 */
	public static double sech(double a) {
		return 1.0D / Math.cosh(a);
	}
}