package com.lichsuvietnam.app.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.ui.activities.*;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.HistoryEventEntity;
import com.lichsuvietnam.app.utils.SessionManager;

public class HomeFragment extends Fragment {
    private AppDatabase db;
    private SessionManager session;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        db = AppDatabase.getInstance(requireContext());
        session = new SessionManager(requireContext());

        // Dynamic greeting from login
        TextView txtGreeting = view.findViewById(R.id.txtGreeting);
        if (session.isLoggedIn()) {
            String name = session.getUserName();
            txtGreeting.setText("Xin chào, " + name + " \uD83D\uDC4B");
        } else if (session.isGuest()) {
            txtGreeting.setText("Xin chào, Khách \uD83D\uDC4B");
        } else {
            txtGreeting.setText("Xin chào \uD83D\uDC4B");
        }

        // Search bar → SearchActivity
        view.findViewById(R.id.searchBar).setOnClickListener(v ->
            startActivity(new Intent(getContext(), SearchActivity.class)));

        // Figures button → FigureListActivity
        view.findViewById(R.id.btnFigures).setOnClickListener(v ->
            startActivity(new Intent(getContext(), FigureListActivity.class)));

        // Feature grid: Bản Đồ TG → TimeMapFragment (tab Bản đồ)
        view.findViewById(R.id.btnFeatureTimemap).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).selectTab(R.id.nav_map);
        });

        // Sự Kiện → EventListActivity
        view.findViewById(R.id.btnFeatureEvents).setOnClickListener(v ->
            startActivity(new Intent(getContext(), EventListActivity.class)));

        // Hình Ảnh → ImageGalleryActivity
        view.findViewById(R.id.btnFeatureImages).setOnClickListener(v ->
            startActivity(new Intent(getContext(), ImageGalleryActivity.class)));

        // Video → VideoListActivity
        view.findViewById(R.id.btnFeatureVideo).setOnClickListener(v ->
            startActivity(new Intent(getContext(), VideoListActivity.class)));

        // Học Tập → Learn tab
        view.findViewById(R.id.btnFeatureLearn).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).selectTab(R.id.nav_learn);
        });

        // TG Động → DynamicTimelineFragment (standalone, not a tab)
        view.findViewById(R.id.btnFeatureDynamic).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).loadFragment(new DynamicTimelineFragment());
        });

        // Tìm Kiếm → SearchActivity
        view.findViewById(R.id.btnFeatureSearch).setOnClickListener(v ->
            startActivity(new Intent(getContext(), SearchActivity.class)));

        // Cộng Đồng → Community tab
        view.findViewById(R.id.btnFeatureCommunity).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).selectTab(R.id.nav_community);
        });

        // Featured event card
        view.findViewById(R.id.featuredEventCard).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EventDetailActivity.class);
            intent.putExtra("event_id", 10L);
            startActivity(intent);
        });

        // See more → EventListActivity
        view.findViewById(R.id.btnSeeMore).setOnClickListener(v ->
            startActivity(new Intent(getContext(), EventListActivity.class)));

        // Load recent events
        db.historyEventDao().getAllEvents().observe(getViewLifecycleOwner(), events -> {
            LinearLayout container2 = view.findViewById(R.id.recentEventsContainer);
            container2.removeAllViews();
            int count = 0;
            for (int i = events.size() - 1; i >= 0 && count < 4; i--, count++) {
                HistoryEventEntity event = events.get(i);
                container2.addView(createRecentEventItem(event));
            }
        });

        return view;
    }

    private View createRecentEventItem(HistoryEventEntity event) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundResource(R.drawable.bg_rounded_card);
        row.setPadding(dp(14), dp(12), dp(14), dp(12));
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rlp.bottomMargin = dp(8);
        row.setLayoutParams(rlp);

        TextView yearTv = new TextView(requireContext());
        yearTv.setText(event.year);
        yearTv.setBackgroundResource(R.drawable.bg_year_tag);
        yearTv.setPadding(dp(10), dp(6), dp(10), dp(6));
        yearTv.setTextColor(0xFFFFFFFF);
        yearTv.setTextSize(13);
        yearTv.setTypeface(null, Typeface.BOLD);
        row.addView(yearTv);

        LinearLayout info = new LinearLayout(requireContext());
        info.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        ilp.setMarginStart(dp(14));
        info.setLayoutParams(ilp);

        TextView titleTv = new TextView(requireContext());
        titleTv.setText(event.title);
        titleTv.setTextColor(requireContext().getColor(R.color.text_primary));
        titleTv.setTextSize(15);
        titleTv.setTypeface(null, Typeface.BOLD);
        titleTv.setMaxLines(1);
        titleTv.setEllipsize(android.text.TextUtils.TruncateAt.END);
        info.addView(titleTv);

        String subtitle = event.year + " · " + (event.location != null ? event.location : "");
        TextView subtitleTv = new TextView(requireContext());
        subtitleTv.setText(subtitle);
        subtitleTv.setTextColor(requireContext().getColor(R.color.text_tertiary));
        subtitleTv.setTextSize(12);
        subtitleTv.setPadding(0, dp(2), 0, 0);
        info.addView(subtitleTv);

        row.addView(info);

        row.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EventDetailActivity.class);
            intent.putExtra("event_id", event.id);
            startActivity(intent);
        });
        return row;
    }

    private int dp(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
