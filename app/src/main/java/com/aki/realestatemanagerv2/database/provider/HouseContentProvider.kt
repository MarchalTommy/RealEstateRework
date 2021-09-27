package com.aki.realestatemanagerv2.database.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.aki.realestatemanagerv2.database.EstateDatabase
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.House
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.IllegalArgumentException

class HouseContentProvider : ContentProvider() {

    companion object{
        const val AUTHORITY = "com.aki.realestatemanagerv2.provider"
        const val TABLE_NAME = "house"
        val URI_ITEM: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor {
        if (context != null) {
            val houseId: Int = ContentUris.parseId(p0).toInt()
            val cursor = EstateDatabase.getDatabase(context!!, CoroutineScope(Dispatchers.Default))
                .houseDao().getHouseWithCursor(houseId)
            cursor.setNotificationUri(context!!.contentResolver, p0)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $p0")
    }

    override fun getType(p0: Uri): String {
        return "vnd.android.cursor.house/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri {
        if(context != null) {
            val id: Long = EstateDatabase.getDatabase(context!!, CoroutineScope(Dispatchers.Default))
                .houseDao().insertHouse(House.fromContentValues(p1))
            EstateDatabase.getDatabase(context!!, CoroutineScope(Dispatchers.Default))
                .houseDao().insertAddress(Address(id.toInt(), "", "", 0, "", id.toInt()))
            if(id != 0L) {
                context!!.contentResolver.notifyChange(p0, null)
                return ContentUris.withAppendedId(p0, id)
            }
        }
        throw IllegalArgumentException("Failed to insert row into $p0")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        if (context != null){
            val count = EstateDatabase.getDatabase(context!!, CoroutineScope(Dispatchers.Default))
                .houseDao().removeHouseById(ContentUris.parseId(p0).toInt());
            context!!.contentResolver.notifyChange(p0, null);
            return count;
        }
        throw IllegalArgumentException("Failed to delete row into $p0");
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        if (context != null){
            val count = EstateDatabase.getDatabase(context!!, CoroutineScope(Dispatchers.Default))
                .houseDao().updateHouse(House.fromContentValues(p1));
            context!!.contentResolver.notifyChange(p0, null);
            return count;
        }
        throw IllegalArgumentException("Failed to update row into $p0");
    }
}