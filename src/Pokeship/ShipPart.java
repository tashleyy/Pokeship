package Pokeship;

public class ShipPart {
	public Coordinate coordinate;
	public boolean damaged;
	
	public ShipPart(int vert, int hori) {
		coordinate = new Coordinate(vert, hori);
		damaged = false;
	}
	
	public boolean underFire(int vert, int hori) {
		if (coordinate.match(vert, hori)) {
			damaged = true;
			return true;
		}
		return false;
	}
	
	public boolean isAt(int vert, int hori) {
		return coordinate.match(vert, hori);
	}
}