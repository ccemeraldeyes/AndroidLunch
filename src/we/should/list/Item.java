package we.should.list;

import java.util.Map;
import java.util.Set;

public abstract class Item {
	
	public abstract Set<Address> getAddresses();
	
	public abstract String getComment();
	
	public abstract void delete();
	
	public abstract String get(String key);
	
	public abstract String getName();
	
	public abstract Map<String, String> getFields();
	
	public abstract String getPhoneNo();
	
	public abstract void set(String key, String value);
	
	public abstract void save();
	
	public abstract Set<String> getTags();
	
}