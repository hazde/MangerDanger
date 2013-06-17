package main;

import java.io.*;

import javax.sound.sampled.*;

public class Sound {
    public static class Clips {
        public Clip[] clips;
        private int p;
        private int count;
        
        public Clips(byte[] buffer, int count) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
            if (buffer==null) return;
            
            clips = new Clip[count];
            this.count = count;
            for (int i=0; i<count; i++) {
                clips[i] = AudioSystem.getClip();
                clips[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
            }
        }
        
        public void play() {
            if (clips==null) return;
            
            clips[p].stop();
            clips[p].setFramePosition(0);
            clips[p].start();
            
            p++;
            if (p>=count) p = 0;
        }
        
        public void play(boolean loop) {
            if (clips==null) return;
            
            
            
            clips[p].stop();
            clips[p].setFramePosition(0);
            if (loop) clips[p].loop(Clip.LOOP_CONTINUOUSLY);
            clips[p].start();
            
            p++;
            if (p>=count) p = 0;
        }
        
        public void stop() {
        	if (clips==null) return;
            clips[p].stop();
            p++;
            if (p>=count) p = 0;
        }
        
        public boolean isPlaying() {
        	return clips[p].isRunning();
        }
        
        public void setVolume(float volume) {
        	FloatControl gainControl = (FloatControl) clips[p].getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(volume);
        }
        
        public void setPosition(int position) {
        	clips[p].setFramePosition(position);
        	clips[p].start();
        	p++;
            if (p>=count) p = 0;
        }
        
        public int getFrames() {
        	return clips[p].getFrameLength();
        }
        
    }
    public static Clips music2 = load("/Sounds/Music/music2.wav", 1);
    public static Clips music3 = load("/Sounds/Music/funkymusic.wav", 1);
    public static Clips music4 = load("/Sounds/Music/music3.wav", 1);
    public static Clips music5 = load("/Sounds/Music/music4.wav", 1);
    public static Clips deathscreen = load("/Sounds/Music/deathscreen.wav", 1);
    public static Clips deathscreen2 = load("/Sounds/Music/deathscreen2.wav", 1);
    public static Clips deathscreen3 = load("/Sounds/Music/deathscreen3.wav", 1);
    public static Clips hit = load("/Sounds/hit.wav", 64);
    public static Clips menu = load("/Sounds/menu.wav", 4);
    public static Clips death = load("/Sounds/death.wav", 4);
    public static Clips jump = load("/Sounds/jump.wav", 1);
    public static Clips ballon = load("/Sounds/peeballon.wav", 1);
    
    public static float DECREASE_5DB = -5.0f;
    public static float DECREASE_10DB = -10.0f;
    public static float DECREASE_15DB = -15.0f;
    public static float DECREASE_20DB = -20.0f;
    
    public static float INCREASE_5DB = 5.0f;
    public static float INCREASE_10DB = 10.0f;
    public static float INCREASE_15DB = 15.0f;
    public static float INCREASE_20DB = 20.0f;

    private static Clips load(String name, int count) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataInputStream dis = new DataInputStream(Sound.class.getResourceAsStream(name));
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = dis.read(buffer)) >= 0) {
                baos.write(buffer, 0, read);
            }
            dis.close();
            
            byte[] data = baos.toByteArray();
            return new Clips(data, count);
        } catch (Exception e) {
            try {
                return new Clips(null, 0);
            } catch (Exception ee) {
                return null;
            }
        }
    }
    
    public static void stopAllMusic() {
    	music2.stop();
    	music3.stop();
    	music4.stop();
    	music5.stop();
    	deathscreen.stop();
    	deathscreen2.stop();
    	deathscreen3.stop();
    }
    
    public static void setPosition(Clips clip, int position) {
    	clip.setPosition(position);
    }
    
    public static boolean isPlaying(Clips clip) {
    	return clip.isPlaying();
    }
    
    public static int getFrames(Clips clip) {
    	return clip.getFrames();
    }
    
    public static void setVolume(Clips clip, float volume) {
    	clip.setVolume(volume);
    }

    public static void touch() {
    }
}
