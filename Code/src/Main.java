
import java.io.FileNotFoundException;

import lib.Util;

public class Main {
	// navigation set
	public static final short EXIT	= -1;  	// terminal navigator exit code
	public static final short BACK	=  0;	// terminal navigator back code
	public static final short AHEAD	=  1;	// terminal navigator ahead code
	public static final	Util.AnsiColours	COLORS		= new Util.AnsiColours();
	public static final	Util.Navigator		NAV			= new Util.Navigator();


	// introduction
	public static void intro(){
		Util.AnsiColours.setGlobalState(false); 							// starting a gray scale terminal
		final String TITLE	= "WELCOME TO THE DIGIT RECOGNITION CHALLENGE";	// title of the program
		final String BOXING = Util.stringRepeat("-", TITLE.length()+4);		// boxing the title

		System.out.println("\r\n\r\n" + BOXING);							// printing the first title boxing
		System.out.println("| " + COLORS.colourText(TITLE,"cyan") + " |");	// printing the title
		System.out.println(BOXING + "\r\n");								// printing the last title boxing
	}


	// setting the console environment
	public static int envSet(){
		System.out.println(COLORS.colourText("Environment setting", "magenta"));

		switch(NAV.navChoice(5, "Colors ON  (Terminal)","Colors OFF (IDE)")){
			case EXIT: return EXIT;
			case BACK: return BACK;
			case	1: Util.AnsiColours.setGlobalState(true);
				break;
			case  	2: Util.AnsiColours.setGlobalState(false);
		}
		return AHEAD;
	}


	// main menu
	public static int menu(){ return menu(2); }
	public static int menu(final int MAX){
		System.out.println(COLORS.colourText("MAIN MENU", "magenta"));

		final String[] OPTIONS = new String[]{"Perform a training","Print "+Ann.fitness.getMissclassified().length+" misclassified"};
		final String[] MAX_OPT = new String[Math.min(MAX, OPTIONS.length)];
		
		for(int i=0; i<MAX_OPT.length; i++) MAX_OPT[i] = OPTIONS[i];
		return NAV.navChoice(5, MAX_OPT);
	}



	

	// The main
	public static void main(String[] args){
		intro();	// printing the introduction

		// initialising the model
		System.out.println(COLORS.colourText("Loading dataset and model...\r\n", "cyan"));
		try{ Ann.loadAndBuild(); }
		catch(FileNotFoundException e){
			System.out.println("Error. File not found!");
			System.exit(1);
		}


		// main loop
		for(int nav = envSet(), iter = 1, opt = 1;	nav!=EXIT;	nav = nav!=BACK? nav: envSet()){
			nav = nav==BACK? nav: menu(opt);

			// Perform the training and validation
			switch(nav){
				case 1: 
					Ann.trainAndTest(iter);
					System.out.println("Epochs completed: " + Ann.EPOCHS * iter++ + "\r\n");
					opt = 2;
					break;
				case 2: 
					Ann.fitness.printMisclassified();
					System.out.println("Samples missclassified: " + Ann.fitness.getMissclassified().length + "\r\n");
					opt = 1;
			}
		}

		// exiting the main loop
		System.out.println(COLORS.colourText("Successfully exited!", "green"));
		System.exit(0);
	}
}
