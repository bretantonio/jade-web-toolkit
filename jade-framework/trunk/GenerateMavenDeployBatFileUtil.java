import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateMavenDeployBatFileUtil {

	/**
	 * Demonstrate use.
	 *
	 * @param aArgs
	 *            - <tt>aArgs[0]</tt> is the full name of an existing directory
	 *            that can be read.
	 * @throws IOException
	 */
	public static void main(String... aArgs) throws IOException {
		File dir1 = new File(".");
		System.out.println("Current dir : " + dir1.getCanonicalPath());

//		String filePath = "C:/dskim/eclipse-jee-helios-SR2-win32/workspace_mvn/jade/trunk/jade-all-4.0.1-maven2/target/jade-4.0.1-all-project/4.0.1-M1-SNAPSHOT/dist/jade-core";
// 		String filePath = "C:/dskim/eclipse-jee-helios-SR2-win32/workspace_mvn/jade/trunk/jade-all-4.0.1-maven2/target/jade-4.0.1-all-project/4.0.1-M1-SNAPSHOT/dist/";
		String filePath = "E:/dskim/eclipse/workspace/jade-all-4.0.1-maven2/target/jade-4.0.1-all-project/4.0.1-M1-SNAPSHOT/dist";
		File startingDirectory = new File(filePath);
		List<File> files = GenerateMavenDeployBatFileUtil
				.getFileListing(startingDirectory);
		HashSet FileNameHashSet = new HashSet(); 
		// print out all file names, in the the order of File.compareTo()
		for (File file : files) {

			String fileExtension = getFileExtensionName(file).toLowerCase();
			String fileNameWithoutExtension = getFileNameWithoutExtension(file).toLowerCase();
			int fileLength = fileNameWithoutExtension.length();
			if ("jar".equals(fileExtension) && fileNameWithoutExtension.indexOf("jade") == -1 && !FileNameHashSet.contains(fileNameWithoutExtension)) {
				//System.out.print(file.getName()+"\t");

				//regex
				String regx = "-[0-9]"; // substring comosed of Eng. alphabet

				Pattern pat = Pattern.compile(regx);
				Matcher mat = pat.matcher(fileNameWithoutExtension);
//				while (mat.find()){
//				System.out.println("-->"+mat.group());
//				}
				if(mat.find()){
				int indexOfVersion=  fileNameWithoutExtension.indexOf(mat.group(0));

				String artifactId = fileNameWithoutExtension.substring(0, indexOfVersion);
				String version =fileNameWithoutExtension.substring(indexOfVersion+1, fileLength);
				//System.out.println(arifactId +"\t"+version);
				System.out.println("mvn deploy:deploy-file -DgroupId="+artifactId+" -DartifactId="+artifactId+" -Dversion="+version+" -Dpackaging=jar -Dfile="+file.getAbsolutePath()+" -DrepositoryId=jade-web-toolkit-maven3-repo -Durl=svn:https://jade-web-toolkit.googlecode.com/svn/maven/3/repositories/external");
				}

//
//				int indexOfseperate = fileNameWithoutExtension.indexOf(".");
//				if(indexOfseperate==-1){
//					indexOfseperate = fileLength;
//				}
//				System.out.println(fileLength);
//				System.out.println(indexOfseperate);
//				String arifactId = fileNameWithoutExtension.substring(0, indexOfseperate-1);
//				String version =fileNameWithoutExtension.substring(arifactId.lastIndexOf("-")+1, fileLength);
//				arifactId = arifactId.substring(0, arifactId.lastIndexOf("-"));
//				System.out.println(arifactId +"\t"+version);

				FileNameHashSet.add(fileNameWithoutExtension);
			}
		}
	}

	public static String getFileExtensionName(File f) {
		if (f.getName().indexOf(".") == -1) {
			return "";
		} else {
			return f.getName().substring(f.getName().length() - 3,
					f.getName().length());
		}
	}

	public static String getFileNameWithoutExtension(File file) {
		int index = file.getName().lastIndexOf('.');
		if (index > 0 && index <= file.getName().length() - 2) {
			return file.getName().substring(0, index);
		}
		return "";
	}

	/**
	 * Recursively walk a directory tree and return a List of all Files found;
	 * the List is sorted using File.compareTo().
	 *
	 * @param aStartingDir
	 *            is a valid directory, which can be read.
	 */
	static public List<File> getFileListing(File aStartingDir)
			throws FileNotFoundException {
		validateDirectory(aStartingDir);
		List<File> result = getFileListingNoSort(aStartingDir);
		Collections.sort(result);
		return result;
	}

	// PRIVATE //
	static private List<File> getFileListingNoSort(File aStartingDir)
			throws FileNotFoundException {
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = aStartingDir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for (File file : filesDirs) {
			result.add(file); // always add, even if directory
			if (!file.isFile()) {
				// must be a directory
				// recursive call!
				List<File> deeperList = getFileListingNoSort(file);
				result.addAll(deeperList);
			}
		}
		return result;
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be
	 * read.
	 */
	static private void validateDirectory(File aDirectory)
			throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: "
					+ aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: "
					+ aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: "
					+ aDirectory);
		}
	}
}
