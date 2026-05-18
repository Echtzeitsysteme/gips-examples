package teachingassistant.uni.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DateTimeUtil {

	private DateTimeUtil() {
	}

	/**
	 * Converts the hours, minutes, and seconds of the given date object to a sum of
	 * seconds. All other date-specific parts (like day, week, month, year) will be
	 * ignored.
	 * 
	 * @param date Date object to extract the sum of seconds from.
	 * @return Sum of the seconds constructed by hours, minutes, and seconds.
	 */
	public static int convertDateTimeToSeconds(final Date date) {
		Objects.requireNonNull(date);

		int seconds = 0;
		final Calendar cal = Calendar.getInstance();
		Objects.requireNonNull(cal);
		cal.setTime(date);

		seconds += cal.get(Calendar.SECOND);
		seconds += (cal.get(Calendar.MINUTE) * 60);
		seconds += (cal.get(Calendar.HOUR_OF_DAY) * 60 * 60);

		return seconds;
	}

	/**
	 * Converts the hours and minutes of the given date object to a sum of seconds.
	 * All other date-specific parts (like day, week, month, year) will be ignored.
	 * 
	 * @param date Date object to extract the sum of seconds from.
	 * @return Sum of the minutes constructed by hours and minutes.
	 */
	public static int convertDateTimeToMinutes(final Date date) {
		Objects.requireNonNull(date);

		int minutes = 0;
		final Calendar cal = Calendar.getInstance();
		Objects.requireNonNull(cal);
		cal.setTime(date);

		minutes += cal.get(Calendar.MINUTE);
		minutes += (cal.get(Calendar.HOUR_OF_DAY) * 60);

		return minutes;
	}

	/**
	 * TODO.
	 * 
	 * @param localDateTime
	 * @return
	 */
	public static Date localDateTimeToDate(final LocalDateTime localDateTime) {
		Objects.requireNonNull(localDateTime);

		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

}
