
import java.io.FileNotFoundException;

import lib.Util;

public class Main {
	// navigation set
	public static final short EXIT = -1;  	// terminal navigator exit code
	public static final short BACK =  0;	// terminal navigator back code
	public static final short AHEAD=  1;	// terminal navigator ahead code


	// setting the console environment
	public static int envSet(){
		System.out.println(Util.colorText("Environment setting", "magenta"));

		switch(Util.navChoice(5,"Colors ON  (VScode)","Colors OFF (Eclipse)")){
			case EXIT: return EXIT;
			case BACK: return BACK;
			case	1: Util.setColor(true);
				break;
			case  	2: Util.setColor(false);
		}
		return AHEAD;
	}


	// main menu
	public static int menu(){
		System.out.println(Util.colorText("MAIN MENU", "magenta"));

		switch(Util.navChoice(5,"Perform a training")){
			case EXIT: return EXIT;
			case BACK: return BACK;
		}
		return AHEAD;
	}



	

	// The main
	public static void main(String[] args){
		Util.setColor(false); // starting a gray scale terminal
		System.out.println(Util.colorText("\r\n\r\nWelcome to Digit recognition challenge\r\n","cyan"));

		// initialising the model
		System.out.println(Util.colorText("Loading Dataset and model...\r\n", "cyan"));
		try{ Ann.setModel(); }
		catch(FileNotFoundException e){ System.out.println("Error. File not found"); }


		// main loop
		for(int nav = envSet();	nav!=EXIT;	nav = nav!=BACK? nav: envSet()){
			nav = nav==BACK? nav: menu();

			// Perform the training and validation
			if(nav == AHEAD)	Ann.testModel();
		}

		// exiting the main loop
		System.out.println(Util.colorText("Successfully exited!", "green"));
	}
}
