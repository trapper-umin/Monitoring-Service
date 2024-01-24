package meter.reading.service.dev;

import java.util.Scanner;

public class MeterReadingServiceCLI {

	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Welcome to the Meter-Reading-Service, select the action: register, log in, exit");

		boolean work = true;
		while (work){
			String action = keyboard.nextLine();

			switch (action){
				case "register" ->{
					System.out.println("register");
				}
				case "log in" ->{
					System.out.println("log in");
				}
				case "exit" ->{
					System.out.println("exit");
					work=false;
				}
			}
		}
	}
}
