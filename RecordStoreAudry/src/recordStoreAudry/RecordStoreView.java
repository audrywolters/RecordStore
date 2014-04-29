package recordStoreAudry;

import java.util.LinkedList;
import java.util.Scanner;

public class RecordStoreView {
	private RecordStoreController controller;
	private Scanner sInt = new Scanner(System.in);
	private Scanner sStrng = new Scanner(System.in);
	private Scanner sDbl = new Scanner(System.in);
	private int QUIT = 0;

	// constructor
	RecordStoreView(RecordStoreController cntrllr) {
		controller = cntrllr;
	}

	//begin the user interface process
	public void runUI() {
		while (true) {
			int userChoice = runMenuGetChoice();
			if (userChoice == QUIT) {
				break;
			} else {
				runTask(userChoice);
			}
		}
	}


	// display the menu and get the choice #
	public int runMenuGetChoice() {
		int userChoice = 0;
		// System.out.println("Welcome to Record Store Manager.");
		while (true) {
			System.out.println("\nRECORDS \n"
					+ "1. Add a new Record \n"
					+ "2. Sell a Record \n"
					// + "3. Delete a Record \n"
					+ "4. View/Edit a Record \n" 
					+ "CONSIGNERS \n"
					+ "5. Add a new Consigner \n" 
					+ "6. Pay a Consigner \n"
					+ "7. View/Edit a Consigner \n" 
					+ "PAYMENTS\n"
					+ "8. View outstanding payments \n"
					+ "9. View past payments \n" 
					+ "\n" 
					+ "0. QUIT");
			userChoice = sInt.nextInt();
			break;
		}
		// debug
		System.out.println(userChoice);
		return userChoice;
	}


	//the switch case mini-controller that responds to what choice was made
	public void runTask(int userChoice) {

		switch (userChoice) {

		case 1: {
			menuAddRecord();
		}
		case 2: {
			//int idFromUser = getIdFromUser();
			//TODO put in variable or not?
			//String tableName = "Records";
			controller.findAllRecords();
			//menuSellRecord(allRecords);

		}
		}
	}


	//menu and view for adding a record to the database
	private void menuAddRecord() {
		// get the data from user
		System.out.println("Enter the title of the record:");
		String title = sStrng.nextLine();
		System.out.println("Enter the artist of the record:");
		String artist = sStrng.nextLine();
		System.out.println("Enter the price of the record:");
		double price = sDbl.nextDouble();

		// create record object
		Record record = new Record(title, artist, price);

		//TODO ok Here?
		//isn't the view just sopposed to hand things to the controller
		controller.requestAddRecord(record);
	}

	
	/* TODO delete?
	private int getIdFromUser() {
		System.out.println("Enter the Id of the item.");
		int idFromUser = sInt.nextInt();
		return idFromUser;
	}
	*/
	

	//menu and view for selling a record
	public Record menuSellRecord(LinkedList<Record> allRecords) {
		System.out.println("Enter the Id of the item.");
		int idFromUser = sInt.nextInt();

		//compare userId to all the record Ids
		for (Record r : allRecords) {
			if (idFromUser == r.getId()) {
				System.out.println("Artist: " + r.getArtist() + ", Title: " + r.getTitle() + ", Consigner Id: " + r.getConsignerId());
				System.out.println("Is this the record you were looking for? [y/n]");
				String userChoice = sStrng.nextLine();

				//TODO ok here?
				//if it is the record they were looking for, update the record list with new data 
				//and send record to database to be updated there
				if (userChoice.equalsIgnoreCase("y")) {
					System.out.println("How much is this record selling for?");
					double priceSold = sDbl.nextDouble();
					r.setPriceSold(priceSold);
					java.util.Date today = new java.util.Date();
					java.sql.Date dateSold = new java.sql.Date(today.getTime());
					r.setDateSold(dateSold);
					return r;
				} 
			}			
		}
		//if the loop completes and hasn't found anything, print sorry message 
		System.out.println("Sorry. No matches found.");
		return null;
		
	}


















}
