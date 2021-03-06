package we.should.list;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

/**
 * 
 * @author Davis
 * This class represents an entry in a user's we should list. It contains all 
 * of the user entered metadata regarding a location or activity, and is linked
 * to the Category object that created it.  The fields of this are limited to the
 * fields defined in the Category factory that created it.
 * 
 * Representation Invariants:
 * 		
 * 		added <---> c.contains(this)
 * 		
 * 		values.keys() == c.fields
 * 
 */

public class GenericItem extends Item {
	public static final Locale DEFAULT_LOCALE = Locale.US; 
		
	protected GenericItem(Category c, Context ctx) {
		super(c, ctx);
		List<Field> fields = this.getFields();
		for(Field i : fields){
			if (i.getType().equals(FieldType.CheckBox)) {
				values.put(i, "false");
			} else {
				values.put(i, "");
			}
		}
		checkRep();
	}
	/**
	 * asserts that the representation invariant is held.
	 */
	private void checkRep(){
		if (c!=null) {
			if (added)
				assert (c.getItems().contains(this));
			else
				assert (!c.getItems().contains(this));
			assert (new HashSet<Field>(this.getFields()).equals(values.keySet()));
		}
	}
	@Override
	public Set<Address> getAddresses() {
		Set<Address> out = new HashSet<Address>();
		String address = this.values.get(Field.ADDRESS);
		if (address == null) return out; //return empty set;
		Address a = JSONToAddress(address);
		out.add(a);
		return out;

	}
	private Address getGeoData(String address){
		boolean err = (ctx == null);
		List<Address> out = new LinkedList<Address>();
		if (address != null) {
			if (!err) {
				Geocoder g = new Geocoder(ctx, DEFAULT_LOCALE);
				try {
					out = g.getFromLocationName(address, 1);
				} catch (IOException e) {
					Log.w("GenericItem.getAdresses",
							"Server error. Could not fetch geo data.");
					err = true;
				}
			}
			if (err) {
				Address a = new Address(DEFAULT_LOCALE);
				a.setAddressLine(0, address);
				out.add(a);
				Log.w("GenericItem.getAdresses",
						"Context is null, so no geo data can be loaded.");
			}
			if(out.size() > 0) return out.get(0);
		}
		return new Address(DEFAULT_LOCALE);
		
	}
	@Override
	public String getComment() {
		return values.get(Field.COMMENT); 
	}

	@Override
	public String get(Field key) throws IllegalArgumentException{
		if(key.equals(Field.TAGS)){
			throw new IllegalArgumentException("Use the getTags() method to access the tags of this.");
		}
		if(getFields().contains(key)) {
			if(key.equals(Field.ADDRESS)){
				try {
					JSONObject o = new JSONObject(values.get(key));
					return o.getString("address");
				} catch (JSONException e) {
					Log.w("GenericItem.get(Field.ADDRESS)", "Address String improperly formed!");
					return "";
				}
			} else {
				return values.get(key);
			}
		} else {
			Log.v("GenericItem.get", this.getName() + " - fields: " + getFields());
			throw new IllegalArgumentException(key.toString() + " is not a field of the " + c.getName() + " category.");
		}
	}

	@Override
	public String getName() {
		return values.get(Field.NAME);
	}

	@Override
	public String getPhoneNo() {
		return values.get(Field.PHONENUMBER);
	}
	
	@Override
	public void set(Field key, String value) throws IllegalArgumentException {
		if(key.equals(Field.TAGS)){
			throw new IllegalArgumentException(Field.TAGS + " cannot be set with this method!");
		}
		if(this.getFields().contains(key)){
			if(key.equals(Field.ADDRESS)){
				Address add = getGeoData(value);
				String newValue = addressToJSON(add, value).toString();
				values.put(key, newValue);
			} else {
				values.put(key, value);
			}
		} else {
			throw new IllegalArgumentException(key.toString() + " is not a field of the " + c.getName() + " category.");
		}
		checkRep();
	}

	@Override
	public Set<Tag> getTags(){
		String tags = this.values.get(Field.TAGS);
		if(tags == null) tags = "";
		Set<Tag> result = new HashSet<Tag>();
		try {
			JSONArray out = new JSONArray(tags);
			for(int i = 0; i < out.length(); i++){
				JSONObject tagString = out.getJSONObject(i);
				result.add(new Tag(tagString));
			}
		} catch (JSONException e) {
			Log.e("GenericItem.getTags", "Tags string improperly formatted, returning empty set!");
		}
		return result;
	}

	private JSONObject addressToJSON(Address a, String addressString){
		JSONObject out = new JSONObject();
		try{
			out.put("address", addressString);
			try{
				out.put("lat", a.getLatitude());
				out.put("long", a.getLongitude());
			} catch (IllegalStateException e){
				out.put("lat", false);
				out.put("long", false);
			}
		} catch (JSONException je){
			throw new IllegalArgumentException("Address improperly formatted: " + addressString); 
		}
		return out;
	}
	private Address JSONToAddress(String add){
		Address out = new Address(DEFAULT_LOCALE);
		JSONObject o;
		try {
			o = new JSONObject(add);
		} catch (JSONException e1) {
			Log.w("GenericItem.JSONToAddress","Address String Improperly formed.");
			return out;
		}
		try{
			out.setAddressLine(0, o.getString("address"));
			if(o.getDouble("lat") != 0){
				out.setLatitude(o.getDouble("lat"));
				out.setLongitude(o.getDouble("long"));
			}
		} catch(JSONException e){}
		return out;
		
	}
	@Override
	public Category getCategory() {
		return this.c;
	}
	public boolean equals(Object other){
		if(other == this) return true;
		if(other == null || !(other instanceof GenericItem)) return false;
		GenericItem cp= GenericItem.class.cast(other);
		return this.values.equals(cp.values);
	}
	public String toString(){
		return this.getName();
	}
	@Override
	public List<Field> getFields() {
		return this.c.getFields();
	}

}