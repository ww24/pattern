package jp.ww24.handwrites;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ww24 on 2016/01/19.
 */
public class Pattern {
    private Config mConfig;
    private Context mContext;
    private Gson mGson = new Gson();
    private static String[] patterns = {
            "154963",
            "75219638",
            "84271639",
            "683975",
            "45362987",
            "51627498",
            "7592614",
            "16954378",
            "1596728",
            "53849721",
            "85972413",
            "3587294",
            "9267843",
            "542836",
            "63259814",
            "5982341",
            "45892136",
            "816532",
            "3684127",
            "75419238",
            "27813659",
            "53421967",
            "56418237",
            "42368751",
            "5342687",
            "96214875",
            "16532874",
            "27849635",
            "4367518",
            "3592864",
            "75238416",
            "69472315",
            "21594387",
            "68921435",
            "51234687",
            "723615",
            "518736",
            "8753419",
            "38796142",
            "7534298",
            "36584917",
            "614927",
            "15378694",
            "7853214",
            "61524783",
            "8627934",
            "7236185",
            "6187492",
            "94876512",
            "54867321",
            "7596812",
            "12459837",
            "7835416",
            "7512364",
            "45213",
            "486927",
            "25687493",
            "68723941",
            "3247581",
            "2568941",
            "62783549",
            "81563297",
            "968723",
            "86753291",
            "548376",
            "48926153",
            "89452671",
            "21573489",
            "65438917",
            "4923167",
            "96158427",
            "68324957",
            "8759236",
            "49623817",
            "76149832",
            "92761854",
            "24786935",
            "34168725",
            "6839752",
            "1542386",
            "8456397",
            "14587639",
            "2943518",
            "2784965",
            "59423187",
            "1857642",
            "47659283",
            "35261947",
            "3674185",
            "61538792",
            "476351",
            "8479562",
            "47512863",
            "3427519",
            "59623487",
            "92458167",
            "54628391",
            "53418627",
            "92581376",
            "42659873"
    };

    public Pattern(Context context) {
        mContext = context;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String patternGson = sharedPreferences.getString("PATTERN_LIST", null);
        if (patternGson == null) {
            init();
        } else {
            mConfig = mGson.fromJson(patternGson, Config.class);
        }
    }

    private void init() {
        mConfig = new Config();

        for (String pattern: patterns) {
            List<Integer> list = new ArrayList<>();
            mConfig.mPatternList.add(list);
            for (String num: pattern.split("")) {
                if (num.equals("")) continue;
                list.add(Integer.parseInt(num));
            }
        }

        Collections.shuffle(mConfig.mPatternList);

        update();
    }

    public void update() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String patternGson = mGson.toJson(mConfig);
        editor.putString("PATTERN_LIST", patternGson);
        editor.apply();
    }

    public List<Integer> getPattern() {
        if (mConfig.mIndex >= mConfig.mPatternList.size()) {
            init();
        }
        return mConfig.mPatternList.get(mConfig.mIndex++);
    }

    public int getIndex() {
        // update saved config data
        return mConfig.mIndex;
    }

    private class Config {
        public List<List<Integer>> mPatternList = new ArrayList<>();
        public int mIndex = 0;
    }
}
