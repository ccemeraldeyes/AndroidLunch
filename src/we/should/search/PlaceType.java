package we.should.search;
/**
 * PlaceType is a list of class that we are searching for in our query.
 * currently it is set to University, Restaurant, movie_rental, movie_theater, cafe, bar
 * The type detail can be find at https://developers.google.com/maps/documentation/places/supported_types
 * 
 * @author Lawrence
 */
public enum PlaceType {
	UNIVERSITY, RESTAURANT, MOVIE_RENTAL, MOVIE_THEATER, CAFE, BAR, INVALID;
	
	//Take in a string to construct a PlaceType
	public static PlaceType createPlaceType(String type) {
		if(type == null) {
			return INVALID;
		} else if(type.toUpperCase().equals(UNIVERSITY.toString())) {
			return UNIVERSITY;
		} else if(type.toUpperCase().equals(RESTAURANT.toString())) {
			return RESTAURANT;
		} else if(type.toUpperCase().equals(MOVIE_RENTAL.toString())) {
			return MOVIE_RENTAL;
		}else if(type.toUpperCase().equals(MOVIE_THEATER.toString())) {
			return MOVIE_THEATER;
		}else if(type.toUpperCase().equals(CAFE.toString())) {
			return CAFE;
		}else if(type.toUpperCase().equals(BAR.toString())) {
			return BAR;
		} else {
			return INVALID;
		}
	}
}
