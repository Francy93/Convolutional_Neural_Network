package lib;


import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is a collection of various tools
 */
public class Util{
	
	/**
	 * Lambda function with just a parameter
	 */ 
	public static interface Lambda1<T, V>{
		public V op(T a);
	}
	public static interface Lambda2<T, V>{
		public V op(V a, T b);
	}




	/**
	 * Generate a string repetition
	 * @param s
	 * @param n
	 * @return
	 */
	public static String stringRepeat(final String S, final long N){
		String str = "";
		for(long i=0; i<N; i++) 	str += S;
		return str;
	}



	/**
	 * Average finder
	 * @param array
	 * @return the average
	 */
	public static int arrayAvarage(final double[] ARRAY){
		int index = 0;
		double difference = 0;
		double mean = ArrayMath.addition(ARRAY) / ARRAY.length;

		for(int i=0; i<ARRAY.length; i++){
			double sub = mean <= ARRAY[i]? ARRAY[i] - mean: mean - ARRAY[i];

			if(i==0 || difference > sub){
				difference = sub;
				index = i;
			}
		}
		return index;
	}


	/**
	 * Logic operator xor
	 * @param X
	 * @param Y
	 * @return xor
	 */
	public static boolean xor(final boolean X, final boolean Y){ return X != Y; }


	/**
	 * Round adjusting decimals
	 * @param N number to be rounded
	 * @param D quantity of decimals to be left
	 * @return rounded
	 */
	public static double round(final double N, final int D){
		double round = Math.pow(10, D>=0? D: 0);
		return Math.round(N*round)/round;
	}


	/**
	 * detect if a string is a number
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(final String STR){ 
		try{ Double.parseDouble(STR); }
		catch(NumberFormatException e){ return false; }
		return true;
	}


	/**
	 * Checking whether a number exists already in the array
	 * @param ARRAY of integers
	 * @param B integer to be found
	 * @return number of times it has been found
	 */
	public static long contains(final int[] ARRAY, final int B){
		long occurrence = 0;
		for(final int A: ARRAY){
			if(A==B) occurrence++;
		}
		return occurrence;
	}

	/**
	 * Check how many times a pattern match in a given string
	 * @param STRING given fild
	 * @param MATCH	given pattern
	 * @return how many times the pattern has occurred
	 */
	public static long strMatch(final String STRING, final String MATCH){
		if(STRING.length() < MATCH.length()) return 0;

		final long MATCH_LAST = MATCH.length()-1;
		long occurrence = 0;

		for(int i=0; i<STRING.length(); i++){
			for(int j=0; j<=MATCH_LAST; j++){
				
				try{
					if(MATCH.charAt(j) == STRING.charAt(i+j)){ if(j==MATCH_LAST) occurrence++; }
					else break;
				}catch(IndexOutOfBoundsException e) { return occurrence; }
			}
		}
		return occurrence;
	}

	/**
	 * Generate an integer within a range
	 * @param min
	 * @param max
	 * @return random double with a given range
	 */
	public static double rangeRandom(double min, double max){
		return (Math.random() * (max - min)) + min;
	}

	// merge arrays
	public static <T> T[] arrayMerge(final T[] A, final T[] B) {
		if(A.length > B.length) System.arraycopy(B, 0, A, A.length - B.length, B.length);
		else return B;
		return A;
	}

	// join arrays
	public static <T> T[] arrayJoin(final T[] A, final T[] B) {
		// Create a new array of the combined length
		final T[] JOINED = Arrays.copyOf(A,  A.length + B.length);
		// Copy elements of array B into the result array
		System.arraycopy(B, 0, JOINED, A.length, B.length);
		
		return JOINED;
	}

	// get the largest element in an array
	public static <T> T[] getLargest(final T[][]ARRAY){
		T[] largest = ARRAY[0];
		for(final T[] ELEM: ARRAY){	if(largest.length > ELEM.length) largest = ELEM; }
		return largest;
	}
	public static String getLargest(final String[] ARRAY){
		String largest = "";
		for(final String ELEM: ARRAY){	if(largest.length() > ELEM.length()) largest = ELEM; }
		return largest;
	}

	// get the index of the largest element in an array
	public static <T> int getLargestIndex(final T[][]ARRAY){
		int largest = 0;
		for(int i=0; i<ARRAY.length; i++){	if(largest > ARRAY[i].length) largest = i; }
		return largest;
	}
	public static int getLargestIndex(final String[] ARRAY){
		int largest = 0;
		for(int i=0; i<ARRAY.length; i++){	if(largest > ARRAY[i].length()) largest = i; }
		return largest;
	}

	// get the size of the largest element in an array
	public static <T> long getLargestSize(final T[][]ARRAY){
		long largest = 0;
		for(final T[] ELEM: ARRAY){	largest = Math.max(largest, ELEM.length); }
		return largest;
	}
	public static long getLargestSize(final String[] ARRAY){
		long largest = 0;
		for(final String ELEM: ARRAY){	largest = Math.max(largest, ELEM.length()); }
		return largest;
	}





//||||||||||||||||||||||||||||||||||||||||||||||||||||||||| ARRAY_MATH |||||||||||||||||||||||||||||||||||||||||||||||||||||||||



	
	/**
	 * Sum values of array
	 * @param array
	 * @return sum
	 */
	public static class ArrayMath {

		public static <T extends Number> double addition(final T[]			array)	{ return sum(array, (a,b) -> a+b.doubleValue()); }
		public static					 double addition(final int[]		array)	{ return sum(array, (a,b) -> a+b			  ); }
		public static					 double addition(final long[]		array)	{ return sum(array, (a,b) -> a+b			  ); }
		public static					 double addition(final double[]		array)	{ return sum(array, (a,b) -> a+b			  ); }
		public static <T extends Number> double addition(final ArrayList<T>	array)	{ return sum(array, (a,b) -> a+b.doubleValue()); }
		public static <T extends Number> double subtract(final T[]			array)	{ return sum(array, (a,b) -> a-b.doubleValue()); }
		public static					 double subtract(final int[]		array)	{ return sum(array, (a,b) -> a-b			  ); }
		public static					 double subtract(final long[]		array)	{ return sum(array, (a,b) -> a-b			  ); }
		public static					 double subtract(final double[]		array)	{ return sum(array, (a,b) -> a-b			  ); }
		public static <T extends Number> double subtract(final ArrayList<T>	array)	{ return sum(array, (a,b) -> a-b.doubleValue()); }
		public static <T extends Number> double multiply(final T[]			array)	{ return sum(array, (a,b) -> a*b.doubleValue()); }
		public static					 double multiply(final int[]		array)	{ return sum(array, (a,b) -> a*b			  ); }
		public static					 double multiply(final long[]		array)	{ return sum(array, (a,b) -> a*b			  ); }
		public static					 double multiply(final double[]		array)	{ return sum(array, (a,b) -> a*b			  ); }
		public static <T extends Number> double multiply(final ArrayList<T>	array)	{ return sum(array, (a,b) -> a*b.doubleValue()); }
		public static <T extends Number> double division(final T[]			array)	{ return sum(array, (a,b) -> a/b.doubleValue()); }
		public static					 double division(final int[]		array)	{ return sum(array, (a,b) -> a/b.doubleValue()); }
		public static					 double division(final long[]		array)	{ return sum(array, (a,b) -> a/b.doubleValue()); }
		public static					 double division(final double[]		array)	{ return sum(array, (a,b) -> a/b.doubleValue()); }
		public static <T extends Number> double division(final ArrayList<T>	array)	{ return sum(array, (a,b) -> a/b.doubleValue()); }
		public static double sum(final int[] array, final Lambda2<Integer, Double> comput){
			double result = array[0];
			for(int i=1; i<array.length; i++)   result = comput.op(result, array[i]);
			return result;
		}
		public static double sum(final double[] array, final Lambda2<Double, Double> comput ){
			double result = array[0];
			for(int i=1; i<array.length; i++)   result = comput.op(result, array[i]);
			return result;
		}
		public static double sum(final long[] array, final Lambda2<Long, Double> comput ){
			double result = array[0];
			for(int i=1; i<array.length; i++)   result = comput.op(result, array[i]);
			return result;
		}
		public static <T extends Number> double sum(final ArrayList<T> array,final Lambda2<T, Double> comput ){
			double result = array.get(0).doubleValue();
			for(int i=1; i<array.size(); i++)   result = comput.op(result, array.get(i));
			return result;
		}
		public static <T extends Number> double sum(final T[] array, final Lambda2<T, Double> comput ){
			double result = array[0].doubleValue();
			for(int i=1; i<array.length; i++)   result = comput.op(result, array[i]);
			return result;
		}
	}




//||||||||||||||||||||||||||||||||||||||||||||||||||||||||| QUICK_SORT |||||||||||||||||||||||||||||||||||||||||||||||||||||||||




	/**
	 * (QuickSort) sorting elements according to their specific values
	 * @see https://bit.ly/3GGQU4W
	 * @param arr
	 * @param mode (SortMode)
	 * @return City[]
	 */
	public static class QuickSort<T, V extends Comparable<V>>{

		private final boolean MODE;				// this is the direction the array get sorted
		private final Lambda1<T, V> COMPARE;	// this is what of the array has to be compared
		private final T[] ARRAY;				// this is the array to be sorted

		/**
		 * (QuickSort) sorting elements according to their specific values
		 * @see https://bit.ly/3GGQU4W
		* @param A array
		* @param M mode
		* @param C lambda method
		*/
		private QuickSort(final T[] A, final boolean M, final Lambda1<T, V> C){
			ARRAY	= A;
			MODE	= M;
			COMPARE = C;
			quickSort(0, ARRAY.length-1);
		}

		private T[] quickSort(final int LEFT, final int RIGHT){
			int l	= LEFT, r = RIGHT; 

			// getting the pivot from a calculated mid point
			final V PIVOT = COMPARE.op(ARRAY[(l + r) / 2]);

			// partition 
			while (l <= r) {
				if(MODE){	// loop left index if the current element is smaller or greater than pivot
					while (COMPARE.op(ARRAY[l]).compareTo(PIVOT) < 0)	l++; 
					while (COMPARE.op(ARRAY[r]).compareTo(PIVOT) > 0)	r--;
				}else{		// loop right index if the current element is greater or smaller than pivot
					while (COMPARE.op(ARRAY[l]).compareTo(PIVOT) > 0)	l++; 
					while (COMPARE.op(ARRAY[r]).compareTo(PIVOT) < 0)	r--;
				}
				//while (MODE? COMPARE.op(ARRAY[l]).compareTo(PIVOT) < 0: COMPARE.op(ARRAY[l]).compareTo(PIVOT) > 0)	l++;
				//while (MODE? COMPARE.op(ARRAY[r]).compareTo(PIVOT) > 0: COMPARE.op(ARRAY[r]).compareTo(PIVOT) < 0)	r--;

				if (l <= r) {
					final T TMP_NODE= ARRAY[l];
					ARRAY[l++]		= ARRAY[r];
					ARRAY[r--]		= TMP_NODE;
				}
			}

			// recursion
			if (LEFT < r ) quickSort(LEFT,  r);
			if (l < RIGHT) quickSort(l, RIGHT);

			return ARRAY;
		}


		public static <T	 extends Comparable<T>> T[] quickSort(final T[] ARRAY){
			if(ARRAY.length > 1) return new QuickSort<T,T>(ARRAY, true, (a) -> a).getSorted();
			else return ARRAY;
		}
		public static <T	 extends Comparable<T>> T[] quickSort(final T[] ARRAY, final boolean mode){
			if(ARRAY.length > 1) return new QuickSort<T,T>(ARRAY, mode, (a) -> a).getSorted();
			else return ARRAY;
		}
		public static <T, V  extends Comparable<V>> T[] quickSort(final T[] ARRAY, final Lambda1<T, V> compare){
			if(ARRAY.length > 1) return new QuickSort<T,V>(ARRAY, true, compare).getSorted();
			else return ARRAY;
		}

		/**
		 * Getting the sorted array
		 * @return sorted array
		 */
		private T[] getSorted(){
			return ARRAY;
		}
	}



///||||||||||||||||||||||||||||||||||||||||||||||||||||||||| TIMER |||||||||||||||||||||||||||||||||||||||||||||||||||||||||



	public class Timer{
		private boolean timerOnOff;
		private final ArrayList<Long> COMP_TIME = new ArrayList<>();

		// constructor
		public Timer(){}

		/**
		 * Benchmarking the computation time
		 * @param ON_OFF
		 * @return current time
		 */
		public double timeTrack(final boolean ON_OFF){
			final long TIME = System.nanoTime();

			if(ON_OFF && !this.timerOnOff)	this.COMP_TIME.add(TIME);
			else if(this.COMP_TIME.size() > 0 && this.timerOnOff){
				this.COMP_TIME.set(this.COMP_TIME.size()-1, TIME - this.COMP_TIME.get(this.COMP_TIME.size()-1));
			}else	return 0;

			this.timerOnOff = ON_OFF;
			return this.COMP_TIME.get(this.COMP_TIME.size()-1);
		}

		// getting the time collected so far
		public long getTime(){
			long totalTime = 0;
			for(long n: COMP_TIME)	totalTime += n;
			return totalTime;
		}

		// setting back the time to zero 0
		public void timeReset(){
			this.COMP_TIME.clear();
		}
	}


//||||||||||||||||||||||||||||||||||||||||||||||||||||||||| ANSI_COLOURS |||||||||||||||||||||||||||||||||||||||||||||||||||||||||


	
	public static class AnsiColours{

		//determining whether the text get colored or not
		private static	boolean		globalState	= true;
		private			boolean		localState	= true;
		private	final	String		RESET		= "\u001B[0m";
		private	final	String[][]	COLOURS		= {	{"black", "\u001B[30m"	},	{"red"		, "\u001B[31m"	},
													{"green", "\u001B[32m"	},	{"yellow"	, "\u001B[33m"	},
													{"blue"	, "\u001B[34m"	},	{"magenta"	, "\u001B[35m"	},
													{"cyan"	, "\u001B[36m"	},	{"white"	, "\u001B[37m"	},
													{"reset", this.RESET	}	};

		// constructor
		public AnsiColours(){}


		public static	boolean	globalState		()						{ return AnsiColours.globalState;	 }
		public			boolean	localState		()						{ return this.localState;			 }
		public static	void	setGlobalState	(final boolean STATE)	{ AnsiColours.globalState	= STATE; }
		public			void	setLocalState	(final boolean STATE)	{ this.localState			= STATE; }

		/**
		 * Get ANSI code for colored text
		 * @param colour
		 * @return ASCII code
		 */
		public String colour(String colour){
			if(this.localState() && AnsiColours.globalState()){
				colour = colour.toLowerCase();

				for(final String[] C: this.COLOURS){	if(colour.equals(C[0]))		return C[1]; }
				return colour;
			}else return "";
		}


		/**
		 * Get colored text
		 * @param text
		 * @param colour
		 * @return colored string (ANSI)
		 */
		public String colourText(final String TEXT, final String COLOUR){
			return colour(COLOUR) + TEXT + (this.localState && AnsiColours.globalState? this.RESET: "");
		}
	}




//||||||||||||||||||||||||||||||||||||||||||||||||||||||||| NAVIGATOR |||||||||||||||||||||||||||||||||||||||||||||||||||||||||



	public static class Navigator{

		// ANSI colours
		private final	AnsiColours	COLORS	= new Util.AnsiColours();
		// customizable parameters
		private	boolean		print			= true;
		private	int			min_delim_length= 3;
		private	int			min_row_length	= 10;
		private	String		delimiter		= ".";
		private	String		color			= "yellow";
		private	String[]	output;
		private	String[]	std_nav			= {"Go back", "Exit"};
		private String[]	std_nav_index	= {"0", "00"};
		private	String		std_nav_num		= "0";

		// constructor
		public Navigator(){ }
		public Navigator(final String DELIM, final String COLOR, final int MIN_DELIM, final int MIN_ROW){
			this.delimiter			= DELIM;
			this.color				= COLOR;
			this.min_delim_length	= MIN_DELIM;
			this.min_row_length		= MIN_ROW;
		}


		// getting console input
		public String cinln(){
			Console console	= System.console();	// getting console
			if(console == null) throw new UnsupportedOperationException("Console not available");
			
			final String INPUT	= console.readLine();
			System.out.println();				// new line
			return INPUT != null? INPUT: "";	// returning input
		}

		/**
		 * Returns a string of enumerated options
		 * @param MIN_SIZE	Minimum size of the row
		 * @param COLOR		Color of the options indexes
		 * @param OPTS		Options
		 * @return String[]	Enumerated options
		 */
		public String[] genOptions(final int MIN_SIZE, final String COLOR, final String ... OPTS){
			this.min_row_length			= MIN_SIZE;	// setting minimum size
			this.setColor(COLOR);					// setting color
			this.genOptions(OPTS);					// returning options
			return this.output;						// returning options
		}
		public void genOptions(final String ... OPTS){
			
			final String COLOR_START	= this.COLORS.colour(this.color);
			final String COLOR_END		= COLOR_START==""? "": this.COLORS.colour("reset");

			// arrays of options strings and index strings
			final String[] STD_OPTS		= new String[this.std_nav.length];
			final String[] OPTIONS		= new String[OPTS.length];

			// getting the size of the longest string when option and index are combined
			int longest_o_and_i	= 0;
			for(int i=0; i<OPTS.length; i++)			longest_o_and_i = Math.max(longest_o_and_i, OPTS[i].length() + Long.toString(i+1).length());
			for(int i=0; i<this.std_nav.length; i++)	longest_o_and_i = Math.max(longest_o_and_i, this.std_nav[i].length() + this.std_nav_index[i].length());

			// getting the minimum delimiter length
			final int MIN_DELIM_LENGTH	= Math.max(longest_o_and_i + this.min_delim_length, this.min_row_length) - longest_o_and_i;

			// making user's options
			for(int i=0; i<OPTS.length; i++){
				final int DELIM			= longest_o_and_i	- (OPTS[i].length() + Long.toString(i+1).length()) + MIN_DELIM_LENGTH;
				final String INDEX		= COLOR_START		+ (i+1) + COLOR_END;
				OPTIONS[i]				= OPTS[i]			+ Util.stringRepeat(this.delimiter, DELIM) + INDEX;
			}

			// making standard navigation options
			for(int i=0; i<this.std_nav.length; i++){
				final int DELIM			= longest_o_and_i	- (this.std_nav[i].length() + this.std_nav_index[i].length()) + MIN_DELIM_LENGTH;
				final String INDEX		= COLOR_START		+ this.std_nav_index[i] + COLOR_END;
				STD_OPTS[i]				= this.std_nav[i]	+ Util.stringRepeat(this.delimiter, DELIM) + INDEX;
			}

			this.output	= Util.arrayJoin(OPTIONS, STD_OPTS);
		}


		/**
		 * Getting the choice via user input
		 * @param options	amount of options
		 * @return 			user choice
		 */
		public int getChoice(int options){
			options = Math.max(1, options);
			
			for(String input = ""; true;){
				System.out.print("Enter a choice here :> ");
				input = this.cinln();

				//checking if the input is calling a std option
				for(int i=0; i<this.std_nav_index.length; i++){ if(input.equals(this.std_nav_index[i])) return -i; }
				
				// checking if the input calls a valid option
				try { final int NUM = Integer.parseInt(input);
					if(NUM > 0 && NUM <= options) return NUM;
				} catch (NumberFormatException e) { }
				
				System.out.println(this.COLORS.colourText("WRONG SELECTION! Try again.", "yellow"));
			}
		}

		/**
		 * Navigation choice. Display options and return choice
		 * @param MIN		Minimum number of options
		 * @param OPTIONS	Strings of options
		 * @return			Returns the user choice
		 */
		public int navOptions(final int MIN, final String ... OPTIONS){
			this.genOptions(MIN, this.color, OPTIONS);
			
			//displaying options
			if(this.print) this.printOptions();
			//getting the choice
			return this.getChoice(OPTIONS.length);
		}
		
		/**
		 * Print the options
		 * @param OPTIONS	options to be printed
		 */
		public void printOptions(){ this.printOptions(this.output); }
		public void printOptions(final String ... OPTIONS){ 
			// printing
			for(final String LINE: OPTIONS) System.out.println(LINE);
			System.out.println();
		}
		
		
		// Setters.........

		public void setMinDelimLength	(final int MIN_DELIM)	{ this.min_delim_length	= MIN_DELIM;}
		public void setPrint			(final boolean PRINT)	{ this.print			= PRINT; 	}
		public void setDelimiter		(final String DELIM)	{ this.delimiter		= DELIM; 	}
		public void setColor			(final String COLOR)	{ this.color 			= COLOR; 	}

		// Setting the std nav indexes
		public void setStdIndexes(final String INDEX){
			this.std_nav_num = INDEX;
			final String[] INDEXES = new String[this.std_nav.length];
			for(int i=0; i<this.std_nav.length; i++) INDEXES[i] = Util.stringRepeat(INDEX, i+1);
			this.std_nav_index = INDEXES;
		}

		// Setting the standard navigation options
		public void setStdNavOpts(final String... OPTIONS){
			this.std_nav = OPTIONS;
			if(OPTIONS.length != this.std_nav.length) this.setStdIndexes(this.std_nav_num);
		}
		
		// Setting the standard navigation options and indexes
		public void setStdNavOpts(final String INDEX, final String... OPTIONS){
			this.setStdNavOpts(OPTIONS);
			this.setStdIndexes(INDEX);
		}
	}




//||||||||||||||||||||||||||||||||||||||||||||||||||||||||| LOADING_BAR |||||||||||||||||||||||||||||||||||||||||||||||||||||||||




	// loading bar class
	public static class Loading {

		private final	AnsiColours	COLOURS		= new AnsiColours();												//	coloured text
		private final	short	MAX_PERCENT		= 100		;														//	max percentage is 100
		private			String[] barColours		= {"red"	,	"yellow"				,	"green"};				//	colours of bar stages
		private final	String	BLOCK			= "█"		,	DOTTED		= "░"		,	TAB			= "	";		//	bar components
		private final	String	ERASE_BELOW		= "\033[0J"	,	GO_LINE_UP	= "\033[A"	,	NL	= "\n"	,	CR		= "\r"; // ERASE_LINE = "\033[2K"
		private			String	messageColour	= "blue"	,	message 	= ""		,	linesUp		= ""		,	bar	= ""	,	output	= ""  ;
		private			short	counter 		= -1		,	barLength	= 50		,	percent		= 0;		//	bar parameters
		private			int		barState 		= 0			,	updates		= this.barLength;						//	calculating loading bar
		private			double	nextUpdate 		= 0			,	coloursIndex= (double)this.barColours.length / (double)(this.MAX_PERCENT+0.1);
		private			boolean	messageUpdate	= false		;														//	notes if message has updated
		private			long	index			= 0			;														//	increasing index per cicle
		private			long	ciclesAmount				;														//	total amount of cicles


		/**
		 * Constructor Method
		 * @param CA CICLES_AMOUNT
		 * @param BL BAR_LENGTH
		 * @param U UPDATES
		 * @param M MESSAGE
		 */
		public Loading(final long CA, final short BL, final int U, final String M){
			this.ciclesAmount	= CA;
			this.barLength(BL);
			this.updates(U);
			this.message(M, this.messageColour);
		}
		public Loading(final long CA){ this(CA, (short)50, 50, ""); }


		/**
		 * Setting the bar colours
		 * @param COLOURS array of strings
		 */
		public void barColours(final String ... COLOURS){
			this.barColours		= COLOURS;
			this.coloursIndex	= (double)this.barColours.length / (double)(this.MAX_PERCENT+0.1);
		}
		/**
		 * Setting the updates rate
		 * @param RATE
		 */
		public void updates(final int RATE){
			if	(RATE < 0)	this.updates = 0;
			else			this.updates = RATE;
		}

		/**
		 * Setting the bar length
		 * @param LENGTH
		 */
		public void barLength(final short LENGTH){
			if(LENGTH > 0) {
				this.barLength = LENGTH;
			}
		}

		/**
		 * Setting the message of the bar
		 * @param TEXT
		 * @param COLOUR
		 */
		public void message(final String TEXT, final String COLOUR){
			this.message = TEXT;
			this.messageColour = COLOUR;
			this.messageUpdate = true;
		}

		/**
		 * Setting the cicles amount
		 * @param CICLES
		 */
		public void ciclesAmount(final long CICLES){
			if(CICLES > 0 && CICLES >= this.index){
				this.ciclesAmount = CICLES;
				this.percent = (short)(this.index * this.MAX_PERCENT / this.ciclesAmount);
			}
		}

		/**
		 * Setting the index state
		 * @param INDEX
		 */
		public boolean indexUpdate(final long INDEX){
			if(INDEX >= 0 && INDEX <= this.ciclesAmount) this.index = INDEX;
			else return false;

			this.percent = (short)(this.index * this.MAX_PERCENT / this.ciclesAmount);
			return true;
		}


		/**
		 * Check if the loading is compleated
		 * @return loading status
		 */
		private boolean loadingComplete(){
			return this.percent >= this.MAX_PERCENT && this.index >= this.ciclesAmount;
		}


		/**
		 * Determines whether to update the whole bar or not
		 * @return update or not the whole bar
		 */
		private boolean updateTime(){
			this.barStateUpdate();
			
			if(this.index >= this.nextUpdate){
				this.nextUpdate = Math.ceil((double)this.ciclesAmount * (double)(this.barState+1) / (double)this.updates);
			}else return false;

			return true;
		}


		/**
		 * Tokens generator
		 * @return token
		 */
		private short tokensGen(){
			return (short)((float)this.barLength / this.updates * this.barState);
		}


		/**
		 * Updating the bar State
		 */
		private void barStateUpdate(){
			this.barState = (int)(this.index * this.updates / this.ciclesAmount);
		}


		/**
		 * loading bar generator
		 * @param TOKENS
		 * @return bar string
		 */
		private void barGen(final short TOKENS){
			//final short PERCENT = (short)(this.index * this.MAX_PERCENT / this.ciclesAmount);
			//final short PERCENT = (short)(TOKENS * this.MAX_PERCENT / this.barLength);

			final String STATUS_FULL	= stringRepeat(this.BLOCK, (long)TOKENS);
			final String STATUS_VOID	= stringRepeat(this.DOTTED, this.barLength-(long)TOKENS);
			String color = "";

			try{ // selecting the bar color
				color = this.barColours[(int)(this.percent*this.coloursIndex)];
			}catch(ArrayIndexOutOfBoundsException e){
				if((int)(this.percent*this.coloursIndex) < 0) color = this.barColours[0];
				else color = this.barColours[this.barColours.length-1];
			}

			this.bar = this.COLOURS.colourText(STATUS_FULL, color)+STATUS_VOID+" "+this.COLOURS.colourText(this.percent+"%", color);
		}


		/**
		 * Merging the bar and the message
		 * @return string of the outcome merged with the message
		 */
		private String barAndMessage(){
			final String OUTCOME	= this.bar+this.TAB+this.COLOURS.colourText(this.message, this.messageColour);
			
			if(this.messageUpdate){
				this.linesUp		= stringRepeat(this.GO_LINE_UP, strMatch(this.message, this.NL));
				this.messageUpdate	= false;
			}
			return this.CR + this.ERASE_BELOW + OUTCOME + this.linesUp;
		}


		/**
		 * Reset main variables
		 * @return instruction to reset the command screen
		 */
		private String reset(){
			this.index		=  0;
			this.counter	= -1;
			this.nextUpdate =  0;
			return this.CR + this.ERASE_BELOW;
		}


		/**
		 * Main operations and updates
		 * @return whether it has updated or not
		 */
		public boolean coreUpdated(){
			if(this.indexUpdate(this.index+1) && this.updateTime()){

				if(loadingComplete())	this.output = this.reset();
				else{
					final short TOKENS = this.tokensGen();

					if(TOKENS != this.counter){
						this.counter = TOKENS; // "counter" determines when to generatee a new bar
						this.barGen(TOKENS);
					}

					this.output = this.barAndMessage();
				}			
				return true;
			}else return false;
		}


		/*
		* Print the Loading Bar
		*/
		public void printNewBar(){
			if(this.coreUpdated())	System.out.print(this.output);
		}

		/**
		 * Print the Loading Bar
		 * @return final resoult
		 */
		public String getNewBar(){
			return this.output;
		}
	}
}
