import org.json.JSONObject;
import java.io.*;

/*
 *  No need for filtering time.
 *  output: userDate + everything.
 *  
*/

public class Mapper {


    public static void main(String[] argvs) throws Exception {

        String curLine = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));

       // BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("part-00000"),"UTF-8"));

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out,"UTF-8"));

        //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("part-00005-mapper-result.csv"),"UTF-8"));


        while ((curLine = br.readLine()) != null) {
            JSONObject jsonObject = new JSONObject(curLine);

            String userID = jsonObject.getJSONObject("user").getString("id_str");

            bw.write( userID + "\t" + curLine + "\n");


        }
        bw.flush();
        bw.close();
        br.close();

    }


}
