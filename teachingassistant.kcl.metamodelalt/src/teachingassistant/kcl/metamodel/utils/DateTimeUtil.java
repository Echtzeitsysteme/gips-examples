package teachingassistant.kcl.metamodel.utils;

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

}
