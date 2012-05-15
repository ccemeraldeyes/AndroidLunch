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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import we.should.database.WSdb;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public abstract class Item {
		
	int id;
	Context ctx;
	Map<Field, String> values;
	boolean added = false;

	
	protected Item(Context ctx){
		this.ctx = ctx;
	}
	/**
	 * @return a set of Address objects corresponding to the location(s) of this.
	 * @throws IOException 
	 */
	public abstract Set<android.location.Address> getAddresses();
	
	/**
	 * Adds an address to this item. Processes the string and translates
	 * to a lat and long.
	 * @param add string representation of an address
	 * @return true if lookup is successful, false otherwise
	 * @modifies this.addresses if lookup is successful
	 */
	public abstract boolean addAddress(String add);
	
	/**
	 * Adds a pre-validated address to this
	 * @param add Address object representation of an address
	 * @return add.hasLatitude && add.hasLongitude
	 * @modifies this.addresses if add.hasLatitude && add.hasLongitude
	 */
	public abstract boolean addAddress(android.location.Address add);
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
	 * Returns the category of this item
	 * @return the category object associated with this item.
	 */
	public abstract Category getCategory();
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
	 * @param key - FieldObject that you wish to alter
	 * @param value - the value that will be assigned to the given field.
	 * @exception IllegalArgumentException
	 */
	public abstract void set(Field key, String value);
	
	/**
	 * Adds this item to the category factory that created it, and saves to the database. 
	 * If this item was not produced from a category factory this will throw an IllegalStateException.
	 * @modifies this.C
	 * @throws IllegalStateException
	 */
	public abstract void save();
	
	/**
	 * Returns the set of tags assigned to this item
	 * @return A Set of tag strings.
	 */
	public abstract Set<Tag> getTags();
	
	/**
	 * Adds a tag string to this item. If s matches an
	 * existing tag, it will not be added
	 * @param s is the tag to be added
	 */
	public abstract void addTag(String s);
	
	/**
	 * Returns the set of Items that have the given tag
	 * @param tag object to search for
	 * @param ctx of the database
	 * @return a set of all Items I s.t for all i in I, i.getTags().contains(tag). 
	 */
	public static Set<Item> getItemsOfTag(Tag tag, Context ctx){
		if(ctx == null){
			throw new IllegalArgumentException("Context cannot be null!");
		}
		Set<Item> out = new HashSet<Item>();
		Map<Integer, Category> cats = new HashMap<Integer, Category>();
		WSdb db = new WSdb(ctx);
		db.open();
		Cursor items = db.getItemsOfTag(tag.getId());
		Category cat;
		Item i;
		while(items.moveToNext()){
			int id = items.getInt(0);
			int catId = items.getInt(2);
			
			if (!cats.containsKey(catId)) {
				Cursor c = db.getCategory(catId);
				c.moveToNext();
				String name = c.getString(1);
				String color = c.getString(2);
				String schema = c.getString(3);
				if (name.equals("Movies")) {
					cat = new Movies(ctx);
				} else {
					JSONArray schemaList;
					try {
						schemaList = new JSONArray(schema);
						cat = new GenericCategory(name, schemaList, ctx);
					} catch (JSONException e) {
						Log.e("Category.getCategories",
								"Field Schema improperly formatted!", e);
						return null;
					}
				}
				cat.id = catId;
				cat.color = color;
				cats.put(catId, cat);
				c.close(); // TS
			} else {
				cat = cats.get(catId);
			}
			if (cat.getName().equals("Movies")) {
				i = new MovieItem(cat, ctx);
			} else {
				i = new GenericItem(cat, ctx);
			}
			i.setID(id);
			JSONObject data;
			try { 
				data = new JSONObject(items.getString(3));
				i.DBtoData(data);
			} catch (JSONException e) { 
				Log.e("Item.getItemsOfTag","Data string not properly formatted in DB!");
			}
			cat.addItem(i);
			i.added = true;
			out.add(i);
		}
		items.close(); // TS
		return out;
	}
	/**
	 * Sets the id of this item for DB lookup.
	 * Should be set to the return value of a DB insert
	 * call
	 * @param i the row value in the database
	 */
	protected void setID(int i){
		this.id = i;
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
}