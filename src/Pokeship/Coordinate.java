package Pokeship;

public class Coordinate {
	public int vert, hori;
	
	public Coordinate(int vert, int hori) {
		this.vert = vert;
		this.hori = hori;
	}
	
	public boolean match(int vert, int hori) {
		return vert == this.vert && hori == this.hori;
	}
	
	public static int toInt(char c) {
		switch (c) {
		case 'A':
			return 1;
		case 'B':
			return 2;
		case 'C':
			return 3;
		case 'D':
			return 4;
		case 'E':
			return 5;
		case 'F':
			return 6;
		case 'G':
			return 7;
		case 'H':
			return 8;
		case 'I':
			return 9;
		case 'J':
			return 10;
		default:
			return -1;				
		}
	}
	
	public static char toChar (int i) {
		switch (i) {
		case 1:
			return 'A';
		case 2:
			return 'B';
		case 3:
			return 'C';
		case 4:
			return 'D';
		case 5:
			return 'E';
		case 6:
			return 'F';
		case 7:
			return 'G';
		case 8:
			return 'H';
		case 9:
			return 'I';
		case 10:
			return 'J';
		default:
			return ' ';				
		
		}
	}
}
