package we.should.list;

import java.util.List;

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
	
	
	public ReferralItem(Category c, List<Field> fields, Context ctx) {
		super(c, ctx);
		this.fields = fields;
	}
	@Override
	public List<Field> getFields(){
		return this.fields;
	}
	

}
