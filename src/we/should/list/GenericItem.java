package we.should.list;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import we.should.Splash;
import we.should.database.WSdb;


import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;

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
	private final Category c;
	private Context ctx;
	private int id;
	private Map<Field, String> values;
	private boolean added = false;
	
	
	
	protected GenericItem(Category c) {
		this.c = c;
		values = new HashMap<Field, String>();
		List<Field> fields = c.getFields();
		for(Field i : fields){
			values.put(i, null);
		}
		checkRep();
	}
	/**
	 * asserts that the representation invariant is held.
	 */
	private void checkRep(){
		if(added) assert(c.getItems().contains(this));
		else assert(!c.getItems().contains(this));
		assert(new HashSet<Field>(c.getFields()).equals(values.keySet()));
	}
	@Override
	public Set<Address> getAddresses(Context c) throws IOException {
		Geocoder g = new Geocoder(c, Locale.ENGLISH);
		String address = this.get(Field.ADDRESS);
		List<Address> out =  g.getFromLocationName(address, 1);
		return new HashSet<Address>(out);
	}

	@Override
	public String getComment() {
		return values.get(Field.COMMENT); 
	}

	@Override
	public void delete() {
		c.removeItem(this);
	}

	@Override
	public String get(Field key) throws IllegalArgumentException{
		if(c.getFields().contains(key)) {
			return values.get(key);
		} else {
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
		if(c.getFields().contains(key)){
			values.put(key, value);
		} else {
			throw new IllegalArgumentException(key.toString() + " is not a field of the " + c.getName() + " category.");
		}
		checkRep();

	}
	
	@Override
	public void save(Context ctx) {
		checkRep();
		if(!added) {
			this.c.addItem(this);
			this.added = true;
		if(this.ctx == null) this.ctx = ctx;
		if(this.ctx != null){
			WSdb db = new WSdb(this.ctx);
			db.open();
			if (this.id==0) {
				//this.id=
				this.id = (int) db.insertItem(this.getName(), this.c.id, false, dataToDB()
						.toString());
			} else {
				//update
			}
			db.close();
		}
		}
		checkRep();
	}

	@Override
	public Set<String> getTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Category getCategory() {
		return this.c;
	}
	
	private JSONObject dataToDB(){
		JSONObject out = new JSONObject();
		for(Entry<Field, String> e : values.entrySet()){
			try {
				out.put(e.getKey().toDB(), e.getValue());
			} catch (JSONException err) {
				err.printStackTrace();
			}
		}
		return out;
	}
	/**
	 * Restores the values held in this item from a JSONObject DB entry
	 * @param d
	 * @throws JSONException
	 * @modifies this.values
	 */
	protected void DBtoData(JSONObject d) throws JSONException{
		@SuppressWarnings("unchecked")
		Iterator<String> i = d.keys();
		while(i.hasNext()){
			String fieldString = i.next();
			String value = d.getString(fieldString);
			Field f = new Field(fieldString);
			this.values.put(f, value);
		}
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

}
