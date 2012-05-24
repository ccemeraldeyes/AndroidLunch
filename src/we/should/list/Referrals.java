package we.should.list;

import java.util.LinkedList;

import org.json.JSONObject;

import android.content.Context;

public class Referrals extends GenericCategory {
	
	public Referrals(Context ctx){
		super(Category.Special.Referrals.toString(), new LinkedList<Field>(), ctx);
	}
	public ReferralItem newItem(JSONObject o){
		return new ReferralItem(this, o, ctx);
	}
	
	@Override
	public Item newItem(){
		ReferralItem out = newItem(new JSONObject());
		return out;
	}
	public static Referrals getReferralCategory(Context ctx){
		Referrals r = (Referrals) Category.getCategory(Category.Special.Referrals.toString(), ctx);
		if(r==null) throw new IllegalStateException("There is no Referrals category!");
		return r;
	}
}
