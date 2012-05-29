package we.should.communication;

import org.json.JSONObject;

/**
 * Referral objects are temporary java objects that can be presented to the user for approval
 * 
 * @author colleen
 *
 */
public class Referral {
	
	/** The name of the item being referred */
	private String mName;
	
	/** The email address of the user who referred this item */
	private String mSender;
	
	/** whether or not the referral has been approved */
	private boolean mApproved;
	
	/** database data stored as a JSONObject */
	private JSONObject mData;
	
	/**
	 * Referral constructor
	 * 
	 * @param name the item name
	 * @param sender the email of the user who referred this item
	 * @param approved a boolean indicating whether or not the item has been approved
	 * @param data database information stored as a JSONObject
	 */
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

	/**
	 * @return the JSONObject containing database representation information
	 */
	public JSONObject getData(){
		return this.mData;
	}
}
