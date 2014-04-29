package recordStoreAudry;

import java.sql.*;
import java.util.LinkedList;

public class RecordStoreModel {
	private static RecordStoreController controller;
	// connect to database
	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String protocol = "jdbc:derby:";
	private static String databaseName = "honeyDB";
	// creds for database
	private static final String USER = "temp";
	private static final String PASS = "password";
	// sql statements
	private static Statement statement = null;
	private static Connection connection = null;
	ResultSet rs = null;
	PreparedStatement psInsert = null;
	private static LinkedList<Statement> allStatements = new LinkedList<Statement>();

	public RecordStoreModel(RecordStoreController rsController) {
		this.controller = rsController;
	}

	// /CONNECT TO DATABASE///
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

	// /MAKE TABLES///
	public void createTables() throws SQLException {
		// table statment Strings
		String createRecordsTable = "CREATE TABLE Records (Id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "Title varchar(40), Artist varchar(40), ConsignerId int, DateAdded date, DateSold date, Sold boolean, "
				+ "BarginBin boolean, Price double, PriceSold double)";

		String createConsignersTable = "CREATE TABLE Consigners (Id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
				+ "Name varchar(40), Phone varchar(10), MoneyOwed double)";

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
			System.out
					.println("Records, Consigners, and Payments Tables created");
		} catch (SQLException sqle) {
			if (sqle.getSQLState().startsWith("X0")) { // if tables exist
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
			} else {
				System.out.println("Something unknown went wrong!");
				throw sqle;
			}
		}
	}

	//
	public void addRecord(Record record) {
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
				+ "VALUES" + "(?, ?, ?, ?, ?, ?)";
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

		} catch (SQLException e) {
			System.out.println("Error adding record to database");
			// delete stack trace
			e.printStackTrace();
		}

	}

	public void addTestData() throws SQLException {
		// /RECORDS///
		// test data
		String[] titles = { "Outside", "Low", "The Fragile", "Head Over Heels" };
		String[] artists = { "David Bowie", "David Bowie", "Nine Inch Nails",
				"Cocteau Twins" };
		int[] consigners = { 1, 1, 2, 3 };
		Date[] datesAdded = { Date.valueOf("2014-04-10"),
				Date.valueOf("2014-04-10"), Date.valueOf("2014-03-13"),
				Date.valueOf("2014-04-10") };
		// dateSold null
		Boolean[] solds = { false, false, false, true };
		Boolean[] barginBins = { false, false, false, false };
		double[] prices = { 10.00, 10.00, 7.00, 9.00 };
		// priceSold null

		// statements
		String recordsInsert = "INSERT INTO Records "
				+ "(Title, Artist, ConsignerId, DateAdded, Sold, BarginBin, Price) "
				+ "VALUES " + "(?, ?, ?, ?, ?, ?, ?)";
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
		}
		System.out.println("Added records to database");

		// /CONSIGNERS///
		// test data
		String[] names = { "Jeremiah Johnson", "Tom Lang", "Jill Ranwieler" };
		String[] phones = { "6121234567", "6123334444", "6515556666" };
		double[] moneyOweds = { 0, 0, 0 };

		// statements
		String consignerInsert = "INSERT INTO Consigners"
				+ "(Name, Phone, MoneyOwed)" + "VALUES" + "(?, ?, ?)";
		psInsert = connection.prepareStatement(consignerInsert);
		allStatements.add(psInsert);

		// insert data
		for (int i = 0; i < names.length; i++) {
			psInsert.setString(1, names[i]);
			psInsert.setString(2, phones[i]);
			psInsert.setDouble(3, moneyOweds[i]);
			psInsert.executeUpdate();
		}
		System.out.println("Added consigners to database.");

		// /PAYMENTS///
		// data
		int[] recordIds = { 1, 3 };
		int[] consignerIds = { 1, 2 };
		boolean[] outstandings = { false, false };

		// statements
		String paymentInsert = "INSERT INTO Payments"
				+ "(RecordId, ConsignerId, Outstanding)" + "VALUES"
				+ "(?, ?, ?)";
		psInsert = connection.prepareStatement(paymentInsert);
		allStatements.add(psInsert);

		// insert data
		for (int i = 0; i < recordIds.length; i++) {
			psInsert.setInt(1, recordIds[i]);
			psInsert.setInt(2, consignerIds[i]);
			psInsert.setBoolean(3, outstandings[i]);
			psInsert.executeUpdate();
		}
		System.out.println("Added payments to the database");

	}

	public void requestAllRecords() {
		LinkedList<Record> allRecords = new LinkedList<Record>();

		// fetch the data
		String fetchAllRecords = "SELECT * FROM Records";
		try {
			rs = statement.executeQuery(fetchAllRecords);
		} catch (SQLException se) {
			System.out.println("Error fetching all records.");
			// TODO delete stack trace
			se.printStackTrace();
		}

		// put data into object and then linked list
		try {
			while (rs.next()) {
				// TODO delete excess data
				// int id = rs.getInt("Id");
				String title = rs.getString("Title");
				String artist = rs.getString("Artist");
				// int consignerId = rs.getInt("ConsignerId");
				// Date dateAdded = rs.getDate("DateAdded");
				double price = rs.getDouble("Price");

				Record r = new Record(/* id, */title, artist, /*
															 * consignerId,
															 * dateAdded,
															 */price);
				allRecords.add(r);

			}
		} catch (SQLException se) {
			System.out.println("Error reading record data.");
			// TODO delete stack trace
			se.printStackTrace();
		}

		for (Record r : allRecords) {
			System.out.println(r);
		}

	}

	public void requestAllConsigners() {
		LinkedList<Consigner> allConsigners = new LinkedList<Consigner>();

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

				Consigner c = new Consigner(id, name, phone);
				allConsigners.add(c);
			}
		} catch (SQLException se) {
			System.out.println("Error reading consigner data");
			// TODO delete stack trace
			se.printStackTrace();
		}

		// print data
		for (Consigner c : allConsigners) {
			System.out.println(c);
		}

	}

	public void requestAllPayments() {
		LinkedList<Payment> allPayments = new LinkedList<Payment>();

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
				Payment p = new Payment(id, recordId, consignerId, outstanding);
				allPayments.add(p);
			}
		} catch (SQLException se) {
			System.out.println("Error reading payment data");
			// TODO delete stack trace
			se.printStackTrace();
		}

		// print data
		for (Payment p : allPayments) {
			System.out.println(p);
		}

	}

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
