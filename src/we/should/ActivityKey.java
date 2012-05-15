package we.should;

/** Activity keys. **/
public enum ActivityKey {
	NEW_CAT,
	NEW_ITEM,
	VIEW_ITEM,
	EDIT_ITEM,
	REFER,
	NEW_REFERRAL;
	
	public static ActivityKey get(int i) {
		return ActivityKey.values()[i];
	}
}