package we.should;

import java.util.ArrayList;
import java.util.List;

import we.should.search.Place;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class PlaceAdapter extends ArrayAdapter<Place> {
	
	/** The context. **/
	private Context mContext;
	
	/** The layout. **/
	private static final int sLayoutResourceId = R.layout.place_row;
	
	/** The places. **/
	private List<Place> mPlaces;

	public PlaceAdapter(Context context, List<Place> places) {
		super(context, sLayoutResourceId, new ArrayList<Place>(places));
		mContext = context;
		mPlaces = places;
	}
	//D.S
	public Place getItem(int position){
		return mPlaces.get(position);
	}
	
	@Override
	public Filter getFilter() {
		return new PlaceFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		PlaceRow placeRow = null;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(sLayoutResourceId, parent, false);
			
			placeRow = new PlaceRow();
			placeRow.name = (TextView) row.findViewById(R.id.name);
			placeRow.vicinity = (TextView) row.findViewById(R.id.vicinity);
			row.setTag(placeRow);
		} else {
			placeRow = (PlaceRow) row.getTag();
		}
		
		Place place = mPlaces.get(position);
		placeRow.name.setText(place.getName());
		placeRow.vicinity.setText(place.getVicinity());
		return row;
	}
	
	private static class PlaceRow {
		TextView name;
		TextView vicinity;
	}
	
	private class PlaceFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();
			List<Place> list = new ArrayList<Place>();
			if (constraint == null) {
				filterResults.values = mPlaces;
				filterResults.count = mPlaces.size();
				return filterResults;
			}
			String query = constraint.toString().toLowerCase();
			for (Place p : mPlaces) {
				if (p.getName().toLowerCase().contains(query)) {
					list.add(p);
				}
			}
			filterResults.values = list;
			filterResults.count = list.size();
			return filterResults;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			clear();
			@SuppressWarnings("unchecked")
			List<Place> list = (List<Place>) results.values;
			for (Place p : list) {
				add(p);
			}
			notifyDataSetChanged();
		}
		
	}

}
