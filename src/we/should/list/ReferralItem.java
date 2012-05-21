package we.should.list;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * This is a specific item class used for referrals from other users. Its fields
 * are stored locally so that all of the items in the Referrals category can be
 * rendered differently. This accommodates custom fields etc made by other users.
 * @author Davis
 *
 */

public class ReferralItem extends GenericItem {
	
	private List<Field> fields;
	

	/**
	 * Creates a new Referral Item from the JSON object passed to it
	 * @param c - indicates in what category the item will be stored
	 * @param values - the JSONObject representing the data to be stored in this
	 * @param ctx - the context of the DB where the item will be manipulated.
	 */
	protected ReferralItem(Category c, JSONObject values, Context ctx) {
		super(c, ctx);
		try {
			DBtoData(values);
		} catch (JSONException e) {
			throw new IllegalArgumentException("JSONObject Improperlly formatted!");
		}
		this.fields = new LinkedList<Field>(this.values.keySet());
	}
	@Override
	public List<Field> getFields(){
		return this.fields;
	}
	

}
