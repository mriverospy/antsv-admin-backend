package py.gov.mitic.htv.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Util {

    public static String getFecha(int mes, int anho) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        c1.clear();
        c1.set(Calendar.MONTH, mes - 1);
        c1.set(Calendar.YEAR, anho);
        return sdf.format(c1.getTime());
    }

    public static String sumarUnDia(String fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(fecha);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Boolean NonNullNonEmpty(String data) {
    	boolean result = false;
        if(Objects.nonNull(data) && !data.isEmpty()) {
            result = true;
        }
        return result;
    }

    public static boolean isNumeric(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }

}
