package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventRecord {
    private String customerCode;
    private double paymentAmount;
    private long timestamp;

    public EventRecord() {}

    public EventRecord(String customerCode, double paymentAmount, long timestamp) {
        this.customerCode = customerCode;
        this.paymentAmount = paymentAmount;
        this.timestamp = timestamp;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this); // Serialize the object to JSON
        } catch (Exception e) {
            e.printStackTrace();
            return "{}"; // Return empty JSON in case of error
        }
    }

}
