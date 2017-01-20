package com.passengerapp.main.network.model.request;

public class SendInvoiceToDriverRequest {

	public int ReservationID;
	public int InvoiceID;
	public float TipAmount;
	public boolean PassengerAction;
	public String Signature;
	public String PaymentMethod;
	public boolean UpdateTip;
}
