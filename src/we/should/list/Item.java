/*
 * Copyright (C) 2012 The Android Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * 
 * The Item Class represents a place or thing to do within the WeShould app. Items may contain any amount of
 * Category specified data in the form of the fields available. Items are contained within category objects and 
 * cannot be created by objects outside of the we.should.list package. 
 */
package we.should.list;

import java.util.Set;

public abstract class Item {
	
	/**
	 * @return a set of Address objects corresponding to the location(s) of this.
	 */
	public abstract Set<android.location.Address> getAddresses();
	
	/**
	 * 
	 * @return the comment field of this. If the comment has not been
	 * set with set(Field.COMMENT, "comment"), returns null.
	 */
	public abstract String getComment();
	
	/**
	 * Removes this from the Category object that this was constructed with.
	 * @modifies this.C
	 */
	public abstract void delete();
	
	/**
	 * Returns the value contained in the given field, only if the given field is
	 * part of this items category, otherwise throws illegal argument exception.
	 * @param key 
	 * @return The value contained in the given Field
	 * @exception IllegalArgumentException
	 */
	public abstract String get(Field key);
	
	/**
	 * Returns the name of this item. If the name has not been set with
	 * set(Field.NAME, "name") then returns null.
	 * @return this.name
	 */
	public abstract String getName();
	
	/**
	 * Returns the phone number associated with this item. If it has not been set with
	 * set(Field.PHONENUMBER, "#") then returns null.
	 * @return this.phonenumber
	 */
	public abstract String getPhoneNo();
	
	/**
	 * Sets the value contained in the Field, key. If key is not contained in this's 
	 * category then throws IllegalArgumentException
	 * @param key-FieldObject that you wish to alter
	 * @param value - the value that will be assigned to the given field.
	 * @exception IllegalArgumentException
	 */
	public abstract void set(Field key, String value);
	
	/**
	 * Adds this item to the category factory that created it.
	 * @modifies this.C
	 */
	public abstract void save();
	
	/**
	 * Returns the set of tags assigned to this item
	 * @return A Set of tag strings.
	 */
	public abstract Set<String> getTags();
	
}