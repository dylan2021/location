package com.sfmap.adcode;

import java.util.Comparator;
import java.util.Locale;

public class AdCityComparator implements Comparator<AdCity> {

    public int compare(AdCity city1, AdCity city2) {
	int pyLength = city1.getInitial().length() > city2.getInitial()
		.length() ? city2.getInitial().length() : city1.getInitial()
		.length();
	char c1, c2;
	for (int i = 0; i < pyLength; i++) {
	    String jp1 = city1.getInitial().toLowerCase(Locale.getDefault());
	    String jp2 = city2.getInitial().toLowerCase(Locale.getDefault());
	    c1 = jp1.charAt(i);
	    c2 = jp2.charAt(i);

	    int dx = String.valueOf(c1).compareTo((String.valueOf(c2)));
	    if (dx != 0)
		return dx;
	}
	return 0;
    }

}
