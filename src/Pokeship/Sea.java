package Pokeship;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Sea {
	private Ship[] ships;
	public boolean[][] shotAtBefore;
	public int numShips = 0;
	public int numDs = 0;
	private URL fileURL;
	
	public Sea() {
		shotAtBefore = new boolean[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				shotAtBefore[i][j] = false;
			}
		}
		ships = new Ship[5];
	}
	
	public Sea(URL fileURL) {
		this.fileURL = fileURL;
		shotAtBefore = new boolean[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				shotAtBefore[i][j] = false;
			}
		}
		ships = new Ship[5];
	}
	
	public boolean locateShips() {
		int numA = 0, numB = 0, numC = 0, numD = 0;
		
		String[] grid = new String[10];
		BufferedReader br2 = null;
		try {
			br2 = new BufferedReader(new InputStreamReader(fileURL.openStream()));
			for (int i = 0; i < 10; i++) {
				grid[i] = br2.readLine();
				if (grid[i] == null || grid[i].length() != 10) {
					System.out.println("Make sure there are 10 rows and 10 columns in the grid.");
					return false;
				}
			}
		} catch (FileNotFoundException fnfe) {
			System.out.println("FileNotFoundException: " + fnfe.getMessage());
			return false;
		} catch (IOException ioe) {
			System.out.println("IOException: " + ioe.getMessage());
		} finally {
			try {
				if (br2 != null) {
					br2.close();
				}
			} catch (IOException ioe) {
				System.out.println("IOException: " + ioe.getMessage());				
			}
		}

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				switch (grid[i].charAt(j)) {
				case 'X':
					break;
				case 'A':
					if (isPartOfShip(i+1, j+1)) {
						break;
					} else if (isValidShip(5, i, j, grid)) {
						if (numA == 1) {
							return false;
						}
						addShip(i+1, j+1, isVertical(i, j, grid), 5);
						numA++;
					} else {
						return false;
					}
					break;
				case 'B':
					if (isPartOfShip(i+1, j+1)) {
						break;
					} else if (isValidShip(4, i, j, grid)) {
						if (numB == 1) {
							return false;
						}
						addShip(i+1, j+1, isVertical(i, j, grid), 4);
						numB++;
					} else {
						return false;
					}
					break;
				case 'C':
					if (isPartOfShip(i+1, j+1)) {
						break;
					} else if (isValidShip(3, i, j, grid)) {
						if (numC == 1) {
							return false;
						}
						addShip(i+1, j+1, isVertical(i, j, grid), 3);
						numC++;
					} else {
						return false;
					}
					break;
				case 'D':
					if (isPartOfShip(i+1, j+1)) {
						break;
					} else if (isValidShip(2, i, j, grid)) {
						if (numD == 2) {
							return false;
						}
						addShip(i+1, j+1, isVertical(i, j, grid), 2);
						numD++;
					} else {
						return false;
					}
					break;
				default:
					return false;
				}
			}
		}
		if (numA+numB+numC+numD == 5) return true;
		return false;
	}
	
	public void addShip(int vert, int hori, boolean isVertical, int size) {
		switch (size) {
		case 5:
			ships[numShips] = new AircraftCarrier(5, vert, hori, isVertical);
			break;
		case 4:
			ships[numShips] = new Battleship(4, vert, hori, isVertical);
			break;
		case 3:
			ships[numShips] = new Cruiser(3, vert, hori, isVertical);
			break;
		case 2:
			ships[numShips] = new Destroyer(2, vert, hori, isVertical);
			numDs++;
			break;
		default:
			System.out.println("Error in Sea.addShip(int, int, boolean, int)");
			return;
		}
		numShips++;
	}
	
	public boolean isPartOfShip(int vert, int hori) {
		for (int i = 0; i < numShips; i++) {
			if (ships[i].hasPartAt(vert, hori)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isValidShip(int size, int vert, int hori, String[] grid) {
		for (int i = 1; i < size; i++) {
			if (vert+i > 9) break;
			if (grid[vert].charAt(hori) != grid[vert+i].charAt(hori)) break;
			if (i == size-1) return true;
		}
		for (int i = 1; i < size; i++) {
			if (hori+i > 9) break;
			if (grid[vert].charAt(hori) != grid[vert].charAt(hori+i)) break;
			if (i == size-1) return true;
		}
		return false;
	}
	
	private boolean isVertical(int vert, int hori, String[] grid) {
		if (vert+1 < 10 && (grid[vert+1].charAt(hori) == grid[vert].charAt(hori) && !isPartOfShip(vert, hori))) return true;
		return false;
	}
	
	public char getShipUnderFire(int vert, int hori) {
		for (int i = 0; i < numShips; i++) {
			char c = ships[i].underFire(vert, hori);
			if (c != 'Z') {
				return c;
			}
		}
		return 'Z';
	}

	public boolean allShipsSunken() {
		for (int i = 0; i < numShips; i++) {
			if(!ships[i].isSunk) {
				return false;
			}
		}
		return true;
	}
	
	public Ship getShipAt(int vert, int hori) {
		for (int i = 0; i < numShips; i++) {
			if (ships[i].hasPartAt(vert, hori)) {
				return ships[i];
			}
		}
		return null;
	}

	public void removeShipAt(int vert, int hori) {
		for (int i = 0; i < numShips; i++) {
			if (ships[i].hasPartAt(vert, hori)) {
				if (ships[i].toString().equals("Chimchar")) {
					numDs--;
				}
				for (int j = i; j < numShips-1; j++) {
					ships[j] = ships[j+1];
				}
				numShips--;
				return;
			}
		}
	}
	
	public Ship[] getAllShips() {
		return ships;
	}
}