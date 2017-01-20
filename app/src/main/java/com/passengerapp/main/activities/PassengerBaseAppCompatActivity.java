package com.passengerapp.main.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.passengerapp.R;
import com.passengerapp.main.services.rest.WebInterface;
import com.passengerapp.util.Utils;

/**
 * Created by adventis on 10/17/15.
 */
public class PassengerBaseAppCompatActivity extends AppCompatActivity {


    public boolean isNetworkAvailable() {
        if (!WebInterface.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(
                    getApplicationContext(),
                    Utils.getStringById(R.string.common_no_internet_connections_after_smth),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void DisplayProcessMessage(final String msg) {
        showProcessingDialog(msg);
    }

    public void DisplayProcessMessage(final boolean hide) {

        hideProcessingDialog();

    }

    private ProgressDialog progressDialog = null;

    private void showProcessingDialog(String msg) {
        if (progressDialog == null)
            progressDialog = ProgressDialog.show(this, "", msg, true, false);
    }

    private void hideProcessingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
