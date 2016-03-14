package iansLibrary.security.auth;

public class Account {

	private Person owner;
	private boolean active;
	
	public Account(Person owner){
		this.setOwner(owner);
		active = true;
	}
	
	public Account(){}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
