package we.should.communication;

import java.util.List;

import we.should.R;
import we.should.list.Item;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ReferralAdapter extends ArrayAdapter<Referral> {
	
	private static int mLayoutResourceId = R.layout.referral_item;
	
	private Context mContext;
	
	private List<Referral> mData;

	public ReferralAdapter(Context context, List<Referral> data) {
		super(context, mLayoutResourceId, data);
		mContext = context;
		mData = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
		
		Referral referral = mData.get(position);
		referralRow.name.setText(referral.getName());
		referralRow.sender.setText(referral.getSender());
		referralRow.approved.setChecked(referral.isApproved());
		
		return row;
	}
	
	private static class ReferralRow {
		TextView name;
		TextView sender;
		CheckBox approved;
	}
}
