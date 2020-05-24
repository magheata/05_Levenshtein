/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Infrastructure;

/**
 * Class used to define the Soundex words
 */
public class Soundex {

    /**
     * Method used to convert a word to its soundex equivalent
     * @param s string to convert
     * @return soundex equivalent
     */
    public static String soundex(String s) {
        char[] x = s.toUpperCase().toCharArray();
        // retain first letter
        String output = x[0] + "";

        // replace consonants with digits
        for (int i = 0; i < x.length; i++) {
            switch (x[i]) {
                case 'B':
                case 'F':
                case 'P':
                case 'V':
                    x[i] = '1';
                    break;

                case 'C':
                case 'G':
                case 'J':
                case 'K':
                case 'Q':
                case 'S':
                case 'X':
                case 'Z':
                    x[i] = '2';
                    break;

                case 'D':
                case 'T':
                    x[i] = '3';
                    break;

                case 'L':
                    x[i] = '4';
                    break;

                case 'M':
                case 'N':
                    x[i] = '5';
                    break;

                case 'R':
                    x[i] = '6';
                    break;

                default:
                    x[i] = '0';
                    break;
            }
        }

        // remove duplicates
        for (int i = 1; i < x.length; i++)
            if (x[i] != x[i - 1] && x[i] != '0')
                output += x[i];

        // right pad with zeros or truncate
        output = output + "0000";
        return output.substring(0, 4);
    }
}

