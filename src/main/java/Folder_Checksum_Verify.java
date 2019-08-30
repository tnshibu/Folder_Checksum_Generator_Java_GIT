import java.io.*;
import java.util.*;

public class Folder_Checksum_Verify {
  private static String SOURCE_BASE_FOLDER = "."; //by default, process current directory
  private static List<String> sourceFileList = new ArrayList<String>();
  private static Map<String, List<String>> propertiesMap = null;
  private static List<String> skippedExtentionList = null;
  private static List<String> repoFlagList = null;
  private static boolean repoFlag = false;
  private static String repoDir = "";

  private static int CHECKSUM_SUCCESS = 1;
  private static int CHECKSUM_FAILURE = 2;
  private static int CHECKSUM_MISSING = 3;
  
  
  public static void main(String[] args) throws Exception {
    if(args != null && args.length > 0 && !args[0].trim().equals("")) {
        SOURCE_BASE_FOLDER = args[0];
    }
    //SOURCE_BASE_FOLDER ="D:\\zShibu\\0_from_TRC\\00-shibu\\fin\\1";
    SOURCE_BASE_FOLDER = SOURCE_BASE_FOLDER.replace("\\", "/");
    //System.out.println("SOURCE_BASE_FOLDER ="+SOURCE_BASE_FOLDER);
    File sourceFolder = new File(SOURCE_BASE_FOLDER);
    if(!sourceFolder.exists()) {
        System.out.println("Folder "+SOURCE_BASE_FOLDER + " does not exist. Exiting");
        System.exit(-1);
    }
    sourceFileList = FileUtil.getFileListFromFolder(SOURCE_BASE_FOLDER);
    String propertyFilePath = Util.locatePropertiesFile();
    propertiesMap = PropertiesLoader.loadToHashMap(propertyFilePath);
    skippedExtentionList = (List<String>)propertiesMap.get("SKIPPED_EXTENSION");
    
    Properties prop = PropertiesLoader.load(propertyFilePath);
    String repoFlagStr = (String)prop.get("ADD_TO_CHECKSUM_REPOSITORY");
    if("YES".equals(repoFlagStr)) {
        repoFlag = true;
    }
    repoDir = (String)prop.get("CHECKSUM_REPOSITORY_DIR");
    if(repoDir != null) {
        repoDir = repoDir.trim();
        repoDir = repoDir.replace("\\", "/");
        if(!repoDir.endsWith("/")) {
            repoDir = repoDir + "/";
        }
    }

    for(int i=0;i<sourceFileList.size();i++) {
        String fileName = sourceFileList.get(i);
        boolean skip = Util.checkIfExtentionIsToBeSkipped(skippedExtentionList, fileName);
        if(skip == true) {
            continue;
        }
		//------------------------------------------------------------
        File file = new File(fileName);
		File md5File = null;
		String parentFolderName = file.getParent();
		String fileNameOnly = file.getName();
		//------------------------------------------------------------
        //System.out.println("-----------------------------------------------------------");
		int result = verifyChecksum(fileName, parentFolderName + "/" + fileNameOnly + ".md5");
		if(result == CHECKSUM_MISSING) {
			result = verifyChecksum(fileName, parentFolderName + "/md5/" + fileNameOnly + ".md5");
		}
		if(result == CHECKSUM_MISSING) {
			result = verifyChecksum(fileName, parentFolderName + "/info/" + fileNameOnly + ".md5");
		}
		if(result == CHECKSUM_MISSING) {
			String fileNameMD5 = MD5Util.getMD5ChecksumAsHEX(fileNameOnly);
			result = verifyChecksum(fileName, repoDir + "/" + fileNameMD5.substring(0,2) + "/" + fileNameMD5 + ".txt");
		}
		//-------------------------------------------------------------
		if(result == CHECKSUM_SUCCESS) {
            System.out.println("OKAY : "+fileName);
		}
		if(result == CHECKSUM_FAILURE) {
            System.out.println("FAIL : "+fileName);
		}
		if(result == CHECKSUM_MISSING) {
            System.out.println("MISS : "+fileName);
		}
        //System.out.println("-----------------------------------------------------------");
    }
  }
  //-----------------------------------------------------------------------------------------------------------------------
  public static int verifyChecksum(String fileName, String md5FileName) throws Exception {
		File md5File = new File(md5FileName);
        if(!md5File.exists()) {
            return CHECKSUM_MISSING;
        }
        String md5String = MD5Util.getMD5ChecksumAsHEX(new File(fileName));
        String md5StringExisting = FileUtil.readFileContentsAsString(md5FileName);
        md5StringExisting = md5StringExisting.substring(0,32);
        //System.out.println("Existing : "+md5StringExisting);
        //System.out.println("Computed : "+md5String);
        if(md5StringExisting.equalsIgnoreCase(md5String)) {
			return CHECKSUM_SUCCESS;
        } else {
			return CHECKSUM_FAILURE;
        }
  }
  //-----------------------------------------------------------------------------------------------------------------------
}
