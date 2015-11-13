/**
 * Created by Lu on 11/5/15.
 */
    import org.apache.hadoop.conf.Configuration;
    import org.apache.hadoop.hbase.HBaseConfiguration;

    import org.apache.hadoop.hbase.HColumnDescriptor;
    import org.apache.hadoop.hbase.HTableDescriptor;
    import org.apache.hadoop.hbase.client.*;
    import org.apache.hadoop.hbase.util.Bytes;


    import java.io.IOException;

    /**
     * Created by Sun on 10/25/15.
     */
    public class HBaseConnector {

        static Configuration conf = null;

        static {
            // TODO : Configure
            conf = HBaseConfiguration.create();

            conf.set("hbase.zookeeper.quorum", "54.86.6.215"); // Master IP
            conf.set("hbase.zookeeper.property.clientPort", "2181"); // 2181
            conf.set("hbase.master", "54.86.6.215:60000"); // Master IP:60000
        /*
        * Modification of the /etc/hosts file :
        * Server Side : Store the Master and Slave's IP in the format of :
        * <Public IP> <Private IP>
        * Master Node: Store the Slave's IP in the same format.
        * No need to configure Slave's etc/hosts.
        * */
        }

        public static String fetch(String tableName, String rowKey) throws IOException {

            HBaseAdmin.checkHBaseAvailable(conf);

            HTable htable = new HTable(conf, Bytes.toBytes(tableName));
            // Produce the get object here.
            Get getResult = new Get(Bytes.toBytes(rowKey));

            // Add the column specification there.
            // getResult.addColumn(Bytes.toBytes("family"), Bytes.toBytes("column"));

            // Get the result here.
            Result r = htable.get(getResult);

            // Get the latest version of the specified column (Family, qualifiers. )
            byte[] value = r.getValue(Bytes.toBytes("data"), Bytes.toBytes("text"));
            String finalValue = Bytes.toString(value);

            return finalValue;
        }

        public static void main(String[] args) throws Exception {
            String rowKey = "465399623733895168";
            String result = fetch("followInfo", rowKey);
            System.out.println(result);
        }
    }
/*
* The raw table for MySQL is : userID, tweetID, date, sentiment score, raw text
* The adopted one for the HBase :
* rowKey : userID&Date
* Family info : tweetId, Score, raw text
* */

/*
* Reference : http://www.xiaoyaochong.net/wordpress/index.php/2013/05/23/hbase入门实例/
* HBase configuration : http://www.cnblogs.com/ggjucheng/p/3381328.html
*
*/

