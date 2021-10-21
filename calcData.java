package calcData;

import java.util.LinkedList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class calcData {
	
	public static DecimalFormat df = new DecimalFormat("##.##");
	public static double groupGPAMean = 0;
	public static double sectionGPAMean = 0;
	
	public static void main(String[] args) {		
		//Print out instructions for the user
		System.out.println("Instructions:" + "\n" +
				"- Enter the file path to the main folder" + "\n" +
				"- Enter a Section Name (Testing: Enter 'COMSC234_01)" + "\n" +
				"- Enter the name of a Group File (Testing: Enter 'Groups')" + "\n" + 
				"- Enter a Group Name (Testing: Enter 'COMSC234')");
		
		getSecFileFolderPath();
    }
	
	public static void getSecFileFolderPath() {
		Scanner scanner1 = new Scanner(System.in);
		//User enters main folder path
		String mainFolderPath = scanner1.nextLine();
		
		File folder = new File(mainFolderPath);
    	File[] listOfFiles = folder.listFiles();
    	
		if (mainFolderPath == null || listOfFiles.length == 0) {
        	System.out.print("The file path is either empty or not inputted correctly");
    	} else {
    		getSecTxtFileName(mainFolderPath);
    		getGroupTxtFileName(mainFolderPath);
    	}
	}
	
	public static void getSecTxtFileName(String mainFolderPath) {
		Scanner scanner2 = new Scanner(System.in);
		
		File folder = new File(mainFolderPath);
    	File[] listOfFiles = folder.listFiles();
    	
    	// create an array that stores the matching section file name
		String secFileName[] = new String[listOfFiles.length];
    	
		String sectionName = scanner2.nextLine();
		
		//Looks for the section text file name inputed by the user
    	for(int i = 0; i < listOfFiles.length; i++) {
    		secFileName[i] = listOfFiles[i].getName();
			if(!secFileName[i].contains(sectionName)) {
    			System.out.println("Invalid Section Text File Name");
			} else if(secFileName[i].contains(sectionName)) {
    			sectionName = secFileName[i];
    			break;
    		}
    	}
    	System.out.println("File was found: " + sectionName);
    	readSecFile(sectionName, mainFolderPath);
	}
	
	public static void getGroupTxtFileName(String mainFolderPath) {
		//User inputs the name of the text file containing the Groups
		Scanner scanner3 = new Scanner(System.in);
		
		File folder = new File(mainFolderPath);
    	File[] listOfFiles = folder.listFiles();
    	
		String groupFileName[] = new String[listOfFiles.length];
		
		String groupTxtFileName = scanner3.nextLine();
		
		//Looks for the group text file name inputed by the user
    	for(int i = 0; i < listOfFiles.length; i++) {
    		groupFileName[i] = listOfFiles[i].getName();
    		if(!groupFileName[i].contains(groupTxtFileName)) {
    			System.out.println("Invalid Group Text File Name");
    		} else if(groupFileName[i].contains(groupTxtFileName)) {
    			groupTxtFileName = groupFileName[i];
    			break;
    		}
    	}
    	System.out.println("File was found: " + groupTxtFileName);
    	readGroupFile(groupTxtFileName, mainFolderPath);
	}
	
	public static void readSecFile(String sectionName, String mainFolderPath) {
		LinkedList<String> listOfSecGPAs = new LinkedList<String>();
		File file = new File(mainFolderPath + "/" + sectionName);
    	
    	try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));) {
    		br.mark((int) (file.length() + 1));
    		getSecName(br);
			getUserIDs(br);
			getUserNames(br,file);
			listOfSecGPAs.addAll(getUserGPAs(br, null));
			
    	} catch(IOException e) {
    		System.out.println("Could not read file");
    	}
    	
		calcSecGPAMean(listOfSecGPAs, groupGPAMean);
	}
	
	public static void readGroupSecFiles(String mainFolderPath, String sectionName, LinkedList<String> listOfGroupSecTxtFiles) {
		LinkedList<String> listOfAllGPAs = new LinkedList<String>();
		for(String s: listOfGroupSecTxtFiles){
			File file = new File(mainFolderPath + "/" + s);
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));) {
				br.mark((int) (file.length() + 1));
	    		getGroupSecName(br, listOfGroupSecTxtFiles);
	    		getUserIDs(br);
	    		getUserNames(br,file);
	    		listOfAllGPAs.addAll(getUserGPAs(br, listOfGroupSecTxtFiles));
	    		
	    	} catch(IOException e) {
	    		System.out.println("Could not read file");
	    	}
		}
		System.out.println("List of all GPAs in the Group: " + listOfAllGPAs);
		calcGroupGPAMean(listOfAllGPAs, groupGPAMean);
	}
	
	public static void readGroupFile(String groupTxtFileName, String mainFolderPath) {
		//User inputs the groupName they want to select that is stored inside the groupTextFile
		Scanner scanner4 = new Scanner(System.in);
		
		File folder = new File(mainFolderPath);
    	File[] listOfFiles = folder.listFiles();
    	
		String groupName = scanner4.nextLine();
		
		int countGroupNames = 0;
		
		File file = new File(mainFolderPath + "/" + groupTxtFileName);
    	
    	try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));) {
    		while(br.readLine() != null) {
    			
    			countGroupNames++;
    			String[] listOfGroupNames = new String[countGroupNames];
    			
    			for (int i = 0; i < countGroupNames; i++) {
        			listOfGroupNames[i] = br.readLine();
        			if (!listOfGroupNames[i].contains(groupName)) {
        				System.out.println("The group name entered was not found inside the group text file");
        			} else if(listOfGroupNames[i].contains(groupName)) {
        				break;
        			}
        		}
    		}
    		
    		System.out.println("Group name found: " + groupName);
    		getGroupSecTxtFiles(mainFolderPath, groupName);
    		
    	} catch(IOException e) {
    		System.out.println("Could not read file");
    	}
	}
	
	public static void getGroupSecTxtFiles(String mainFolderPath, String groupName) {
		LinkedList<String> listOfGroupSecTxtFiles = new LinkedList<String>();
		File folder = new File(mainFolderPath);
    	File[] listOfFiles = folder.listFiles();
    	
		String[] groupSecFileName = new String[listOfFiles.length];
		
		for(int i = 0; i < listOfFiles.length; i++) {
			groupSecFileName[i] = listOfFiles[i].getName();
			
			if(!groupSecFileName[i].contains(groupName)) {
				System.out.println("Section File names associated with the group are not found");
			} else if(groupSecFileName[i].contains(groupName)) {
				listOfGroupSecTxtFiles.add(groupSecFileName[i]);
			} 
		}
		
		System.out.println(listOfGroupSecTxtFiles);
		readGroupSecFiles(mainFolderPath, groupName, listOfGroupSecTxtFiles);
	}
	
	public static void getSecName(BufferedReader br) {
		LinkedList<String> listOfSecNames = new LinkedList<String>();
		String sectionName="";
		String[] lines = null;
    	
		try {
			sectionName = br.readLine();
			//Remove commas and credits after section name
	    	lines = sectionName.split(" ");
			listOfSecNames.add(lines[0]);
			
		} catch (IOException e) {
			System.out.print("Cannot read first line");
		}
		System.out.println("Section Name: " + listOfSecNames);
    	}
	
	public static void getGroupSecName(BufferedReader br, LinkedList<String> listOfGroupSecTxtFiles) {
		LinkedList<String> listOfSecNames = new LinkedList<String>();
		String sectionName="";
		String[] lines = null;
		int numOfSecNames = 1;
    	
		try {
			sectionName = br.readLine();
		} catch (IOException e) {
			System.out.print("Cannot read first line");
		}
		//Remove commas and credits after section name
    	lines = sectionName.split(" ");
    	for (int i = 0; i < numOfSecNames; i++) {
    		listOfSecNames.add(i, lines[0]);
    		if( numOfSecNames >= listOfGroupSecTxtFiles.size()) {
    			break;
    		}
    	}
    	
    	System.out.println("Section Name: " + listOfSecNames.toString());
	}
	
	public static void getUserIDs(BufferedReader br) {
		LinkedList<String> listOfIDs = new LinkedList<String>();
    	
    	String[] lines = null;
    	String line = "";
		try {
			while((line = br.readLine()) != null) {
				lines = line.split(",");
				listOfIDs.add(lines[0]);
			}
			
		} catch (IOException e) {
			System.out.println("Cannot read user IDs'");
		}
		
		System.out.println(listOfIDs);
	}
	
	public static void getUserNames(BufferedReader br, File file) {
		LinkedList<String> listOfUserNames = new LinkedList<String>();
    	
    	String[] lines = null;
    	String line = "";
    
		try {
			br.reset();
			br.readLine();
			
			while((line = br.readLine()) != null) {
				lines = line.split("\"");
				listOfUserNames.add(lines[1]);
			}
			
		} catch (IOException e) {
			System.out.println("Cannot read user names");
		}
		
		System.out.println(listOfUserNames);
	}
	
	public static LinkedList<String> getUserGPAs(BufferedReader br, LinkedList<String> listOfGroupSecTxtFiles) {
		LinkedList<String> listOfUserGPAs = new LinkedList<String>();
    	
    	String[] lines = null;
    	String line = "";
    
		try {
			br.reset();
			br.readLine();
			
			while((line = br.readLine()) != null) {
				lines = line.split(",");
				convertUserGPAs(listOfUserGPAs, lines, listOfGroupSecTxtFiles);
			}
			
		} catch (IOException e) {
			System.out.println("Cannot read user GPAs'");
		}
		System.out.println(listOfUserGPAs);
		return listOfUserGPAs;
	}
	
	public static void convertUserGPAs(LinkedList<String> listOfUserGPAs, String[] lines, LinkedList<String> listOfGroupSecTxtFiles) {
		switch(lines[3]) {
			case "A": 
				lines[3] = "4.0";
				break;
			case "A-": 
				lines[3] = "3.67";
				break;
			case "B+": 
				lines[3] = "3.33";
				break;
			case "B": 
				lines[3] = "3.00";
				break;
			case "B-": 
				lines[3] = "2.67";
				break;
			case "F": 
				lines[3] = "0.00";
				break;
			default:
				break;
		}
		listOfUserGPAs.add(lines[3].replace(" ", ""));
	}
	
	public static void calcGroupGPAMean(LinkedList<String> listOfAllGPAs, double groupGPAMean) {
		double parsedGPA = 0;
		double sumGroupGPA = 0;
		
		for (int i = 0; i < listOfAllGPAs.size(); i++) {
			parsedGPA = Double.parseDouble(listOfAllGPAs.get(i));
			sumGroupGPA += parsedGPA;
		}
		groupGPAMean = sumGroupGPA / listOfAllGPAs.size();
	
		System.out.println("Group GPA Mean: " + df.format(groupGPAMean));
		
		if(groupGPAMean != 0) {
			calcPopStanDev(listOfAllGPAs, sumGroupGPA, groupGPAMean);
		}
	}
	
	public static void calcSecGPAMean(LinkedList<String> listOfSecGPAs, double sectionGPAMean) {
		double parsedGPA = 0;
		
		for (int i = 0; i < listOfSecGPAs.size(); i++) {
			parsedGPA = Double.parseDouble(listOfSecGPAs.get(i));
			sectionGPAMean += parsedGPA;
		}
		sectionGPAMean = sectionGPAMean / listOfSecGPAs.size();
		
		System.out.println("Section GPA Mean: " + df.format(sectionGPAMean));
	}
	
	public static void calcPopStanDev(LinkedList<String> listOfAllGPAs, double sumGroupGPA, double groupGPAMean) {
		double popStandDev = 0;
		for(int i = 0; i < listOfAllGPAs.size(); i++) {
			popStandDev += ((Double.parseDouble(listOfAllGPAs.get(i)) - (sumGroupGPA / listOfAllGPAs.size()))*
			(Double.parseDouble(listOfAllGPAs.get(i)) - (sumGroupGPA / listOfAllGPAs.size())));
		}
		popStandDev += popStandDev / listOfAllGPAs.size();
		Math.sqrt(popStandDev);
		System.out.println("Population Standard Deviation: " + df.format(popStandDev));
		
		if(popStandDev != 0) {
			calcZScore(sectionGPAMean, groupGPAMean, popStandDev);
		}
	}
	
	public static void calcZScore(double sectionGPAMean, double groupGPAMean, double popStanDev) {
		double zScore = 0;
		zScore = (sectionGPAMean - groupGPAMean) / popStanDev;
		System.out.println("Z-Score: " + df.format(zScore));
	}
}
