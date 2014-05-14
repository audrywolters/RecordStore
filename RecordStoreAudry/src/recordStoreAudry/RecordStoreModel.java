package recordStoreAudry;

import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class RecordStoreModel {
	private RecordStoreController controller;
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
			e.printStackTrace();
			System.out.println("Could not connect to the Database.");
		} catch (SQLException se) {
			System.out.println("Could not connect to the Database.");
		}
	}



	///MAKE TABLES///
	public void createTables() {
		// table statement Strings
		String createRecordsTable = "CREATE TABLE Records (Id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "Title varchar(40), Artist varchar(40), ConsignerId int, CheckedInBy int, DateAdded date, SoldBy int, DateSold date, "
				+ "Sold boolean, BarginBin boolean, Price double, PriceSold double)";

		String createConsignersTable = "CREATE TABLE Consigners (Id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "Name varchar(40), Phone varchar(13), MoneyOwed double)";

		String createPaymentsTable = "CREATE TABLE Payments (Id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "RecordId int, ConsignerId int, Outstanding boolean, AmountDue double, AmountPaid double, PaymentMadeBy int, DateMade date)";
		
		
		String createStaffTable = "CREATE TABLE Staff (Id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
				+ "Name varchar(40), UserName varchar(40), Password varchar(40), Manager boolean)";
		
		String createLoginTable = "CREATE TABLE Logins (Id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "StaffId int, TimeIn timestamp, TimeOut timestamp)";
				

		String deleteRecords = "DROP TABLE Records";
		String deleteConsigners = "DROP TABLE Consigners";
		String deletePayments = "DROP TABLE Payments";
		String deleteStaff = "DROP TABLE Staff";
		String deleteLogins = "DROP TABLE Logins";

		// try to create the tables
		try {
			statement.executeUpdate(createLoginTable);
			statement.executeUpdate(createRecordsTable);
			statement.executeUpdate(createConsignersTable);
			statement.executeUpdate(createPaymentsTable);
			statement.executeUpdate(createStaffTable);
			statement.executeUpdate(deleteLogins);
			
			System.out.println("All Tables created");

		} catch (SQLException se) {

			// if tables exist				
			if (se.getSQLState().startsWith("X0")) { 		

				try {
					// delete them
					statement.executeUpdate(deleteRecords);
					statement.executeUpdate(deleteConsigners);
					statement.executeUpdate(deletePayments);
					statement.executeUpdate(deleteStaff);
					statement.executeUpdate(deleteLogins);
					
					// and recreate
					statement.executeUpdate(createRecordsTable);
					System.out.println("created table");
					statement.executeUpdate(createConsignersTable);
					System.out.println("created table");
					statement.executeUpdate(createPaymentsTable);
					System.out.println("created table");
					statement.executeUpdate(createStaffTable);
					System.out.println("created staff table");
					statement.executeUpdate(createLoginTable);
					System.out.println("created login table");

				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("Something went wrong while creating tables.");
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
			return false;
		}
		
		
		
	}
	
	

	///UPDATE SOLD RECORD TO DB///
	public boolean updateSoldRecord(Record record) {	
		//prep date for SQL
		Calendar dateSold = record.getDateSold();
		//convert to sql format
		java.sql.Date sqlDateSold =  new java.sql.Date(dateSold.getTime().getTime());


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
			return true;
		} catch (SQLException e) {
			System.out.println("Unable to delete record");
			return false;
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
							 3, 2, 1 };

		double[] prices = { 10.00, 10.00, 7.00, 
							9.00, 8.00, 1.01 };
			
		Boolean[] solds = { true, false, false, 
							false, false, false };
		
		int[] checkedIns = { 1, 3, 5,
							 2, 3, 5};
		
		Date[] datesAdded = { Date.valueOf("2014-04-10"), Date.valueOf("2014-05-02"), Date.valueOf("2013-05-03"), 
							  Date.valueOf("2014-04-01"), Date.valueOf("2014-05-02"), Date.valueOf("2014-04-30") };
		
		// dateSold null
		
		Boolean[] barginBins = { false, false, true, 
								 true, false, false };
		
		
		// priceSold null


		// statements
		String recordsInsert = "INSERT INTO Records "
				+ "(Title, Artist, ConsignerId, Price, Sold, CheckedInBy, DateAdded, BarginBin) "
				+ "VALUES " + "(?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			psInsert = connection.prepareStatement(recordsInsert);
			allStatements.add(psInsert);
			// insert data
			for (int i = 0; i < titles.length; i++) {
				psInsert.setString(1, titles[i]);
				psInsert.setString(2, artists[i]);
				psInsert.setInt(3, consigners[i]);
				psInsert.setDouble(4, prices[i]);
				psInsert.setBoolean(5, solds[i]);
				psInsert.setInt(6, checkedIns[i]);
				psInsert.setDate(7, datesAdded[i]);
				psInsert.setBoolean(8, barginBins[i]);
				
				psInsert.executeUpdate();
				//update id so controller can keep track of ids
				controller.generateRecordId();
			}
			

		} catch (SQLException e) {
			System.out.println("Error creating record test data.");
		}


		///CONSIGNERS///
		//test data
		String[] names = { "Jeremiah Johnson", "Tom Lang", "Jill Ranwieler" };
		String[] phones = { "(612)123-4567", "(612)333-4444", "(651)666-7890" };
		double[] moneyOweds = { 5, 0, 0 };

		//statements
		String consignerInsert = "INSERT INTO Consigners"
				+ "(Name, Phone, MoneyOwed)" + "VALUES" + "(?, ?, ?)";
		try {
			psInsert = connection.prepareStatement(consignerInsert);
			allStatements.add(psInsert);
			//insert data
			for (int i = 0; i < names.length; i++) {
				psInsert.setString(1, names[i]);
				psInsert.setString(2, phones[i]);
				psInsert.setDouble(3, moneyOweds[i]);
				psInsert.executeUpdate();
				//update id for controller
				controller.generateConsignerId();
			}
			

		} catch (SQLException e) {
			System.out.println("Error creating consigner test data.");
		}


		///PAYMENTS///
		//data
		int[] recordIds = { 1, 2, 6, 3, 4, 5 };
		int[] consignerIds = { 1, 1, 1, 2, 3, 3 };
		double[] amountDue = { 5, 0, 0, 0, 0, 0 };
		boolean[] outstandings = { true, false, false, false, false, false };

		// statements
		String paymentInsert = "INSERT INTO Payments"
				+ "(RecordId, ConsignerId, AmountDue, Outstanding)" 
				+ "VALUES (?, ?, ?, ?)";

		try {
			psInsert = connection.prepareStatement(paymentInsert);
			allStatements.add(psInsert);
			//insert test data
			for (int i = 0; i < recordIds.length; i++) {
				psInsert.setInt(1, recordIds[i]);
				psInsert.setInt(2, consignerIds[i]);
				psInsert.setDouble(3, amountDue[i]);
				psInsert.setBoolean(4, outstandings[i]);
				psInsert.executeUpdate();
				//update id for controller
				controller.generatePaymentId();
			}
			

		} catch (SQLException e) {
			System.out.println("Error creating payment test data.");
		}
		
		
		///STAFF///
		//data
		String[] staffNames = { "Audry Wolters", "Mervyn Peake", "Mina Murray" };
		String[] userNames = { "audry", "mervyn", "mina" };
		String[] passwords = { "aud", "mer", "min" };
		boolean[] managers = { true, false, false };

		//statement
		String staffInsert = "INSERT INTO Staff"
				+ "(Name, UserName, Password, Manager)"
				+ "VALUES (?, ?, ?, ?)";
		
		//insert
		try {
			psInsert = connection.prepareStatement(staffInsert);
			allStatements.add(psInsert);
			for (int i=0; i<staffNames.length; i++) {
				psInsert.setString(1, staffNames[i]);
				psInsert.setString(2, userNames[i]);
				psInsert.setString(3, passwords[i]);
				psInsert.setBoolean(4, managers[i]);
				psInsert.executeUpdate();
				//update Id for controller
				controller.generateStaffId();
			}
			System.out.println("inserted staff data");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error inserting staff test data");
		}
		
		
		///LOGIN///
		//data
		int[] staffIds = { 1, 2, 3 };
		Timestamp[] timesIn = { Timestamp.valueOf("2014-05-01 23:03:20"), Timestamp.valueOf("2014-05-02 20:09:20"), Timestamp.valueOf("2014-05-03 24:00:20") };
		Timestamp[] timesOut = { Timestamp.valueOf("2014-05-01 01:03:20"), Timestamp.valueOf("2014-05-01 24:03:20"), Timestamp.valueOf("2014-05-01 23:33:20") };
		
		//statment
		String loginInsert = "INSERT INTO Logins"
				+ "(StaffId, TimeIn, TimeOut)"
				+ "VALUES (?, ?, ?)";
		
		//insert
		try {
			psInsert = connection.prepareStatement(loginInsert);
			allStatements.add(psInsert);
			for (int i=0; i<staffIds.length; i++ ) {
				psInsert.setInt(1, staffIds[i]);
				psInsert.setTimestamp(2, timesIn[i]);
				psInsert.setTimestamp(3, timesOut[i]);
				//update id for controller
				controller.generateLoginId();
			}
			System.out.println("inserted login data");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error inserting staff data");
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
				int checkedInBy = rs.getInt("CheckedInBy");
				//parse date 
				Date dateAdded = rs.getDate("DateAdded");
				Calendar calAdded = new GregorianCalendar();
				calAdded.setTime(dateAdded);

				Record r = new Record(title, artist, consignerId, price, checkedInBy, calAdded);
				r.setId(id);

				controller.addToAllRecords(r);

			}
		} catch (SQLException se) {
			System.out.println("Error reading record data.");

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
		}

		// store data
		try {
			while (rs.next()) {
				int id = rs.getInt("Id");
				String name = rs.getString("Name");
				String phone = rs.getString("Phone");
				double moneyOwed = rs.getDouble("MoneyOwed");

				Consigner c = new Consigner (name, phone);
				c.setId(id);
				c.setMoneyOwed(moneyOwed);
				controller.addToAllConsigners(c);
			}
		} catch (SQLException se) {
			System.out.println("Error reading consigner data");

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

		}

		// store data
		try {
			while (rs.next()) {
				int id = rs.getInt("Id");
				int recordId = rs.getInt("RecordId");
				int consignerId = rs.getInt("ConsignerId");
				double amountDue = rs.getDouble("AmountDue");
				boolean outstanding = rs.getBoolean("Outstanding");
				Payment p = new Payment(recordId, consignerId, outstanding);
				p.setId(id);
				p.setAmountDue(amountDue);
				controller.addToAllPayments(p);
			}
		} catch (SQLException se) {
			System.out.println("Error reading payment data");

		}
	}
	
	
	///GET ALL STAFF AND STORE///
	public void requestAllStaff() {
		//fetch all data
		String fetchAllStaff = "SELECT * FROM Staff";
		try {
			rs = statement.executeQuery(fetchAllStaff);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error fetching staff");
		}
		
		//store data
		try {
			while (rs.next()) {  
				int id = rs.getInt("Id");
				String name = rs.getString("Name");
				String username = rs.getString("UserName");
				String password = rs.getString("Password");
				boolean manager = rs.getBoolean("Manager");
				Staff s = new Staff(name, username, password, manager);
				s.setId(id);
				controller.addToAllStaff(s);
			}
			System.out.println("Added all staff to storage");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error storing staff data");
		}
	}
	

	
	///GET ALL LOGINS AND STORE///
	public void requestAllLogins() {
		String fetchAllLogins = "SELECT * FROM Logins";
		//fetch all data
		try {
			rs = statement.executeQuery(fetchAllLogins);
			System.out.println("got a rs");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error fetching logins");
		}
		
		//store data
		try {
			while (rs.next()) {
				//fetch from result set
				int id = rs.getInt("Id");
				Timestamp timeIn = rs.getTimestamp("TimeIn");
				Timestamp timeOut = rs.getTimestamp("TimeOut");
				//store in login object
				Login l = new Login(timeIn);
				l.setTimeOut(timeOut);
				l.setId(id);
				controller.addToAllLogins(l);
				System.out.println("Added logins to storage.");
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error storing login");
		}
		
	}




	///SHUT DOWN DATABASE///
	public void cleanUp() {
		// close result set
		try {
			if (rs != null) {
				rs.close();
				//System.out.println("Closed the Result Set");
			}
		} catch (SQLException se) {
			System.out.println("Couldn't disconnect from database!");
		}

		// close statements
		for (Statement s : allStatements) {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException se) {
					System.out.println("Couldn't disconnect from database!");
					//se.printStackTrace();
				}
			}
		}

		// close the connection
		try {
			if (connection != null) {
				connection.close();
				//System.out.println("Disconnected from database.");
				System.out.println("Goodbye.");
			}
		} catch (SQLException se) {
			System.out.println("Couldn't disconnect from database!");
		}
	}





	


	



}