package com.smartpantry.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.smartpantry.manager.R;
import com.smartpantry.manager.adapters.PantryAdapter;
import com.smartpantry.manager.database.PantryDbHelper;
import com.smartpantry.manager.models.PantryItem;

import java.util.List;

/** Shows the pantry list from SQLite and lets the user add, edit and delete items. */
public class PantryActivity extends AppCompatActivity
        implements PantryAdapter.OnPantryItemActionListener {

    private PantryDbHelper dbHelper;
    private PantryAdapter adapter;
    private RecyclerView recyclerPantry;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_pantry);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new PantryDbHelper(this);

        recyclerPantry = findViewById(R.id.recyclerPantry);
        textEmpty = findViewById(R.id.textEmpty);
        adapter = new PantryAdapter(this);
        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        recyclerPantry.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditPantryActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload every time we come back so edits and new items show immediately
        loadPantryItems();
    }

    private void loadPantryItems() {
        List<PantryItem> items = dbHelper.getAllItems();
        adapter.setItems(items);

        boolean empty = items.isEmpty();
        textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerPantry.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

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
        int rowsDeleted = dbHelper.deleteItem(item.getId());
        if (rowsDeleted > 0) {
            loadPantryItems();
            Snackbar.make(findViewById(R.id.pantryRoot),
                    getString(R.string.message_item_deleted, item.getName()),
                    Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(findViewById(R.id.pantryRoot), R.string.error_database,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
