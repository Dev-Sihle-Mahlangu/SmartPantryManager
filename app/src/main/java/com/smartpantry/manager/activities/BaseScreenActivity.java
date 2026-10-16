package com.smartpantry.manager.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.smartpantry.manager.R;

/**
 * Shared base for screens that are not built yet. It shows a toolbar with an
 * Up button and a short message. Each real screen will replace its subclass
 * in a later phase.
 */
public abstract class BaseScreenActivity extends AppCompatActivity {

    @StringRes
    protected abstract int getScreenTitleRes();

    @StringRes
    protected abstract int getPlaceholderMessageRes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getScreenTitleRes());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView message = findViewById(R.id.textPlaceholder);
        message.setText(getPlaceholderMessageRes());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // The Up arrow simply closes this screen and returns to the previous one
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
