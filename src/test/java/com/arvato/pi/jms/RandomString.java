package com.arvato.pi.jms;

import java.util.Random;

public class RandomString {
    private static final String dCase = "abcdefghijklmnopqrstuvwxyz";
    private static final String uCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String sChar = "!@#$%^&*()<>+-=\\|`~?/ \t\n";
    private static final String intChar = "0123456789";
    private static Random r = new Random();
    
    public static String makeString(int len) {
    	String randomString = "";
    	while(randomString.length() < len) {
    		int rPick = r.nextInt(4);
    		String optionString;
    		switch(rPick) {
    			case 0:
    				optionString = dCase;
    				break;
    			case 1:
    				optionString = uCase;
    				break;
    			case 2:
    				optionString = sChar;
    				break;
    			default:
    				optionString = intChar;
    				break;
    		}
    		int spot = r.nextInt(optionString.length());
    		randomString += optionString.charAt(spot);
    	}
    	return randomString;
    }
}
