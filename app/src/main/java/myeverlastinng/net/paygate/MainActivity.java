package myeverlastinng.net.paygate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.inapp.PayWithCard;
import com.interswitchng.sdk.payment.android.inapp.PayWithToken;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.interswitchng.sdk.util.RandomString;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private Activity activity;
    private Context context;
    private String tref;
    private Button doPay;
    private Button ptoken;
    private TextView txtStatus;
    private TextView token;
    private TextView expiry;
    private EditText amt;
    String pref = "mypref";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        Passport.overrideApiBase(Passport.LIVE_API_BASE);
        Payment.overrideApiBase(Payment.LIVE_API_BASE);

        amt = (EditText) findViewById(R.id.editAmt);

        doPay = (Button) findViewById(R.id.btnPay);
        ptoken = (Button) findViewById(R.id.btntoken);
        token = (TextView) findViewById(R.id.txttoken);
        expiry = (TextView) findViewById(R.id.txtExpiry);

        doPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tref = RandomString.numeric(12);
                String amtvalue = amt.getText().toString().trim();
                final RequestOptions options = RequestOptions.builder().setClientId("IKIA8AFD010B509D5A05A0B139879B50E6E4B4D24EF3").setClientSecret("Ch+VOI6P6nLYuhIasogQi7/T54Ro2O8kkW8sywChuwA=").build();
                PayWithCard payWithCard = new PayWithCard(activity,"BABA001","Demo test",amtvalue,"NGN",options, new IswCallback<PurchaseResponse>() {
                    @Override
                    public void onError(Exception error){
                        Util.notify(context, "ERROR", error.getMessage(), "Close", false);
                        System.out.println(error);
                        Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG);
                        Log.d("Debug", error.getMessage());
                        txtStatus = (TextView) findViewById(R.id.txtStatus);
                        txtStatus.setText("Error: " + error.getMessage());

                    }
                    @Override
                    public void onSuccess(final PurchaseResponse purchaseResponse){
                        //SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = getSharedPreferences(pref, MODE_PRIVATE).edit();
                        editor.putString("token", purchaseResponse.getToken());
                        editor.putString("tokenExpiry", purchaseResponse.getTokenExpiryDate());
                        editor.commit();

                        String ref = purchaseResponse.getTransactionIdentifier();
                        Util.notify(context,"Success","Ref: "+ ref+ " Tranx Ref : " + purchaseResponse.getTransactionRef(),"Close", false);
                        System.out.println("Success : " + ref);
                        Toast.makeText(getApplicationContext(), "Success: "+ref,Toast.LENGTH_LONG);
                        txtStatus = (TextView) findViewById(R.id.txtStatus);
                        txtStatus.setText("Success: " + ref);
                    }
                });
                payWithCard.start();
            }
        });

        ptoken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences(pref, MODE_PRIVATE);
                String tk = sharedPref.getString("token", null);
                String dat = sharedPref.getString("tokenExpiry", null);
                token.setText(tk);
                expiry.setText(dat);
                System.out.println(tk + " " + dat);
                RequestOptions options = RequestOptions.builder()
                        .setClientId("IKIA8AFD010B509D5A05A0B139879B50E6E4B4D24EF3")
                        .setClientSecret("Ch+VOI6P6nLYuhIasogQi7/T54Ro2O8kkW8sywChuwA=")
                        .build();
                PayWithToken payWithToken = new PayWithToken(activity, "BABA001", "25", "5061762203217534", "1709", "NGN",
                        "MasterCard", "4751", "TEST", options, new IswCallback<PurchaseResponse>() {
                    @Override
                    public void onError(Exception error) {
                        // Handle error
                        // Payment not successful.
                        System.out.println(error);
                        Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG);
                        Log.d("Debug", error.getMessage());
                        txtStatus = (TextView) findViewById(R.id.txtStatus);
                        txtStatus.setText("Error: " + error.getMessage());
                    }

                    @Override
                    public void onSuccess(final PurchaseResponse response) {
            /* Handle success
               Payment successful. The response object contains fields transactionIdentifier,
               message, amount, token, tokenExpiryDate, panLast4Digits, transactionRef and cardType.
               Save the token, tokenExpiryDate cardType and panLast4Digits
               in order to pay with the token in the future.
            */
                        String ref = response.getTransactionIdentifier();
                        Util.notify(context,"Success","Ref: "+ ref+ " Tranx Ref : " + response.getTransactionRef(),"Close", false);
                        System.out.println("Success : " + ref);
                        Toast.makeText(getApplicationContext(), "Success: "+ref,Toast.LENGTH_LONG);
                        txtStatus = (TextView) findViewById(R.id.txtStatus);
                        txtStatus.setText("Success: " + ref);
                    }
                });
                payWithToken.start();
            }
        });

    }
}
