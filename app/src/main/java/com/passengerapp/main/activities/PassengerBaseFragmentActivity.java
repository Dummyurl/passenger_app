package com.passengerapp.main.activities;
//2015-10-14 Auto Jacobi
import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.passengerapp.R;
import com.passengerapp.main.services.rest.WebInterface;
import com.passengerapp.util.Utils;

/**
 * Created by adventis on 10/7/15.
 */
public class PassengerBaseFragmentActivity extends FragmentActivity {
    private ProgressDialog progressDialog = null;
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

    public void showToastLong(String msg) {
        Toast.makeText(
                getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT).show();
    }

    public void DisplayProcessMessage(final String msg) {
        showProcessingDialog(msg);
    }

    public void DisplayProcessMessage(final boolean hide) {
        hideProcessingDialog();
    }

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
