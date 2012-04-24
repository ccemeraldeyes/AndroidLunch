package we.should.list;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GenericItem extends Item {
	private final Category c;
	private Map<String, String> values;
	private String name;
	
	
	public GenericItem(Category c) {
		this.c = c;
		values = new HashMap<String, String>();
		Map<String, FieldType> fields = c.getFields();
		for(Map.Entry<String, FieldType> i : fields.entrySet()){
			String key = i.getKey().toString() + "," + i.getValue().toString();
			values.put(key, null);
		}
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<Address> getAddresses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getComment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete() {
		// TODO 

	}

	@Override
	public String get(String key) {
		return values.get(key);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Map<String, String> getFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPhoneNo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(String key, String value) {
		// TODO Auto-generated method stub

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
