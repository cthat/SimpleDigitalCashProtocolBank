import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class BankTransactionProcessor {
	private final BankInfo bankInfo;
	private LinkedList<Order> recvdMoneyOrders;
	private Order signedMoneyOrder;
	private Order recvdVendOrder;
	
	//TODO: Trying out map obj
	private Map<String, ArrayList<byte[]>> database; // Database of idents with associated serial numbers

	/* Don't need with maps
	 * 
	 *
	private ArrayList<String> depositedSerials; // "DB" for the uniqueness strings
	private ArrayList<ArrayList<byte[]>> identityStrings; // "DB" for identity strings
	*/
		
	public BankTransactionProcessor(BankInfo bankInfo) {
		this.bankInfo = bankInfo;
	}
	
	public void process() {
		handleCustRequest();
		handleVendRequest();
	}
			
	private void handleCustRequest() {
		recvCustOrders();
		requestUnblind();
		if(verifyCustMoneyOrders()) {
			// TODO: After verification and before signing must request ident reveal?			
			signMoneyOrder();
			decrementCustAcct(signedMoneyOrder.getAmount());
		}		
	}
	
	private void handleVendRequest() {
		recvVendOrder();
		if(verifyVendOrder()) {
			incrementVendAcct(recvdVendOrder.getAmount());
			storeToDatabase();
		}
	}
	
	// TODO: Not sure if this was done correctly
	private void recvCustOrders() {
		try {
			Socket socket = new Socket(bankInfo.getCustIP(), bankInfo.getCustPort());
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			recvdMoneyOrders = (LinkedList<Order>) objectInputStream.readObject();
			socket.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// TODO: Not sure if this was done correctly
	private void recvVendOrder() {
		try {
			Socket socket = new Socket(bankInfo.getVendorIP(), bankInfo.getVendorPort());
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			recvdVendOrder = (Order) objectInputStream.readObject();
			socket.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void requestUnblind() {
		try {
			Socket socket = new Socket(bankInfo.getCustIP(), bankInfo.getCustPort());
			// Send n-1 money orders to be unblinded
			sendMoneyOrders(socket.getOutputStream());	
			// Get unblinded money orders back
			recvCustOrders();			
			socket.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMoneyOrders(OutputStream outputStream) throws IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		// Randomly select a money order, this will be the order the bank signs
		Random rand = new Random();
		int index = rand.nextInt(recvdMoneyOrders.size());
		signedMoneyOrder = recvdMoneyOrders.get(index);
		recvdMoneyOrders.remove(index);		
		// Send n-1 to be unblinded
		objectOutputStream.writeObject(recvdMoneyOrders);
	}
	
	private boolean verifyCustMoneyOrders() {
		// Iterate through the orders
		Order mo = recvdMoneyOrders.getFirst();
		while(mo != null) {		
			// Check to see if amount of order is > than customer account balance
			//TODO: Is this the right check here?
			if (mo.getAmount() > bankInfo.getCustAcctBalance()) {
				System.out.println("Amount exceeds customer account balance");
				return false;
			}
			// or if serial number has been used before
			if(database.containsKey(mo.getSerialNumber())) {
				System.out.println("Duplicate serial number detected");
				return false;
			}
			
			/* Don't need with maps
			 *
			 * 
			if(checkSerial(mo.getSerialNumber())) {
				System.out.println("Duplicate serial number detected");
				return false;
			}
			*/
		}	
		return true;
	}
	
	private boolean verifyVendOrder() {
		// Verify  the signature
		if (recvdVendOrder.getSignature().compareTo(bankInfo.getSignature()) != 0)
			return false;	
		
		// Check if serial number has been used
		if(database.containsKey(recvdVendOrder.getSerialNumber())) {
			if(database.containsValue(recvdVendOrder.getCommitment())) {
				System.out.println("Vendor copied Money Order");
			} else {
				//TODO: Handle customer copied money order and ident reveal??
			}
			return false;
		}
		
		/* Don't need with maps
		 * 
		 * 
		if(checkSerial(recvdVendOrder.getSerialNumber())) {
			for(int i = 0; i < identityStrings.size(); i++) {
			 	//TODO: search for matching strings
			}		
			return false;
		}
		*/
		return true;		
	}

	/* Don't need with maps
	 * 
	 * 
	private boolean checkSerial(String serial) {
		return depositedSerials.contains(serial);
	}
	*/
	
	private void signMoneyOrder() {
		// Sign the money order
		signedMoneyOrder.setSignature(bankInfo.getSignature());
		// Send to customer
		try {
			Socket socket = new Socket(bankInfo.getCustIP(), bankInfo.getCustPort());
			sendSignedMoneyOrder(socket.getOutputStream());
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendSignedMoneyOrder(OutputStream outputStream) throws IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(signedMoneyOrder);
	}
	
	private void decrementCustAcct(long amt) {
		bankInfo.setCustomerAcctBalance(bankInfo.getCustAcctBalance() - amt);		
	}
	
	private void incrementVendAcct(long amt) {
		bankInfo.setVendorAcctBalance(bankInfo.getVendorAcctBalance() + amt);
	}
	
	private void storeToDatabase() {
		/* Don't need with maps
		 * 
		 * 
		depositedSerials.add(recvdVendOrder.getSerialNumber());
		identityStrings.add(recvdVendOrder.getCommitment()); 
		*/
		database.put(recvdVendOrder.getSerialNumber(), recvdVendOrder.getCommitment());
	}
}
