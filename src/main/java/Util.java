
import java.io.*;
import java.util.*;

public class Util {
	  //-----------------------------------------------------------------------------------------------------------------------
	  public static boolean checkIfExtentionIsToBeSkipped(List<String> skippedExtentionList, String fileName) {
	    for(int i=0;i<skippedExtentionList.size();i++) {
	        String extention = skippedExtentionList.get(i);
	        if(fileName.endsWith(extention)) {
	            return true;
	        }
	    }
	    return false;
	  }

	  //-----------------------------------------------------------------------------------------------------------------------
	  public static String locatePropertiesFile() {
	      String filePath="";
	      filePath = "C:\\checksum_generator.properties";
	      if((new File(filePath)).exists()) return filePath;
	      
	      filePath = "D:\\checksum_generator.properties";
	      if((new File(filePath)).exists()) return filePath;
	      
	      filePath = "C:\\Programs_Portable_GIT\\Java_Utils\\checksum_generator.properties";
	      if((new File(filePath)).exists()) return filePath;
	            
	      filePath = "D:\\Programs_Portable_GIT\\Java_Utils\\checksum_generator.properties";
	      if((new File(filePath)).exists()) return filePath;
	            
	      filePath = "/home/pi/utils/checksum_generator.properties";
	      if((new File(filePath)).exists()) return filePath;
	            
	      return "";
	  }

    //=========================================================================================
    //=========================================================================================
    //=========================================================================================
    //=========================================================================================
    //=========================================================================================
    //=========================================================================================
}
