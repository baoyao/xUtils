package test_jar;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author houen.bao
 * @date Jul 29, 2016 11:28:16 AM
 */
public class FindStrInJar {

	public static final String SEARCH_TEXT = "append";// 要寻找的字符串
	public static final String DIRECTORY_NAME = "/data/jar/";// 要寻找的jar包目录

	public ArrayList<String> jarFiles = new ArrayList<String>();
	public String condition = SEARCH_TEXT;

	public static void main(String args[]) {
		FindStrInJar findInJar = new FindStrInJar();
		Scanner s = new Scanner(System.in);
		while (true) {
			System.out.println("----------------------------------");
			List<String> jarFiles = findInJar.find(DIRECTORY_NAME, true);
			if (jarFiles.size() == 0) {
				System.out.println("Not Found");
			} else {
				for (int i = 0; i < jarFiles.size(); i++) {
					System.out.println(jarFiles.get(i));
				}
			}
			System.out.println("----------------------------------");

			System.out.print("search text:");
			String input = input(s);
			findInJar.jarFiles.clear();
			findInJar.setCondition(input);
			if ("0".equals(input)) {
				System.out.println("exit!");
				break;
			}
		}
		s.close();
	}

	private static String input(Scanner s) {
		String input = s.nextLine();
		if ("".equals(input)) {
			System.out.print("search text:");
			return input(s);
		}
		return input;
	}

	public FindStrInJar() {
	}

	public FindStrInJar(String condition) {
		this.condition = condition;
	}

	public FindStrInJar(String condition, String exclude) {
		this.condition = condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public List<String> find(String dir, boolean recurse) {
		searchDir(dir, recurse);
		return this.jarFiles;
	}

	public List<String> getFilenames() {
		return this.jarFiles;
	}

	protected String getClassName(ZipEntry entry) {
		StringBuffer className = new StringBuffer(entry.getName().replace("/", "."));
		return className.toString();
	}

	protected void searchDir(String dir, boolean recurse) {
		try {
			File d = new File(dir);
			if (!d.isDirectory()) {
				return;
			}
			File[] files = d.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (recurse && files[i].isDirectory()) {
					searchDir(files[i].getAbsolutePath(), true);
				} else {
					String filename = files[i].getAbsolutePath();
					System.out.println("file name: " + filename);
					if (filename.endsWith(".jar") || filename.endsWith(".zip")) {
						ZipFile zip = new ZipFile(filename);
						Enumeration entries = zip.entries();
						while (entries.hasMoreElements()) {
							ZipEntry entry = (ZipEntry) entries.nextElement();
							String thisClassName = getClassName(entry);
							if (thisClassName.lastIndexOf(".class") == -1) {
								BufferedReader r = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
								int line = 0;
								String tempStr = "";
								while ((tempStr = r.readLine()) != null) {
									line++;
									if (null != tempStr && tempStr.indexOf(condition) > -1) {
										this.jarFiles.add(thisClassName + " [" + line + "] " + tempStr);
									}
								}
							}
						}
						zip.close();
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
