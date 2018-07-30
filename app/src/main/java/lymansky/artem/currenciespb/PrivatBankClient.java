package lymansky.artem.currenciespb;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PrivatBankClient {
    @GET("p24api/pubinfo?json&exchange")
    Call<List<Currency>> getData(@Query("coursid") int coursId);
}
