package com.base22.harvestmonthlyupdate.demo.Utils;

import com.base22.harvestmonthlyupdate.demo.Model.Pair;
import com.base22.harvestmonthlyupdate.demo.Service.HarvestClient;
import org.apache.logging.log4j.Level;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class HarvestUtils {

    public static List fetchWeeklyDateRanges(){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        dtf.format(now);

        int currentYear = now.getYear();
        int currentMonth = now.getMonth().ordinal();

        YearMonth yearMonth = YearMonth.of(currentYear , currentMonth);
        LocalDate firstOfMonth = yearMonth.atDay( 1 );
        LocalDate lastOfMonth = yearMonth.atEndOfMonth();


        LocalDate startDate = firstOfMonth.with(TemporalAdjusters.previousOrSame( DayOfWeek.SUNDAY ));
        LocalDate endDate =lastOfMonth.with(TemporalAdjusters.previousOrSame( DayOfWeek.SATURDAY ));

        LocalDate from = startDate;
        LocalDate to;

        List<Pair<String,String>> dates = new ArrayList<>();

        while(from.isBefore(endDate)){
            to = from.with(TemporalAdjusters.next( DayOfWeek.SATURDAY));
            dates.add(new Pair(from.toString(),to.toString()));
            from = to.with(TemporalAdjusters.nextOrSame( DayOfWeek.SUNDAY ));
        }
        return dates;
    }
}
