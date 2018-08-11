package util;

import java.text.Normalizer;

public class Utl {
    /**
     * @author David Conrad
     * https://stackoverflow.com/questions/3322152/is-there-a-way-to-get-rid-of-accents-and-convert-a-whole-string-to-regular-lette
     *
     * @param str
     * @return
     */
    public static String flattenToASCII(String str) {
        char[] out = new char[str.length()];
        str = Normalizer.normalize(str, Normalizer.Form.NFD);

        int j = 0;
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c <= '\u007F') {
                out[j++] = c;
            }
        }

        return new String(out).toLowerCase();
    }

    public static float round(float value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (float) Math.round(value * scale) / scale;
    }
}
