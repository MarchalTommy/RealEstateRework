package com.aki.realestatemanagerv2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

public class UtilsTest {

    @Test
    public void convertDollarToEuro() {
        int dollar = 1000;
        int euro = Utils.convertDollarToEuro(dollar);

        assertEquals(812, euro);
    }

    @Test
    public void convertEuroToDollars() {
        int euro = 1000;
        int dollar = Utils.convertEuroToDollars(euro);

        assertEquals(1188, dollar);
    }

    @Test
    public void getTodayDate() {
        String date = Utils.getTodayDate();

        assertTrue(date.matches("../../...."));
    }

    @Test
    public void getTimestampFromDate() throws ParseException {
        String date = "24/12/2021";
        Long timestamp = Utils.getTimestampFromDate(date);

        // Should be 1640304000 in our time zone, but in GMT it's 23/12/2021 23:00, so 1640300400
        assertEquals(1640300400, (long) timestamp);
    }

    @Test
    public void getDateFromTimestamp() throws ParseException {
        Long timestamp = 1640304000L;
        String date = Utils.getDateFromTimestamp(timestamp);

        assertEquals("24/12/2021", date);
    }
}