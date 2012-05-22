package we.should.communication;

import org.json.JSONObject;

public class Referral {
	
	private String mName;
	
	private String mSender;
	
	private boolean mApproved;
	
	private JSONObject mData;
	
	public Referral(String name, String sender, boolean approved, JSONObject data) {
		mName = name;
		mSender = sender;
		mApproved = approved;
		mData = data;
	}

	/**
	 * @return the name of this referral
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return the email address of the sender
	 */
	public String getSender() {
		return mSender;
	}

	/**
	 * @return whether this has been approved for saving
	 */
	public boolean isApproved() {
		return mApproved;
	}

	/**
	 * @param approved whether or not to approve this for saving
	 */
	public void setApproved(boolean approved) {
		this.mApproved = approved;
	}

}
