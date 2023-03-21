
import java.io.FileNotFoundException;

import lib.Util;

public class Main {
	// navigation set
	public static final short EXIT = -1;  	// terminal navigator exit code
	public static final short BACK =  0;	// terminal navigator back code
	public static final short AHEAD=  1;	// terminal navigator ahead code
	public static final	Util.AnsiColours	COLOURS		= new Util.AnsiColours();
	public static final	Util.Navigator		NAV			= new Util.Navigator();


	// setting the console environment
	public static int envSet(){
		System.out.println(COLOURS.colourText("Environment setting", "magenta"));

		switch(NAV.navChoice(5,"Colors ON  (VScode)","Colors OFF (Eclipse)")){
			case EXIT: return EXIT;
			case BACK: return BACK;
			case	1: Util.AnsiColours.setGlobalState(true);
				break;
			case  	2: Util.AnsiColours.setGlobalState(false);
		}
		return AHEAD;
	}


	// main menu
	public static int menu(){
		System.out.println(COLOURS.colourText("MAIN MENU", "magenta"));

		switch(NAV.navChoice(5,"Perform a training")){
			case EXIT: return EXIT;
			case BACK: return BACK;
		}
		return AHEAD;
	}



	

	// The main
	public static void main(String[] args){
		Util.AnsiColours.setGlobalState(false); // starting a gray scale terminal
		System.out.println(COLOURS.colourText("\r\n\r\nWelcome to Digit recognition challenge\r\n","cyan"));

		// initialising the model
		System.out.println(COLOURS.colourText("Loading Dataset and model...\r\n", "cyan"));
		try{ Ann.loadAndBuild(); }
		catch(FileNotFoundException e){
			System.out.println("Error. File not found");
			System.exit(1);
		}


		// main loop
		for(int nav = envSet(), iter = 1;	nav!=EXIT;	nav = nav!=BACK? nav: envSet(), iter++){
			nav = nav==BACK? nav: menu();

			// Perform the training and validation
			if(nav == AHEAD){
				Ann.trainAndTest();
				System.out.println("Epochs completed: " + Ann.EPOCHS * iter + "\r\n");
			}
		}

		// exiting the main loop
		System.out.println(COLOURS.colourText("Successfully exited!", "green"));
	}
}
