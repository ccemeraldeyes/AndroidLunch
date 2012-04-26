package we.should.list;

import java.util.Set;

public abstract class Item {
	
	public abstract Set<android.location.Address> getAddresses();
	
	public abstract String getComment();
	
	public abstract void delete();
	
	public abstract String get(String key);
	
	public abstract String getName();
		
	public abstract String getPhoneNo();
	
	public abstract void set(Field key, String value);
	
	public abstract void save();
	
	public abstract Set<String> getTags();
	
}