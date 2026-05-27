package com.lichsuvietnam.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.ui.activities.NotificationActivity;
import com.lichsuvietnam.app.ui.activities.SavedActivity;
import com.lichsuvietnam.app.ui.activities.SettingsActivity;
import com.lichsuvietnam.app.ui.activities.WelcomeActivity;
import com.lichsuvietnam.app.ui.activities.admin.AdminDashboardActivity;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.UserEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SessionManager session = new SessionManager(requireContext());
        AppDatabase db = AppDatabase.getInstance(requireContext());

        if (session.isGuest() || !session.isLoggedIn()) {
            View view = inflater.inflate(R.layout.fragment_profile_guest, container, false);
            view.findViewById(R.id.btnLogin).setOnClickListener(v -> {
                session.logout();
                startActivity(new Intent(getContext(), WelcomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            });
            return view;
        }

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        long userId = session.getUserId();

        // Load user info + real stats
        Executors.newSingleThreadExecutor().execute(() -> {
            UserEntity user = db.userDao().getUserById(userId);
            if (user == null || getActivity() == null) return;

            // Real total points from quiz results
            int totalPoints = db.quizDao().getTotalPoints(userId);
            // Sync user.points to match quiz total
            if (user.points != totalPoints) {
                db.userDao().setPoints(userId, totalPoints);
            }

            int bookmarkCount = 0;
            try { bookmarkCount = db.bookmarkDao().getBookmarkCount(userId).getValue() != null ? db.bookmarkDao().getBookmarkCount(userId).getValue() : 0; } catch (Exception e) {}
            final int fBookmarks = bookmarkCount;

            // Hours = quiz results count (rough estimate: 1 quiz session ~= some learning time)
            int quizCount = db.quizDao().getResultsByUserSync(userId).size();
            int learningCompleted = db.learningDao().getTotalCompletedLessons(userId);
            int estimatedHours = Math.max(user.hoursLearned, (quizCount + learningCompleted) / 3);

            final int fPoints = totalPoints;
            final int fHours = estimatedHours;

            getActivity().runOnUiThread(() -> {
                ((TextView) view.findViewById(R.id.tvUserName)).setText(user.name);
                ((TextView) view.findViewById(R.id.tvUserEmail)).setText(user.email);
                String initials = "";
                for (String part : user.name.split(" ")) { if (!part.isEmpty()) initials += part.charAt(0); }
                ((TextView) view.findViewById(R.id.tvInitials)).setText(initials.length() > 2 ? initials.substring(0, 2) : initials);

                ((TextView) view.findViewById(R.id.tvPoints)).setText(String.valueOf(fPoints));
                ((TextView) view.findViewById(R.id.tvHours)).setText(fHours + "h");
            });
        });

        // Real bookmark count via LiveData
        db.bookmarkDao().getBookmarkCount(userId).observe(getViewLifecycleOwner(), count -> {
            TextView tvSaved = view.findViewById(R.id.tvSaved);
            if (tvSaved != null) tvSaved.setText(String.valueOf(count != null ? count : 0));
        });

        view.findViewById(R.id.btnSettings).setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));

        // Saved events button
        View btnSaved = view.findViewById(R.id.btnSavedEvents);
        if (btnSaved != null) btnSaved.setOnClickListener(v -> startActivity(new Intent(getContext(), SavedActivity.class)));

        // Notification button
        View btnNotif = view.findViewById(R.id.btnNotifications);
        if (btnNotif != null) {
            btnNotif.setOnClickListener(v -> startActivity(new Intent(getContext(), NotificationActivity.class)));
            // Show unread count
            db.notificationDao().getUnreadCount(userId).observe(getViewLifecycleOwner(), count -> {
                TextView tvBadge = view.findViewById(R.id.tvNotifBadge);
                if (tvBadge != null) {
                    if (count != null && count > 0) {
                        tvBadge.setText(count + " mới");
                        tvBadge.setVisibility(View.VISIBLE);
                    } else {
                        tvBadge.setVisibility(View.GONE);
                    }
                }
            });
        }

        // Admin button - only for admins
        View btnAdmin = view.findViewById(R.id.btnAdmin);
        if (btnAdmin != null) {
            if (session.isAdmin()) {
                btnAdmin.setVisibility(View.VISIBLE);
                btnAdmin.setOnClickListener(v -> startActivity(new Intent(getContext(), AdminDashboardActivity.class)));
            } else {
                btnAdmin.setVisibility(View.GONE);
            }
        }

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(getContext(), WelcomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });

        return view;
    }
}
