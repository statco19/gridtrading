package testtest;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ybigtaconference.gridtrading.service.BotServiceImpl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import com.ybigtaconference.gridtrading.credentials.TokenAndKey;

public class test_ {
    public static void main(String[] args) {

        TokenAndKey token_key = new TokenAndKey();
        System.out.println("Acc: "+ token_key.ACCESS_KEY );
        System.out.println("Sec: " + token_key.SECRET_KEY);
        BotServiceImpl bot_ = new BotServiceImpl(token_key.ACCESS_KEY, token_key.SECRET_KEY);

        bot_.connect_upbit();



    }
}