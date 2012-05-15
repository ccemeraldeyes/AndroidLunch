package we.should.list;

import java.util.List;

import android.content.Context;


public class ReferralItem extends GenericItem {
	
	private List<Field> fields;
	
	protected ReferralItem(Category c, List<Field> fields, Context ctx) {
		super(c, ctx);
		this.fields = fields;
	}
	@Override
	public List<Field> getFields(){
		return this.fields;
	}
	

}
