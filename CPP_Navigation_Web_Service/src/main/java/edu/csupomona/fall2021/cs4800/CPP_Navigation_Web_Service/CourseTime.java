package edu.csupomona.fall2021.cs4800.CPP_Navigation_Web_Service;

public enum CourseTime {
    ONE_A("01:00:00 AM"),
    TWO_AM("02:00:00 AM"),
    THREE_AM("03:00:00 AM"),
    FOUR_AM("04:00:00 AM"),
    FIVE_AM("05:00:00 AM"),
    SIX_AM("06:00:00 AM"),
    SEVEN_AM("07:00:00 AM"),
    EIGHT_AM("08:00:00 AM"),
    NINE_AM("09:00:00 AM"),
    TEN_AM("10:00:00 AM"),
    ELEVEN_AM("11:00:00 AM"),
    TWELVE_PM("12:00:00 PM"),
    ONE_PM("01:00:00 PM"),
    TWO_PM("02:00:00 PM"),
    THREE_PM("03:00:00 PM"),
    FOUR_PM("04:00:00 PM"),
    FIVE_PM("05:00:00 PM"),
    SIX_PM("06:00:00 PM"),
    SEVEN_PM("07:00:00 PM"),
    EIGHT_PM("08:00:00 PM"),
    NINE_PM("09:00:00 PM"),
    TEN_PM("10:00:00 PM"),
    ELEVEN_PM("11:00:00 PM"),
    TWELVE_AM("12:00:00 AM");
	private final String name;
	
	private CourseTime(String name) {
		this.name = name;
	}
}
