package recordStoreAudry;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

public class RecordStoreView {

	private RecordStoreController controller;
	private Scanner sInt = new Scanner(System.in);
	private Scanner sStrng = new Scanner(System.in);
	private Scanner sDbl = new Scanner(System.in);



	// constructor
	RecordStoreView(RecordStoreController contrllr) {
		controller = contrllr;
	}



	// display the menu and get the choice #
	public void runMenu() {

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
					+ "8. Make a payment \n"
					+ "9. View outstanding payments \n"
					+ "QUIT \n"
					+ "0. Quit Record Store Manager \n"
					+ "-----------------------------");


			int userChoice = -1;

			try {
				Scanner sMenu = new Scanner(System.in);
				userChoice = sMenu.nextInt();

				if (userChoice > 0 || userChoice < 10) {
					runTask(userChoice);
				} else if (userChoice == 0) {
					break;
					//TODO cannot figure out how to quit!
					//try/catch?
				} else {
					System.out.println("Please enter a number*** [0 - 9]");
				}


			} catch (InputMismatchException e) {
				System.out.println("Please enter a number [0 - 9]");

			} 
		}
	}




	//the switch case mini-controller that responds to what choice was made
	public void runTask(int userChoice) {

		switch (userChoice) {

		case 0: {
			break;
		} case 1: {
			addUserRecord();
			break;

		} case 2: {
			controller.sellRecord();
			break;

		} case 3: {
			controller.printAllRecords();
			break;

		} case 4: {
			Record record = controller.searchForRecord();
			int userChoiceEdit = editOrDeleteMenu();
			if (userChoiceEdit == 1) {
				controller.deleteRecord(record);
			} 
			break;

		} case 5: {
			Consigner consigner = addConsigner();
			controller.addConsigner(consigner);
			break;

		} case 6: {
			Consigner c = controller.searchForConsigner();
			//String consUserChoice = isThisYourConsigner(c);
			if (c != null) {
				printDetailedConsigner(c);
			}

			break;

		} case 7: {
			controller.printAllConsigners();
			break;

		} case 8: {
			Payment payment = controller.searchForPayment();
			if (payment != null) {
				int payUserChoice = payConsigner(payment);
				if (payUserChoice == 1) {
					controller.updatePaymentCont(payment);
				} else {
					//do nothing
				}
			}
			break;

		} case 9: {
			controller.findOutsandingPayments();
			break;

		}



		} 
	}


	//menu and view for adding a record to the database
	private void addUserRecord() {
		// get the data from user
		System.out.println("Enter the title of the record:");
		String title = sStrng.nextLine();
		System.out.println("Enter the artist of the record:");
		String artist = sStrng.nextLine();

		//check if too many copies
		boolean tooMany = controller.tooManyCopies(title, artist);

		
		if(tooMany) {
			System.out.println("Sorry, there are too many copies of " + title);
			runMenu();
		} else {
			System.out.println("Enter the price of the record:");
			double price = sDbl.nextDouble();

			int consignerId = getIdFromUser("Consigner");
			Calendar dateAdded = new GregorianCalendar();

			//create record
			Record record = new Record(title, artist, price, consignerId, dateAdded);

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
				Scanner sMenu = new Scanner(System.in);
				userChoice = sMenu.nextInt();
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
		String name = sStrng.nextLine();
		System.out.println("Enter the phone number of the consigner [(XXX)XXX-XXXX]");
		String phone = sStrng.nextLine();


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
				Scanner sMenu = new Scanner(System.in);
				idFromUser = sMenu.nextInt();
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
		String userChoice = sStrng.nextLine();
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
				Scanner sMenu = new Scanner(System.in);
				userChoice = sMenu.nextInt();
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
		
			Scanner sMenu = new Scanner(System.in);
			
			userChoice = sMenu.nextLine();
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