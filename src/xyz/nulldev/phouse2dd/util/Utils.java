package xyz.nulldev.phouse2dd.util;

/**
 * Project: Phouse2DD
 * Created: 14/12/15
 * Author: nulldev
 */
public class Utils {
    public static Byte[] box(byte[] array) {
        Byte[] newArray = new Byte[array.length];
        for(int i = 0; i < array.length; i++){
            newArray[i] = array[i];
        }
        return newArray;
    }
    public static byte[] unbox(Byte[] array) {
        byte[] newArray = new byte[array.length];
        for(int i = 0; i < array.length; i++){
            newArray[i] = array[i];
        }
        return newArray;
    }
}
