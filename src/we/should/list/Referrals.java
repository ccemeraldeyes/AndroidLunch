package we.should.list;

import java.util.LinkedList;
import java.util.Set;

import org.json.JSONObject;

import android.content.Context;

public class Referrals extends GenericCategory {
	
	public Referrals(Context ctx){
		super(Category.Special.Referrals.toString(), new LinkedList<Field>(), ctx);
	}
	public Item newItem(JSONObject o){
		return new ReferralItem(this, o, ctx);
	}
	
	public static Referrals getReferralCategory(Context ctx){
		Referrals r;
		Set<Category> cats = Category.getCategories(ctx);
		for(Category c : cats){
			if(c.getName().equals(Category.Special.Referrals.toString())) {
				r = (Referrals) c;
				return r;
			}
			
		}
		throw new IllegalStateException("There is no Referrals category!");
	}
}
