package com.ecosystem.kin.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;

import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.exception.TaskFailedException;
import com.kin.ecosystem.util.StringUtil;
import io.jsonwebtoken.Jwts;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Ecosystem - SampleApp";

    private TextView balanceView;
    private static final String GET_BALANCE = "Get Balance: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        balanceView = findViewById(R.id.get_balance);
        balanceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBalance();
            }
        });
        findViewById(R.id.launch_marketplace).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openKinMarketplace();
            }
        });
        findViewById(R.id.native_spend_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createNativeSpendOffer();
            }
        });
    }

    private void getBalance() {
        try {
            Kin.getBalance(new Callback<Integer>() {
                @Override
                public void onResponse(Integer balance) {
                    balanceView.setText(getSpannableBalance(balance));
                }

                @Override
                public void onFailure(Throwable t) {
                    balanceView.setText(R.string.failed_to_get_balance);
                }
            });
        } catch (TaskFailedException e) {
            balanceView.setText(R.string.failed_to_get_balance);
            e.printStackTrace();
        }
    }

    private SpannableString getSpannableBalance(Integer balance) {
        StringBuilder text = new StringBuilder(GET_BALANCE);
        text.append(StringUtil.getAmountFormatted(balance));

        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(),0, GET_BALANCE.length() - 1,0);

        return spannableString;
    }

    private void openKinMarketplace() {
        try {
            Kin.launchMarketplace(MainActivity.this);
        } catch (TaskFailedException e) {
            e.printStackTrace();
        }
    }

    private void createNativeSpendOffer() {
        String offerJwt = JwtUtil.generateSpendOfferExampleJWT(BuildConfig.SAMPLE_APP_ID);
        Log.d(TAG, "createNativeSpendOffer: " + offerJwt);
        try {
            Kin.purchase(offerJwt, new Callback<String>() {
                @Override
                public void onResponse(String jwtConfirmation) {
                    showToast("Succeed to create native spend");
                    Log.d(TAG, "Jwt confirmation: \n" + jwtConfirmation);
                }

                @Override
                public void onFailure(Throwable t) {
                    showToast("Failed - " + t.getMessage());
                }
            });
        } catch (TaskFailedException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
