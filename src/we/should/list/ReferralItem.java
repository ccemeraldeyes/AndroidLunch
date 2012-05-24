package we.should.list;

import java.util.Collections;
import java.util.Iterator;
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
		if(values == null){
			throw new IllegalArgumentException("Values is null!");
		}
		try {
			DBtoData(values);
		} catch (JSONException e) {
			throw new IllegalArgumentException("JSONObject Improperlly formatted!");
		}
	}
	@Override
	public List<Field> getFields(){
		List<Field> out = new LinkedList<Field>(this.values.keySet());
		out.remove(Field.TAGS); //Remove special field
		return out;
	}
	@SuppressWarnings("unchecked")
	protected void DBtoData(JSONObject d) throws JSONException{
		this.fields = new LinkedList<Field>();
		super.DBtoData(d);
		Iterator<String> keys = d.keys();
		String k;
		while(keys.hasNext()){
			k = keys.next();
			fields.add(new Field(k));
		}
		Collections.sort(fields);
	}
}
