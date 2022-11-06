package lib;


import java.util.Scanner;
import java.util.ArrayList;

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
		double mean = ArrayMath.plus(ARRAY) / ARRAY.length;

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
	public static boolean xor(final boolean X, final boolean Y){
		return ( ( X || Y ) && ! ( X && Y ) );
	}


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
		min = Math.min(min, max);
		max = Math.max(min, max);
        return (Math.random() * (max - min)) + min;
    }

	// merge arrays
	public static <T> T[] merge(final T[] A, final T[] B) {
		final T[] LONGEST = A.length >= B.length? A: B;
		final T[] SHORTEST = A.length >= B.length? B: A;

		for(int i=0; i<SHORTEST.length; i++) LONGEST[i] = SHORTEST[i];
		return LONGEST;
	}

	public static <T> int findLargest(final T[][]ARRAY){
		int largest = 0;
		for(final T[] ELEM: ARRAY){	if(largest > ELEM.length) largest = ELEM.length; }
		return largest;
	}
	public static int findLargest(final String[] ARRAY){
		int largest = 0;
		for(final String ELEM: ARRAY){	if(largest > ELEM.length()) largest = ELEM.length(); }
		return largest;
	}




//||||||||||||||||||||||||||||||||||||||||||||||||||||||||| ARRAY_MATH |||||||||||||||||||||||||||||||||||||||||||||||||||||||||



	
	/**
	 * Sum values of array
	 * @param array
	 * @return sum
	 */
	public static class ArrayMath {

		public static					 double plus (final int[]		array)	{ return sum(array, (a,b) -> a+b			  ); }
		public static					 double minus(final int[]		array)	{ return sum(array, (a,b) -> a-b			  ); }
		public static					 double plus (final double[]	array)	{ return sum(array, (a,b) -> a+b			  ); }
		public static					 double minus(final double[]	array)	{ return sum(array, (a,b) -> a-b			  ); }
		public static					 double plus (final long[]		array)	{ return sum(array, (a,b) -> a+b			  ); }
		public static					 double minus(final long[]		array)	{ return sum(array, (a,b) -> a-b			  ); }
		public static <T extends Number> double plus (final T[]			array)	{ return sum(array, (a,b) -> a+b.doubleValue()); }
		public static <T extends Number> double minus(final T[]			array)	{ return sum(array, (a,b) -> a-b.doubleValue()); }
		public static <T extends Number> double plus (final ArrayList<T>array)	{ return sum(array, (a,b) -> a+b.doubleValue()); }
		public static <T extends Number> double minus(final ArrayList<T>array)	{ return sum(array, (a,b) -> a-b.doubleValue()); }
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
			}

			return "";
		}


		/**
		 * Get colored text
		 * @param text
		 * @param colour
		 * @return colored string (ANSI)
		 */
		public String colourText(final String TEXT, final String COLOUR){
			return colour(COLOUR) + TEXT + this.RESET;
		}
	}




//||||||||||||||||||||||||||||||||||||||||||||||||||||||||| NAVIGATOR |||||||||||||||||||||||||||||||||||||||||||||||||||||||||




	public static class Navigator{

		private final Scanner		CIN		= new Scanner(System.in);
		private final AnsiColours	COLOURS	= new AnsiColours();

		// constructor
		public Navigator(){}


		// getting console input
		public String cinln(){
			String input = CIN.nextLine();
			System.out.println();
			return input;
		}

		/**
		 * Returns a string of enumerated options
		 * @param opts
		 * @param minSize
		 * @param colors
		 * @param print
		 * @return String[]
		 */
		public String[] navOptions(final long MIN_SIZE, final String COLORS, final boolean PRINT, final String ... OPTS){
			// customizable parameters
			final String DELIMITER = ".", STD_NAV_NUM = "0";
			final String[] STD_NAV = {"Go back", "Exit"};
			final long MIN_DELIM_LENGTH = 3;

			// getting colors
			final String COL_START = this.COLOURS.colour(COLORS);								//yellow corresponds to: "\033[1;35m"
			final String COL_END = COL_START==""? COL_START: this.COLOURS.colour("reset");		//reset  corresponds to: "\033[0m"
			
			// arrays of options strings and index strings
			final String[] STD_NAV_INDEX = new String[STD_NAV.length];
			for(int i=0; i<STD_NAV_INDEX.length; i++) STD_NAV_INDEX[i] = stringRepeat(STD_NAV_NUM, i+1);
			final String[] OPTIONS = merge(OPTS, new String[OPTS.length+STD_NAV.length]);

			// getting the size of the longest index num and the size of the longest option string
			final long oSize = OPTS.length;
			long iSize = findLargest(STD_NAV_INDEX), i = 0, longest = findLargest(STD_NAV);

			// getting the longest string size
			for(String o: OPTS){
				final long strSize = o.length();
				longest = strSize > longest? strSize: longest;
				if(++i == oSize) iSize = Long.toString(i).length() > iSize? Long.toString(i).length(): iSize;
			}


			i = 0;
			longest = longest+MIN_DELIM_LENGTH>=(double)MIN_SIZE-iSize? longest+MIN_DELIM_LENGTH: MIN_SIZE-iSize;
			for(String o: OPTS){
				final long indexSize = iSize - Long.toString(++i).length();
				final long gap = longest - o.length();
				final String INDEX = COL_START + Long.toString(i) + COL_END;

				OPTIONS[(int)i-1] = o + stringRepeat(DELIMITER, gap+indexSize) + INDEX;
			}

			// making standard navigation options
			for(int j=0; j<STD_NAV_INDEX.length; j++){
				OPTIONS[OPTS.length+j]	= STD_NAV[j] + stringRepeat(DELIMITER,longest-STD_NAV[j].length()+iSize-STD_NAV_INDEX[j].length()) + COL_START+STD_NAV_INDEX[j]+COL_END;
			}
			
			// printing
			if(PRINT){ for(final String LINE: OPTIONS) System.out.println(LINE); }
			return OPTIONS;
		}


		/**
		 * @since getting user input
		 * @param max
		 * @return int
		 */
		public int getChoice(int options){
			options = options<2?1: options;
			
			//checking the choice
			String input ="";
			
			while(true){
				System.out.print("Enter a choice here :> ");
				input = cinln();
				
				if(!input.equals("0") && !input.equals("00")){
					for(int i=1; i<=options; i++){
						if(input.equals(Integer.toString(i))) return i;
					}
					System.out.println(this.COLOURS.colourText("WRONG SELECTION! Try again.", "yellow"));
				}else if(input.equals("0")) return 0;
				else return -1;
			}
		}

		/**
			 * @since display options and return choice
			 * @param options 
			 * @param min
			 * @return int 
			 */
		public int navChoice(final long MIN, final String ... OPTIONS){
			
			//displaying options
			navOptions(MIN, "yellow", true, OPTIONS);
			System.out.println();
			//getting the choice
			return getChoice(OPTIONS.length);
		}
	}




//||||||||||||||||||||||||||||||||||||||||||||||||||||||||| LOADING_BAR |||||||||||||||||||||||||||||||||||||||||||||||||||||||||




	// loading bar class
	public static class Loading {

		private final	AnsiColours	COLOURS		= new AnsiColours();												//	coloured text
		private final	short	MAX_PERCENT		= 100		;														//	max percentage is 100
		private			String[] barColours		= {"red"	,	"yellow"				,	"green"};				//	colours of bar stages
		private final	String	BLOCK			= "█"		,	DOTTED		= "░"		,	TAB			= "	";		//	bar components
		private final	String	ERASE_LINE		= "\033[2K"	,	ERASE_BELOW = "\033[0J"	,	GO_LINE_UP	= "\033[A"	,	NL	= "\n"	,	CR		= "\r";
		private			String	messageColour	= "blue"	,	message 	= ""		,	linesUp		= ""		,	bar	= ""	,	output	= ""  ;
        private			short	counter 		= -1		,	barLength	= 50		,	percent		= 0;		//	bar parameters
		private			int		barState 		= 0			,	updates		= this.barLength;						//	calculating loading bar
		private			double	nextUpdate 		= 0			,	coloursIndex= (double)this.barColours.length / (double)(this.MAX_PERCENT+0.1);
		private			boolean	messageUpdate	= false		;														//	notes if message has updated
		private			long	index			= -1		;														//	increasing index per cicle
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
			final String COLORS			= this.barColours[(int)(this.percent*this.coloursIndex)]; // selecting the bar color

			this.bar					= this.COLOURS.colourText(STATUS_FULL, COLORS)+STATUS_VOID+" "+this.COLOURS.colourText(this.percent+"%", COLORS);
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
			return this.CR + this.ERASE_BELOW + OUTCOME + this.linesUp + this.CR;
		}


		/**
		 * Reset main variables
		 * @return instruction to reset the command screen
		 */
		private String reset(){
			this.index		= -1;
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
