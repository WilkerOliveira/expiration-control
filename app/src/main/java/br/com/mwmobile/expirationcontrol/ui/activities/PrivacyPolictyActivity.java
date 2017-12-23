package br.com.mwmobile.expirationcontrol.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.ui.activities.base.LifecycleAppCompatActivity;

public class PrivacyPolictyActivity extends LifecycleAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policty);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ((TextView) findViewById(R.id.txtPrivacy)).setText(Html.fromHtml(getString(R.string.privacy_policity_text)));
    }
}
