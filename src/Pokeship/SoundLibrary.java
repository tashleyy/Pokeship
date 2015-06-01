package Pokeship;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundLibrary {
	private static Map<String, File> soundMap;
	static{
		soundMap = new HashMap<String,File>();
		soundMap.put("sound/cannon.wav", new File("sound/cannon.wav"));
		soundMap.put("sound/explode.wav", new File("sound/explode.wav"));
		soundMap.put("sound/sinking.wav", new File("sound/sinking.wav"));
		soundMap.put("sound/splash.wav", new File("sound/splash.wav"));
	}

	public static void playSound(String sound) {
		File toPlay = soundMap.get(sound);
		if(toPlay == null) {
			toPlay = new File(sound);
			soundMap.put(sound, toPlay);
		}
		
		try {
		AudioInputStream stream = AudioSystem.getAudioInputStream(toPlay);
		AudioFormat format = stream.getFormat();
		SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class,format,(int) (stream.getFrameLength() * format.getFrameSize()));
		SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
		
		line.open(stream.getFormat());
		line.start();
		int num_read = 0;
		byte[] buf = new byte[line.getBufferSize()];
		while ((num_read = stream.read(buf, 0, buf.length)) >= 0)
		{
			int offset = 0;
			
			while (offset < num_read)
			{
				offset += line.write(buf, offset, num_read - offset);
			}
		}
		line.drain();
		line.stop();
		} catch(IOException | UnsupportedAudioFileException | LineUnavailableException ioe) {
			System.out.println("Audio file is invalid!");
		}
	}
}
