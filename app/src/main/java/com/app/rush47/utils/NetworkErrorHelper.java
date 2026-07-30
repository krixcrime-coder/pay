package com.app.rush47.utils;

import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;

import java.nio.charset.StandardCharsets;

/**
 * Volley's generic VolleyError doesn't tell you WHY a request failed.
 * This turns it into a specific message so real problems (server 500,
 * bad response body, timeout, no internet) aren't all shown as the
 * same vague "network error".
 */
public class NetworkErrorHelper {

    public static String describe(VolleyError error) {
        if (error instanceof TimeoutError) {
            return "Request timed out. Please check your internet and try again.";
        }
        if (error instanceof NoConnectionError) {
            return "Could not reach the server. Check your internet connection.";
        }

        NetworkResponse response = error.networkResponse;
        if (response != null) {
            String body = "";
            if (response.data != null) {
                body = new String(response.data, StandardCharsets.UTF_8);
            }
            if (error instanceof ServerError) {
                // Server responded, but with an error status (4xx/5xx) or a
                // body Volley couldn't parse as the expected JSON.
                String snippet = body.length() > 200 ? body.substring(0, 200) + "..." : body;
                return "Server error (" + response.statusCode + "): "
                        + (snippet.isEmpty() ? "no details returned." : snippet);
            }
            return "Unexpected response (" + response.statusCode + ").";
        }

        // No networkResponse at all - this is Volley's generic NetworkError
        // (SSL/certificate failure, redirect loop, connection reset mid-read,
        // DNS oddity, etc). error.getCause() usually has the real exception
        // (e.g. SSLHandshakeException, UnknownHostException) - surface it
        // instead of a vague message so the real problem is visible.
        Throwable cause = error.getCause();
        if (cause != null && cause.getMessage() != null) {
            return "Connection failed: " + cause.getClass().getSimpleName() + " - " + cause.getMessage();
        }
        return "Network error. Please check your connection.";
    }
}
