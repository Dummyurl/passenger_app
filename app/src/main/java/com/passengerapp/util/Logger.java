package com.passengerapp.util;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.TreeMap;

/**
 * Created by OzzMAN on 31.10.14.
 */
public class Logger {
    public static final int LOG_LEVEL_SIMPLE = 1;
    public static int logLevel = LOG_LEVEL_SIMPLE;
    public static final int LOG_LEVEL_EXTENDED = 2;
    public static final int LOG_LEVEL_FULL = 3;
    private static final int LOG_QUOTE_COUNT = 3;
    public static boolean isLoggingEnabled = false;
    public static String logDir = "";
    private static int lastDay = -1;

    private static void removeOldLogs() {
        if (!logDir.equals("") && isExternalStorageMounted()) {
            File logDirectory = getFolderToStorageLogs();

            if (logDirectory==null || logDirectory.listFiles().length <= LOG_QUOTE_COUNT)
                return;

            TreeMap<Long, String> logFiles = new TreeMap<Long, String>();

            for (File log : logDirectory.listFiles()) {
                logFiles.put(log.lastModified(), log.getAbsolutePath() + "/" + log.getName());
            }

            while (logFiles.size() > LOG_QUOTE_COUNT) {
                Long key = logFiles.firstEntry().getKey();
                String val = logFiles.firstEntry().getValue();

                File f = new File(val);
                if (f.exists())
                    f.delete();

                logFiles.remove(key);
            }
        }
    }

    private static void write(String message) {
        if (!logDir.equals("") && isExternalStorageMounted()) {
            int currDay = Integer.valueOf(DateFormat.format("dd", getCurrentDateTime()).toString());
            if (lastDay != currDay) {
                lastDay = currDay;
                removeOldLogs();
            }
            FileWriter fileWriter = null;

            String fileName = DateFormat.format("dd-MM-yyyy.txt", getCurrentDateTime()).toString();
            File logFile = new File(getFolderToStorageLogs(), fileName);
            logFile.getParentFile().mkdirs();

            try {
                String timeStamp = DateFormat.format("[dd.MM.yyyy, kk:mm:ss] ", getCurrentDateTime()).toString();
                fileWriter = new FileWriter(logFile, true);
                fileWriter.write(timeStamp + message + "\n");
            } catch (IOException ex) {
                //
            } finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.flush();
                        fileWriter.close();
                    }
                } catch (IOException e) {
                    Log.e("adsplayer", e.getMessage());
                }
            }
        }
    }

    public static long getCurrentDateTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static boolean isExternalStorageMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File getFolderToStorageLogs() {
        if(logDir.isEmpty()) return null;
        File logDirectory = new File(Environment.getExternalStorageDirectory(), "/"+logDir);
        if(!logDirectory.exists()) {
            logDirectory.mkdirs();
        }

        return logDirectory;
    }

    public static void writeSimple(String message) {
        if (isLoggingEnabled && logLevel >= LOG_LEVEL_SIMPLE)
            write(message);

    }

    public static void writeExtended(String message) {
        if (isLoggingEnabled && logLevel >= LOG_LEVEL_EXTENDED)
            write(message);
    }

    public static void writeFull(String message) {
        if (isLoggingEnabled && logLevel == LOG_LEVEL_FULL)
            write(message);
    }
}
