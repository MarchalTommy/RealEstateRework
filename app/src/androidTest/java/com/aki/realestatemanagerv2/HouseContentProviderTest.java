package com.aki.realestatemanagerv2;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.aki.realestatemanagerv2.database.EstateDatabase;
import com.aki.realestatemanagerv2.database.provider.HouseContentProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HouseContentProviderTest {

    // FOR DATA
    private ContentResolver mContentResolver;
    private EstateDatabase database;

    // DATA SET FOR TEST
    private static final long HOUSE_ID = 42;
    private Uri houseUri;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                EstateDatabase.class)
                .allowMainThreadQueries()
                .build();

        mContentResolver = ApplicationProvider.getApplicationContext().getContentResolver();
    }

    @After
    public void reset() {
        if(houseUri != null){
            mContentResolver.delete(houseUri, null);
        }
        database.close();
    }

    @Test
    public void getItemsWhenNoItemInserted() {
        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(HouseContentProvider.Companion.getURI_ITEM(), HOUSE_ID), null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(0));
        cursor.close();
    }

    @Test
    public void insertAndGetItem() {
        // BEFORE : Adding demo item
        houseUri = mContentResolver.insert(HouseContentProvider.Companion.getURI_ITEM(), generateItem());
        // TEST
        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(HouseContentProvider.Companion.getURI_ITEM(), HOUSE_ID), null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(1));
        assertThat(cursor.moveToFirst(), is(true));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("price")), is("0"));
    }

    @Test
    public void removeItem() {
        // BEFORE : Adding demo item
        houseUri = mContentResolver.insert(HouseContentProvider.Companion.getURI_ITEM(), generateItem());
        // TEST
        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(HouseContentProvider.Companion.getURI_ITEM(), HOUSE_ID), null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(1));
        assertThat(cursor.moveToFirst(), is(true));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("price")), is("0"));
        // REMOVING THE ITEM
        mContentResolver.delete(ContentUris.withAppendedId(HouseContentProvider.Companion.getURI_ITEM(), HOUSE_ID), null, null);
        // TEST
        final Cursor cursor2 = mContentResolver.query(ContentUris.withAppendedId(HouseContentProvider.Companion.getURI_ITEM(), HOUSE_ID), null, null, null, null);
        assertThat(cursor2, notNullValue());
        assertThat(cursor2.getCount(), is(0));
    }

    // ---

    private ContentValues generateItem(){
        final ContentValues values = new ContentValues();
        values.put("price", "0");
        values.put("type", "House");
        values.put("size", "28");
        values.put("nbrRooms", "1");
        values.put("description", "This is a test Estate, with a test description");
        values.put("parkAround", "true");
        values.put("stillAvailable", "true");
        values.put("dateEntry", "1631708261");
        values.put("agentId", "1");
        values.put("addressId", "666");
        values.put("addressId", "1");
        values.put("houseId", "42");
        return values;
    }
}