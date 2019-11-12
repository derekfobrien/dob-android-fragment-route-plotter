package com.example.fragmentsrouteplotter;

public class GpsStamp {
    private long msSince01Jan1970;
    private double speedMetresPerSec;
    private double latitude;
    private double longitude;
    private int stampNumber;

    public GpsStamp(long msSince01Jan1970, double speedMetresPerSec, double latitude, double longitude, int num) {
        this.msSince01Jan1970 = msSince01Jan1970;
        this.speedMetresPerSec = speedMetresPerSec;
        this.longitude = longitude;
        this.latitude = latitude;
        this.stampNumber = num;

    }

    public int getStampNumber() {
        return stampNumber;
    }

    public long getMsSince01Jan1970() {
        // return the time, in milliseconds since 1/1/1970 as a long integer
        return msSince01Jan1970;
    }

    public String getMsSince01Jan1970String() {
        return Long.toString(msSince01Jan1970);
    }

    public double getSpeedKmH() {
        // return the speed in kilometres per hour as a double number
        return speedMetresPerSec * 3.6;
    }

    public String getSpeedKmHOneDP() {
        // return the speed in km/h to one place of decimals
        return String.format("%1.1f", speedMetresPerSec * 3.6);
    }

    public double getLatitude() {
        // return the latitude as a double number
        return latitude;
    }

    public String getLatitudeTo6DP() {
        return String.format("%1.6f", (float)latitude);
    }

    public double getLongitude() {
        // return the longitude as a double number
        return longitude;
    }

    public String getLongitudeTo6DP() {
        return String.format("%1.6f", (float)longitude);
    }

    public String getSpeedKmHString()
    {
        // return the speed in kilometres per hour as a string, to one place of decimals
        return String.format("%1.1f km/h", speedMetresPerSec * 3.6);
    }

    public String getLatitudeString() {
        // return the latitude as a string, formatted "0.000000 N/S"
        String ns = "";
        if (latitude < 0) {
            ns = " S";
        } else if (latitude > 0) {
            ns = " N";
        }

        double abslat = Math.abs(latitude);
        return String.format("%1.6f", (float)abslat) + ns;
    }

    public String getLongitudeString() {
        // return the longitude as a string, formatted "0.000000 W/E"
        String ew = "";
        if (longitude < 0) {
            ew = " W";
        } else if (longitude > 0) {
            ew = " E";
        }

        double abslong = Math.abs(longitude);
        return String.format("%1.6f", (float)abslong) + ew;
    }

    public String getFullCoords() {
        // return both the longitude and longitude on one string
        return "(" + getLatitudeString() + ", " + getLongitudeString() + ")";
    }

    public String getHumanReadableTimeDate() {
        // return the time and date in the format "10:00:00.001 on 01/05/2019"
        String final_output = "";
        long num, milliseconds, seconds, minutes, hours, days, fouryears;
        int year, month, day, daysinyear, daysinmonth;
        int[] monthsinyear = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        num = msSince01Jan1970; // ms since 1/1/1970

        // milliseconds
        milliseconds = num % 1000;

        // seconds
        num = (num - milliseconds) / 1000; // sec since 1/1/1970
        seconds = num % 60;

        // minutes
        num = (num - seconds) / 60; // min since 1/1/1970
        minutes = num % 60;

        // hours
        num = (num - minutes) / 60; // hours since 1/1/1970
        hours = num % 24;

        // days
        num = (num - hours) / 24; // days since 1/1/1970
        days = num % 1461; // days in a 4-year period

        // 4-year periods
        fouryears = (num - days) / 1461;
        year = 1969 + (4 * (int) fouryears);

        // the year
        if (days < 365) {
            daysinyear = (int) days;
            year += 1;
        } else if (days < 730) {
            daysinyear = (int) days - 365;
            year += 2;
        } else if (days < 1096) {
            daysinyear = (int) days - 730;
            year += 3;
        } else {
            daysinyear = (int) days - 1096;
            year += 4;
        }

        // the month
        if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
            monthsinyear[1] = 29;
        }

        month = 0;

        while (monthsinyear[month] <= daysinyear) {
            daysinyear -= monthsinyear[month];
            month++;
        }

        day = daysinyear;

        final_output = String.format("%02d:%02d:%02d.%03d on %02d/%02d/%d", hours, minutes, seconds, milliseconds, day + 1, month + 1, year);

        return final_output;
    }
}
