package com.smartpantry.manager.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.smartpantry.manager.R;
import com.smartpantry.manager.adapters.PantryAdapter;
import com.smartpantry.manager.database.PantryDbHelper;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.utils.AppSettings;
import com.smartpantry.manager.utils.ExpiryUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Shows the pantry list from SQLite and lets the user add, edit and delete items.
 * Pressing and holding an item starts selection mode, where several items can be deleted together.
 */
public class PantryActivity extends BaseNavigationActivity
        implements PantryAdapter.OnPantryItemActionListener {

    private PantryDbHelper dbHelper;
    private AppSettings settings;
    private PantryAdapter adapter;
    private RecyclerView recyclerPantry;
    private TextView textEmpty;
    private TextView textExpiryBanner;
    private TextView textSelectHint;
    private View toolbar;
    private View layoutSelectionBar;
    private TextView textSelectionCount;
    private FloatingActionButton fabAdd;
    private OnBackPressedCallback exitSelectionOnBack;

    @Override
    protected int getCurrentNavItemId() {
        return R.id.action_pantry;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        setUpToolbar(R.string.title_pantry);

        dbHelper = new PantryDbHelper(this);
        settings = new AppSettings(this);

        toolbar = findViewById(R.id.toolbar);
        layoutSelectionBar = findViewById(R.id.layoutSelectionBar);
        textSelectionCount = findViewById(R.id.textSelectionCount);
        recyclerPantry = findViewById(R.id.recyclerPantry);
        textEmpty = findViewById(R.id.textEmpty);
        textExpiryBanner = findViewById(R.id.textExpiryBanner);
        textSelectHint = findViewById(R.id.textSelectHint);
        fabAdd = findViewById(R.id.fabAdd);

        adapter = new PantryAdapter(this);
        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        recyclerPantry.setAdapter(adapter);

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditPantryActivity.class)));

        // While selecting, the Back button should leave selection mode instead of closing the screen
        exitSelectionOnBack = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                adapter.clearSelection();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, exitSelectionOnBack);

        findViewById(R.id.buttonCancelSelection).setOnClickListener(v -> adapter.clearSelection());
        findViewById(R.id.buttonSelectAll).setOnClickListener(v -> adapter.selectAll());
        findViewById(R.id.buttonDeleteSelected).setOnClickListener(v -> confirmDeleteSelected());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload every time we come back so edits, new items and changed settings show immediately
        loadPantryItems();
    }

    private void loadPantryItems() {
        try {
            List<PantryItem> items = dbHelper.getAllItems();
            adapter.setExpiryOptions(settings.isAlertsEnabled(), settings.getExpiringSoonDays());
            adapter.setItems(items); // also refreshes the selection bar through onSelectionChanged

            boolean empty = items.isEmpty();
            textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerPantry.setVisibility(empty ? View.GONE : View.VISIBLE);
            updateExpiryBanner(items);
        } catch (SQLiteException e) {
            showMessage(getString(R.string.error_database));
        }
    }

    /** Shows how many items are expired or expiring soon, when alerts are switched on. */
    private void updateExpiryBanner(List<PantryItem> items) {
        if (!settings.isAlertsEnabled()) {
            textExpiryBanner.setVisibility(View.GONE);
            return;
        }
        int count = ExpiryUtils.countNeedingAttention(items, LocalDate.now(),
                settings.getExpiringSoonDays());
        if (count == 0) {
            textExpiryBanner.setVisibility(View.GONE);
        } else {
            textExpiryBanner.setText(getResources().getQuantityString(
                    R.plurals.pantry_attention_banner, count, count));
            textExpiryBanner.setVisibility(View.VISIBLE);
        }
    }

    // ------------------------------------------------------------ list actions

    @Override
    public void onEditClicked(PantryItem item) {
        // Only the id is passed. The edit screen loads the item from the database itself.
        Intent intent = new Intent(this, AddEditPantryActivity.class);
        intent.putExtra(AddEditPantryActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClicked(PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, item.getName()))
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_delete, (dialog, which) -> deleteItem(item))
                .show();
    }

    private void deleteItem(PantryItem item) {
        try {
            int rowsDeleted = dbHelper.deleteItem(item.getId());
            if (rowsDeleted > 0) {
                loadPantryItems();
                showMessage(getString(R.string.message_item_deleted, item.getName()));
                return;
            }
        } catch (SQLiteException e) {
            // falls through to the error message below
        }
        showMessage(getString(R.string.error_database));
    }

    // ------------------------------------------------------------ selection mode

    /** Switches between the normal toolbar and the selection bar. */
    @Override
    public void onSelectionChanged(boolean selectionMode, int selectedCount) {
        toolbar.setVisibility(selectionMode ? View.GONE : View.VISIBLE);
        layoutSelectionBar.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
        exitSelectionOnBack.setEnabled(selectionMode);

        if (selectionMode) {
            textSelectionCount.setText(getResources().getQuantityString(
                    R.plurals.selection_count, selectedCount, selectedCount));
            fabAdd.hide();
        } else {
            fabAdd.show();
        }
        // The tip is only useful when there is something to select
        textSelectHint.setVisibility(!selectionMode && adapter.getItemCount() > 0
                ? View.VISIBLE : View.GONE);
    }

    private void confirmDeleteSelected() {
        int count = adapter.getSelectedCount();
        if (count == 0) {
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_delete_selected_title)
                .setMessage(getResources().getQuantityString(
                        R.plurals.dialog_delete_selected_message, count, count))
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_delete, (dialog, which) -> deleteSelected())
                .show();
    }

    private void deleteSelected() {
        List<Long> ids = adapter.getSelectedIds();
        try {
            int deleted = dbHelper.deleteItems(ids);
            adapter.clearSelection();
            loadPantryItems();
            showMessage(getResources().getQuantityString(
                    R.plurals.message_items_deleted, deleted, deleted));
            return;
        } catch (SQLiteException e) {
            // falls through to the error message below
        }
        showMessage(getString(R.string.error_database));
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.pantryRoot), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
