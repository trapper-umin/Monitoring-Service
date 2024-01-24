package monitoring.service.dev;

import monitoring.service.dev.controllers.AuthController;

import java.util.Scanner;

public class MonitoringServiceCLI {

	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		AuthController auth=AuthController.getInstance();

		System.out.println("Welcome to the Monitoring-Service, select the action: register, log in, exit");

		boolean work = true;
		while (work){
			String action = keyboard.nextLine();

			switch (action){
				case "register" ->{
					System.out.println("To register, you need to come up with a username and password");
					System.out.print("Username: ");
					String username = keyboard.nextLine();
					System.out.print("Password: ");
					String password = keyboard.nextLine();
					auth.registration(username, password);

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
