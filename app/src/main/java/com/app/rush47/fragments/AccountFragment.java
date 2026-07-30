package com.app.rush47.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.AboutUsActivity;
import com.app.rush47.AnnouncementActivity;
import com.app.rush47.ForgotPasswordOtpActivity;
import com.app.rush47.LeaderboardActivity;
import com.app.rush47.LoginActivity;
import com.app.rush47.MyMatchesActivity;
import com.app.rush47.MyOrderActivity;
import com.app.rush47.MyProfileActivity;
import com.app.rush47.MyReferralsActivity;
import com.app.rush47.MyStatisticsActivity;
import com.app.rush47.MyWalletActivity;
import com.app.rush47.R;
import com.app.rush47.models.CurrentUser;
import com.app.rush47.utils.LoadingDialog;
import com.app.rush47.utils.NetworkErrorHelper;
import com.app.rush47.utils.UserLocalStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Account tab - matches the original app's "me_home" screen layout
 * (header with coin balance, profile circle, stats bar, full menu list).
 *
 * Rows wired to a real screen/action:
 *   My Profile        -> MyProfileActivity (read-only info saved on this device)
 *   My Wallet          -> MyWalletActivity
 *   My Matches         -> MyMatchesActivity
 *   My Order           -> MyOrderActivity (placeholder screen, no API yet)
 *   My Statistics      -> MyStatisticsActivity (statistics.php)
 *   My Referrals       -> MyReferralsActivity
 *   Announcement       -> AnnouncementActivity (announcements.php)
 *   Top Players / Leaderboard -> LeaderboardActivity (leaderboard.php, ranked by prize won)
 *   About Us           -> AboutUsActivity
 *   Customer Support   -> opens an email to the support address (update
 *                          R.string.support_email with your real address)
 *   Share App          -> real Android share sheet
 *   Logout             -> clears the session and returns to Login
 *
 * Rows that still show "coming soon" because there is no matching
 * screen/API in this codebase yet: My Rewards, App Tutorial,
 * Terms & Conditions, Change Language.
 */
public class AccountFragment extends Fragment {

    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private LoadingDialog loadingDialog;
    private String apiBase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());
        userLocalStore = new UserLocalStore(requireContext());
        loadingDialog = new LoadingDialog(requireActivity());
        apiBase = getString(R.string.api);

        String username = userLocalStore.getLoggedInUser().getUsername();
        if (username != null && !username.isEmpty()) {
            TextView headerUsername = view.findViewById(R.id.accountUsername);
            TextView bigUsername = view.findViewById(R.id.accountUsernameBig);
            headerUsername.setText(username);
            bigUsername.setText(username);
        }

        setAppVersion(view);
        fetchCoinBalance(view);

        setupRow(view, R.id.rowMyProfile, R.drawable.myprofile, R.string.my_profile,
                () -> startActivity(new Intent(getContext(), MyProfileActivity.class)));

        setupRow(view, R.id.rowMyWallet, R.drawable.mywallet, R.string.my_wallet,
                () -> startActivity(new Intent(getContext(), MyWalletActivity.class)));

        setupRow(view, R.id.rowMyMatches, R.drawable.gameicongreen, R.string.my_matches,
                () -> {
                    Intent intent = new Intent(getContext(), MyMatchesActivity.class);
                    intent.putExtra(MyMatchesActivity.EXTRA_INITIAL_STATUS, "upcoming");
                    startActivity(intent);
                });

        setupRow(view, R.id.rowMyOrder, R.drawable.my_order_icon, R.string.my_order,
                () -> startActivity(new Intent(getContext(), MyOrderActivity.class)));

        setupRow(view, R.id.rowMyStatistics, R.drawable.mystatics, R.string.my_statistics,
                () -> startActivity(new Intent(getContext(), MyStatisticsActivity.class)));

        setupRow(view, R.id.rowMyRewards, R.drawable.myreward_icon, R.string.my_reward,
                this::showComingSoon);

        setupRow(view, R.id.rowMyReferrals, R.drawable.myreferrals, R.string.my_referrals,
                () -> startActivity(new Intent(getContext(), MyReferralsActivity.class)));

        setupRow(view, R.id.rowAnnouncement, R.drawable.announcementfreen, R.string.announcement,
                () -> startActivity(new Intent(getContext(), AnnouncementActivity.class)));

        setupRow(view, R.id.rowTopPlayers, R.drawable.topplayers, R.string.top_players,
                () -> startActivity(new Intent(getContext(), LeaderboardActivity.class)));

        setupRow(view, R.id.rowLeaderboard, R.drawable.leaderboard, R.string.leaderboard,
                () -> startActivity(new Intent(getContext(), LeaderboardActivity.class)));

        setupRow(view, R.id.rowAppTutorial, R.drawable.help, R.string.app_tutorial,
                this::showComingSoon);

        setupRow(view, R.id.rowAboutUs, R.drawable.aboutus, R.string.about_us,
                () -> startActivity(new Intent(getContext(), AboutUsActivity.class)));

        setupRow(view, R.id.rowCustomerSupport, R.drawable.customersupport, R.string.customer_support,
                this::openCustomerSupport);

        setupRow(view, R.id.rowShareApp, R.drawable.shareyellow, R.string.share_app,
                this::shareApp);

        setupRow(view, R.id.rowTermsConditions, R.drawable.termandconditon, R.string.terms_and_condition,
                this::showComingSoon);

        setupRow(view, R.id.rowChangeLanguage, R.drawable.ic_language, R.string.change_language,
                this::showComingSoon);

        setupRow(view, R.id.rowLogout, R.drawable.logout, R.string.logout, this::logout);
    }

    private void showComingSoon() {
        Toast.makeText(requireContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
    }

    private void setAppVersion(View view) {
        TextView versionText = view.findViewById(R.id.accountVersion);
        try {
            PackageInfo info = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0);
            versionText.setText(getString(R.string.version_prefix) + " : " + info.versionName);
        } catch (Exception e) {
            versionText.setText(R.string.version_prefix);
        }
    }

    /** Coin balance pill in the header reuses the same wallet_balance field My Wallet shows. */
    private void fetchCoinBalance(View view) {
        TextView coinBalance = view.findViewById(R.id.accountCoinBalance);
        String url = apiBase + "earn.php";

        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, params,
                response -> {
                    try {
                        if (TextUtils.equals(response.getString("status"), "true")) {
                            String balance = response.getJSONObject("message")
                                    .optString("wallet_balance", "0.00");
                            coinBalance.setText(balance);
                        }
                    } catch (JSONException ignored) {
                    }
                },
                error -> { });

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Play, compete and win with " + getString(R.string.app_name) + "! Download now.");
        startActivity(Intent.createChooser(intent, getString(R.string.share_app)));
    }

    private interface RowAction {
        void run();
    }

    /** Fills in one included menu row's icon/title and wires its real click action. */
    private void setupRow(View parent, int rowId, int iconRes, int titleRes, RowAction action) {
        View row = parent.findViewById(rowId);
        if (row == null) return;

        ImageView icon = row.findViewById(R.id.menuIcon);
        TextView title = row.findViewById(R.id.menuTitle);
        icon.setImageResource(iconRes);
        title.setText(titleRes);

        row.setOnClickListener(v -> action.run());
    }

    // ---------------------------------------------------------------
    // Change Password: reuse the OTP-based reset flow, auto-sent to
    // this account's own email/mobile instead of asking for it again.
    // ---------------------------------------------------------------
    private void startChangePassword() {
        CurrentUser user = userLocalStore.getLoggedInUser();
        String emailOrMobile = !TextUtils.isEmpty(user.getEmail()) ? user.getEmail() : user.getMobile();

        if (TextUtils.isEmpty(emailOrMobile)) {
            Toast.makeText(requireContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog.show();
        String url = apiBase + "sendOTP.php";

        Map<String, Object> params = new HashMap<>();
        params.put("email_mobile", emailOrMobile);

        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                response -> {
                    loadingDialog.dismiss();
                    try {
                        if (TextUtils.equals(response.getString("status"), "true")) {
                            Intent intent = new Intent(getContext(), ForgotPasswordOtpActivity.class);
                            intent.putExtra("EMAIL_OR_MOBILE", emailOrMobile);
                            intent.putExtra("MEMBER_ID", response.optString("member_id", ""));
                            intent.putExtra("SERVER_OTP", response.optString("otp", ""));
                            startActivity(intent);
                        } else {
                            Toast.makeText(requireContext(), response.optString("message", ""), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingDialog.dismiss();
                    Toast.makeText(requireContext(), NetworkErrorHelper.describe(error), Toast.LENGTH_SHORT).show();
                });

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    // ---------------------------------------------------------------
    // Customer Support: opens an email to the support address.
    // TODO: R.string.support_email is a placeholder - update it with
    // your real support contact (or swap this for a WhatsApp/Telegram
    // deep link) once you have one.
    // ---------------------------------------------------------------
    private void openCustomerSupport() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + getString(R.string.support_email)));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " - Support");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        userLocalStore.clearUserData();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
