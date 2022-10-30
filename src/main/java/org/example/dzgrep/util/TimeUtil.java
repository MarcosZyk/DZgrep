package org.example.dzgrep.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

  private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  private TimeUtil() {}

  /**
   * @param startTime yyyy-mm-dd hh:mm:ss
   * @param endTime yyyy-mm-dd hh:mm:ss
   * @return a fuzz regex represents the time between startTime and endTime
   */
  public static String parseTimeRangeToRegex(String startTime, String endTime) {
    if (isNullOrEmpty(startTime) && isNullOrEmpty(endTime)) {
      return ".*";
    } else if (isNullOrEmpty(startTime)) {
      startTime = "0000-00-00 00:00:00";
    } else if (isNullOrEmpty(endTime)) {
      endTime = "9999-99-99 99:99:99";
    }

    StringBuilder regexBuilder = new StringBuilder();
    char[] startTimeCharArray = startTime.toCharArray();
    char[] endTimeCharArray = endTime.toCharArray();
    int firstDiffIndex = -1;
    for (int i = 0; i < startTimeCharArray.length; i++) {
      if (startTimeCharArray[i] == endTimeCharArray[i]) {
        continue;
      }

      firstDiffIndex = i;
      break;
    }

    if (firstDiffIndex == -1) {
      return startTime;
    }

    regexBuilder.append(startTime);

    for (int i = startTimeCharArray.length - 1; i > firstDiffIndex; i--) {
      if (isNotNum(startTimeCharArray[i])) {
        continue;
      }
      regexBuilder.append('|');
      for (int j = 0; j < i; j++) {
        regexBuilder.append(startTimeCharArray[j]);
      }

      if (startTimeCharArray[i] == '9') {
        regexBuilder.append('9');
      } else {
        regexBuilder.append('[').append(incChar(startTimeCharArray[i], 1)).append("-9]");
      }

      for (int j = i + 1; j < startTimeCharArray.length; j++) {
        if (isNotNum(startTimeCharArray[j])) {
          regexBuilder.append(startTimeCharArray[j]);
        }
        regexBuilder.append("[0-9]");
      }
    }

    if (endTimeCharArray[firstDiffIndex] - startTimeCharArray[firstDiffIndex] > 1) {
      regexBuilder.append('|');
      for (int i = 0; i < firstDiffIndex; i++) {
        regexBuilder.append(startTimeCharArray[i]);
      }
      regexBuilder
          .append('[')
          .append(incChar(startTimeCharArray[firstDiffIndex], 1))
          .append('-')
          .append(incChar(endTimeCharArray[firstDiffIndex], -1))
          .append(']');
      for (int i = firstDiffIndex + 1; i < startTimeCharArray.length; i++) {
        regexBuilder.append("[0-9]");
      }
    }

    for (int i = firstDiffIndex + 1; i < startTimeCharArray.length; i++) {
      if (isNotNum(startTimeCharArray[i])) {
        continue;
      }
      regexBuilder.append('|');
      for (int j = 0; j < i; j++) {
        regexBuilder.append(endTimeCharArray[j]);
      }

      if (endTimeCharArray[i] == '0') {
        regexBuilder.append('0');
      } else {
        regexBuilder.append("[0-").append(incChar(endTimeCharArray[i], -1)).append(']');
      }

      for (int j = i + 1; j < endTimeCharArray.length; j++) {
        if (isNotNum(startTimeCharArray[j])) {
          regexBuilder.append(startTimeCharArray[j]);
        }
        regexBuilder.append("[0-9]");
      }
    }

    regexBuilder.append('|').append(endTime);

    return regexBuilder.toString();
  }

  private static boolean isNotNum(char c) {
    return c == '-' || c == ' ' || c == ':';
  }

  private static char incChar(char c, int diff) {
    return (char) (c + diff);
  }

  private static boolean isNullOrEmpty(String s) {
    return s == null || s.isEmpty();
  }

  public static Date parseTime(String time) throws ParseException {
    return dateFormat.parse(time);
  }

  public static String formatTime(Date date) {
    return dateFormat.format(date);
  }
}
