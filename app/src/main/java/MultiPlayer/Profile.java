package MultiPlayer;

import android.graphics.drawable.BitmapDrawable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Profile implements Serializable {

    public String nick;
    public byte[] photo;
    public int rating;
    public String date_registration;
    public int wins;
    public int draws;
    public int loses;

    public Profile(String nick, byte[] photo, int rating, String date_registration, int wins,int draws , int loses) {
        this.nick = nick;
        this.photo = photo;
        this.rating = rating;
        this.date_registration = date_registration;
        this.wins = wins;
        this.loses = loses;
        this.draws = draws;
    }

    public static Profile make_new_profile(String nick){
        return new Profile(nick,null,1500,new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis())),0,0,0);
    }

    public static Profile make_profile_from_profile(Profile profile){
        return new Profile(profile.nick,profile.photo,profile.rating,profile.date_registration,profile.wins,profile.draws,profile.loses);
    }

    public static ArrayList<Byte> loginPlusPass(String login, char[] pass){
        ArrayList<Byte> pass_and_login = new ArrayList<>();
        for (byte b : (byte[]) Profile.encryption(pass))
            pass_and_login.add(b);
        for (byte b : login.getBytes())
            pass_and_login.add(b);
        return pass_and_login;
    }

    public static boolean sameBytes(char[] bytes1, char[] bytes2){
        if (bytes1.length != bytes2.length)
            return false;
        for (int i = 0; i < bytes1.length; i++)
            if (bytes1[i] != bytes2[i])
                return false;
        return true;
    }

    public static Object encryption(char[] bytePass){
        long seed = 0;
        try {
            for (int i = 0; i < 7; i++) {
                seed |= bytePass[i];
                seed <<= 8;
            }
            seed += bytePass[7];
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        byte[] encrypted_password = new byte[128];
        Random random = new Random(seed);

        for (int i = 0; i < 128; i++) {
            try {
                encrypted_password[i] = (byte) ((byte) random.nextLong() ^ bytePass[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                encrypted_password[i] = (byte) seed;
            }
        }
        return encrypted_password;
    }
}

class ConvertImage {

    public static byte[] convertToByte(BitmapDrawable image){
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(image);
            } catch (Exception e){
            }
            return b.toByteArray();
        } catch (Exception e){
        }
        return null;
    }

    public static BitmapDrawable convertToImageIcon(byte[] bytes){
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return (BitmapDrawable) o.readObject();
            } catch (Exception e){
            }
        } catch (Exception e){
        }
        return null;
    }
}