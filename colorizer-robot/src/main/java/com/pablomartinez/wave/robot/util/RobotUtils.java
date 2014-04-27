package com.pablomartinez.wave.robot.util;

import java.net.URLEncoder;
import java.util.Random;

public class RobotUtils {

	
	public  final static String LOREM_IPSUM_CONTENT_1 = 
			"Lorem ipsum dolor sit amet, "
			+ "consectetur adipisicing elit, "
			+ "sed do eiusmod tempor incididunt ut "
			+ "labore et dolore magna aliqua. Ut enim "
			+ "ad minim veniam, quis nostrud exercitation "
			+ "ullamco laboris nisi ut aliquip ex ea commodo consequat. "
			+ "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore"
			+ " eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, "
			+ "sunt in culpa qui officia deserunt mollit anim id est laborum";
	
	public static String serialisedWaveIdstoURLParam(String waveletId, String waveId, String blipId ) {
		
		String strWaveRef = "waveId=" + URLEncoder.encode(waveId) 
				+ "&waveletId=" + URLEncoder.encode(waveletId) + "&documentId=" + URLEncoder.encode(blipId);
		
		
		
		return strWaveRef;
				
	}	
}
