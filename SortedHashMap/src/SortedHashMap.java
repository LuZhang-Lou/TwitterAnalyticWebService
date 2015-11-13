import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Lu on 11/7/15.
 */
public class SortedHashMap {
    public static void main(String [] a){

    }


    private static String num2Str(int num) {


        //System.out.println(num2Str(1000));
        TreeSet<Integer> tr = new TreeSet<Integer>();
        tr.add(5);
        tr.add(2);
        tr.add(9);
        for (Integer l : tr){
            System.out.println(l);
        }

    }
        /*HashMap<String, SortedObject> hashmap = new HashMap<String, SortedObject>();
        SortedObject so1 = new SortedObject(1);
        SortedObject so2 = new SortedObject(3);
        SortedObject so3 = new SortedObject(2);
        hashmap.put("1", so1);
        hashmap.put("2", so2);
        hashmap.put("3", so3);
        ArrayList<SortedHashMap> = hashmap.values()
        Collections.sort(hashmap);

        for (Map.Entry<String, SortedObject> entry : hashmap.entrySet()) {
            String curKey = entry.getKey();
            SortedObject curValue = entry.getValue();
            System.out.println(curKey + " " + curValue.count);

        }*/

    }

    private static class SortedObject extends Collections implements Comparable<SortedObject>{

        @Override
        public int compareTo(SortedObject o) {
            return (int)(o.count - this.count);
        }

        public int count;

        public SortedObject(int count){
            this.count = count;
        }
    }

}
