package iansLibrary.utilities;

import java.time.Month;
import java.util.ArrayList;
import java.util.Date;

public class ModdedDate {

	private ArrayList<Integer> value;
	
	public ModdedDate(int month, int date, int year){
		this.value = new ArrayList<Integer>();
		this.value.add(month);
		this.value.add(date);
		this.value.add(year);
	}
	
	public ModdedDate(){
		Date now = new Date();
		this.value = new ArrayList<Integer>();
		this.value.add(now.getMonth()+1);
		this.value.add(now.getDate());
		this.value.add(now.getYear()+1900);
	}
	
	public ModdedDate(String dateString){
			String[] data = dateString.split("/");
			int[] intData = {Integer.valueOf(data[0]), Integer.valueOf(data[1]), Integer.valueOf(data[2])};
			this.value = new ArrayList<Integer>();
			this.value.add(intData[0]);
			this.value.add(intData[1]);
			this.value.add(intData[2]);
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
		return this.value.get(0);
	}
	
	public int date(){
		return this.value.get(1);
	}
	
	public int year(){
		return this.value.get(2);
	}
	
	public String toString(){
		return (this.month() + "/" + this.date() + "/" + this.year());
	}
}
