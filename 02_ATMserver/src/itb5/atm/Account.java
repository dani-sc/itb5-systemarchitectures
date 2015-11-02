package itb5.atm;

public class Account {
	private float _balance;

	public void deposit(float amount) {
		_balance += amount;
	}
	
	public void withdraw(float amount) {
		_balance -= amount;
	}
	
	public float getBalance(int accountNo) {
		return _balance;
	}
	
	
}
