package de.stango.eventclock.ui;

/**
 * Basic user interface showing the count-down time and the hint.
 */
public interface IClockPresenter {
	void updateClockText(String clockValue);
	
	void updateHintText(String summary, String location);
}
