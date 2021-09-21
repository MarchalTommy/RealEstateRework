package com.aki.realestatemanagerv2;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param dollars
     * @return
     */
    public static int convertDollarToEuro(int dollars) {
        return (int) Math.round(dollars * 0.812);
    }

    public static int convertEuroToDollars(int euro) {
        return (int) Math.round(euro * 1.188);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @return
     */
    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        return dateFormat.format(new Date());
    }

    public static Long getTimestampFromDate(String s) throws ParseException {
        DateTimeFormatter DATE_FORMAT =
                new DateTimeFormatterBuilder().appendPattern("dd/MM/yyyy[ [HH][:mm][:ss][.SSS]]")
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                        .toFormatter();
        LocalDate test = LocalDate.parse(s, DATE_FORMAT);
        ZonedDateTime dateT = test.atStartOfDay(ZoneId.systemDefault());
        Instant instant = Instant.from(dateT);

        return instant.toEpochMilli() / 1000;
    }

    public static String getDateFromTimestamp(Long l) throws ParseException {
        LocalDate date = Instant.ofEpochMilli(l * 1000).atZone(ZoneId.systemDefault()).toLocalDate();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateString = date.format(df);

        return dateString;
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     */
    public static boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            Log.e(TAG, "isOnline: ERROR --> ", e);
            return false;
        }
    }

    public static Boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public static void setNotificationWorker() {
        OneTimeWorkRequest notificationRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInputData(new Data.Builder()
                        .build())
                .setBackoffCriteria(BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();
    }
}


