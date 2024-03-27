
import java.io.FileNotFoundException;

import lib.Util;

public class Main {
	// navigation set
	public static final short EXIT	= -1;  	// terminal navigator exit code
	public static final short BACK	=  0;	// terminal navigator back code
	public static final short AHEAD	=  1;	// terminal navigator ahead code
	public static final	Util.AnsiColours	COLORS	= new Util.AnsiColours();
	public static final	Util.Navigator		NAV		= new Util.Navigator();


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
		System.out.println(COLORS.colourText("ENVIRONMENT SETTING", "magenta"));

		switch(NAV.navOptions(0, "Colors ON  (Terminal)","Colors OFF (IDE)")){
			case EXIT: return EXIT;
			case BACK: return BACK;
			case	1: Util.AnsiColours.setGlobalState(true);
				break;
			case  	2: Util.AnsiColours.setGlobalState(false);
		}
		return AHEAD;
	}


	// main menu
	public static int menu(final int OPTS){
		String[] options	= new String[]{"Perform a training"};

		switch(OPTS){
			case 1: options = Util.arrayJoin(options, new String[]{"Print "+Ann.fitness.getMissclassified().length+" misclassified"});
		}
		return printMenu(options);
	}
	// printing the main menu
	public static int printMenu(final String[] OPTIONS){
		System.out.println(COLORS.colourText("MAIN MENU", "magenta"));
		return NAV.navOptions(25, OPTIONS);
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
		for(int nav = envSet(), iter = 1, opt = 0;	nav!=EXIT;	nav = nav!=BACK? nav: envSet()){
			nav = nav==BACK? nav: menu(opt);

			// Perform the training and validation
			switch(nav){
				case 1: 
					Ann.trainAndTest(iter);
					System.out.println("Epochs completed: " + Ann.EPOCHS * iter++ + "\r\n");
					opt = nav;
					break;
				case 2: 
					Ann.fitness.printMisclassified();
					System.out.println("Samples missclassified: " + Ann.fitness.getMissclassified().length + "\r\n");
					opt = nav;
			}
		}

		// exiting the main loop
		System.out.println(COLORS.colourText("Successfully exited!", "green"));
		System.exit(0);
	}
}
