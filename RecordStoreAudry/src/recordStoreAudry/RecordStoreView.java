package recordStoreAudry;

import java.util.Calendar;
import java.util.GregorianCalendar;
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
		System.out.println("\nWelcome to Record Store Manager.");
		while (true) {
			System.out.println("-----------------------------\n"
					+ "RECORDS \n"
					+ "1. Add a new Record \n"
					+ "2. Sell a Record \n"
					+ "3. View all Records \n"
					+ "4. Delete a Record \n" 
					+ "CONSIGNERS \n"
					+ "5. Add a new Consigner \n" 
					+ "6. Pay a Consigner \n"
					+ "7. View all Consigners \n" 
					+ "PAYMENTS \n"
					+ "8. Make a payment \n"
					+ "9. View outstanding payments \n"
					+ "QUIT \n"
					+ "0. Quit Record Store Manager \n"
					+ "-----------------------------");
			int userChoice = sInt.nextInt();

			if (userChoice > 0) {
				runTask(userChoice);
			} else {
				break;
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
			controller.requestUpdateSoldRecord();
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
			String consUserChoice = isThisYourConsigner(c);
			if (consUserChoice.equalsIgnoreCase("y")) {
				printConsigner(c);
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
				//TODO add user val
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

		//
		if(tooMany) {
			System.out.println("Sorry, there are too many copies of " + title);
			runMenu();
		} else {
			System.out.println("Enter the price of the record:");
			double price = sDbl.nextDouble();
			System.out.println("Enter the Id of the Consigner:");
			int consignerId = sInt.nextInt();
			Calendar dateAdded = new GregorianCalendar();

			//TODO add validation

			//create record
			Record record = new Record(title, artist, price, consignerId, dateAdded);

			//send to controller to send to DB
			controller.requestAddRecord(record);
		}
	}


	///ASK USER TO MOVE TO BARGIN BIN///
	public int moveRecordOrCall(Record oldRecord, String area) {
		if(oldRecord != null && area.equals("Bargin Bin")) {
			System.out.println("\n***" + oldRecord.getTitle() + " has been in inventory for more than 30 days.***\n"
					+ "1. Move to Bargin Bin \n"
					+ "2. Call Consigner \n");

			int userChoice = sInt.nextInt();
			return userChoice;
		} else if (oldRecord != null && area.equals("Charity")) {
			System.out.println("\n***" + oldRecord.getTitle() + " has been in Bargin Bin for more than 1 year.***\n"
					+ "1. Donate to Charity \n"
					+ "2. Call Consigner \n");

			int userChoice = sInt.nextInt();
			return userChoice;
		}
		//TODO add validation
		return -1;
	}

	
	
	///MAKE PAYMENT///
	public int payConsigner(Payment payment) {
		System.out.println(payment.getAmountDue() + " is owed to the consigner.");
		System.out.println("1. Payment Made. \n"
				+ "2. Main Menu");
		int userChoice = sInt.nextInt();
		return userChoice;
		
		//TODO add user validation
	}
	


	public Consigner addConsigner() {
		//get data from user
		System.out.println("Enter the name of the consigner");
		String name = sStrng.nextLine();
		System.out.println("Enter the phone number of the consigner [(XXX)XXX-XXXX]");
		String phone = sStrng.nextLine();

		//TODO add validation

		//create new consigner
		Consigner consigner = new Consigner(name, phone);

		return consigner;
	}



	//get id from user
	public int getIdFromUser() {
		System.out.println("Enter the Id of the item.");
		int idFromUser = sInt.nextInt();
		return idFromUser;
		//TODO add validation
	}



	//confirm if user wants this record
	public String isThisYourRecord(Record r) {
		System.out.println(r);
		System.out.println("Is this the record you were looking for? [y/n]");
		String userChoice = sStrng.nextLine();
		return userChoice;
		//TOOD add validation
	}
	//consigner
	public String isThisYourConsigner(Consigner consigner) {
		System.out.println(consigner);
		System.out.println("Is this the consigner you were looking for? [y/n]");
		String userChoice = sStrng.nextLine();
		return userChoice;
		//TOOD add validation

	}
	//payment
	public String isThisYourPayment(Payment payment) {
		System.out.println(payment);
		System.out.println("Is this the payment you were looking for? [y/n]");
		String userChoice = sStrng.nextLine();
		return userChoice;
		//TOOD add validation	
	}

	//delete or ignore record
	private int editOrDeleteMenu() {
		System.out.println("1. Delete");
		System.out.println("2. Main Menu");
		int userChoice = sInt.nextInt();
		return userChoice;
		//TODO add Val

	}


	//get selling price from user
	public double getPriceFromUser() {
		System.out.println("How much is this record selling for?");
		double priceSold = sDbl.nextDouble();
		return priceSold;
		//TODO add Val
	}

	

	private void printConsigner(Consigner c) {
		double moneyOwed = controller.calcMoneyOwed(c);
		System.out.println("ID: " + c.getId() + "Name : " + c.getName() + " Phone: " + c.getPhone() + " Money Owed : " + moneyOwed);
		
	}




}