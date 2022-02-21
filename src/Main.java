
import java.io.FileNotFoundException;

import lib.Util;

public class Main {
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






	

	// The main
	public static void main(String[] args){
		Util.setColor(false); // starting without colors in the terminal
		System.out.println(Util.colorText("\r\n\r\nWelcome to Digit recognition challenge\r\n","cyan"));

		// main loop
		for(int nav = envSet();	nav!=EXIT;	nav = nav!=BACK? nav: envSet()){
			//nav = nav==BACK? nav: readCities();

			// getting result before and after cycling the generations
			if(nav == AHEAD){
				try{ Ann.runModel(); }
				catch(FileNotFoundException e){ System.out.println("Error. File not found"); }
			}
			
			break;
		}

		// exiting the main loop
		System.out.println(Util.colorText("Successfully exited!", "green"));
	}
}
