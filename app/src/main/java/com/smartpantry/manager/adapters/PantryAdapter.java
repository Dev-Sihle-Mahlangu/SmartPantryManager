package com.smartpantry.manager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.smartpantry.manager.R;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Shows the pantry items in a RecyclerView. The Activity decides what Edit and Delete do. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnPantryItemActionListener {
        void onEditClicked(PantryItem item);

        void onDeleteClicked(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnPantryItemActionListener listener;

    public PantryAdapter(OnPantryItemActionListener listener) {
        this.listener = listener;
    }

    /** Replaces the list contents after the database has been read again. */
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.textName.setText(item.getName());
        holder.textQuantity.setText(context.getString(R.string.pantry_quantity_format,
                FormatUtils.formatQuantity(item.getQuantity()), item.getUnit()));

        // yyyy-MM-dd text can be compared directly with today's date in the same format
        String expiry = item.getExpiryDate();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        if (expiry == null || expiry.isEmpty()) {
            holder.textExpiry.setText(R.string.pantry_expiry_none);
            holder.textExpiry.setTextColor(ContextCompat.getColor(context, R.color.pantry_text_secondary));
        } else if (expiry.compareTo(today) < 0) {
            holder.textExpiry.setText(context.getString(R.string.pantry_expired_format, expiry));
            holder.textExpiry.setTextColor(ContextCompat.getColor(context, R.color.pantry_red));
        } else {
            holder.textExpiry.setText(context.getString(R.string.pantry_expires_format, expiry));
            holder.textExpiry.setTextColor(ContextCompat.getColor(context, R.color.pantry_text_secondary));
        }

        holder.buttonEdit.setOnClickListener(v -> listener.onEditClicked(item));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClicked(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textQuantity;
        final TextView textExpiry;
        final MaterialButton buttonEdit;
        final MaterialButton buttonDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textItemName);
            textQuantity = itemView.findViewById(R.id.textItemQuantity);
            textExpiry = itemView.findViewById(R.id.textItemExpiry);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
