package Pokeship;

public class LockedSoundThread extends SoundThread {
	String sound;
	BackgroundLabel bl;
	
	LockedSoundThread(String sound, BackgroundLabel bl) {
		super(sound);
		this.sound = sound;
		this.bl = bl;
	}
	
	public void run() {
		bl.lock.lock();
		super.run();
		bl.lock.unlock();
	}
}
