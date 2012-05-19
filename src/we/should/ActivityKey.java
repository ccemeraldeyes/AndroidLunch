package we.should;

/** Activity keys. **/
public enum ActivityKey {
	NEW_CAT,
	NEW_ITEM,
	VIEW_ITEM,
	EDIT_ITEM,
	NEW_REFERRAL,
	SET_TAGS,
	REFER;
	
	public static ActivityKey get(int i) {
		return ActivityKey.values()[i];
	}
}