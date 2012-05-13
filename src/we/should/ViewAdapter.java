package we.should;

import java.util.List;
import java.util.Map;

import we.should.list.Field;
import we.should.list.FieldType;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * ViewAdapters handle displaying the generic fields for ViewScreen.
 * 
 * @author Will
 */

public class ViewAdapter extends ArrayAdapter<Field> {
	
	private LayoutInflater mInflater;
	
	private List<Field> mFields;
	
	private Map<Field, String> mData;

	public ViewAdapter(Context context, List<Field> fields, Map<Field, String> data) {
		super(context, R.layout.edit_row_textline);
		mInflater = ((Activity) context).getLayoutInflater();
		mFields = fields;
		mData = data;
	}
	
	@Override
	public int getItemViewType(int position) {
		return mFields.get(position).getType().ordinal();
	}
	
	@Override
	public int getViewTypeCount() {
		return FieldType.size;
	}
	
	@Override
	public int getCount() {
		return mFields.size();
	}
	
	@Override
	public Field getItem(int position) {
		return mFields.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return false;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);
		FieldType enumType = FieldType.get(type);
		
		if (convertView == null) {
			holder = new ViewHolder();
			switch (enumType) {
			case TextField:
			case MultilineTextField:
			case PhoneNumber:
				convertView = mInflater.inflate(R.layout.view_row_text, null);
				break;
			case Rating:
				convertView = mInflater.inflate(R.layout.view_row_rating, null);
				break;
			default:
				throw new IllegalStateException("Do not know how to handle enum" + enumType);
			}
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.value = convertView.findViewById(R.id.value);
			loadData(mFields.get(position), holder.value);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(mFields.get(position).getName());
		return convertView;
	}
	
	private void loadData(final Field field, View view) {
		switch (field.getType()) {
		case TextField:
		case MultilineTextField:
		case PhoneNumber:
			((TextView) view).setText(mData.get(field));
			break;
		case Rating:
			if (mData.get(field).equals("")) {
				((RatingBar) view).setRating(0.0f);
			} else {
				((RatingBar) view).setRating(Float.parseFloat(mData.get(field)));
			}
			break;
		}
	}
	
	private static class ViewHolder {
		public TextView name;
		public View value;
	}
}