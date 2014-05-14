package recordStoreAudry;

public class Staff {
	private int id;
	private String name;
	private String username;
	private String password;
	private boolean manager;
	
	
	//construct
	public Staff(String name, String username, String password, boolean manager) {
		this.name = name;
		this.username = username;
		this.password = password;
		this.manager = manager;
	}



	//get and set
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public boolean isManager() {
		return manager;
	}


	public void setManager(boolean manager) {
		this.manager = manager;
	}
	
	

	
}
