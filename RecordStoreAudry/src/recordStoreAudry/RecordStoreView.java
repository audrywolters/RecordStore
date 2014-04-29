package recordStoreAudry;

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
			System.out.println("\n RECORDS \n"
					+ "1. Add a new Record \n"
					+ "2. Sell a Record \n"
					// + "3. Delete a Record \n"
					+ "4. View/Edit a Record \n" + "CONSIGNERS \n"
					+ "5. Add a new Consigner \n" + "6. Pay a Consigner \n"
					+ "7. View/Edit a Consigner \n" + "PAYMENTS\n"
					+ "8. View outstanding payments \n"
					+ "9. View past payments \n" + "\n" + "0. QUIT");
			userChoice = sInt.nextInt();
			break;
		}
		// debug
		System.out.println(userChoice);
		return userChoice;
	}

	private void runTask(int userChoice) {

		switch (userChoice) {

		case 1: {
			userAddRecord();
		}
		case 2: {
			sellRecord();
		}
		}

	}

	private void userAddRecord() {
		// get the data from user
		System.out.println("Enter the title of the record:");
		String title = sStrng.nextLine();
		System.out.println("Enter the artist of the record:");
		String artist = sStrng.nextLine();
		System.out.println("Enter the price of the record:");
		double price = sDbl.nextDouble();

		// create record object
		Record record = new Record(title, artist, price);

		controller.requestAddRecord(record);
		// return record;

	}

	private void sellRecord() {
		// TODO Auto-generated method stub

	}

}
