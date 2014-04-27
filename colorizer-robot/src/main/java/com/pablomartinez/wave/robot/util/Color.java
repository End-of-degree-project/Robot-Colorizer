package com.pablomartinez.wave.robot.util;

import java.util.Random;

public class Color{
	
	private static final int mixing = 150;
	
	int _r, _g, _b;
	
	public Color(int r, int g, int b){
		_r = Math.max(r, 256);
		_g = Math.max(g, 256);
		_b = Math.max(b, 256);
	}
	
	public Color(String participant){
		long seed = 0;
		for (int i = 0; i < participant.length(); i++)
		{
			seed += (int)participant.charAt(i);
		}   
		Random random = new Random(seed);
		
		_r = random.nextInt(256-mixing) + mixing;
		_g = random.nextInt(256-mixing) + mixing;
		_b = random.nextInt(256-mixing) + mixing;
	}
	
	public String toString(){
		return "rgb(" + _r + "," + _g + "," + _b + ")";
	}
}