package Pokeship;

public abstract class Ship {
	public int size = 0, hitAnims = 0;
	public ShipPart shipParts[];
	public boolean isSunk = false, isVertical;
	
	public Ship(int size, int vert, int hori, boolean isVertical) {
		this.size = size;
		this.isVertical = isVertical;
		shipParts = new ShipPart[size];
		for (int i = 0; i < size; i++) {
			shipParts[i] = new ShipPart(vert, hori);
			if (isVertical) {
				vert++;
			} else {
				hori++;
			}
		}
	}
	
	public char underFire(int vert, int hori) {
		for (int i = 0; i < size; i++) {
			if (shipParts[i].underFire(vert, hori)) {
				for (int j = 0; j < size; j++) {
					if (!shipParts[j].damaged) {
						isSunk = false;
						break;
					} else if (j == size-1) {
						isSunk = true;
					}
				}
				if (size == 2) {
					return 'D';
				} else if (size == 3) {
					return 'C';
				} else if (size == 4) {
					return 'B';
				} else {
					return 'A';
				}
			}
		}
		return 'Z';
	}
	
	public boolean hasPartAt(int vert, int hori) {
		for (int i = 0; i < size; i++) {
			if (shipParts[i].isAt(vert, hori)) {
				return true;
			}
		}
		return false;
	}
}
