package cn.smbms.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test1 {
	public static void main(String[] args) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = format.parse("20180909");
			System.out.println(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
