package Pokeship;

public class SoundThread extends Thread {
	String sound;
	
	SoundThread(String sound) {
		this.sound = sound;
	}
	
	public void run() {
		SoundLibrary.playSound(sound);
	}
}
