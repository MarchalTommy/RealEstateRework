package com.aki.realestatemanagerv2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
}