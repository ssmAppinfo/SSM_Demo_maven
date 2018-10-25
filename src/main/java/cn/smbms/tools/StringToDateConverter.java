package cn.smbms.tools;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDateConverter implements Converter<String, Date> {

	private String pattern;

	public StringToDateConverter(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public Date convert(String date) {
		Date date1 = null;
		try {
			date1 = new SimpleDateFormat(pattern).parse(date);
			System.out.println("StringToDateConverter converte date：" + date1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date1;
	}

}
