package we.should.list;

import java.io.IOException;
import java.util.*;

import we.should.Splash;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;





public class GenericItem extends Item {
	private final Category c;
	private Map<Field, String> values;
	private boolean added = false;
	
	
	
	protected GenericItem(Category c) {
		this.c = c;
		values = new HashMap<Field, String>();
		List<Field> fields = c.getFields();
		for(Field i : fields){
			values.put(i, null);
		}
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
	}
	/**
	 * Adds this to the category object that produced it.
	 * Will only add itself once even after multiple calls will 
	 */
	@Override
	public void save() {
		if(!added) {
			c.addItem(this);
			added = true;
		}

	}

	@Override
	public Set<String> getTags() {
		// TODO Auto-generated method stub
		return null;
	}

}
