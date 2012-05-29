package we.should.communication;

import java.util.ArrayList;
import java.util.List;

import we.should.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ReferralAdapter extends ArrayAdapter<Referral> {
	
	/** The layout to use for displaying each Referral. **/
	private static int mLayoutResourceId = R.layout.referral_item;
	
	/** A context to use for inflating the layout. **/
	private Context mContext;

	/**
	 * ReferralAdapter constructor
	 * 
	 * @param context the context to use for inflating the layout
	 * @param data the list of Referral objects to be approved
	 */
	public ReferralAdapter(Context context, List<Referral> data) {
		super(context, mLayoutResourceId, new ArrayList<Referral>(data));
		mContext = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ReferralRow referralRow = null;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
			
			referralRow = new ReferralRow();
			referralRow.name = (TextView) row.findViewById(R.id.name);
			referralRow.sender = (TextView) row.findViewById(R.id.sender);
			referralRow.approved = (CheckBox) row.findViewById(R.id.approved);
			row.setTag(referralRow);
		} else {
			referralRow = (ReferralRow) row.getTag();
		}
		
		Referral referral = getItem(position);
		referralRow.name.setText(referral.getName());
		referralRow.sender.setText(referral.getSender());
		referralRow.approved.setChecked(referral.isApproved());
		
		referralRow.approved.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				getItem(position).setApproved(isChecked);
			}
			
		});
		
		return row;
	}
	
	/**
	 * Represents a row in the list of referrals to be approved
	 * 
	 * @author colleen
	 *
	 */
	private static class ReferralRow {
		TextView name;
		TextView sender;
		CheckBox approved;
	}

	/**
	 * Returns a list of all referrals that are approved.
	 * 
	 * @return a list of all approved referrals
	 */
	public List<Referral> getApprovedList() {
		List<Referral> list = new ArrayList<Referral>();
		for (int i = 0; i < getCount(); i++) {
			Referral r = getItem(i);
			if (r.isApproved()) {
				list.add(r);
			}
		}
		return list;
	}
}
