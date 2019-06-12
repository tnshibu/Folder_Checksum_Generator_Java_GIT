import java.io.*;
import java.util.*;
import java.nio.file.Files;

public class Folder_Checksum_Generator {
  //-----------------------------------------------------------------------------------------------------------------------
  private static String SOURCE_BASE_FOLDER = "."; //by default, process current directory
  private static List<String> sourceFileList = new ArrayList<String>();
  private static Map<String, List<String>> propertiesMap = null;
  private static List<String> skippedExtentionList = null;
  private static List<String> repoFlagList = null;
  private static boolean repoFlag = false;
  private static String repoDir = "";
  //-----------------------------------------------------------------------------------------------------------------------
  public static void main(String[] args) throws Exception {
    if(args != null && args.length > 0 && !args[0].trim().equals("")) {
    	SOURCE_BASE_FOLDER = args[0];
    }
    //SOURCE_BASE_FOLDER ="D:\\zShibu\\0_from_TRC\\00-shibu\\fin\\1";
    //System.out.println("SOURCE_BASE_FOLDER ="+SOURCE_BASE_FOLDER);
    File sourceFolder = new File(SOURCE_BASE_FOLDER);
    if(!sourceFolder.exists()) {
        System.out.println("Folder "+SOURCE_BASE_FOLDER + " does not exist. Exiting");
        System.exit(-1);
    }
    sourceFileList = FileUtil.getFileListFromFolder(SOURCE_BASE_FOLDER);
    String propertyFilePath = Util.locatePropertiesFile();
    propertiesMap = PropertiesLoader.loadToHashMap(propertyFilePath);

    Properties prop = PropertiesLoader.load(propertyFilePath);
    String repoFlagStr = (String)prop.get("ADD_TO_CHECKSUM_REPOSITORY");
    if(repoFlagStr.equals("YES")) {
        repoFlag = true;
    }
    repoDir = (String)prop.get("CHECKSUM_REPOSITORY_DIR");
    repoDir = repoDir.trim();
    repoDir = repoDir.replace("\\", "/");
    if(!repoDir.endsWith("/")) {
    	repoDir = repoDir + "/";
    }

    skippedExtentionList = (List<String>)propertiesMap.get("SKIPPED_EXTENSION");
    for(int i=0;i<sourceFileList.size();i++) {
        String fileName = sourceFileList.get(i);
        boolean skip = Util.checkIfExtentionIsToBeSkipped(skippedExtentionList, fileName);
        if(skip == true) {
            continue;
        }
		//------------------------------------------------------------
        File file = new File(fileName);
        if(!file.exists()) {
			System.out.println("FILE_NOT_FOUND:"+fileName);
            continue;
        }
		//------------------------------------------------------------
		File md5File = null;
		String parentFolderName = file.getParent();
		String fileNameOnly = file.getName();
		//------------------------------------------------------------
		String md5FileInSubDirectoryStr1 = parentFolderName + "/info/" + fileNameOnly + ".md5";
		md5File = new File(md5FileInSubDirectoryStr1);
        if(md5File.exists()) {
            continue;
        }
		//------------------------------------------------------------
		String md5FileInSubDirectoryStr2 = parentFolderName + "/md5/" + fileNameOnly + ".md5";
		md5File = new File(md5FileInSubDirectoryStr2);
        if(md5File.exists()) {
            continue;
        }
		//------------------------------------------------------------
        //check if the checksum file exists in the checksum repo
		String fileNameMD5 = MD5Util.getMD5ChecksumAsHEX(fileNameOnly);
		File ff = new File(repoDir + "/" + fileNameMD5.substring(0,2) + "/" + fileNameMD5 + ".txt");
		if(ff.exists()) {
			continue;
		}
		//------------------------------------------------------------
        md5File = new File(fileName+".md5");
        if(md5File.exists()) {
            continue;
        }
		//------------------------------------------------------------
        String md5String = MD5Util.getMD5ChecksumAsHEX(new File(fileName));
        FileOutputStream fos =  new FileOutputStream(md5File);
        fos.write((md5String + " " + md5File.getName().substring(0,md5File.getName().length()-4)).getBytes());
        fos.close();
        //writeToChecksumRepo(md5String, md5File);
        writeToChecksumRepo(md5File);
        System.out.println(fileName);
    }
  }
  //-----------------------------------------------------------------------------------------------------------------------
  public static void writeToChecksumRepo(String md5String, File md5File) throws Exception {
        String prefix = md5String.substring(0,2);
        File folder = new File(repoDir+prefix);
        folder.mkdirs();
        File destination = new File(repoDir + prefix + "/" + md5String + ".txt");
        Files.copy(md5File.toPath(), destination.toPath());
  }
  //-----------------------------------------------------------------------------------------------------------------------
  public static void writeToChecksumRepo(File md5File) throws Exception {
      List<String> md5List = FileUtil.readFileContentsAsStringList(md5File.getAbsolutePath());
      for(int i=0;i<md5List.size(); i++) {
        String oneMd5Line = md5List.get(i);
        
        //first write to repo based for file content MD5
        String fileContentMd5 = oneMd5Line.substring(0,32);
        String fileName = oneMd5Line.substring(oneMd5Line.indexOf(" ")+1);
        
        String prefix = fileContentMd5.substring(0,2);
        File folder = new File(repoDir+prefix);
        folder.mkdirs();
        File destination = new File(repoDir + prefix + "/" + fileContentMd5 + ".txt");
        FileOutputStream fos =  new FileOutputStream(destination);
        fos.write((fileContentMd5 + " " + fileName).getBytes());
        fos.close();
        
        //second write to repo based for file name MD5
        String fileNameMD5 = MD5Util.getMD5ChecksumAsHEX(fileName);
        prefix = fileNameMD5.substring(0,2);
        folder = new File(repoDir+prefix);
        folder.mkdirs();
        destination = new File(repoDir + prefix + "/" + fileNameMD5 + ".txt");
        fos =  new FileOutputStream(destination);
        fos.write((fileContentMd5 + " " + fileName).getBytes());
        fos.close();
      }
  }

  //-----------------------------------------------------------------------------------------------------------------------
}
