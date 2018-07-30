package lymansky.artem.currenciespb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int USD_INDEX = 0;
    private static final int EUR_INDEX = 1;

    private static final String COURSE_CASH = "course_cash";

    private TextView usdBuy;
    private TextView usdSale;
    private TextView eurBuy;
    private TextView eurSale;

    private RadioButton internetBanking;
    private RadioButton cashExchange;

    private List<Currency> exchangeList;
    private PrivatBankClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializations
        usdBuy = findViewById(R.id.usd_buy);
        usdSale = findViewById(R.id.usd_sale);
        eurBuy = findViewById(R.id.eur_buy);
        eurSale = findViewById(R.id.eur_sale);
        internetBanking = findViewById(R.id.internet_banking);
        cashExchange = findViewById(R.id.cash_exchange);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.privatbank.ua/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        client = retrofit.create(PrivatBankClient.class);

        cashExchange.setChecked(true);
        if(savedInstanceState != null) {
            cashExchange.setChecked(savedInstanceState.getBoolean(COURSE_CASH));
        }
        checkExchange();

        cashExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkExchange();
            }
        });

        internetBanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkExchange();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(COURSE_CASH, cashExchange.isChecked());
    }

    private void getData(int coursId) {
        Call<List<Currency>> call = client.getData(coursId);

        call.enqueue(new Callback<List<Currency>>() {
            @Override
            public void onResponse(Call<List<Currency>> call, Response<List<Currency>> response) {
                exchangeList = response.body();
                updateCurrencies();
            }

            @Override
            public void onFailure(Call<List<Currency>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "connection error", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void updateCurrencies() {
        usdBuy.setText(exchangeList.get(USD_INDEX).getBuy());
        usdSale.setText(exchangeList.get(USD_INDEX).getSale());
        eurBuy.setText(exchangeList.get(EUR_INDEX).getBuy());
        eurSale.setText(exchangeList.get(EUR_INDEX).getSale());
    }

    private void resetExchangeValues() {
        usdBuy.setText(getString(R.string.default_exchange));
        usdSale.setText(getString(R.string.default_exchange));
        eurBuy.setText(getString(R.string.default_exchange));
        eurSale.setText(getString(R.string.default_exchange));
    }

    private void checkExchange() {
        if (cashExchange.isChecked()) {
            resetExchangeValues();
            getData(Currency.COURSE_CASH);
        } else {
            resetExchangeValues();
            getData(Currency.COURSE_INTERNET);
        }
    }
}
