package we.should.list;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;

public class Referrals extends GenericCategory {
	
	public Referrals(Context ctx){
		super(Category.Special.Referrals.toString(), new LinkedList<Field>(), ctx);
	}
	public Item newItem(JSONObject o){
		return new ReferralItem(this, o, ctx);
	}
}
