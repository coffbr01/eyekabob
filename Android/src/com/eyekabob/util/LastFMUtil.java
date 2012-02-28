package com.eyekabob.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.DOMException;

public class LastFMUtil {
	public static String toReadableDate(String unparsedDate) {
		String result = unparsedDate;
		try {
			DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
			Date d = df.parse(unparsedDate);
			DateFormat readableDF = new SimpleDateFormat("EEE, MMM d, h:mm a");
			result = readableDF.format(d);
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
}
