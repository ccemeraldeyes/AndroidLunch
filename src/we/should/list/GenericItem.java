package we.should.list;

import java.util.*;



public class GenericItem extends Item {
	private final Category c;
	private Map<Field, String> values;
	private Set<android.location.Address> addresses;
	private boolean added = false;
	
	
	
	public GenericItem(Category c) {
		this.c = c;
		values = new HashMap<Field, String>();
		List<Field> fields = c.getFields();
		for(Field i : fields){
			values.put(i, null);
		}
		for(Field i : Field.values()) {
			if(! values.containsKey(i.toString())){
				values.put(i, null);
			}
		}
	}

	@Override
	public Set<android.location.Address> getAddresses() {
		return Collections.unmodifiableSet(addresses);
	}

	@Override
	public String getComment() {
		return values.get(Field.Comment); 
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
			throw new IllegalArgumentException(key.toString() + " is not a field of the " + c.name + " category.");
		}
	}

	@Override
	public String getName() {
		return values.get(Field.Name);
	}

	@Override
	public String getPhoneNo() {
		return values.get(Field.PhoneNumber);
	}
	
	@Override
	public void set(Field key, String value) throws IllegalArgumentException {
		if(c.getFields().contains(key)){
			values.put(key, value);
		} else {
			throw new IllegalArgumentException(key.toString() + " is not a field of the " + c.name + " category.");
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
