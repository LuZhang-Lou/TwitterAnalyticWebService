/**
 * Created by Lu on 10/22/15.
 */



import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class json2DB {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
    private static Date dateThreshold = null;
    private static HashMap<String, Integer> scoreTable = new HashMap<String, Integer>();
    private static ArrayList<String> bannedList = new ArrayList<String>();
    private static final String COMMA = ",";
    private static final String NEW_LINE = "\n";

    //Thu May 15 09:02:20 +0000 2014
    public static void main(String[] args) throws Exception {


        String jsonString = "{\"created_at\":\"Thu May 15 09:02:20 +0000 2014\",\"id\":466866157933182977,\"id_str\":\"466866157933182977\",\"text\":\"RT @Xxxshota7xxx: \\u3010\\u885d\\u6483\\u6620\\u50cf\\u3011\\n\\u30d0\\u30e9\\u30a8\\u30c6\\u30a3\\u756a\\u7d44\\u3067\\u30d0\\u30a4\\u30ad\\u30f3\\u30b0\\u30fb\\u5c0f\\u5ce0\\u306b\\n\\u4ed5\\u639b\\u3051\\u305f\\u30c9\\u30c3\\u30ad\\u30ea\\u304c\\u9177\\u3059\\u304e\\u3066\\u5927\\u708e\\u4e0awww\\n\\n\\u52d5\\u753b\\u306f\\u3053\\u3061\\u3089\\u21d2http:\\/\\/t.co\\/DFtzc8nOcN\\n\\n\\u4e2d\\u5c45\\u304f\\u3093\\u6016\\u3059\\u304e\\u30ef\\u30ed\\u30bfww http:\\/\\/t.co\\/Zph8jC6QzY\",\"source\":\"\\u003ca href=\\\"http:\\/\\/yahoo.co.jp\\\" rel=\\\"nofollow\\\"\\u003e\\u3010\\u7f8e\\u4eba\\u3011 \\u60a9\\u6bba\\u7387100\\uff05\\u3067\\u4eba\\u6c17\\u8870\\u3048\\u305a\\uff01\\u003c\\/a\\u003e\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":1391544108,\"id_str\":\"1391544108\",\"name\":\"v.i.p ryokun\",\"screen_name\":\"ryoryoryoryo10\",\"location\":\"\\u26612013.06.29\\u301c  m.love\\u2026\\u2661\",\"url\":null,\"description\":\"Osaka Nishiyodogawa 17age Follow me Utajima 65th\\u261eKitayodo51th\",\"protected\":false,\"followers_count\":814,\"friends_count\":689,\"listed_count\":1,\"created_at\":\"Tue Apr 30 08:53:33 +0000 2013\",\"favourites_count\":838,\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":true,\"verified\":false,\"statuses_count\":9828,\"lang\":\"ja\",\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"C0DEED\",\"profile_background_image_url\":\"http:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_image_url_https\":\"https:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_tile\":false,\"profile_image_url\":\"http:\\/\\/pbs.twimg.com\\/profile_images\\/464402846293573632\\/iDbrYwVz_normal.jpeg\",\"profile_image_url_https\":\"https:\\/\\/pbs.twimg.com\\/profile_images\\/464402846293573632\\/iDbrYwVz_normal.jpeg\",\"profile_banner_url\":\"https:\\/\\/pbs.twimg.com\\/profile_banners\\/1391544108\\/1398871213\",\"profile_link_color\":\"0084B4\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"default_profile\":true,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"retweeted_status\":{\"created_at\":\"Thu May 15 07:43:29 +0000 2014\",\"id\":466846313691115520,\"id_str\":\"466846313691115520\",\"text\":\"\\u3010\\u885d\\u6483\\u6620\\u50cf\\u3011\\n\\u30d0\\u30e9\\u30a8\\u30c6\\u30a3\\u756a\\u7d44\\u3067\\u30d0\\u30a4\\u30ad\\u30f3\\u30b0\\u30fb\\u5c0f\\u5ce0\\u306b\\n\\u4ed5\\u639b\\u3051\\u305f\\u30c9\\u30c3\\u30ad\\u30ea\\u304c\\u9177\\u3059\\u304e\\u3066\\u5927\\u708e\\u4e0awww\\n\\n\\u52d5\\u753b\\u306f\\u3053\\u3061\\u3089\\u21d2http:\\/\\/t.co\\/DFtzc8nOcN\\n\\n\\u4e2d\\u5c45\\u304f\\u3093\\u6016\\u3059\\u304e\\u30ef\\u30ed\\u30bfww http:\\/\\/t.co\\/Zph8jC6QzY\",\"source\":\"\\u003ca href=\\\"http:\\/\\/yahoo.co.jp\\\" rel=\\\"nofollow\\\"\\u003e\\u30d0\\u30a4\\u30ad\\u30f3\\u30b0\\u30fb\\u5c0f\\u5ce0\\u306b \\u4ed5\\u639b\\u3051\\u305f\\u30c9\\u30c3\\u30ad\\u30ea\\u003c\\/a\\u003e\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":962038489,\"id_str\":\"962038489\",\"name\":\"\\u3057\\u3087\\u30fc\\u305f\",\"screen_name\":\"Xxxshota7xxx\",\"location\":\"\\u5bcc\\u7530\\u6797\",\"url\":null,\"description\":null,\"protected\":false,\"followers_count\":194,\"friends_count\":156,\"listed_count\":0,\"created_at\":\"Wed Nov 21 08:46:53 +0000 2012\",\"favourites_count\":347,\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":false,\"verified\":false,\"statuses_count\":2913,\"lang\":\"ja\",\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"C0DEED\",\"profile_background_image_url\":\"http:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_image_url_https\":\"https:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_tile\":false,\"profile_image_url\":\"http:\\/\\/pbs.twimg.com\\/profile_images\\/378800000718547747\\/89daed934729b4744480381a59081425_normal.jpeg\",\"profile_image_url_https\":\"https:\\/\\/pbs.twimg.com\\/profile_images\\/378800000718547747\\/89daed934729b4744480381a59081425_normal.jpeg\",\"profile_banner_url\":\"https:\\/\\/pbs.twimg.com\\/profile_banners\\/962038489\\/1387966350\",\"profile_link_color\":\"0084B4\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"default_profile\":true,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"retweet_count\":402,\"favorite_count\":0,\"entities\":{\"hashtags\":[],\"symbols\":[],\"urls\":[{\"url\":\"http:\\/\\/t.co\\/DFtzc8nOcN\",\"expanded_url\":\"http:\\/\\/tinyurl.com\\/lnpfgjb\",\"display_url\":\"tinyurl.com\\/lnpfgjb\",\"indices\":[53,75]}],\"user_mentions\":[],\"media\":[{\"id\":466846313540116480,\"id_str\":\"466846313540116480\",\"indices\":[90,112],\"media_url\":\"http:\\/\\/pbs.twimg.com\\/media\\/BnqSP6tCYAAt8C2.jpg\",\"media_url_https\":\"https:\\/\\/pbs.twimg.com\\/media\\/BnqSP6tCYAAt8C2.jpg\",\"url\":\"http:\\/\\/t.co\\/Zph8jC6QzY\",\"display_url\":\"pic.twitter.com\\/Zph8jC6QzY\",\"expanded_url\":\"http:\\/\\/twitter.com\\/Xxxshota7xxx\\/status\\/466846313691115520\\/photo\\/1\",\"type\":\"photo\",\"sizes\":{\"large\":{\"w\":610,\"h\":343,\"resize\":\"fit\"},\"thumb\":{\"w\":150,\"h\":150,\"resize\":\"crop\"},\"small\":{\"w\":339,\"h\":191,\"resize\":\"fit\"},\"medium\":{\"w\":599,\"h\":337,\"resize\":\"fit\"}}}]},\"favorited\":false,\"retweeted\":false,\"possibly_sensitive\":false,\"lang\":\"ja\"},\"retweet_count\":0,\"favorite_count\":0,\"entities\":{\"hashtags\":[],\"symbols\":[],\"urls\":[{\"url\":\"http:\\/\\/t.co\\/DFtzc8nOcN\",\"expanded_url\":\"http:\\/\\/tinyurl.com\\/lnpfgjb\",\"display_url\":\"tinyurl.com\\/lnpfgjb\",\"indices\":[71,93]}],\"user_mentions\":[{\"screen_name\":\"Xxxshota7xxx\",\"name\":\"\\u3057\\u3087\\u30fc\\u305f\",\"id\":962038489,\"id_str\":\"962038489\",\"indices\":[3,16]}],\"media\":[{\"id\":466846313540116480,\"id_str\":\"466846313540116480\",\"indices\":[108,130],\"media_url\":\"http:\\/\\/pbs.twimg.com\\/media\\/BnqSP6tCYAAt8C2.jpg\",\"media_url_https\":\"https:\\/\\/pbs.twimg.com\\/media\\/BnqSP6tCYAAt8C2.jpg\",\"url\":\"http:\\/\\/t.co\\/Zph8jC6QzY\",\"display_url\":\"pic.twitter.com\\/Zph8jC6QzY\",\"expanded_url\":\"http:\\/\\/twitter.com\\/Xxxshota7xxx\\/status\\/466846313691115520\\/photo\\/1\",\"type\":\"photo\",\"sizes\":{\"large\":{\"w\":610,\"h\":343,\"resize\":\"fit\"},\"thumb\":{\"w\":150,\"h\":150,\"resize\":\"crop\"},\"small\":{\"w\":339,\"h\":191,\"resize\":\"fit\"},\"medium\":{\"w\":599,\"h\":337,\"resize\":\"fit\"}},\"source_status_id\":466846313691115520,\"source_status_id_str\":\"466846313691115520\"}]},\"favorited\":false,\"retweeted\":false,\"possibly_sensitive\":false,\"lang\":\"ja\"}";



        dateThreshold = sdf.parse("Sun Apr 20 00:00:00 +0000 2014");
        initScoreTable();
        initBannedList();

        String curLine = null;
        //BufferedReader br = new BufferedReader(new FileReader("/Users/Lu/Courses/15619/GroupProject/dataset/part-00000"));
        //BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/Lu/Courses/15619/GroupProject/dataset/part-00000-result-key-json2.csv"));

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/Lu/Courses/15619/GroupProject/dataset/part-000004"),"UTF-8"));
        //BufferedReader br = new BufferedReader(new FileReader("/Users/Lu/Courses/15619/GroupProject/dataset/part-00003-mapper-result.csv"));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/Lu/Courses/15619/GroupProject/dataset/part-00004-result-key-json2.csv"),"UTF-8"));


        while ( (curLine  = br.readLine()) != null){
            JSONObject jsonObject = new JSONObject(curLine);
            String date = jsonObject.get("created_at").toString();
            if (isValidTime(date) == false){
                continue;
            }
            date = simplifyDate(date);


            JSONObject user = (JSONObject)(jsonObject.getJSONObject("user"));

            // user ID
            String usrID = user.get("id").toString();

            // tweet ID
            String tweetID = jsonObject.get("id_str").toString();
            // tweet content
            String text = jsonObject.get("text").toString();
            int score = getSentiScore(text);
            String mosaicText = censor(text);

            //EMR json
            //bw.write( tweetID + "\t" + curLine + "\n");


            //csv
            bw.write(usrID + "," + tweetID + "," + date + "," + score + "," + mosaicText + "\n");

            //tsv
            //bw.write(tweetID + "\t" + usrID + "\t" + date + "\t" + score + "\t" + mosaicText + "\n");
            //System.out.println( usrID + "," + tweetID + "," + date + "," + score + "," + mosaicText + "\n");

            //System.out.println("");

        }
        br.close();
        bw.close();




    }

    private static String simplifyDate(String date) {
        String[] comp = date.split(" ");
        String simpDate = comp[1] + " " + comp[2] + " " + comp[5] + " " + comp[3];
        return simpDate;
    }


    private static boolean isValidTime(String timeStr) throws Exception {
        Date time = sdf.parse(timeStr);
        //System.out.println(timeStr);
        if (time.compareTo(dateThreshold) < 0){ // if true
            return false;
        }
        return true;

    }

    private static String decodeBannedWord(String encodedWord) {
        int len = encodedWord.length();
        StringBuffer decodedWord = new StringBuffer(encodedWord);
        for (int i = 0; i < len ; i++){
            char ch = encodedWord.charAt(i);
            if (ch >= 'a' && ch <= 'm')
                decodedWord.setCharAt(i, (char) (ch + 13));
            else if (ch >= 'n' && ch <= 'z'){
                decodedWord.setCharAt(i, (char) (ch - 13));
            }
        }
        return decodedWord.toString();


    }

    private static int getSentiScore(String text){
        int textLen = text.length();
        int score = 0;
        for (Map.Entry<String, Integer> entry :  scoreTable.entrySet()){
            int index = 0;
            int wordLen = entry.getKey().length();
            int startIndex = 0;
            //System.out.println(entry.getKey());

            while ( (index = text.indexOf(entry.getKey(), startIndex)) != -1){
//                if (text.substring(index, index+wordLen).equals(entry.getKey())){
                    if (index == 0 && index + wordLen == textLen){
                        score += entry.getValue();
                        break;
                    }
                    if (index != 0  && index + wordLen != textLen
                            && isNotAlNum(text.charAt(index-1))
                            && isNotAlNum(text.charAt(index + wordLen)) ){
                        score += entry.getValue();
                    }
                    if (index == 0 && index + wordLen != textLen
                            && isNotAlNum(text.charAt(index + wordLen))){
                        score += entry.getValue();
                    }
                    if (index + wordLen == textLen && index != 0
                            && isNotAlNum(text.charAt(index - 1))){
                        score += entry.getValue();
                        break;
                    }
                    startIndex = index + wordLen;
            //    }


            }
            //System.out.println(entry.getKey() + " " + entry.getValue());
        }
        //System.out.println(score);
        return score;
    }


    private static String censor(String text){
        int textLen = text.length();
        int score = 0;
        StringBuffer mosaicText = new StringBuffer(text);
        String lowerCaseText = text.toLowerCase();
        boolean mosaiced = false;
        int rIndex = 0;
        //while ((rIndex = mosaicText.indexOf("\n")) !=-1){
         //   mosaicText.deleteCharAt(rIndex);
        //}


        for (String bannedWord : bannedList){
            int index = 0;
            int wordLen = bannedWord.length();
            int startIndex = 0;
            //System.out.println(entry.getKey());

            while ( (index = lowerCaseText.indexOf(bannedWord, startIndex)) != -1){
                if (index == 0 && index + wordLen == textLen){
                    for (int i = index + 1; i <= index+wordLen-2; ++i){
                        mosaicText.setCharAt(i,'*');
                    }
                    mosaiced = true;
                    break;
                }
                if (index != 0  && index + wordLen != textLen
                        && isNotAlNum(lowerCaseText.charAt(index-1))
                        && isNotAlNum(lowerCaseText.charAt(index + wordLen)) ){
                    for (int i = index + 1; i <= index+wordLen-2; ++i){
                        mosaicText.setCharAt(i,'*');
                    }
                    mosaiced = true;
                }
                if (index == 0 && index + wordLen != textLen
                        && isNotAlNum(lowerCaseText.charAt(index + wordLen))){
                    for (int i = index + 1; i <= index+wordLen-2; ++i){
                        mosaicText.setCharAt(i,'*');
                    }
                    mosaiced = true;
                }
                if (index + wordLen == textLen && index != 0
                        && isNotAlNum(lowerCaseText.charAt(index - 1))){

                    for (int i = index + 1; i <= index+wordLen-2; ++i){
                        mosaicText.setCharAt(i,'*');
                    }
                    mosaiced = true;
                    break;
                }
                startIndex = index + wordLen;


            }
        }
        int startIndex = 0;
        int index = 0;
        while ( (index = mosaicText.indexOf("\"", startIndex)) != -1){
            mosaicText.insert(index,  "\"");
            startIndex = index + 2;
        }
        //if (mosaiced )
           // System.out.println(mosaicText);

        mosaicText.insert(0, "\"");
        mosaicText.append("\"");
        return mosaicText.toString();
    }


    private static boolean isNotAlNum(char ch) {
        if ( ch >= 'A' && ch <= 'Z')
            return false;
        if ( ch >= 'a' && ch <= 'z')
            return  false;
        if ( ch >= '0' && ch <= '9' )
            return false;
        return true;
    }

    private static void initScoreTable() throws  IOException{
        String curLine = null;
        BufferedReader br = new BufferedReader(new FileReader("/Users/Lu/Courses/15619/GroupProject/dataset/scoreList"));
        while ((curLine = br.readLine()) != null){
            String []list = curLine.split("[\t]");
            scoreTable.put(list[0], Integer.parseInt(list[1]));
            //System.out.println(list[0] + " " + list[1]);
        }
    }


    private static void initBannedList() throws  IOException{
        String curLine = null;
        BufferedReader br = new BufferedReader(new FileReader("/Users/Lu/Courses/15619/GroupProject/dataset/bannedWord"));
        while ((curLine = br.readLine()) != null){
            bannedList.add(curLine);
            //System.out.println(curLine);
        }
    }


    private static void processBannedWord() throws IOException{
        String curLine = null;
        BufferedReader br = new BufferedReader(new FileReader("/Users/Lu/Courses/15619/GroupProject/Phase1Query2/bannedEncoded"));
        while ((curLine = br.readLine()) != null){
            curLine = decodeBannedWord(curLine);
            System.out.println(curLine);
        }

    }



}