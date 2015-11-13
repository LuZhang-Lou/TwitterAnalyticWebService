import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import org.json.JSONObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class Mapper {


    private static final SimpleDateFormat simpleSdf = new SimpleDateFormat("MMM-dd-yyyy-HH:mm:ss");



    public static void main(String[] argvs) throws Exception {

        String curLine = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));

        //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("../part-00000"),"UTF-8"));

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out,"UTF-8"));

        //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("part-00005-mapper-result.csv"),"UTF-8"));


        while ((curLine = br.readLine()) != null) {
            JSONObject jsonObject = new JSONObject(curLine);

            /*
            // read output JSON test
            String yaosi = "";
            String [] strs = curLine.split("\t");
            JSONArray ja = new JSONArray(strs[1]);
            for (int i = 0; i < ja.length(); ++i){
                JSONObject jo = ja.getJSONObject(i);
                yaosi = jo.getString("info");
            }*/


            String date = jsonObject.get("created_at").toString();
            date = simplifyDate(date);


            JSONObject entities = jsonObject.getJSONObject("entities");

            JSONArray hashtags = entities.getJSONArray("hashtags");

            String userID = jsonObject.getJSONObject("user").getString("id_str");

            JSONObject newJsonObject = new JSONObject(curLine);


            newJsonObject.remove("entities");
            newJsonObject.remove("user");
            newJsonObject.put("userID", userID);
            newJsonObject.remove("created_at");
            newJsonObject.put("created_at", date);

            int len = hashtags.length();
            for (int i = 0; i < len; ++i){
                String hashtag = hashtags.getJSONObject(i).getString("text");
                bw.write(hashtag + "\t" + newJsonObject.toString() + "\n");
            }

        }
        bw.flush();
        bw.close();
        br.close();

    }




    private static String simplifyDate(String dateStr) {
        String[] comp = dateStr.split(" ");
        String simpDate = comp[1] + "-" + comp[2] + "-" + comp[5] + "-" + comp[3];

        Date time = null;
        try {
            time = simpleSdf.parse(simpDate);
        } catch (Exception e){
            e.printStackTrace();
        }

        SimpleDateFormat newSdf = new SimpleDateFormat("yyyyMMdd-HHmmss");

        return newSdf.format(time);
    }



}
