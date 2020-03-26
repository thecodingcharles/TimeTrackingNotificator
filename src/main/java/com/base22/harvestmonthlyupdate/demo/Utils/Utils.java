package com.base22.harvestmonthlyupdate.demo.Utils;

import com.base22.harvestmonthlyupdate.demo.Model.DateInterval;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class Utils {


    private static StringBuilder weeklyDateIntervalsText = new StringBuilder();

    public static List<DateInterval> getDateIntervals(){


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        dtf.format(now);

        int currentYear = now.getYear();
        //int currentMonth = now.getMonth().ordinal();
        int currentMonth = 3;

        YearMonth yearMonth = YearMonth.of(currentYear , currentMonth);
        LocalDate firstOfMonth = yearMonth.atDay( 1 );
        LocalDate lastOfMonth = yearMonth.atEndOfMonth();

        LocalDate startDate = firstOfMonth.with(TemporalAdjusters.previousOrSame( DayOfWeek.SUNDAY ));
        LocalDate endDate =lastOfMonth.with(TemporalAdjusters.previousOrSame( DayOfWeek.SATURDAY ));

        LocalDate from = startDate;
        LocalDate to;

        List<DateInterval> dateIntervals = new ArrayList<>();

        while(from.isBefore(endDate)){
            to = from.with(TemporalAdjusters.next( DayOfWeek.SATURDAY));
            dateIntervals.add(new DateInterval(from.toString(),to.toString()));

            weeklyDateIntervalsText.append("_");
            weeklyDateIntervalsText.append(dateFormatter(from.toString()));
            weeklyDateIntervalsText.append("\t-\t");
            weeklyDateIntervalsText.append(dateFormatter(to.toString()));
            weeklyDateIntervalsText.append("_\n");

            from = to.with(TemporalAdjusters.nextOrSame( DayOfWeek.SUNDAY ));
        }
        return dateIntervals;
    }

    private static String dateFormatter(String dateStr){

        DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
        // parse the date string into Date object
        Date date = new Date();

        try {
           date = srcDf.parse(dateStr);
        } catch (ParseException e) {
               e.printStackTrace();
        }
        DateFormat destDf = new SimpleDateFormat("MMMM-dd-yyyy");
        //format the date into another format
        return destDf.format(date);

    }


    public static String getLogYourTimesheetMessage(){

        String message = "Howdy partner! :hi: \n\n This is a friendly reminder for you to log your" +
                         " time *_today_* at the latest, otherwise I am going to help you out *_tomorrow_* " +
                         "by logging your missing hours myself for the following weeks :spiral_calendar_pad:\n\n";
        message +=  weeklyDateIntervalsText.toString();
        message += "\nNOTE: Whether you opt or not to log your time, I'll be passing by tomorrow _once again_ to remind you to SUMBIT your timesheets :wink:\n";
        message += "\n :star: Keep in mind that logging your time is important as it helps to accurately plan for future tasks and projects _and_ makes it easier " +
                   "and more efficient for the project management team\n";
        message += "\n*[Please do not reply to this message]*";
        return message;
    }

    public static String getSubmitYourTimesheetMessage(){
        String message = "Hello! :hi: \n\n I joyfully, gracefully and wonderfully did your job of *_log your missing hours_*.\nPlease submit your timesheets *A.S.A.P.* for the following weeks :spiral_calendar_pad:\n\n";
        message +=  weeklyDateIntervalsText.toString();
        message += "\n \n";
        message += "\n*[Please do not reply to this message]*";
        return message;
    }

    public static Set<String> getDayDatesOfWeek(String startDate,String endDate){

        Set<String> weekDays = new LinkedHashSet<>();
        DateTimeFormatter srcDf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate from = LocalDate.parse(startDate,srcDf);
        LocalDate to = LocalDate.parse(endDate,srcDf);

        LocalDate sunday = from;
        from = from.plusDays(1);
        weekDays.add(from.toString());

        while(from.isBefore(to)){
            from = from.plusDays(1);
            weekDays.add(from.toString());
        }
        weekDays.add(sunday.toString());
        return weekDays;
    }

}


