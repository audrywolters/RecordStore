package recordStoreAudry;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

public class RecordStoreView {

	private RecordStoreController controller;
	private NewScanner scanner = new NewScanner();



	// constructor
	RecordStoreView(RecordStoreController contrllr) {
		controller = contrllr;
	}



	// display the menu and get the choice #
	public void runMenu(boolean managerStatus) {

		while (true) {
			System.out.println("-----------------------------\n"
					+ "RECORDS \n"
					+ "1. Add a new Record \n"
					+ "2. Sell a Record \n"
					+ "3. View all Records \n"
					+ "4. Delete a Record \n" 
					+ "CONSIGNERS \n"
					+ "5. Add a new Consigner \n" 
					+ "6. View details about a Consigner \n"
					+ "7. View all Consigners \n" 
					+ "PAYMENTS \n"
					+ "8. View all payments \n"
					+ "9. View outstanding payments \n"
					+ "MANAGER \n"
					+ "10. Make a payment \n"
					+ "11. Make Report \n"
					+ "12. Change password \n"
					+ "13. Add Staff member \n"
					+ "QUIT \n"
					+ "0. Quit Record Store Manager \n"
					+ "-----------------------------");


			int userChoice;
			//if a manager
			if (managerStatus) {

				try {
					//Scanner sMenu = new Scanner(System.in);
					userChoice = scanner.getScanner().nextInt();


					if (userChoice > 0 && userChoice <= 13) {
						runTask(userChoice);

					} else if (userChoice == 0) {
						//go to shutdown procedures
						break;
					} else {
						System.out.println("Please enter a number*** [ 0 - 13 ]");
					}


				} catch (InputMismatchException e) {
					System.out.println("Please enter a number [ 0 - 13 ]");

				} 

			} else {
				
				try {
					//Scanner sMenu = new Scanner(System.in);
					userChoice = scanner.getScanner().nextInt();


					if (userChoice > 0 && userChoice <= 8) {
						runTask(userChoice);

					} else if (userChoice == 0) {
						//go to shutdown procedures
						break;
					} else {
						System.out.println("Please enter a number*** [ 0 - 8 ]");
					}


				} catch (InputMismatchException e) {
					System.out.println("Please enter a number [ 0 - 8 ]");

				} 
			}
		}
	}




	//the switch case mini-controller that responds to what choice was made
	public void runTask(int userChoice) {

		switch (userChoice) {

		case 0: { //quit
			break;
			
		} case 1: { //add a new record
			addUserRecord();
			break;

		} case 2: { //sell a record
			controller.sellRecord();
			break;

		} case 3: { //view all records
			controller.printAllRecords();
			break;

		} case 4: { //delete a record
			Record record = controller.searchForRecord();
			int userChoiceEdit = editOrDeleteMenu();
			if (userChoiceEdit == 1) {
				controller.deleteRecord(record);
			} 
			break;

		} case 5: { //add a new consigner
			Consigner consigner = addConsigner();
			controller.addConsigner(consigner);
			break;

		} case 6: { //view one consigner
			Consigner consigner = controller.searchForConsigner();
			if (consigner != null) {
				printDetailedConsigner(consigner);
			}
			break;

		} case 7: { //view all consigners
			controller.printAllConsigners();
			break;

		} case 8: { //view all payments
			controller.printAllPayments();
			break;

		} case 9: { //view outstanding payments
			controller.findOutsandingPayments();
			break;

		} case 10: { //make a payment
			Payment payment = controller.searchForPayment();
			if (payment != null) {
				int payUserChoice = payConsigner(payment);
				if (payUserChoice == 1) {
					controller.updatePaymentCont(payment);
				} 
			}
			break;
		
	
		} case 11: { //make a report
			System.out.println("not yet implemented.");
			break;
		
		} case 12: { //change password
			System.out.println("not yet implemented.");
			break;
			
		} case 13: { //add staff member
			System.out.println("not yet implemented.");
			break;
		}
		}
	}


	public String getEntryUsername() {
		System.out.println("Enter your username:");
		String username = scanner.getScanner().nextLine();
		return username;
	}

	public String getEntryPassword() {
		System.out.println("Enter your password:");
		String password = scanner.getScanner().nextLine();
		return password;
	}


	

	//menu and view for adding a record to the database
	private void addUserRecord() {
		// get the data from user
		System.out.println("Enter the title of the record:");
		String title = scanner.getScanner().nextLine();
		System.out.println("Enter the artist of the record:");
		String artist = scanner.getScanner().nextLine();

		//check if too many copies
		boolean tooMany = controller.tooManyCopies(title, artist);


		if(tooMany) {
			System.out.println("Sorry, there are too many copies of " + title);
			//TODO fix - wants manager status runMenu();
		} else {
			System.out.println("Enter the price of the record:");
			double price = scanner.getScanner().nextDouble();

			int consignerId = getIdFromUser("Consigner");

			//date is now
			Calendar dateAdded = new GregorianCalendar();

			//TODO automate user Id
			System.out.println("Enter your Id");
			int checkedInBy = scanner.getScanner().nextInt();


			//create record
			Record record = new Record(title, artist, consignerId, price, checkedInBy, dateAdded);

			//send to controller to send to DB
			controller.requestAddRecord(record);
		}
	}


	///ASK USER TO MOVE TO BARGIN BIN///
	public int moveRecordOrCall(Record oldRecord, String area) {
		boolean valid = false;
		int userChoice = 0;

		//while userChoice invalid, loop
		while(!valid) {
			if(oldRecord != null && area.equals("Bargin Bin")) {
				System.out.println("\n***" + oldRecord.getTitle() + " has been in inventory for more than 30 days.***\n"
						+ "1. Move to Bargin Bin \n"
						+ "2. Return to Consinger \n");


				try {
					//TODO replace w/ scanner object
					Scanner sMenu = new Scanner(System.in);
					userChoice = sMenu.nextInt();
					if (userChoice < 1 || userChoice > 2) {
						System.out.println("Please enter a number [1 or 2]");
						valid = false;
					} else {
						valid = true;
					}
				} catch (InputMismatchException e) {
					System.out.println("Please enter a number [1 or 2]");
					valid = false;
					continue;
				}


			} else if (oldRecord != null && area.equals("Charity")) {
				System.out.println("\n***" + oldRecord.getTitle() + " has been in Bargin Bin for more than 1 year.***\n"
						+ "1. Donate to Charity \n"
						+ "2. Return to Consinger \n");

				try {
					//replace with scanner object
					Scanner sMenu2 = new Scanner(System.in);
					userChoice = sMenu2.nextInt();
					if (userChoice < 1 || userChoice > 2) {
						System.out.println("Please enter a number [1 or 2]");
						valid = false;
					} else {
						valid = true;
					}
				} catch (InputMismatchException e) {
					System.out.println("Please enter a number [1 or 2]");
					valid = false;
					continue;
				}


			}


		}
		return userChoice;
	}




	///MAKE PAYMENT///
	public int payConsigner(Payment payment) {
		System.out.println(payment.getAmountDue() + " is owed to the consigner.");
		boolean valid = false;
		int userChoice = 0;

		//while userChoice invalid, loop
		while(!valid) {
			System.out.println("1. Make Payment");
			System.out.println("2. Main Menu");
			try {
				userChoice = scanner.getScanner().nextInt();
				if (userChoice < 1 || userChoice > 2) {
					System.out.println("Please enter 1 or 2.");
					valid = false;
				} else {
					valid = true;
				}
			} catch (InputMismatchException e) {
				System.out.println("Please enter a number.");
				continue;
			}
		}
		return userChoice;
	}



	public Consigner addConsigner() {
		//get data from user
		System.out.println("Enter the name of the consigner");
		String name = scanner.getScanner().nextLine();
		System.out.println("Enter the phone number of the consigner [(XXX)XXX-XXXX]");
		String phone = scanner.getScanner().nextLine();


		//create new consigner
		Consigner consigner = new Consigner(name, phone);

		return consigner;
	}



	//get id from user
	public int getIdFromUser(String item) {
		boolean valid = false;
		int idFromUser = 0;
		while(valid == false) {
			System.out.println("Enter the Id of the " + item);
			try {
				//Scanner sMenu = new Scanner(System.in);
				idFromUser = scanner.getScanner().nextInt();
				valid = true;
			} catch (InputMismatchException e) {
				System.out.println("Please enter a number.");
				continue;
			}
		}
		return idFromUser;
	}



	//confirm if user wants this record
	public String isThisYourRecord(Record record) {
		System.out.println(record.getTitle());
		System.out.println("Is this the record you were looking for? [y/n]");
		String userChoice = validateString();
		return userChoice;
		//TOOD add validation
	}
	//consigner
	public String isThisYourConsigner(Consigner consigner) {
		System.out.println(consigner.getName());
		System.out.println("Is this the consigner you were looking for? [y/n]");
		String userChoice = validateString();
		return userChoice;
		//TOOD add validation

	}
	//payment
	public String isThisYourPayment(Payment payment) {
		System.out.println("Consigner ID: " + payment.getConsignerId() + "Amount Due: " + payment.getAmountDue());
		System.out.println("Is this the payment you were looking for? [y/n]");
		String userChoice = validateString();
		return userChoice;
		//TOOD add validation	
	}

	//delete or ignore record
	private int editOrDeleteMenu() {

		boolean valid = false;
		int userChoice = 0;

		//while userChoice invalid, loop
		while(!valid) {
			System.out.println("1. Delete");
			System.out.println("2. Main Menu");
			try {
				//Scanner sMenu = new Scanner(System.in);
				userChoice = scanner.getScanner().nextInt();
				if (userChoice < 1 || userChoice > 2) {
					System.out.println("Please enter 1 or 2.");
					valid = false;
				} else {
					valid = true;
				}
			} catch (InputMismatchException e) {
				System.out.println("Please enter a number.");
				continue;
			}
		}
		return userChoice;

	}



	private void printDetailedConsigner(Consigner consigner) {
		double moneyOwed = controller.calcMoneyOwed(consigner);
		LinkedList<Record> consignerRecords = controller.findConsingerRecords(consigner);
		System.out.println(consigner);
		System.out.println("Total money owed: " + moneyOwed);
		System.out.println("Records owned by " + consigner.getName());
		for (Record record : consignerRecords) {
			System.out.println(record);
		}

	}

	private String validateString() {

		boolean valid = false;
		String userChoice = null;
		while (!valid) {

			//Scanner sMenu = new Scanner(System.in);

			userChoice = scanner.getScanner().nextLine();
			if (userChoice.equalsIgnoreCase("y") || userChoice.equalsIgnoreCase("n")) {
				valid = true;
			} else {
				System.out.println("Please enter [ y / n ]");
				valid = false;
				continue;
			}
		}

		return userChoice;
	}







}