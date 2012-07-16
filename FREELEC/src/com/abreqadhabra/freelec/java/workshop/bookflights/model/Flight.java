package com.abreqadhabra.freelec.java.workshop.bookflights.model;

import java.sql.Timestamp;

public class Flight  {

	
	int id = 0;
	String flight_number = null;
	String origin_airport = null;
	String destination_airport = null;
	String carrier = null;
	int price = 0;
	Timestamp departure = null;
	String flight_type = null;
	int available_seats = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFlight_number() {
		return flight_number;
	}

	public void setFlight_number(String flight_number) {
		this.flight_number = flight_number;
	}

	public String getOrigin_airport() {
		return origin_airport;
	}

	public void setOrigin_airport(String origin_airport) {
		this.origin_airport = origin_airport;
	}

	public String getDestination_airport() {
		return destination_airport;
	}

	public void setDestination_airport(String destination_airport) {
		this.destination_airport = destination_airport;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Timestamp getDeparture() {
		return departure;
	}

	public void setDeparture(Timestamp departure) {
		this.departure = departure;
	}

	public String getFlight_type() {
		return flight_type;
	}

	public void setFlight_type(String flight_type) {
		this.flight_type = flight_type;
	}

	public int getAvailable_seats() {
		return available_seats;
	}

	public void setAvailable_seats(int available_seats) {
		this.available_seats = available_seats;
	}

}
