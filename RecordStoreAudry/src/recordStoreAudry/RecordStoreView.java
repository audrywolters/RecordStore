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
					+ "3. View/Delete a Record \n" 
					+ "CONSIGNERS \n"
					+ "4. Add a new Consigner \n" 
					+ "5. Pay a Consigner \n"
					+ "6. View/Delete a Consigner \n" 
					+ "PAYMENTS \n"
					+ "7. View outstanding payments \n"
					+ "8. View past payments \n"
					+ "0. QUIT \n"
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
			Record record = controller.searchForRecord();
			int userChoiceEdit = editOrDeleteMenu();
			if (userChoiceEdit == 1) {
				controller.deleteRecord(record);
			} 
			break;
		
		} case 4: {
			Consigner consigner = addConsigner();
			controller.addConsigner(consigner);
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
		System.out.println("Enter the price of the record:");
		double price = sDbl.nextDouble();
		System.out.println("Enter the Id of the Consigner:");
		int consignerId = sInt.nextInt();
		Calendar dateAdded = new GregorianCalendar();

		//create record
		Record record = new Record(title, artist, price, consignerId, dateAdded);

		//send to controller to send to DB
		controller.requestAddRecord(record);
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
	public int getIdFromUser() {
		System.out.println("Enter the Id of the item.");
		int idFromUser = sInt.nextInt();
		return idFromUser;
	}



	//confirm if user wants this record
	public String isThisYourRecord(Record r) {
		System.out.println(r);
		System.out.println("Is this the record you were looking for? [y/n]");
		String userChoice = sStrng.nextLine();
		return userChoice;
	}

	public String isThisYourConsigner(Consigner consigner) {
		// TODO Auto-generated method stub
		return null;
	}

	public String isThisYourPayment(Payment payment) {
		// TODO Auto-generated method stub
		return null;
	}

	private int editOrDeleteMenu() {
		System.out.println("1. Delete");
		System.out.println("2. Main Menu");
		int userChoice = sInt.nextInt();
		return userChoice;

	}


	//get selling price from user
	public double getPriceFromUser() {
		System.out.println("How much is this record selling for?");
		double priceSold = sDbl.nextDouble();
		return priceSold;
	}








}