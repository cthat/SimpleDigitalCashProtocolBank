
public class BankInfo {
	private String custIP;
	private int custPort;
	private String vendorIP;
	private int vendorPort;
	private String signature; // TODO: Should this be a string or something else?
	private long custAcctBalance; // TODO: Is this needed?
	private long vendorAcctBalance; // TODO: Is this needed?
	
	public BankInfo(String custIP, int custPort, String vendorIP, int vendorPort, String signature, long custAcctBal, long vendAcctBal) {
		this.custIP = custIP;
		this.custPort = custPort;
		this.vendorIP = vendorIP;
		this.vendorPort = vendorPort;		
		this.signature = signature;
		this.custAcctBalance = custAcctBal;
		this.vendorAcctBalance = vendAcctBal;
	}
	
	public String getCustIP() {
		return custIP;
	}
	
	public int getCustPort() {
		return custPort;
	}
	
	public String getVendorIP() {
		return vendorIP;
	}
	
	public int getVendorPort() {
		return vendorPort;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public long getCustAcctBalance() {
		return custAcctBalance;
	}
	
	public long getVendorAcctBalance() {
		return vendorAcctBalance;
	}
	
	public void setVendorAcctBalance(long amount) {
		this.vendorAcctBalance = amount;
	}
	
	public void setCustomerAcctBalance(long amount) {
		this.custAcctBalance = amount;
	}
}
