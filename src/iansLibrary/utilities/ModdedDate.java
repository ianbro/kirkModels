package iansLibrary.utilities;

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * days are 0 indexed (first day of the year is 0. feb. 1st denoted by "02/00").
 * first year available is 1970
 * last year available is 9999
 * months are also 0 - indexed
 * 
 * @author Ian
 *
 */
public class ModdedDate {
	
	public static final int BEGIN_YEAR = 1970;
	public static final int FIRST_LEAP_YEAR = 1972;
	
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
	
	private Date dateObject;
	private Calendar calender;
	
	private long longVal;

	private int month;
	private int date;
	private int year;
	private int dayFromJan1; // for example: february 3rd would be day 34 (DAYS_IN_JAN + 3) - 1 (0 - indexed)
	private int hour;
	private int minute;
	private int second;
	private int millisecond;
	
	public ModdedDate(int month, int date, int year){
		this.month = month;
		this.date = date;
		this.year = year;
		
		// every unit under a day is now set to 00
		this.hour = 0;
		this.minute = 0;
		this.second = 0;
		this.millisecond = 0;
		
		this.setDateFromBeginningOfYear();
		this.longVal = this.getLong();
		
		
		this.dateObject = new Date(this.longVal);
		this.calender = Calendar.getInstance();
		this.calender.setTime(this.dateObject);
	}
	
	public ModdedDate() {
		
	}
	
	public boolean isLeapYear() {
		
		/*
		 * to do this method, subtract the year by 2 to get the number of years after 1972 that this year is.
		 * 		I only subtract by 2 because year will already be the number of years after 1970 that it is
		 * 		so I just subtract 2 to get the difference from 1972.
		 * 
		 * I then divide the result by 4 (years between leap years). if the result of that is 0,
		 * 		then the current year is a leap year.
		 */
		int diff = FIRST_LEAP_YEAR - BEGIN_YEAR; // this evaluates to 2 if BEGIN_YEAR is 1970 and FIRST_LEAP_YEAR is 1972
		
		if ( ((this.year - diff) % 4) == 0 ) {
			return true;
		} else {
			return false;
		}
	}
	
	public long adjustLongForLeapYears(long val) {
		long numLeapYearsPassed = this.year - (FIRST_LEAP_YEAR - BEGIN_YEAR) - 1;
		
		System.out.println("years since 1972: " + numLeapYearsPassed);
		
		numLeapYearsPassed = (long) (numLeapYearsPassed / 4);
		
		System.out.println("leap years since 1972: " + numLeapYearsPassed);
		
		long millisToAdd = numLeapYearsPassed * MILLIS_IN_DAY;
		
		System.out.println("long to add: " + millisToAdd);
		
		val += millisToAdd;
		
		return val;
	}
	
	public void setDateFromBeginningOfYear() {
		if (this.isLeapYear()) {
			int count = this.month() - 1; // start counting from previous month
			int day = this.date();
			
			while (count >= 0) {
				day += DAYS_IN_EACH_MONTH.get(count);
				
				count --;
			}
			
			/*
			 *  this is a leap year which adds a day to the year but
			 *  	only if day is greater than feb 29 (day 60)
			 */
			if(day > 60) {
				day ++;
			}
			
			this.dayFromJan1 = day;
			
		} else {
			int count = this.month() - 1; // start counting from previous month
			int day = this.date();
			
			while (count >= 0) {
				day += DAYS_IN_EACH_MONTH.get(count);
				
				count --;
			}
			
			this.dayFromJan1 = day;
			
		}
	}
	
	public long getLong() {
		long value = 0;
		
		long years = this.year * MILLIS_IN_YEAR;
		long days = (this.dayFromJan1 + 1) * MILLIS_IN_DAY; // added one because I want the real value... not the 0 - indexed value.
		long hours = this.hour * MILLIS_IN_HOUR;
		long minutes = this.minute * MILLIS_IN_MINUTE;
		long seconds = this.second * MILLIS_IN_SECOND;
		
		value = years + days + hours + minutes + seconds;
		
		value = this.adjustLongForLeapYears(value);
		
		return value;
	}
	
	public void getValsFromLong() {
		
		long seconds = (long) (this.longVal / MILLIS_IN_SECOND);
		long millis = this.longVal % MILLIS_IN_SECOND;
		
		long minutes = (long) (seconds / SECONDS_IN_MINUTE);
		seconds = seconds % SECONDS_IN_MINUTE;
		
		long hours = (long) (minutes / MINUTES_IN_HOUR);
		minutes = minutes % MINUTES_IN_HOUR;
		
		long days = (long) (hours / HOURS_IN_DAY);
		hours = hours % HOURS_IN_DAY;
		
		long years = (long) (days / DAYS_IN_YEAR);
		days = days % DAYS_IN_YEAR;
		
		System.out.println(years + 1970 + " " + days + " " + hours + " " + minutes + " " + seconds + " " + millis);
	}
	
	public int compareTo(ModdedDate other){
		if(other.year() == this.year()){
			if(other.month() == this.month()){
				if(other.date() == this.date()){
					return 0;
				}
				else if(other.date() < this.date()){
					return 1;
				}
				else{
					return -1;
				}
			}
			else if(other.month() < this.month()){
				return 1;
			}
			else{
				return -1;
			}
		}
		else if(other.year() < this.year()){
			return 1;
		}
		else{
			return -1;
		}
	}
	
	public int month(){
		return this.month;
	}
	
	public int nonIndexedMonth() {
		return this.month + 1;
	}
	
	public int date(){
		return this.date;
	}
	
	public int nonIndexedDate(){
		return this.date + 1;
	}
	
	public int year(){
		return this.year;
	}
	
	public int yearAs0000() {
		return this.year + 1970;
	}
	
	public String toString(){
		return (this.nonIndexedMonth() + "/" + this.nonIndexedDate() + "/" + this.yearAs0000());
	}
}
