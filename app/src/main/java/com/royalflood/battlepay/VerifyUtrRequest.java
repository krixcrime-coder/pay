package com.royalflood.battlepay;

public class VerifyUtrRequest {
    public int transaction_id;
    public String utr;

    public VerifyUtrRequest(int transaction_id, String utr) {
        this.transaction_id = transaction_id;
        this.utr = utr;
    }
}
