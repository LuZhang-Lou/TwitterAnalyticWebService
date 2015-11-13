/*
  Output Schema:
    userID+"+"+date + "\t" + tweetID + "\t" + impact + "\t" + escapedText + "\n";
 */


import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.lang3.StringEscapeUtils;


public class Reducer{

 
    private static final SimpleDateFormat simpleSdf = new SimpleDateFormat("MMM-dd-yyyy-HH:mm:ss");
    private static HashMap<String, Integer> scoreTable = new HashMap<String, Integer>();
    private static ArrayList<String> bannedList = new ArrayList<String>();


    public static void main (String args[]) throws Exception {
		initScoreTable();
		initBannedList();

		try{

           // BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("part-00005-mapper-result.csv"),"UTF-8"));
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out,"UTF-8"));
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("part-00005-reducer-result.csv"),"UTF-8"));


            //Initialize Variables
            String input = null;
            String userID = "";
            String preUserID = "";
            ArrayList<SortedUserKeyValue> curList = new ArrayList<SortedUserKeyValue>();
            while((input=br.readLine())!=null){
			    try{
                    String[] parts = input.split("\t");
                    userID = parts[0];
                    String curLine = parts[1];


                    if ((userID.equals(preUserID) == false) && (preUserID.equals("") == false)){
                        // previous key all finished, output now

                        Collections.sort(curList);
                        int len = curList.size();
                        for (int i = 0; i < len; ++i) {
                            // todo
                            // modify printable String!
                            bw.write(curList.get(i).printableString());
                        }

                        //renew list
                        curList = new ArrayList<SortedUserKeyValue>();
                    }


                    JSONObject jsonObject = new JSONObject(curLine);

                    // follower count
                    JSONObject user = (JSONObject)(jsonObject.getJSONObject("user"));
                    String followerCount = user.get("followers_count").toString();

                    // tweet content
                    String text = jsonObject.get("text").toString();
                    int score = getSentiScore(text);

                    long impact = score * (Long.parseLong(followerCount) + 1);

                    if (impact == 0) {
                        //ignore those with 0 points.
                        continue;
                    }

                    String time = jsonObject.get("created_at").toString();
                    time = simplifyDate(time);

                    String tweetID = jsonObject.get("id_str").toString();

                    String mosaicText = censor(text);

                    // For HBase:
                    //String escapedText = StringEscapeUtils.escapeJava(mosaicText);
                    // For MySQL:
                    String escapedText = escape(mosaicText);

                    SortedUserKeyValue skv = new SortedUserKeyValue(userID, time, tweetID, impact, escapedText);
                    curList.add(skv);

            	}
            	catch(NumberFormatException e){
					continue;
            	}
                preUserID = userID;

			} // end of while


            // output last key's list
            Collections.sort(curList);
            int len = curList.size();
            for (int i = 0; i < len; ++i) {
                //bw.write(userID + "\t" + tweetID + "\t" + date + "\t" + impact +  "\t" + text2 + "\n");
                bw.write(curList.get(i).printableString());
            }


			bw.flush();
			br.close();
			bw.close();

		}catch(Exception io){
			io.printStackTrace();
		}

  	} // end of main


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

    public  static String escape(String text){
        int startIndex = 0;
        int index = 0;
        StringBuffer escapedText = new StringBuffer(text);
        while ( (index = text.indexOf("\"", startIndex)) != -1){
            escapedText.insert(index,  "\"");
            startIndex = index + 2;
        }
        escapedText.insert(0, "\"");
        escapedText.append("\"");

        return escapedText.toString();
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
        // avoid case insensitivity.
        text = text.toLowerCase();
        int score = 0;
        for (Map.Entry<String, Integer> entry :  scoreTable.entrySet()){
            int index = 0;
            int wordLen = entry.getKey().length();
            int startIndex = 0;

            while ( (index = text.indexOf(entry.getKey(), startIndex)) != -1){
                if (index == 0 && index + wordLen == textLen){
                    score += entry.getValue();
                    break;
                }
                //if (index != 0  && index + wordLen != textLen
                if (index > 0  && index + wordLen < textLen                
                        && isNotAlNum(text.charAt(index-1))
                        && isNotAlNum(text.charAt(index + wordLen)) ){
                    score += entry.getValue();
                }
                //if (index == 0 && index + wordLen != textLen
                if (index == 0 && index + wordLen < textLen
                        && isNotAlNum(text.charAt(index + wordLen))){
                    score += entry.getValue();
                }
//                if (index + wordLen == textLen && index != 0
                if (index + wordLen == textLen && index > 0
                        && isNotAlNum(text.charAt(index - 1))){
                    score += entry.getValue();
                    break;
                }
                startIndex = index + wordLen;


            }
        }
        return score;
    }


    private static String censor(String text){
        int textLen = text.length();
        int score = 0;
        String lowerCaseText = convertToLower(text);
        //StringBuffer mosaicText = new StringBuffer(lowerCaseText);
        StringBuffer mosaicText = new StringBuffer(text);


        boolean mosaiced = false;
        int rIndex = 0;

        for (String bannedWord : bannedList){
            int index = 0;
            int wordLen = bannedWord.length();
            int startIndex = 0;

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
        // maunal escape is done after returning.
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
        }
    }


    private static void initBannedList() throws  IOException{
        String curLine = null;
        InputStream is = Reducer.class.getResourceAsStream("bannedWord");

        BufferedReader br = new BufferedReader(new InputStreamReader(is));


        while ((curLine = br.readLine()) != null){
            bannedList.add(curLine);
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


    private static class SortedUserKeyValue implements Comparable<SortedUserKeyValue> {
        // The text is already escaped.
        public String userID;
        public String time;
        public String tweetID;
        public String escapedText;
        public String impact;
        public SortedUserKeyValue(String userID, String time, String tweetID, long impact, String escapedText){
            this.userID = userID;
            this.time = time;
            this.tweetID = tweetID;
            this.impact = String.valueOf(impact);
            this.escapedText = escapedText;
        }

        @Override
        public int compareTo(SortedUserKeyValue kv){
            return this.time.compareTo(kv.time);
        }

        public String printableString(){
            // For MySQL
            return userID + "+" + time + "\t" + tweetID + "\t" + impact + "\t" + escapedText + "\n";
        }
    }



}// end of Reducer 
