package com.lichsuvietnam.app.ui.adapters;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.utils.ImageUtils;
import com.lichsuvietnam.app.data.database.entities.HistoricalFigureEntity;
import java.util.List;

public class FigureAdapter extends RecyclerView.Adapter<FigureAdapter.VH> {
    private List<HistoricalFigureEntity> figures;
    private final OnFigureClick listener;
    public interface OnFigureClick { void onClick(HistoricalFigureEntity figure); }

    public FigureAdapter(List<HistoricalFigureEntity> figures, OnFigureClick listener) {
        this.figures = figures;
        this.listener = listener;
    }

    public void updateData(List<HistoricalFigureEntity> newData) {
        this.figures = newData;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_figure, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        HistoricalFigureEntity f = figures.get(pos);
        h.txtName.setText(f.name);
        h.txtTitle.setText(f.title != null ? f.title : "");

        if (h.txtShortDesc != null) {
            h.txtShortDesc.setText(f.shortDesc != null ? f.shortDesc : "");
            h.txtShortDesc.setVisibility(f.shortDesc != null ? View.VISIBLE : View.GONE);
        }

        String dynasty = f.dynasty != null ? f.dynasty : "";
        h.txtDynasty.setText(dynasty + "  •  " + f.formatLifeSpan());

        // Avatar: image or initial
        if (f.imageUrl != null && !f.imageUrl.isEmpty() && h.imgAvatar != null) {
            h.txtInitial.setVisibility(View.GONE);
            h.imgAvatar.setVisibility(View.VISIBLE);
            ImageUtils.loadCircle(h.itemView.getContext(), f.imageUrl, h.imgAvatar);
        } else {
            h.txtInitial.setVisibility(View.VISIBLE);
            if (h.imgAvatar != null) h.imgAvatar.setVisibility(View.GONE);
            h.txtInitial.setText(f.name.substring(0, 1));
            int[] colors = {0xFF8C1D18, 0xFF8B6914, 0xFF2E5A3A, 0xFF1A3C6B, 0xFF6B4A14, 0xFF4A2D6B};
            h.avatarBg.getBackground().setTint(colors[pos % colors.length]);
        }

        h.txtPeriod.setText(f.period != null ? f.period : "");
        h.txtPeriod.setVisibility(f.period != null ? View.VISIBLE : View.GONE);

        if (h.txtRole != null) {
            h.txtRole.setText(f.role != null ? f.role : "");
            h.txtRole.setVisibility(f.role != null ? View.VISIBLE : View.GONE);
        }

        h.itemView.setOnClickListener(v -> listener.onClick(f));
    }

    @Override public int getItemCount() { return figures != null ? figures.size() : 0; }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtName, txtTitle, txtDynasty, txtInitial, txtPeriod, txtRole, txtShortDesc;
        ImageView imgAvatar;
        FrameLayout avatarBg;
        VH(View v) {
            super(v);
            txtName = v.findViewById(R.id.txtFigureName);
            txtTitle = v.findViewById(R.id.txtFigureTitle);
            txtDynasty = v.findViewById(R.id.txtFigureDynasty);
            txtInitial = v.findViewById(R.id.txtFigureInitial);
            txtPeriod = v.findViewById(R.id.txtFigurePeriod);
            txtRole = v.findViewById(R.id.txtFigureRole);
            txtShortDesc = v.findViewById(R.id.txtFigureShortDesc);
            imgAvatar = v.findViewById(R.id.imgFigureAvatar);
            avatarBg = v.findViewById(R.id.figureAvatarBg);
        }
    }
}
