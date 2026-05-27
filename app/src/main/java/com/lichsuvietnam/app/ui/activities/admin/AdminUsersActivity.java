package com.lichsuvietnam.app.ui.activities.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.database.AppDatabase;
import com.lichsuvietnam.app.data.database.entities.UserEntity;
import com.lichsuvietnam.app.utils.SessionManager;
import java.util.List;
import java.util.concurrent.Executors;

public class AdminUsersActivity extends AppCompatActivity {
    private AppDatabase db;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager session = new SessionManager(this);
        if (!session.isAdmin()) { finish(); return; }

        setContentView(R.layout.activity_admin_users);
        db = AppDatabase.getInstance(this);

        findViewById(R.id.btnBackAdmin).setOnClickListener(v -> finish());
        rv = findViewById(R.id.rvUsers);
        rv.setLayoutManager(new LinearLayoutManager(this));

        EditText etSearch = findViewById(R.id.etSearchUsers);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String q = s.toString().trim();
                if (q.length() >= 2) searchUsers(q);
                else loadUsers();
            }
        });

        loadUsers();
    }

    private void loadUsers() {
        db.userDao().getAllUsers().observe(this, this::showUsers);
    }

    private void searchUsers(String q) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<UserEntity> results = db.userDao().searchUsers(q);
            runOnUiThread(() -> showUsers(results));
        });
    }

    private void showUsers(List<UserEntity> users) {
        if (users == null) return;
        rv.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LinearLayout row = new LinearLayout(AdminUsersActivity.this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setBackgroundResource(R.drawable.bg_rounded_card);
                row.setPadding(dp(14), dp(12), dp(14), dp(12));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = dp(8); lp.leftMargin = dp(16); lp.rightMargin = dp(16);
                row.setLayoutParams(lp);

                LinearLayout info = new LinearLayout(AdminUsersActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);
                info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                TextView tvName = new TextView(AdminUsersActivity.this); tvName.setId(android.R.id.text1); tvName.setTextSize(14); tvName.setTextColor(getColor(R.color.text_primary)); tvName.setTypeface(null, android.graphics.Typeface.BOLD);
                TextView tvEmail = new TextView(AdminUsersActivity.this); tvEmail.setId(android.R.id.text2); tvEmail.setTextSize(11); tvEmail.setTextColor(getColor(R.color.text_tertiary));
                TextView tvRole = new TextView(AdminUsersActivity.this); tvRole.setId(android.R.id.message); tvRole.setTextSize(10);
                info.addView(tvName); info.addView(tvEmail); info.addView(tvRole);
                row.addView(info);

                Button btnAction = new Button(AdminUsersActivity.this); btnAction.setId(android.R.id.button1); btnAction.setTextSize(10); btnAction.setAllCaps(false);
                btnAction.setBackgroundResource(R.drawable.bg_chip); btnAction.setPadding(dp(8), dp(4), dp(8), dp(4));
                btnAction.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(36)));
                row.addView(btnAction);
                return new RecyclerView.ViewHolder(row) {};
            }
            @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int pos) {
                UserEntity u = users.get(pos);
                ((TextView) h.itemView.findViewById(android.R.id.text1)).setText(u.name);
                ((TextView) h.itemView.findViewById(android.R.id.text2)).setText(u.email);
                TextView tvRole = h.itemView.findViewById(android.R.id.message);
                String status = ("admin".equals(u.role) ? "Admin" : "User") + (u.isActive ? "" : " • Bị khóa");
                tvRole.setText(status);
                tvRole.setTextColor(u.isActive ? getColor(R.color.success) : getColor(R.color.error));

                Button btn = h.itemView.findViewById(android.R.id.button1);
                btn.setText("Thao tác");
                btn.setOnClickListener(v -> showUserActions(u));
            }
            @Override public int getItemCount() { return users.size(); }
        });
    }

    private void showUserActions(UserEntity user) {
        String[] items = {
            user.isActive ? "Khóa tài khoản" : "Mở khóa tài khoản",
            "admin".equals(user.role) ? "Hạ quyền về user" : "Cấp quyền admin"
        };
        new AlertDialog.Builder(this).setTitle(user.name).setItems(items, (d, which) -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                if (which == 0) {
                    db.userDao().updateActiveStatus(user.id, !user.isActive);
                } else {
                    String newRole = "admin".equals(user.role) ? "user" : "admin";
                    db.userDao().updateRole(user.id, newRole);
                }
                runOnUiThread(() -> { Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show(); loadUsers(); });
            });
        }).show();
    }

    private int dp(int dp) { return (int)(dp * getResources().getDisplayMetrics().density); }
}
