package com.royalflood.battlepay;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("generate_qr.php")
    Call<GenerateQrResponse> generateQr(@Body GenerateQrRequest request);

    @POST("verify_utr.php")
    Call<VerifyUtrResponse> verifyUtr(@Body VerifyUtrRequest request);
}
