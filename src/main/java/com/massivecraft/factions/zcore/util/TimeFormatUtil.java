package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeFormatUtil {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH");


    public String getCurrentTimeInNewYorkFormatted() {
        World currentWorld = Bukkit.getWorlds().get(0); // Get the first world (you can adjust this)
        long currentTick = currentWorld.getTime();

        int ticksPerSecond = 20; // Minecraft operates at 20 ticks per second
        long seconds = currentTick / ticksPerSecond;

        ZoneId newYorkZone = ZoneId.of("America/New_York");
        ZonedDateTime newYorkTime = ZonedDateTime.now(newYorkZone).plusSeconds(seconds);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        return formatter.format(newYorkTime);
    }

    public String calculateStartTime(int hour) {
        if (hour < 0 || hour >= 24) {
            throw new IllegalArgumentException("Hour must be between 0 and 23");
        }

        LocalTime time = LocalTime.of(hour, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return time.format(formatter);
    }

    public LocalTime calculateStartTimeFormat(int hour) {
        if (hour < 0 || hour >= 24) {
            throw new IllegalArgumentException("Hour must be between 0 and 23");
        }
        return LocalTime.of(hour, 0);
    }

    public LocalTime calculateEndTimeFormat(int startTimeHour, int shieldDurationHours) {
        int endTimeHour = (startTimeHour + shieldDurationHours) % 24;
        return calculateStartTimeFormat(endTimeHour);
    }

    public String calculateEndTime(int startTimeHour, int shieldDurationHours) {
        int endTimeHour = (startTimeHour + shieldDurationHours) % 24;
        return calculateStartTime(endTimeHour);
    }

    public boolean isTimeInTimeRange(LocalTime start, LocalTime end, LocalTime current) {
        if (start.isBefore(end)) {
            // Case where the time range does not include midnight
            return current.isAfter(start) && current.isBefore(end);
        } else if (start.isAfter(end)) {
            // Case where the time range includes midnight
            return current.isAfter(start) || current.isBefore(end);
        } else {
            // Case where start and end times are equal (shouldn't normally happen)
            return current.equals(start);
        }
    }

    public LocalTime getCurrentTime() {
        ZoneId newYorkZone = ZoneId.of("America/New_York");
        ZonedDateTime currentTimeInNewYork = ZonedDateTime.now(newYorkZone);
        return currentTimeInNewYork.toLocalTime();
    }

    public long serializeLocalDateTime(LocalDateTime dateTime) {
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return Long.parseLong(formattedDateTime);
    }

    public LocalDateTime deserializeLocalDateTime(long serializedValue) {
        String serializedString = String.valueOf(serializedValue);
        int year = Integer.parseInt(serializedString.substring(0, 4));
        int month = Integer.parseInt(serializedString.substring(4, 6));
        int day = Integer.parseInt(serializedString.substring(6, 8));
        int hour = Integer.parseInt(serializedString.substring(8, 10));
        int minute = Integer.parseInt(serializedString.substring(10, 12));
        int second = Integer.parseInt(serializedString.substring(12, 14));

        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    public boolean has24HoursPassed(LocalDateTime startTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, currentTime);
        long hoursPassed = duration.toHours();

        return hoursPassed >= 24;
    }

    public String getStatus(LocalTime start, LocalTime end, LocalTime current, LocalDateTime lastSetDate) {
        if (!has24HoursPassed(lastSetDate)) {
            return CC.translate("&c&lINACTIVE");
        } else if (isTimeInTimeRange(start, end, current)) {
            return CC.translate("&a&lACTIVE");
        } else {
            return CC.translate("&c&lINACTIVE");
        }
    }

    public boolean getBooleanStatus(LocalTime start, LocalTime end, LocalTime current, LocalDateTime lastSetDate) {
        if (!has24HoursPassed(lastSetDate)) {
            return false;
        } else if (isTimeInTimeRange(start, end, current)) {
            return true;
        } else {
            return false;
        }
    }

}
