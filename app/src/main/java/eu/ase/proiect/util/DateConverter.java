package eu.ase.proiect.util;

import androidx.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    @TypeConverter
    public static Date fromStringToDate (String value) {
        try{
            return simpleDateFormat.parse(value);
        }
        catch (Exception ex){
            return null;
        }

    }

    @TypeConverter
    public static String fromDateToString (Date value){
        if(value == null){
            return null;
        }
        return simpleDateFormat.format(value);

    }

}
