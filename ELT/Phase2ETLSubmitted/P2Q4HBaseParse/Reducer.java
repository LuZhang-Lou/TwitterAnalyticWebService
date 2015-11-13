import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.lang3.StringEscapeUtils;


public class Reducer{

 
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
    private static HashMap<String, Integer> scoreTable = new HashMap<String, Integer>();
    private static ArrayList<String> bannedList = new ArrayList<String>();
    private static final String COMMA = ",";
    private static final String NEW_LINE = "\n";

    public static void main (String args[]) throws Exception {

		try{

            //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("part-00005-mapper-result.csv"),"UTF-8"));
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out,"UTF-8"));
            //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("part-00005-reducer-result.csv"),"UTF-8"));


            //Initialize Variables
            String input = null;
            String hashtag = "";
            String preHashtag = "";
            HashMap<String, DailyRecord> dailyRecordList = new HashMap<String, DailyRecord>();

			while((input=br.readLine())!=null){
			    try{
                    String[] parts = input.split("\t");
                    hashtag = parts[0];
                    String curLine = parts[1];

                    if (hashtag.equals(preHashtag) == false && preHashtag.equals("") == false){
                       // sort previous Hashtag's list
                        ArrayList<DailyRecord> dailyRecordArrayList  = new ArrayList<DailyRecord>(dailyRecordList.values());
                        Collections.sort(dailyRecordArrayList);

                        /*
                        // MySQLprint
                        int cnt = 1;
                        for (DailyRecord dr : dailyRecordArrayList){
                            bw.write(preHashtag + num2Str(cnt) + "\t" + dr.printableString());
                            ++cnt;
                        }*/

                        // HBase print
                        JSONArray ja = new JSONArray();
                        bw.write(preHashtag  + "\t");
                        for (DailyRecord dr : dailyRecordArrayList){
                            JSONObject jo = new JSONObject();
                            jo.put("info", dr.printableString());
                            ja.put(jo);
                        }
                        bw.write(ja.toString() + "\n");




                       // new hashtag, renew hashmap
                       dailyRecordList = new HashMap<String, DailyRecord>();
                    }

                    JSONObject jsonObject = new JSONObject(curLine);

                    String time = jsonObject.get("created_at").toString(); // already simplified in Mapper
                    String nyr = time.substring(0, 8); // year month and day
                    String sfm = time.substring(9); //hour minute and second
                    String userID = jsonObject.get("userID").toString();
                    String text = jsonObject.get("text").toString();



                    if (dailyRecordList.containsKey(nyr)){
                        DailyRecord dailyRecord = dailyRecordList.get(nyr);
                        dailyRecord.incrementCount(1);
                        dailyRecord.updateText(sfm, text);
                        dailyRecord.addUser(userID);
                    } else {
                        DailyRecord dailyRecord = new DailyRecord(nyr, sfm, text);
                        dailyRecord.addUser(userID);
                        dailyRecordList.put(nyr, dailyRecord);
                    }

            	}
            	catch(NumberFormatException e){
					continue;
            	}

                preHashtag = hashtag;
			} // end of while


            // For last hashtag
            // sort previous Hashtag's list
            ArrayList<DailyRecord> dailyRecordArrayList  = new ArrayList<DailyRecord>(dailyRecordList.values());
            Collections.sort(dailyRecordArrayList);
            // print

            JSONArray ja = new JSONArray();
            bw.write(preHashtag  + "\t");
            for (DailyRecord dr : dailyRecordArrayList){
                JSONObject jo = new JSONObject();
                jo.put("info", dr.printableString());
                ja.put(jo);
            }
            bw.write(ja.toString() + "\n");


			bw.flush();
			br.close();
			bw.close();

		}catch(Exception io){
			io.printStackTrace();
		}

  	} // end of main



    private static String num2Str(int num) {
        if (num < 10)
            return "000" + num;
        else if ( num < 100)
            return "00" + num;
        else if (num < 1000)
            return "0" + num;
        return "" + num;
    }



    private static class DailyRecord implements Comparable<DailyRecord>{
        public String date;
        public long count;
        public String earliestTime; // format : HHMMSS
        public String earliestText;
        public TreeSet<Long> userList;

        public DailyRecord(String date, String earliestTime, String earliestText){
            this.date = date;
            this.earliestText = earliestText;
            this.count = 1;
            this.earliestTime = earliestTime;
            userList = new TreeSet<Long>();
        }

        public void incrementCount(int delta){
            count += delta;
        }

        public void addUser(String userID){
            Long id = Long.parseLong(userID);
            if (userList.contains(id)){
                return;
            } else
                userList.add(id);
        }

        public void updateText(String newTime, String newText){ // NOT YET ESCAPED
            int result = earliestTime.compareTo(newTime);
            if ( (result > 0) || (result == 0 && newText.compareTo(earliestText) < 0)) {
                earliestTime = newTime;
                earliestText = newText;
            }
        }

        @Override
        public int compareTo(DailyRecord o) {
            if (o.count == this.count)
                return this.date.compareTo(o.date);
            return (int)(o.count - this.count);
        }

        public String printableString(){
            StringBuffer ret = new StringBuffer();
            ret.append(date + ":" + count + ":");
            //ret.append(date + "-" + earliestTime + ":" + count + ":");


            for (Long userID : userList){
                ret.append(userID+",");
            }
            ret.setCharAt(ret.length() - 1, ':');
            ret.append(StringEscapeUtils.escapeJava(earliestText) + "\n");
            return  ret.toString() ;
        }

    }



}// end of Reducer 
