
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Util {

  /******************************************************************************************/
  //sample code from http://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
    public static byte[] createChecksumAsBinary(File filename) throws Exception {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }
    /******************************************************************************************/
    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public static String getMD5ChecksumAsHEX(File filename) throws Exception {
        byte[] b = createChecksumAsBinary(filename);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result.toUpperCase();
    }
    /******************************************************************************************/
    public static String getMD5ChecksumAsHEX(String inString) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5sum = md.digest(inString.getBytes());
        String result = String.format("%032X", new BigInteger(1, md5sum));
        return result.toUpperCase();
    }
  /******************************************************************************************/
}
