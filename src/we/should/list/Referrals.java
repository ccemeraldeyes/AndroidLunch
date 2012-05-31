package we.should.list;

import java.util.LinkedList;

/**
 * This is the special Referrals category, that handles
 * Referrals.  It has no fields, and the fields of each item
 * are instead stored on a per item basis in each item.
 * 
 * Rep Invariant:
 * 
 * this.fields = []
 */

import org.json.JSONObject;

import android.content.Context;

public class Referrals extends GenericCategory {
	
	/**
	 * Creates a new instance of the Referrals category
	 * @param ctx - context of the db to which items of this category will be saved.
	 */
	public Referrals(Context ctx){
		super(Category.Special.Referrals.toString(), new LinkedList<Field>(), ctx);
	}
	/**
	 * Returns a new Referral Item linked to this category
	 * initialized to the data contained in o
	 * @param o - a JSON representation of the data to be
	 * contained in the returned item
	 * @return a new ReferralItem with data from o
	 */
	public ReferralItem newItem(JSONObject o){
		return new ReferralItem(this, o, ctx);
	}
	
	@Override
	public Item newItem(){
		ReferralItem out = this.newItem(new JSONObject());
		return out;
	}
	/**
	 * Returns the special Referrals instance that is stored in the db.
	 * @param ctx - the context of the db to search
	 * @return the Referrals Category
	 */
	public static Referrals getReferralCategory(Context ctx){
		Referrals r = (Referrals) Category.getCategory(Category.Special.Referrals.toString(), ctx);
		if(r==null) throw new IllegalStateException("There is no Referrals category!");
		return r;
	}
}
