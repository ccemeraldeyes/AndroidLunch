package we.should;

import java.util.List;
import java.util.Map;

import we.should.list.Field;
import we.should.list.FieldType;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ViewAdapters handle displaying the generic fields for ViewScreen.
 * 
 * @author Will
 */

public class ViewAdapter extends ArrayAdapter<Field> {
	
	/** The context to use for our actionable views. **/
	private Context mContext;
	
	/** The inflater to use. **/
	private LayoutInflater mInflater;
	
	/** The fields contained in this Item. **/
	private List<Field> mFields;
	
	/** The raw data of the item. **/
	private Map<Field, String> mData;

	public ViewAdapter(Context context, List<Field> fields, Map<Field, String> data) {
		super(context, R.layout.edit_row_textline);
		mContext = context;
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
		Field field = mFields.get(position);
		ViewHolder holder = null;
		//int type = getItemViewType(position);
		FieldType enumType = field.getType();
		
		if (convertView == null) {
			holder = new ViewHolder();
			switch (enumType) {
			case CheckBox:
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
			loadData(field, holder.value);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(field.getName());
		
		final ViewHolder finalHolder = holder;
		if (field.equals(Field.PHONENUMBER)) {
			((TextView) finalHolder.value).setTextColor(Color.BLUE);
			convertView.setOnClickListener(new View.OnClickListener() {
				/** Call the phone number. **/
				public void onClick(View arg0) {
					String phoneNumber = "tel:" + ((TextView) finalHolder.value).getText();
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(phoneNumber));
					mContext.startActivity(intent);
				}
			});
		} else if (field.equals(Field.WEBSITE)) {
			((TextView) finalHolder.value).setTextColor(Color.BLUE);
			convertView.setOnClickListener(new View.OnClickListener() {
				/** Go to the web site. **/
				public void onClick(View v) {
					String url = ((TextView) finalHolder.value).getText().toString();
					if (!URLUtil.isValidUrl(url)) {
						Toast.makeText(mContext, url + " is not a valid URL",
								Toast.LENGTH_LONG).show();
						return;
					}
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					mContext.startActivity(intent);
				}
			});
		}
		return convertView;
	}
	
	private void loadData(final Field field, View view) {
		switch (field.getType()) {
		case CheckBox:
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