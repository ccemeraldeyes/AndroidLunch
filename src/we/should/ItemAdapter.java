package we.should;

import java.util.List;

import we.should.list.Item;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemAdapter extends ArrayAdapter<Item> {
	
	private int mLayoutResourceId;
	
	private Context mContext;

	public ItemAdapter(Context context, List<Item> data) {
		super(context, R.layout.item_row, data);
		mLayoutResourceId = R.layout.item_row;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ItemRow itemRow = null;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
			
			itemRow = new ItemRow();
			itemRow.name = (TextView) row.findViewById(R.id.name);
			itemRow.comment = (TextView) row.findViewById(R.id.comment);
			row.setTag(itemRow);
		} else {
			itemRow = (ItemRow) row.getTag();
		}
		
		Item item = getItem(position);
		itemRow.name.setText(item.getName());
		itemRow.comment.setText(item.getComment());
		
		return row;
	}
	
	private static class ItemRow {
		TextView name;
		TextView comment;
	}
}
