package org.processmining.plugins.compliance.rules.select.constraints;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NumericValueConversion {
	public static double convertToFloat(String value)
	{
		double base=Math.pow(255, 2);
		double asNumber=0;
		for(int i=0;i<value.length();i++) {
			
			if (value.charAt(i)<=255)
				asNumber+=value.charAt(i)*(base);
			else
				asNumber+=base;
			base/=255;
		}
		return asNumber;
	}
	
	public static double toNumericValue(Object value) {
		if (value instanceof Boolean) 
			return((Boolean) value ? 1 : 0);
		else if (value instanceof Number)
			return ((Number)value).floatValue();
		else if (value instanceof String)
		{
            String target = (String) value;
            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy",Locale.US);
            try {
				Date result =  df.parse(target);
				return result.getTime();
			} catch (ParseException e) {
				return convertToFloat((String) value);
			}
		}
		else if (value instanceof Date)
			return ((Date)value).getTime()/1000D;
		else
			return 0;
	}
	
	public static Object fromNumericValue(double value,Class<?> type)
	{
		if (type == Boolean.class)
			return (value == 0 ? false : true);		
		if (type == Integer.class)
			return ((int)value);
		if (type == Double.class)
			return roundThreeDecimals(value);
		if (type == Float.class)
			return roundThreeDecimals((float)value);
		if (type == Date.class)
			return new java.util.Date((long)(value*1000));
		if (type != String.class)
			return value;

		
		StringBuffer retValue=new StringBuffer();
		long intPart=(long)value;
		double decimalPart=value-intPart;
		while(intPart>0)
		{
			char module=(char) (intPart % 255);
			if (module>=32)
				retValue.insert(0,module);
			intPart/=255;
		}
		int iteration=1;
		while(iteration<15 && decimalPart>0)
		{
			decimalPart=decimalPart*255;
			if (decimalPart<32)
				break;
			retValue.append((char)decimalPart);
			decimalPart-=(int)decimalPart;
		}
		return retValue.toString();
	}	
	
	private static double roundThreeDecimals(double d) {
		double value=(int) (d*1000);
		return value/1000D;
	}	

	private static float roundThreeDecimals(float d) {
		float value=(int) (d*1000);
		return value/1000F;
	}		
}
