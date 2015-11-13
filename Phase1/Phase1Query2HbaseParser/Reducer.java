import net.spy.memcached.MemcachedClient;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import java.io.*;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * Lu Zhang
 * lzhang3
 * Description: Reducer
 **/
public class Reducer{

 
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
    private static final SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");

    private static Date dateThreshold = null;
    private static HashMap<String, Integer> scoreTable = new HashMap<String, Integer>();
    private static ArrayList<String> bannedList = new ArrayList<String>();
    private static final String COMMA = ",";
    private static final String NEW_LINE = "\n";

	public static void main (String args[]) throws Exception {
		dateThreshold = sdf.parse("Sun Apr 20 00:00:00 +0000 2014");

        //System.out.println(simplifyDate("Sun Apr 20 00:00:00 +0000 2014"));

        //String origin = "Hello\no.";
        //String escaped = StringEscapeUtils.escapeJava(origin);
        //System.out.println(origin);
        //System.out.println(escaped);//
        //System.out.println(StringEscapeUtils.unescapeJava(escaped));


        /*
        *
        *
        * */
        MemcachedClient c=new MemcachedClient(
                new InetSocketAddress("localhost", 59999));

        String str = null;
// Store a value (async) for one hour
        c.set("someKey", 3600, str);
// Retrieve a value (synchronously).
        Object myObject=c.get("someKey");


        /**
         *
         *
         *
         *
         *
         */

        initScoreTable();
		initBannedList();



        try{

            //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("part-00005-mapper-result.csv"),"UTF-8"));
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out,"UTF-8"));
            //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("part-00005-reducer-result.csv"),"UTF-8"));


            //Initialize Variables
            String preTweetId = "";
            String input = null;
			int count = 0;
	        //While we have input on stdin

			while((input=br.readLine())!=null){
			    try{
                    String[] parts = input.split("\t");
                    String tweetID = parts[0];
                    String curLine = parts[1];


                    if (tweetID.equals(preTweetId)){
						//System.out.println("DUPLICATE: " + tweetID);
						continue;
					}

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
                    //String tweetID = jsonObject.get("id_str").toString();
                    // tweet content
                    String text = jsonObject.get("text").toString();
                    text = StringEscapeUtils.escapeJava(text);
                    int score = getSentiScore(text);
                    String mosaicText = censor(text);

                    preTweetId = tweetID;
					count++;

                    //EMR json√
                    //bw.write(usrID + "&" + date + "\t" + tweetID + "&" + score + "&" + mosaicText + "\n");
                    bw.write( usrID + "&" + date + "&" + tweetID + "\t"  + score + "&" + mosaicText + "\n");
                    //bw.flush();

            	}
            	catch(NumberFormatException e){
					continue;
            	}

			}
			bw.flush();
			//System.out.println(count);
			br.close();
			bw.close();

		}catch(Exception io){
			io.printStackTrace();
		}


  	} // end of main


    private static String simplifyDate(String date) throws Exception {

        Date time = sdf.parse(date);

        String simpDate = newFormat.format(time);

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
        String lowerCaseText = convertToLower(text);
        //String lowerCaseText = text.toLowerCase();
        StringBuffer mosaicText = new StringBuffer(lowerCaseText);
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

        /*
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
        */
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
        InputStream is = Reducer.class.getResourceAsStream("scoreList");

        BufferedReader br = new BufferedReader(new InputStreamReader(is));


        while ((curLine = br.readLine()) != null){
            String []list = curLine.split("[\t]");
            scoreTable.put(list[0], Integer.parseInt(list[1]));
            //System.out.println(list[0] + " " + list[1]);
        }
    }


    private static void initBannedList() throws  IOException{
        String curLine = null;
        //BufferedReader br = new BufferedReader(new FileReader("bannedWord"));
        InputStream is = Reducer.class.getResourceAsStream("bannedWord");

        BufferedReader br = new BufferedReader(new InputStreamReader(is));


        while ((curLine = br.readLine()) != null){
            bannedList.add(curLine);
            //System.out.println(curLine);
        }
    }

    private static String convertToLower(String text) {
        int len = text.length();
        StringBuffer sb = new StringBuffer(text);
        for (int i = 0; i < len; ++i){
            char ch = sb.charAt(i);
            if ( ch >= 'A' && ch <= 'Z')
                sb.setCharAt(i, (char)(ch + 32));
        }
        return sb.toString();
    }





}// end of Reducer 
