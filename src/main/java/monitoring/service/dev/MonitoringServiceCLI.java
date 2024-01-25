package monitoring.service.dev;

import monitoring.service.dev.controllers.AuthController;
import monitoring.service.dev.dtos.CredentialsDTO;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;

import java.util.Scanner;

public class MonitoringServiceCLI {

	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		AuthController auth=AuthController.getInstance();

		System.out.println("Welcome to the Monitoring-Service, select the action: register, log in, exit");

		boolean work = true;
		String phase;
		while (work){
			String action = keyboard.nextLine();
			phase="start";
			if(action.equals("/help")){
				help(phase);
			}

			switch (action){
				case "/register" ->{
					System.out.println("To register, you need to come up with a username and password");

					while (true)
						try {
							System.out.print("Username: ");
							String username = keyboard.nextLine();
							if (back(username)) {
								break;
							}

							System.out.print("Password: ");
							String password = keyboard.nextLine();
							if (back(password)) {
								break;
							}

							CredentialsDTO dto = CredentialsDTO.builder()
									.username(username)
									.password(password)
									.build();

							auth.registration(dto);
							System.out.println("Successful registration!");
							break;
						}catch (NotValidException e){
							System.out.println(e.getMessage());

					}
				}
				case "/log in" ->{
					System.out.println("To authenticate, you need to enter username and password");

					while (true){
						try {
							System.out.print("Username: ");
							String username = keyboard.nextLine();
							if (back(username)) {
								break;
							}
							System.out.print("Password: ");
							String password = keyboard.nextLine();
							if (back(password)) {
								break;
							}

							CredentialsDTO dto = CredentialsDTO.builder()
									.username(username)
									.password(password)
									.build();

							Person person = auth.authentication(dto);
							System.out.println("Successful authentication! You are "+person.getRole());
							break;
						}catch (NotFoundException | NotValidException e){
							System.out.println(e.getMessage());
						}
					}
				}
				case "/exit" ->{
					System.out.println("exit");
					work=false;
				}
			}
		}
	}

	private static boolean back(String string){
		if ("/back".equalsIgnoreCase(string)) {
			System.out.println("Action canceled.");
			return true;
		}
		return false;
	}

	private static void help(String phase){
		switch (phase){
			case "start" -> System.out.println("The commands \"/register\" \"/log in\" \"exit\" are available to you");
			case "register", "log in" -> System.out.println("The command \"/back\" is available to you");
		}
	}
}
