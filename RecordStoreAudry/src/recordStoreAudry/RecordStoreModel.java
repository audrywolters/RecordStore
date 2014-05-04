package recordStoreAudry;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class RecordStoreModel {
	private static RecordStoreController controller;
	// connect to database
	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String protocol = "jdbc:derby:";
	private static String databaseName = "recordStoreDB";
	// creds for database
	private static final String USER = "temp";
	private static final String PASS = "password";
	// sql statements
	private static Statement statement = null;
	private static Connection connection = null;
	ResultSet rs = null;
	PreparedStatement psInsert = null;
	private static LinkedList<Statement> allStatements = new LinkedList<Statement>();



	//constructor
	public RecordStoreModel(RecordStoreController rsController) {
		//TODO fix static issue
		this.controller = rsController;
	}



	///CONNECT TO DATABASE///
	public void createConnection() {
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(protocol + databaseName
					+ ";create=true", USER, PASS);
			statement = connection.createStatement();
			allStatements.add(statement);
		} catch (ClassNotFoundException e) {
			System.out.println("Could not connect to the Database.");
			// TODO delete stack trace
			e.printStackTrace();
		} catch (SQLException se) {
			// TODO delete stack trace
			se.printStackTrace();
		}
	}



	///MAKE TABLES///
	public void createTables() {
		// table statement Strings
		String createRecordsTable = "CREATE TABLE Records (Id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "Title varchar(40), Artist varchar(40), ConsignerId int, DateAdded date, DateSold date, Sold boolean, "
				+ "BarginBin boolean, Price double, PriceSold double)";

		String createConsignersTable = "CREATE TABLE Consigners (Id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "Name varchar(40), Phone varchar(13), MoneyOwed double)";

		String createPaymentsTable = "CREATE TABLE Payments (Id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "RecordId int, ConsignerId int, Outstanding boolean, AmountDue double, AmountPaid double, DateMade date)";

		String deleteRecords = "DROP TABLE Records";
		String deleteConsigners = "DROP TABLE Consigners";
		String deletePayments = "DROP TABLE Payments";

		// try to create the tables
		try {
			statement.executeUpdate(createRecordsTable);
			statement.executeUpdate(createConsignersTable);
			statement.executeUpdate(createPaymentsTable);
			System.out.println("Records, Consigners, and Payments Tables created");

		} catch (SQLException se) {

			// if tables exist				
			if (se.getSQLState().startsWith("X0")) { 		

				try {
					// delete them
					statement.executeUpdate(deleteRecords);
					statement.executeUpdate(deleteConsigners);
					statement.executeUpdate(deletePayments);
					// and recreate
					statement.executeUpdate(createRecordsTable);
					statement.executeUpdate(createConsignersTable);
					statement.executeUpdate(createPaymentsTable);
					System.out
					.println("Tables already exist. Dropped and recreated.");
				} catch (SQLException e) {
					System.out.println("Something went wrong while creating tables.");
					//TODO delete stackTrace
					e.printStackTrace();
				}
			} 
		}
	}



	///ADD NEW RECORD TO DB///
	public boolean addUserRecord(Record record) {
		//prepare id
		//controller.setRecordId();

		// prepare variables
		String title = record.getTitle();
		String artist = record.getArtist();
		// get today's date/time in sql format
		java.util.Date today = new java.util.Date();
		java.sql.Date dateAdded = new java.sql.Date(today.getTime());
		boolean sold = false;
		boolean barginBin = false;
		double price = record.getPrice();

		// prepare statement
		String newRecordInsert = "INSERT INTO Records"
				+ "(Title, Artist, DateAdded, Sold, BarginBin, Price)"
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try {
			psInsert = connection.prepareStatement(newRecordInsert);
			allStatements.add(psInsert);

			// insert data
			psInsert.setString(1, title);
			psInsert.setString(2, artist);
			psInsert.setDate(3, dateAdded);
			psInsert.setBoolean(4, sold);
			psInsert.setBoolean(5, barginBin);
			psInsert.setDouble(6, price);
			psInsert.executeUpdate();


			System.out.println("Added " + title + " to database");
			return true;
		} catch (SQLException e) {
			System.out.println("Error adding record to database");
			// delete stack trace
			e.printStackTrace();
			return false;
		}

	}

	public boolean addPayment(Payment payment) {
		//prep variables
		int recId = payment.getRecordId();
		int consignId = payment.getConsignerId();
		boolean outstand = payment.getOutstanding();
		double amountDue = payment.getAmountDue();
		
		
		String createPayment = "INSERT INTO Payments"
				+ " (RecordId, ConsignerId, Outstanding, AmountDue)"
				+ " VALUES (?, ?, ?, ?)";

		try {
			psInsert = connection.prepareStatement(createPayment);
			allStatements.add(psInsert);
			
			psInsert.setInt(1, recId);
			psInsert.setInt(2, consignId);
			psInsert.setBoolean(3, outstand);
			psInsert.setDouble(4, amountDue);
			psInsert.executeUpdate();
			
			System.out.println("Added payment to database.");
			return true;
			
		} catch (SQLException se) {
			System.out.println("Error adding payment to database");
			//TODO DST
			se.printStackTrace();
			return false;
		}
		
		
		
	}

	///UPDATE SOLD RECORD TO DB///
	public boolean updateSoldRecord(Record record) {	
		//prep date for SQL
		Calendar dateSold = record.getDateSold();
		java.sql.Date sqlDateSold =  new java.sql.Date(dateSold.getTime().getTime() );


		String updateRecord = "UPDATE Records"
				+ " SET DateSold = '" + sqlDateSold + "', Sold = 'TRUE', PriceSold = " + record.getPriceSold()
				+ " WHERE Id = " + record.getId();

		try {
			statement.executeUpdate(updateRecord);
			System.out.println("Record updated");
			System.out.println(record);
			return true;
		} catch (SQLException e) {
			System.out.println("Unable to update record.");
			// TODO delete stack trace
			e.printStackTrace();
			return false;
		}
	}
	
	
	///UPDATE PAID FOR PAYMENT///
	public boolean updatePayment(Payment payment) {
		//prep date for SQL
		Calendar dateMade = payment.getDateMade();	
		java.sql.Date sqlDateMade = new java.sql.Date(dateMade.getTime().getTime());
		
		//prep statement
		String updatePayment = "UPDATE Payments"
				+ " SET AmountDue = " + payment.getAmountDue() +   
				", AmountPaid = " + payment.getAmountPaid() +  ", Outstanding = 'FALSE'" + ", DateMade = '" + sqlDateMade  
				+ "'  WHERE Id = " + payment.getId();
				
		//update the DB
		try {
			statement.executeUpdate(updatePayment);
			System.out.println("Record Updated");
			return true;
		} catch (SQLException se) {
			System.out.println("Error updating payment");
			//TODO DST
			se.printStackTrace();
			return false;
		}
	}
	
	

	///UPDATE BARGIN BIN STAUTS///
	public boolean updateInventoryStatus(Record record) {
		//prep statement
		String updateStatus= "UPDATE Records"
				+ " SET BarginBin = TRUE, Price = 1.00"
				+ " WHERE Id = " + record.getId();

		//update the DB
		try {
			statement.executeUpdate(updateStatus);
			System.out.println("Record updated.");
			return true;
		} catch (SQLException se) {
			System.out.println("Error updating record.");
			//TODO DST
			se.printStackTrace();
			return false;
		}
	}



	///DELETE RECORD///
	public boolean deleteRecord(Record record) {
		String deleteRecord = "DELETE FROM Records "
				+ "WHERE Id=" + record.getId();

		try {
			statement.executeUpdate(deleteRecord);
			System.out.println("Record Deleted");
			boolean success = true;
			return success;
		} catch (SQLException e) {
			System.out.println("Unable to delete record");
			boolean success = false;
			//TODO DST
			e.printStackTrace();
			return success;
		}

	}



	///ADD NEW CONSIGNER///
	public boolean addConsigner(Consigner consigner) {
		//get variables out of consigner object
		String name = consigner.getName();
		String phone = consigner.getPhone();		

		String insertConsigner = "INSERT INTO Consigners (Name, Phone) VALUES (?, ?)";

		try {
			psInsert = connection.prepareStatement(insertConsigner);
			allStatements.add(psInsert);

			// insert data
			psInsert.setString(1, name);
			psInsert.setString(2, phone);
			System.out.println("Consigner added to database");
			return true;
		} catch (SQLException e) {
			System.out.println("Unable to add consigner to database");
			// TODO DST
			e.printStackTrace();
			return false;
		}


	}



	///ADD TEST DATA///
	public void addTestData() { 
		///RECORDS///
		// test data
		String[] titles = { "Outside", "Low", "The Fragile", 
							"Head Over Heels", "Low", "Low" };
		
		String[] artists = { "David Bowie", "David Bowie", "Nine Inch Nails", 
							 "Cocteau Twins", "David Bowie", "Yung Bundle" };
		
		int[] consigners = { 1, 1, 2, 
							 3, 3, 1 };
		
		Date[] datesAdded = { Date.valueOf("2014-04-10"), Date.valueOf("2014-05-02"), Date.valueOf("2013-05-03"), 
							  Date.valueOf("2014-04-01"), Date.valueOf("2014-05-02"), Date.valueOf("2014-04-30") };
		// dateSold null
		
		Boolean[] solds = { true, false, false, 
							false, false, false };
		
		Boolean[] barginBins = { false, false, true, 
								 true, false, false };
		
		double[] prices = { 10.00, 10.00, 7.00, 
							9.00, 8.00, 1.01 };
		
		// priceSold null


		// statements
		String recordsInsert = "INSERT INTO Records "
				+ "(Title, Artist, ConsignerId, DateAdded, Sold, BarginBin, Price) "
				+ "VALUES " + "(?, ?, ?, ?, ?, ?, ?)";
		try {
			psInsert = connection.prepareStatement(recordsInsert);
			allStatements.add(psInsert);
			// insert data
			for (int i = 0; i < titles.length; i++) {
				psInsert.setString(1, titles[i]);
				psInsert.setString(2, artists[i]);
				psInsert.setInt(3, consigners[i]);
				psInsert.setDate(4, datesAdded[i]);
				psInsert.setBoolean(5, solds[i]);
				psInsert.setBoolean(6, barginBins[i]);
				psInsert.setDouble(7, prices[i]);
				psInsert.executeUpdate();
				//update id for controller
				controller.setRecordId();
			}
			System.out.println("Added records to database");

		} catch (SQLException e) {
			System.out.println("Error creating test data.");
			//TODO delete ST
			e.printStackTrace();
		}


		///CONSIGNERS///
		//test data
		String[] names = { "Jeremiah Johnson", "Tom Lang", "Jill Ranwieler" };
		String[] phones = { "(612)123-4567", "(612)333-4444", "(651)666-7890" };
		//double[] moneyOweds = { 0, 0, 0 };

		//statements
		String consignerInsert = "INSERT INTO Consigners"
				+ "(Name, Phone)" + "VALUES" + "(?, ?)";
		try {
			psInsert = connection.prepareStatement(consignerInsert);
			allStatements.add(psInsert);
			//insert data
			for (int i = 0; i < names.length; i++) {
				psInsert.setString(1, names[i]);
				psInsert.setString(2, phones[i]);
				psInsert.executeUpdate();
				//update id for controller
				controller.setConsignerId();
			}
			System.out.println("Added consigners to database.");

		} catch (SQLException e) {
			System.out.println("Error creating test data.");
			// TODO DST
			e.printStackTrace();
		}


		///PAYMENTS///
		//data
		int[] recordIds = { 1, 2, 6, 3, 4, 5 };
		int[] consignerIds = { 1, 1, 1, 2, 3, 3 };
		boolean[] outstandings = { true, false, false, false, false, false };

		// statements
		String paymentInsert = "INSERT INTO Payments"
				+ "(RecordId, ConsignerId, Outstanding)" + "VALUES"
				+ "(?, ?, ?)";

		try {
			psInsert = connection.prepareStatement(paymentInsert);
			allStatements.add(psInsert);
			//insert test data
			for (int i = 0; i < recordIds.length; i++) {
				psInsert.setInt(1, recordIds[i]);
				psInsert.setInt(2, consignerIds[i]);
				//psInsert.setDouble(3, amountDue[i]);
				psInsert.setBoolean(3, outstandings[i]);
				psInsert.executeUpdate();
				//update id for controller
				controller.setPaymentId();
			}
			System.out.println("Added payments to the database");

		} catch (SQLException e) {
			System.out.println("Error creating test data.");
			// TODO DST
			e.printStackTrace();
		}
	}



	///GET ALL RECORDS AND STORE THEM///
	public void requestAllRecords() {
		//fetch the data
		String fetchAllRecords = "SELECT * FROM Records";
		try {
			rs = statement.executeQuery(fetchAllRecords);
		} catch (SQLException se) {
			System.out.println("Error fetching all records.");
			// TODO delete stack trace
			se.printStackTrace();
		}

		//put data into object and then linked list
		try {
			while (rs.next()) {
				//get data from result set to make object
				int id = rs.getInt("Id");
				String title = rs.getString("Title");
				String artist = rs.getString("Artist");
				double price = rs.getDouble("Price");
				int consignerId = rs.getInt("ConsignerId");
				//parse date 
				Date dateAdded = rs.getDate("DateAdded");
				Calendar calAdded = new GregorianCalendar();
				calAdded.setTime(dateAdded);

				Record r = new Record(title, artist, price, consignerId, calAdded);
				r.setId(id);

				controller.addToAllRecords(r);

			}
		} catch (SQLException se) {
			System.out.println("Error reading record data.");
			// TODO delete stack trace
			se.printStackTrace();
		}
	}




	///GET ALL CONSIGNERS AND STORE THEM///
	public void requestAllConsigners() {
		// fetch all data
		String fetchAllConsigners = "SELECT * FROM Consigners";
		try {
			rs = statement.executeQuery(fetchAllConsigners);
		} catch (SQLException se) {
			System.out.println("Error fetching Consigners");
			// TODO delete stack trace
			se.printStackTrace();
		}

		// store data
		try {
			while (rs.next()) {
				int id = rs.getInt("Id");
				String name = rs.getString("Name");
				String phone = rs.getString("Phone");
				// double moneyOwed = rs.getDouble("MoneyOwed");

				Consigner c = new Consigner (name, phone);
				c.setId(id);
				controller.addToAllConsigners(c);
			}
		} catch (SQLException se) {
			System.out.println("Error reading consigner data");
			// TODO delete stack trace
			se.printStackTrace();
		}
	}




	///GET ALL PAYMENTS AND STORE THEM///
	public void requestAllPayments() {
		// fetch all data
		String fetchAllPayments = "SELECT * FROM Payments";
		try {
			rs = statement.executeQuery(fetchAllPayments);
		} catch (SQLException se) {
			System.out.println("Error fetching Payments");
			// TODO delete stack trace
			se.printStackTrace();
		}

		// store data
		try {
			while (rs.next()) {
				int id = rs.getInt("Id");
				int recordId = rs.getInt("RecordId");
				int consignerId = rs.getInt("ConsignerId");
				boolean outstanding = rs.getBoolean("Outstanding");
				Payment p = new Payment(recordId, consignerId, outstanding);
				p.setId(id);
				controller.addToAllPayments(p);
			}
		} catch (SQLException se) {
			System.out.println("Error reading payment data");
			// TODO delete stack trace
			se.printStackTrace();
		}
	}




	///SHUT DOWN DATABASE///
	public void cleanUp() {
		// close result set
		try {
			if (rs != null) {
				rs.close();
				System.out.println("Closed the Result Set");
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}

		// close statements
		for (Statement s : allStatements) {
			if (s != null) {
				try {
					s.close();
					System.out.println("Closed Statement.");
				} catch (SQLException se) {
					System.out.println("Couldn't close statement!");
					se.printStackTrace();
				}
			}
		}

		// close the connection
		try {
			if (connection != null) {
				connection.close();
				System.out.println("Disconnected from database.");
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}



	


	



}