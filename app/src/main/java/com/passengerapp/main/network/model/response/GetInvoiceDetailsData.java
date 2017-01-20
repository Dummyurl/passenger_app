package com.passengerapp.main.network.model.response;

import java.util.List;

/**
 * Created by adventis on 11/4/15.
 */
public class GetInvoiceDetailsData {
    public List<InvoiceItem> InvoiceItems;
    public int PaymentID;
    public String PaymentMethod;
    public String MerchantId;
    public String FulFillingMerchantId;
    public String TransactionId;
    public String PassengerName;
    public String UserName;
    public int MostRecentStateTimestamp;
    public boolean DepositExported;
    public boolean Exists;
    public boolean IsCaptive;
    public boolean IsFarmed;

    public class InvoiceItem {
        public String FeeDescription;
        public String FeeCode;
        public float Amount;
    }
}
