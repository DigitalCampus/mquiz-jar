package org.digitalcampus.mquiz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Prog {

	public static void main(String [ ] args)
	{
	      System.out.println("From string:");
	      String md5 = createMD5("he");
	      System.out.println(md5);
	      System.out.println("From file:");
	      String datafile = "/home/alex/data/development/mquiz-jar/src/org/digitalcampus/mquiz/Prog.java";
	      File f = new File(datafile);
	      String h = createMD5(f);
	      
	      System.out.println(h);
		
	}
	
	public static String createMD5(File filename){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			InputStream is = new FileInputStream(filename);
			is = new DigestInputStream(is, md);
			byte[] digest = md.digest();
			is.close();
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				hexString.append(Integer.toHexString(0xFF & digest[i]));
			}
			String s = hexString.toString();
			return hexString.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static String createMD5(String content){
		byte[] bytesOfMessage;
		try {
			bytesOfMessage = content.getBytes("UTF-8");
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(bytesOfMessage);
			byte[] hash = digest.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i=0;i<hash.length;i++) {
				hexString.append(Integer.toHexString(0xFF & hash[i]));
			}
			return hexString.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
