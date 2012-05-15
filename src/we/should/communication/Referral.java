package we.should.communication;

public class Referral {
	
	private String mName;
	
	private String mSender;
	
	private boolean mApproved;
	
	public Referral(String name, String sender, boolean approved) {
		mName = name;
		mSender = sender;
		mApproved = approved;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getSender() {
		return mSender;
	}
	
	public boolean isApproved() {
		return mApproved;
	}

}
