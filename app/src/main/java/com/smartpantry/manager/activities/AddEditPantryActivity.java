package com.smartpantry.manager.activities;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.smartpantry.manager.R;
import com.smartpantry.manager.database.PantryDbHelper;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.utils.FormatUtils;
import com.smartpantry.manager.utils.PantryValidator;

import java.util.Calendar;
import java.util.Locale;

/**
 * One screen used for both adding and editing an ingredient.
 * If an item id is passed in the Intent we are editing, otherwise we are adding.
 */
public class AddEditPantryActivity extends BaseNavigationActivity {

    public static final String EXTRA_ITEM_ID = "com.smartpantry.manager.EXTRA_ITEM_ID";

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputLayout layoutUnit;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputEditText editExpiry;
    private AutoCompleteTextView dropdownUnit;

    private PantryDbHelper dbHelper;
    private PantryItem existingItem; // null when adding a new item

    /** A form should not offer a way to wander off and lose what has been typed. */
    @Override
    protected boolean showNavigationMenu() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_pantry);
        setUpToolbar("");

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        layoutUnit = findViewById(R.id.layoutUnit);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        dropdownUnit = findViewById(R.id.dropdownUnit);
        MaterialButton buttonSave = findViewById(R.id.buttonSave);
        MaterialButton buttonClearDate = findViewById(R.id.buttonClearDate);

        dbHelper = new PantryDbHelper(this);

        // An error message disappears as soon as the user starts fixing that field
        clearErrorOnEdit(editName, layoutName);
        clearErrorOnEdit(editQuantity, layoutQuantity);

        dropdownUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, PantryValidator.UNITS));
        dropdownUnit.setOnItemClickListener((parent, view, position, id) -> layoutUnit.setError(null));

        long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, PantryItem.NO_ID);
        if (itemId != PantryItem.NO_ID) {
            try {
                existingItem = dbHelper.getItemById(itemId);
            } catch (SQLiteException e) {
                existingItem = null; // handled together with "not found" below
            }
            if (existingItem == null) {
                // The row may have been deleted already, so there is nothing to edit
                Toast.makeText(this, R.string.error_item_not_found, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            setTitle(R.string.title_edit_ingredient);
            fillFormFromItem(existingItem);
        } else {
            setTitle(R.string.title_add_ingredient);
        }

        editExpiry.setOnClickListener(v -> showDatePicker());
        buttonClearDate.setOnClickListener(v -> editExpiry.setText(""));
        buttonSave.setOnClickListener(v -> savePantryItem());
    }

    private void clearErrorOnEdit(TextInputEditText field, TextInputLayout layout) {
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                layout.setError(null);
            }
        });
    }

    private void fillFormFromItem(PantryItem item) {
        editName.setText(item.getName());
        editQuantity.setText(FormatUtils.formatQuantity(item.getQuantity()));
        // false = do not filter the dropdown list by the text we just set
        dropdownUnit.setText(item.getUnit(), false);
        if (item.getExpiryDate() != null) {
            editExpiry.setText(item.getExpiryDate());
        }
    }

    private void savePantryItem() {
        String name = PantryValidator.normaliseName(textOf(editName));
        String quantityText = textOf(editQuantity);
        String unit = dropdownUnit.getText().toString().trim();

        int nameError = PantryValidator.validateName(name);
        int quantityError = PantryValidator.validateQuantity(quantityText);
        int unitError = PantryValidator.validateUnit(unit);

        // Show every error at once so the user can fix the whole form in one go
        layoutName.setError(nameError == PantryValidator.VALID ? null : getString(nameError));
        layoutQuantity.setError(quantityError == PantryValidator.VALID ? null : getString(quantityError));
        layoutUnit.setError(unitError == PantryValidator.VALID ? null : getString(unitError));

        if (nameError != PantryValidator.VALID) {
            editName.requestFocus();
            return;
        }
        if (quantityError != PantryValidator.VALID) {
            editQuantity.requestFocus();
            return;
        }
        if (unitError != PantryValidator.VALID) {
            return;
        }

        double quantity = PantryValidator.parseQuantity(quantityText);
        String expiry = textOf(editExpiry).trim();
        if (expiry.isEmpty()) {
            expiry = null; // the expiry date is optional
        }

        boolean saved;
        try {
            if (existingItem == null) {
                saved = dbHelper.insertItem(new PantryItem(name, quantity, unit, expiry)) != -1;
            } else {
                saved = dbHelper.updateItem(
                        new PantryItem(existingItem.getId(), name, quantity, unit, expiry)) > 0;
            }
        } catch (SQLiteException e) {
            saved = false; // shown to the user below
        }

        if (saved) {
            Toast.makeText(this, R.string.message_item_saved, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_LONG).show();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        String current = textOf(editExpiry).trim();
        if (!current.isEmpty()) {
            // Start the picker on the date already chosen (stored as yyyy-MM-dd)
            String[] parts = current.split("-");
            if (parts.length == 3) {
                try {
                    calendar.set(Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]) - 1,
                            Integer.parseInt(parts[2]));
                } catch (NumberFormatException e) {
                    // keep today's date if the text is not a date
                }
            }
        }

        new DatePickerDialog(this,
                (picker, year, month, day) -> editExpiry.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private String textOf(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
