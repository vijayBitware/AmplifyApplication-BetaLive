package com.amplify.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GetCurrentDate {

	public String getCurrentDateTime() {
		// TODO Auto-generated method stub
		Calendar ci = Calendar.getInstance();

		String CiDateTime = "" + ci.get(Calendar.YEAR) + "-" +
				(ci.get(Calendar.MONTH) + 1) + "-" +
				ci.get(Calendar.DAY_OF_MONTH);
		
		//using SimpleDateFormat class
		SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		//SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String newtime =  sdfDateTime.format(new Date(System.currentTimeMillis()));
		return newtime;
	}

	public String getCurrentDate() {
		// TODO Auto-generated method stub
		Calendar ci = Calendar.getInstance();

		String CiDateTime = "" + ci.get(Calendar.YEAR) + "-" +
				(ci.get(Calendar.MONTH) + 1) + "-" +
				ci.get(Calendar.DAY_OF_MONTH);

		//using SimpleDateFormat class
		SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

		String newtime =  sdfDateTime.format(new Date(System.currentTimeMillis()));

		return newtime;
	}

	public String convertGMTdateToLcaldate(String serverDate) {
		// TODO Auto-generated method stub
		String outputText = "";
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat
					("yyyy-MM-dd", Locale.ENGLISH);
			inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

			SimpleDateFormat outputFormat =
					new SimpleDateFormat("yyyy-MM-dd");
			// Adjust locale and zone appropriately
			Date date;
			date = inputFormat.parse(serverDate);
			outputText = outputFormat.format(date);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		return outputText;
	}



	public String convertGMTtimeToLcalTime(String serverDate){
		String outputText = "";
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat
					("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

//			Calendar cal = Calendar.getInstance();
//			inputFormat.setTimeZone(cal.getTimeZone());

			inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

			SimpleDateFormat outputFormat =
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Adjust locale and zone appropriately
			Date date;
			date = inputFormat.parse(serverDate);
			outputText = outputFormat.format(date);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		return outputText;
	}
	
	
	public String convertGMTtimeToFormatedLcalTime(String serverDate){
		String outputText = "";
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat
					("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

			SimpleDateFormat outputFormat =
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Adjust locale and zone appropriately
			Date date;

			date = inputFormat.parse(serverDate);
			outputText = outputFormat.format(date);
			
			DateFormat readFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
			DateFormat writeFormat = new SimpleDateFormat( "dd MMM yy, hh:mm a");
			Date dateNew = null;

			try
			{
				dateNew = readFormat.parse( outputText );
			}
			catch ( ParseException e )
			{
				e.printStackTrace();
			}

			outputText = writeFormat.format( dateNew );

		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		return outputText;
	}

	
	public boolean dayDifferenceBetweenCurrentDateAndArriveDate(String arriveDateString){
		
		try
		{
			
			DateFormat readArriveDateFormat = new SimpleDateFormat( "d MMM yyyy");
			DateFormat writeArriveDateFormat = new SimpleDateFormat( "yyyy-MM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
			String currentDate = getCurrentDate();
			Date dateCurrent = sdf.parse(currentDate);
			Date dateArrive = null;
			
			dateArrive = readArriveDateFormat.parse(arriveDateString);
			
			long timeCurrent = dateCurrent.getTime();
			long timeArrive = dateArrive.getTime();
			long oneDay = 1000*60*60*24;
			long tenDays = 10 * oneDay;
			long dateDiffernce = ((timeArrive + tenDays) - timeCurrent)/ oneDay;
			if(dateDiffernce > 0 && dateDiffernce <= 10){
				return true;
			}else if(dateDiffernce <= 0){
				return false;
			}
			
		}
		catch ( ParseException e )
		{
			e.printStackTrace();
		}
		
		return true;
		
	}

}
