import org.json.JSONObject;

import java.io.*;
import org.json.JSONObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 *
 * Lu Zhang
 * Andrew ID: lzhang3
 */
public class Mapper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
    private static Date dateThreshold = null;
    private static HashMap<String, Integer> scoreTable = new HashMap<String, Integer>();
    private static ArrayList<String> bannedList = new ArrayList<String>();
    private static final String COMMA = ",";
    private static final String NEW_LINE = "\n";

    public static void main(String[] argvs) throws Exception {

        dateThreshold = sdf.parse("Sun Apr 20 00:00:00 +0000 2014");

        String curLine = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));

        //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("part-00005"),"UTF-8"));

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out,"UTF-8"));

        //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("part-00005-mapper-result.csv"),"UTF-8"));


        while ((curLine = br.readLine()) != null) {
            JSONObject jsonObject = new JSONObject(curLine);
            String date = jsonObject.get("created_at").toString();
            if (isValidTime(date) == false) {
                continue;
            }
            // tweet ID
            String tweetID = jsonObject.get("id_str").toString();
            //EMR json
            bw.write( tweetID + "\t" + curLine + "\n");

        }
        bw.flush();
        bw.close();
        br.close();

    }


    private static boolean isValidTime(String timeStr) throws Exception {
        Date time = sdf.parse(timeStr);
        //System.out.println(timeStr);
        if (time.compareTo(dateThreshold) < 0){ // if true
            return false;
        }
        return true;

    }




}
