package uk.ac.ucl.cs.radar.utilities;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import uk.ac.ucl.cs.radar.model.Model;
import uk.ac.ucl.cs.radar.model.Parser;

public class Helper {
	
	public static Model loadModel (String model, int nbr_Simulation, String infoValueObjective, String subGraphObjective) throws Exception{
		Model semanticModel = null;
		try {
			semanticModel = new Parser().parseCommandLineModel(model.trim(), nbr_Simulation, infoValueObjective,subGraphObjective);
		}catch (RuntimeException re){
			throw new RuntimeException( "Error: "+ re.getMessage());
		}
		return semanticModel;
		
	}
	public static void writeImageToFolder (String directory, BufferedImage image, String imageFormat, String imageName) throws IOException{
		File resultFile;
		resultFile = new File(directory);
		if (!resultFile.exists()) {
			new File(directory).mkdirs();
			System.out.println("Creating " + directory);
		}
        ImageIO.write(image, imageFormat, new File( directory + imageName  + "." + imageFormat.toLowerCase(Locale.ENGLISH)));
		
	}
	public static void writeToAFile (String path,String content) throws IOException{
		FileOutputStream fos   = new FileOutputStream(path) ;
	      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
	      BufferedWriter bw      = new BufferedWriter(osw)        ;
	      bw.write(content);
	      bw.write("\n");
	      bw.close();
	}
	public static void printResults (String directory, String expResult, String fileName, boolean append) throws IOException{
		File resultFile;
		resultFile = new File(directory);
		if (!resultFile.exists()) {
			new File(directory).mkdirs();
			System.out.println("Creating " + directory);
		}
	      FileOutputStream fos   = new FileOutputStream(directory + fileName, append ) ;
	      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
	      BufferedWriter bw      = new BufferedWriter(osw)        ;
	      bw.write(expResult);
	      bw.write("\n");
	      bw.close();
	}
	@SuppressWarnings("resource")
	public static String readFile(String fileName) {
		StringBuilder model = new StringBuilder(100);
		BufferedReader bfr = null;

		try {
			bfr = new BufferedReader(new FileReader(new File(fileName)));
			String line = null;
			while ((line = bfr.readLine()) != null) {
				model.append(line + "\n");
			}
			model.delete(model.length() - 1, model.length());
			return model.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static boolean deletePreviousResults (String outputdirectory, String folderToDelete){
		String path = outputdirectory + folderToDelete ;
		File previousResults = new File(path);
		boolean deleted = false;
		if (previousResults.exists()) {
			deleted = previousResults.delete();
			System.out.println("deleted " + path);
		}else{
			return false;
		}
		return deleted;
	     
	}
	public static BufferedImage getImage(Component c) {
	    BufferedImage bi = null;
	    try {
	        bi = new BufferedImage(c.getWidth(),c.getHeight(), BufferedImage.TYPE_INT_RGB);
	        Graphics2D g2d =bi.createGraphics();
	        c.print(g2d);
	        g2d.dispose();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    return bi;
	}
	
	public static BufferedImage createImage(JPanel panel) {
	    int w = panel.getWidth();
	    int h = panel.getHeight();
	    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = bi.createGraphics();
	    panel.printAll(g);
	    return bi;
	}
	public static String addDirectorySlash(String path, char separator){
		if (path != "" && path.trim().charAt(path.length()-1) != separator){
			path =path.trim() + separator;
			
		}
		return path;
	}
	public static String processJarLocationPath(String  outPutDirectory){
		String result = outPutDirectory;
		File f = new File(outPutDirectory);
		char separator = f.separator.toCharArray()[0];
		boolean filebeginningHasSeparator = false;
		if (outPutDirectory != null && outPutDirectory != "" ){
			if (outPutDirectory.toCharArray()[0] == separator){
				filebeginningHasSeparator = true;
			}
			String[] foldersInPath = StringUtils.split(outPutDirectory,f.separator);
			
			// check if the path where the jar is stored contains the jar itself. if yes, remove the jar name to have only folders
			if (foldersInPath != null && foldersInPath[foldersInPath.length-1].contains(".jar")){
				String jarPath = "";
				for (int i =0; i< foldersInPath.length-1; i++){
					jarPath += foldersInPath[i] + f.separator;
				}
				// did this because the splitting of the path removes the separator at the beginning if it exist.
				if(filebeginningHasSeparator){
					jarPath = f.separator + jarPath;
				}
				outPutDirectory = jarPath;
			}
			
		}
		
		result = addDirectorySlash(outPutDirectory, separator);
		return result;
	}
	
	

}
