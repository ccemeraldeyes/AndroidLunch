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
			placeRow.detail = (TextView) row.findViewById(R.id.detail);
			row.setTag(placeRow);
		} else {
			placeRow = (PlaceRow) row.getTag();
		}
		
		Place place = mPlaces.get(position);
		placeRow.name.setText(place.getName());
		placeRow.detail.setText(place.getDetail());
		return row;
	}
	
	private static class PlaceRow {
		TextView name;
		TextView detail;
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
			String query = constraint.toString().toLowerCase().substring(0, constraint.length() - 2);//Looser filtering
			for (Place p : mPlaces) {
				String name = p.getName().toLowerCase();
				if (filter(name, query) || filter(query, name) || name.contains(query) || query.contains(name)) {
					list.add(p);
				}
			}
			filterResults.values = list;
			filterResults.count = list.size();
			return filterResults;
		}
		private boolean filter(String name, String query){
			boolean out = false;
			String[] qS = query.split(" ");
			for(String s : qS){
				out = name.contains(s);
				if(out) return out;
			}
			return out;
		}
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			clear();
			@SuppressWarnings("unchecked")
			List<Place> list = (List<Place>) results.values;
			if(list == null) return;
			for (Place p : list) {
				add(p);
			}
			notifyDataSetChanged();
		}
		
	}

}
