package we.should.list;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GenericItem extends Item {
	private final Category c;
	private Map<Field, String> values;
	private Set<android.location.Address> addresses;
	
	
	
	public GenericItem(Category c) {
		this.c = c;
		values = new HashMap<Field, String>();
		Set<Field> fields = c.getFields();
		for(Field i : fields){
			values.put(i, null);
		}
		for(Field i : Field.values()) {
			if(! values.containsKey(i.key())){
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
		return values.get(Field.Comment.key()); 
	}

	@Override
	public void delete() {
		c.removeItem(this);
	}

	@Override
	public String get(String key) {
		return values.get(key);
	}

	@Override
	public String getName() {
		return values.get(Field.Name.key());
	}

	@Override
	public String getPhoneNo() {
		return values.get(Field.PhoneNumber.key());
	}

	@Override
	public void set(Field key, String value) {
		values.put(key, value);
	}

	@Override
	public void save() {
		c.addItem(this);
	}

	@Override
	public Set<String> getTags() {
		// TODO Auto-generated method stub
		return null;
	}

}
