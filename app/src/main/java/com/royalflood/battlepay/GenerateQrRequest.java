package com.royalflood.battlepay;

public class GenerateQrRequest {
    public int user_id;
    public double amount;

    public GenerateQrRequest(int user_id, double amount) {
        this.user_id = user_id;
        this.amount = amount;
    }
}
