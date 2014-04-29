package recordStoreAudry;

import java.sql.*;
import java.util.LinkedList;

public class RecordStoreController {

	private static RecordStoreModel model;
	private static RecordStoreView view;

	public static void main(String args[]) {

		RecordStoreController controller = new RecordStoreController();
		model = new RecordStoreModel(controller);
		view = new RecordStoreView(controller);

		// connect to db
		model.createConnection();

		// make tables
		try {
			model.createTables();
		} catch (SQLException e) {
			// TODO delete stack trace
			e.printStackTrace();
		}

		// insert data
		try {
			model.addTestData();
		} catch (SQLException e) {
			// TODO delete stack trace
			e.printStackTrace();
		}

		// launch the menu, get the user's choice
		view.runUI();

		/*
		 * //get data model.requestAllRecords(); model.requestAllConsigners();
		 * model.requestAllPayments();
		 */

		// clean up
		model.cleanUp();
	}

	
	public void requestAddRecord(Record record) {
		model.addRecord(record);
	}
	
	
	public void findAllRecords() {
		//TODO put this in the beginning and change this method to sell record
		model.requestAllRecords();
		LinkedList<Record> allRecords = model.getAllRecords();
		Record soldRecord = view.menuSellRecord(allRecords);
		model.updateRecord(soldRecord);
	}

	//public void requestItem(int idFromUser, String tableName) {
		//model.getItem(idFromUser, tableName);
		
	//}

}
