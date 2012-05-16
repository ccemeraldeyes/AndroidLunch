package we.should;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import we.should.list.Field;
import we.should.list.FieldType;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * EditAdapter adapts a field into an editable view for use in the edit screen.
 * 
 * @author Will
 */

public class EditAdapter extends ArrayAdapter<Field> {
	
	private LayoutInflater mInflater;
	
	private List<Field> mFields;
	
	private Map<Field, String> mData;

	public EditAdapter(Context context, List<Field> fields, Map<Field, String> data) {
		super(context, R.layout.edit_row_textline, new ArrayList<Field>(fields));
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);
		FieldType enumType = FieldType.get(type);
		
		if (convertView == null) {
			holder = new ViewHolder();
			switch (enumType) {
			case TextField:
				convertView = mInflater.inflate(R.layout.edit_row_textline, null);
				break;
			case MultilineTextField:
				convertView = mInflater.inflate(R.layout.edit_row_multiline, null);
				break;
			case Rating:
				convertView = mInflater.inflate(R.layout.edit_row_rating, null);
				break;
			case PhoneNumber:
				convertView = mInflater.inflate(R.layout.edit_row_phone, null);
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
			TextView tv = (TextView) view;
			tv.setText(mData.get(field));
			tv.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {
					mData.put(field, s.toString());
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {}

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {}
				
			});
			break;
		case Rating:
			RatingBar rb = (RatingBar) view;
			if (mData.get(field).equals("")) {
				rb.setRating(0.0f);
			} else {
				rb.setRating(Float.parseFloat(mData.get(field)));
			}
			rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) {
					mData.put(field, ((RatingBar) ratingBar).getRating() + "");
				}
				
			});
			break;
		}
	}
	
	private static class ViewHolder {
		public TextView name;
		public View value;
	}
}