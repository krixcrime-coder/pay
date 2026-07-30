package com.app.rush47;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.adapters.AnnouncementAdapter;
import com.app.rush47.models.Announcement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Full announcement list (Account tab -> Announcement), via announcements.php. No login required. */
public class AnnouncementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private View emptyText;
    private RequestQueue requestQueue;
    private String apiBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        apiBase = getString(R.string.api);

        recyclerView = findViewById(R.id.announcementRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchAnnouncements();
    }

    private void fetchAnnouncements() {
        String url = apiBase + "announcements.php";

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, new JSONObject(),
                this::handleResponse,
                error -> { });

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleResponse(JSONObject response) {
        List<Announcement> announcements = new ArrayList<>();
        try {
            if (TextUtils.equals(response.getString("status"), "true")) {
                JSONArray arr = response.getJSONObject("message").optJSONArray("announcements");
                if (arr != null) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject a = arr.getJSONObject(i);
                        announcements.add(new Announcement(
                                a.optString("message", ""),
                                a.optString("created_at", "")));
                    }
                }
            }
        } catch (JSONException ignored) {
        }

        emptyText.setVisibility(announcements.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setAdapter(new AnnouncementAdapter(announcements));
    }
}
