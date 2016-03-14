package iansLibrary.utilities;

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ModdedDate {
	
	/*
	 * got these conversions off of google... hope they're accurate.
	 */
	public static final long MILLIS_IN_YEAR = 31540000000L;
	public static final long MILLIS_IN_DAY = 86400000L;
	public static final long MILLIS_IN_HOUR = 3600000L;
	public static final long MILLIS_IN_MINUTE = 60000L;
	public static final long MILLIS_IN_SECOND = 1000L;
	
	public static final int SECONDS_IN_MINUTE = 60;
	public static final int MINUTES_IN_HOUR = 60;
	public static final int HOURS_IN_DAY = 24;
	public static final int DAYS_IN_YEAR = 365;
	
	public static final int DAYS_IN_JAN = 31;
	public static final int DAYS_IN_FEB = 28;
	public static final int DAYS_IN_MAR = 31;
	public static final int DAYS_IN_APR = 30;
	public static final int DAYS_IN_MAY = 31;
	public static final int DAYS_IN_JUN = 30;
	public static final int DAYS_IN_JUL = 31;
	public static final int DAYS_IN_AUG = 31;
	public static final int DAYS_IN_SEP = 30;
	public static final int DAYS_IN_OCT = 31;
	public static final int DAYS_IN_NOV = 30;
	public static final int DAYS_IN_DEC = 31;
	
	public static final HashMap<Integer, Integer> DAYS_IN_EACH_MONTH = new HashMap<Integer, Integer>(){{
		put(JANUARY, DAYS_IN_JAN);
		put(FEBRUARY, DAYS_IN_FEB);
		put(MARCH, DAYS_IN_MAR);
		put(APRIL, DAYS_IN_APR);
		put(MAY, DAYS_IN_MAY);
		put(JUNE, DAYS_IN_JUN);
		put(JULY, DAYS_IN_JUL);
		put(AUGUST, DAYS_IN_AUG);
		put(SEPTEMBER, DAYS_IN_SEP);
		put(OCTOBER, DAYS_IN_OCT);
		put(NOVEMBER, DAYS_IN_NOV);
		put(DECEMBER, DAYS_IN_DEC);
	}};
	
	public static final int JANUARY = 0;
	public static final int FEBRUARY = 1;
	public static final int MARCH = 2;
	public static final int APRIL = 3;
	public static final int MAY = 4;
	public static final int JUNE = 5;
	public static final int JULY = 6;
	public static final int AUGUST = 7;
	public static final int SEPTEMBER = 8;
	public static final int OCTOBER = 9;
	public static final int NOVEMBER = 10;
	public static final int DECEMBER = 11;
	
	private Calendar calendar;
	
	private long longVal;

	private int month;
	private int date;
	private int year;
	private int hour;
	private int minute;
	private int second;
	private int millisecond;
	
	public ModdedDate(int month, int date, int year){
		
		this.calendar = Calendar.getInstance();
		this.setDate(year, month, date, 0, 0, 0, 0);
		this.getValsFromCalendar();
	}
	
	public ModdedDate() {
		
		Date now = new Date();
		this.calendar = Calendar.getInstance();
		this.calendar.setTime(now);
		this.getValsFromCalendar();
	}
	
	public ModdedDate(int month, int date, int year, int hour, int minute, int seconds, int millis) {
		
		this.calendar = Calendar.getInstance();
		this.setDate(year, month, date, hour, minute, seconds, millis);
		this.getValsFromCalendar();
	}
	
	public void setDate(int year, int month, int date, int hour, int minute, int seconds, int millis) {
		this.calendar.set(Calendar.YEAR, year);
		
		this.calendar.set(Calendar.MONTH, month);
		
		this.calendar.set(Calendar.DATE, date);
		
		this.calendar.set(Calendar.HOUR, hour);
		
		this.calendar.set(Calendar.MINUTE, minute);
		
		this.calendar.set(Calendar.SECOND, seconds);
		
		this.calendar.set(Calendar.MILLISECOND, millis);
		
		this.longVal = this.calendar.getTimeInMillis();
	}
	
	public void getValsFromCalendar() {
		
		int y = this.calendar.get(Calendar.YEAR);
		
		int d = this.calendar.get(Calendar.DATE);
		
		int mon = this.calendar.get(Calendar.MONTH);
		
		int h = this.calendar.get(Calendar.HOUR);
		
		int min = this.calendar.get(Calendar.MINUTE);
		
		int s = this.calendar.get(Calendar.SECOND);
		
		int mil = this.calendar.get(Calendar.MILLISECOND);
		
		this.year = y;
		this.date = d;
		this.month = mon;
		this.hour = h;
		this.minute = min;
		this.second = s;
		this.millisecond = mil;
	}
	
	public int compareTo(ModdedDate other){

		return this.calendar.compareTo(other.calendar);
	}
	
	public boolean before(ModdedDate when){
		
		return this.calendar.before(when.calendar);
	}
	
	public boolean after(ModdedDate when){
		
		return this.calendar.before(when.calendar);
	}
	
	public int month(){
		return this.month;
	}
	
	public int date(){
		return this.date;
	}
	
	public int year(){
		return this.year;
	}
	
	public int hour(){
		return this.hour;
	}
	
	public int minute(){
		return this.minute;
	}
	
	public int seconds(){
		return this.second;
	}
	
	public int millis(){
		return this.millisecond;
	}
	
	public String toString(){
		return ( (this.month() + 1) + "/" + this.date() + "/" + this.year());
	}
}
