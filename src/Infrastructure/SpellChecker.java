/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Infrastructure;

/**
 * Class used to check the spelling of words
 */
public class SpellChecker {

    /**
     * Method that compares two strings and returns the Levenshtein distance bewteen them
     * @param a first string
     * @param b second string
     * @return distance bewteen them
     */
    public int levenshtein(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[] costs = new int[b.length() + 1];

        for (int j = 0; j < costs.length; j++)
            costs[j] = j;

        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;

            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(Math.min(costs[j], costs[j - 1]) + 1, a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}