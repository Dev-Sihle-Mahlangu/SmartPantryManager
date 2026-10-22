package com.smartpantry.manager.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.smartpantry.manager.R;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.utils.AppSettings;
import com.smartpantry.manager.utils.ExpiryUtils;
import com.smartpantry.manager.utils.FormatUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Shows the pantry items in a RecyclerView. It also keeps track of which items are
 * selected when the user is choosing several ingredients to delete.
 * The Activity decides what Edit and Delete actually do.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnPantryItemActionListener {
        void onEditClicked(PantryItem item);

        void onDeleteClicked(PantryItem item);

        /** Called whenever selection mode starts, stops, or the number of selected items changes. */
        void onSelectionChanged(boolean selectionMode, int selectedCount);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnPantryItemActionListener listener;

    // Ids of the selected items. A set means selecting the same item twice does nothing odd.
    private final Set<Long> selectedIds = new LinkedHashSet<>();
    private boolean selectionMode = false;

    // Taken from the Settings screen
    private boolean alertsEnabled = true;
    private int soonDays = AppSettings.DEFAULT_SOON_DAYS;

    public PantryAdapter(OnPantryItemActionListener listener) {
        this.listener = listener;
    }

    /** Tells the adapter whether to highlight items that are expiring soon, and how many days counts as soon. */
    public void setExpiryOptions(boolean alertsEnabled, int soonDays) {
        this.alertsEnabled = alertsEnabled;
        this.soonDays = soonDays;
    }

    /** Replaces the list contents after the database has been read again. */
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);

        // Forget selected items that no longer exist, for example after a delete
        Set<Long> existingIds = new HashSet<>();
        for (PantryItem item : items) {
            existingIds.add(item.getId());
        }
        selectedIds.retainAll(existingIds);
        if (selectedIds.isEmpty()) {
            selectionMode = false;
        }

        notifyDataSetChanged();
        listener.onSelectionChanged(selectionMode, selectedIds.size());
    }

    // ------------------------------------------------------------ selection

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public int getSelectedCount() {
        return selectedIds.size();
    }

    public List<Long> getSelectedIds() {
        return new ArrayList<>(selectedIds);
    }

    public void selectAll() {
        for (PantryItem item : items) {
            selectedIds.add(item.getId());
        }
        selectionMode = !selectedIds.isEmpty();
        selectionChanged();
    }

    /** Leaves selection mode and unticks everything. */
    public void clearSelection() {
        selectedIds.clear();
        selectionMode = false;
        selectionChanged();
    }

    private void startSelection(PantryItem item) {
        selectionMode = true;
        selectedIds.add(item.getId());
        selectionChanged();
    }

    private void toggleSelection(PantryItem item) {
        long id = item.getId();
        if (!selectedIds.remove(id)) {
            selectedIds.add(id);
        }
        // Unticking the last item ends selection mode
        if (selectedIds.isEmpty()) {
            selectionMode = false;
        }
        selectionChanged();
    }

    private void selectionChanged() {
        notifyDataSetChanged();
        listener.onSelectionChanged(selectionMode, selectedIds.size());
    }

    // ------------------------------------------------------------ RecyclerView

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
        bindExpiry(holder.textExpiry, item.getExpiryDate(), context);

        // In selection mode the checkbox replaces the Edit and Delete buttons
        boolean selected = selectedIds.contains(item.getId());
        holder.checkSelect.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
        holder.checkSelect.setChecked(selected);
        holder.layoutActions.setVisibility(selectionMode ? View.GONE : View.VISIBLE);
        if (selected) {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.pantry_green_light));
        } else {
            holder.card.setCardBackgroundColor(holder.defaultCardColor);
        }

        // Tap: pick the item in selection mode, otherwise open it for editing.
        // Long press: start selecting (or pick another item when already selecting).
        holder.card.setOnClickListener(v -> {
            if (selectionMode) {
                toggleSelection(item);
            } else {
                listener.onEditClicked(item);
            }
        });
        holder.card.setOnLongClickListener(v -> {
            if (selectionMode) {
                toggleSelection(item);
            } else {
                startSelection(item);
            }
            return true;
        });

        holder.buttonEdit.setOnClickListener(v -> listener.onEditClicked(item));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClicked(item));
    }

    private void bindExpiry(TextView view, String expiry, Context context) {
        LocalDate today = LocalDate.now();
        ExpiryUtils.Status status = ExpiryUtils.getStatus(expiry, today, soonDays);
        if (!alertsEnabled && status == ExpiryUtils.Status.EXPIRING_SOON) {
            status = ExpiryUtils.Status.OK; // alerts are off, so show it like any other date
        }

        int colorRes = R.color.pantry_text_secondary;
        switch (status) {
            case NONE:
                view.setText(R.string.pantry_expiry_none);
                break;
            case EXPIRED:
                view.setText(context.getString(R.string.pantry_expired_format, expiry));
                colorRes = R.color.pantry_red;
                break;
            case EXPIRING_SOON:
                int days = ExpiryUtils.daysUntil(expiry, today).intValue();
                if (days == 0) {
                    view.setText(context.getString(R.string.pantry_expires_today, expiry));
                } else {
                    view.setText(context.getResources().getQuantityString(
                            R.plurals.pantry_expiring_soon, days, days, expiry));
                }
                colorRes = R.color.pantry_warning;
                break;
            default:
                view.setText(context.getString(R.string.pantry_expires_format, expiry));
                break;
        }
        // Set the colour every time, because RecyclerView reuses rows
        view.setTextColor(ContextCompat.getColor(context, colorRes));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final ColorStateList defaultCardColor; // remembered so a deselected card can go back to it
        final TextView textName;
        final TextView textQuantity;
        final TextView textExpiry;
        final MaterialCheckBox checkSelect;
        final View layoutActions;
        final MaterialButton buttonEdit;
        final MaterialButton buttonDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView;
            defaultCardColor = card.getCardBackgroundColor();
            textName = itemView.findViewById(R.id.textItemName);
            textQuantity = itemView.findViewById(R.id.textItemQuantity);
            textExpiry = itemView.findViewById(R.id.textItemExpiry);
            checkSelect = itemView.findViewById(R.id.checkSelect);
            layoutActions = itemView.findViewById(R.id.layoutItemActions);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
