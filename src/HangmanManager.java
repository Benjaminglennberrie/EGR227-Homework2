import java.util.*;

/**
 *  Primary class for "Evil Hangman"

 */
public class HangmanManager {


    /* Contains all currently considered words */
    private Set<String> answerSpace;
    /* All guesses made by the player */
    private SortedSet<Character> guessedCharacters;
    /* The number of guesses remaining */
    private int guessesRemaining;
    /* The pattern that is to be displayed */
    private String pattern;

    /*
     * Constructor
     * Initializes the HangmanManager from dictionary with words of length length. Allows a maximum of max guesses.
     *
     * Throws IllegalArgument exceptions if length is less than one or if max is less than zero.
     *
     * Parameters: dictionary of words to use, length of words to select, number of allowed incorrect guesses
     */
    public HangmanManager(Collection<String> dictionary, int length, int max) {
        /* Exception checking */
        if(length < 1) {
            throw new IllegalArgumentException("Length must be 1 or more.");
        } else if (max < 0) {
            throw new IllegalArgumentException("Maximum incorrect guesses must be more than 0.");
        }


        /* Add all words of the correct length from dictionary */
        answerSpace = new TreeSet<>();
        for(String word:dictionary) {
            if (word.length() == length) {
                answerSpace.add(word);
            }
        }

        guessedCharacters = new TreeSet<>();
        guessesRemaining = max;

        /* Make a pattern of dashes */
        pattern = "-";
        for (int i = 1; i < length; i++) {
            pattern += " -";
        }
    }

    /* Returns a copy of answerSpace */
    public Set<String> words() {
        /* Use the copy constructor */
        return new TreeSet<>(answerSpace);
    }

    /* Returns the number of guesses remaining */
    public int guessesLeft() {
        return guessesRemaining;
    }

    /* Returns a copy of guessedCharacters */
    public SortedSet<Character> guesses() {
        /* Uses the copy constructor */
        return new TreeSet<>(guessedCharacters);
    }

    /* Returns the pattern to the user */
    public String pattern() {
        if (answerSpace.isEmpty())
            throw new IllegalStateException("answer space should not be empty");
        return pattern;
    }

    /*
     * Allows the user to guess a letter of the word and updates the state of HangmanManager accordingly.
     *
     * If there are no possible answers remaining, throws an IllegalStateException (no words were found).
     * If there are not enough guessedCharacters remaining, throws an IllegalStateException.
     * If the character has already been guessed, throws an IllegalArgumentException.
     *
     * Parameter: Letter being guessed
     * Returns: Number of occurrences  of that letter in the pattern
     */
    public int record(char guess) {
        /* Exception checking */
        if(answerSpace.size() == 0) {
            throw new IllegalStateException("The set of possible answers is empty.");
        } else if (guessesLeft() < 1) {
            throw new IllegalStateException("No guesses remaining.");
        } else if (guessedCharacters.contains(guess)) {
            throw new IllegalArgumentException(guess + " has already been guessed.");
        }

        /* Update fields */
        guessedCharacters.add(guess);    //Update guessedCharacters

        Map<String, Set<String>> answerMap = buildMap(guess); // Build the map of potential results.
        int numOccur = updateAnswerInfo(answerMap, guess); //update answerSpace and pattern, returns the num occurrences

        if (numOccur == 0) guessesRemaining--; //only if wrong guess then decrease guesses remaining
        return numOccur;
    }

    private String buildKey(String word, char guess) {
        StringBuilder sb = new StringBuilder(pattern); //initialize with current pattern
        for(int i = 0; i < word.length(); i++) {
            if(word.charAt(i) == guess) { //match
                sb.setCharAt(i * 2, guess); //change the char at index i * 2 to guess
            }
        }
        return sb.toString();
    }

    /*
     * Helper method for record()
     * Generates a map of index combinations to sets of word that match those index combinations.
     *
     * Parameters: character being guessed
     * Returns: map of index combinations to word sets
     */
    private Map<String, Set<String>> buildMap(char guess) {
        Map<String, Set<String>> answerMap = new TreeMap<>();
        for(String word:answerSpace) {
            String key = buildKey(word, guess);
            /* If the key does not exist, add it */
            Set<String> value;
            if(!answerMap.containsKey(key)) {
                value = new TreeSet<>();
            } else {  /* If the key does exist, update it */
                value = answerMap.get(key);
            }
            value.add(word);
            answerMap.put(key, value);
        }
        return answerMap;
    }

    /*
     * Helper method for record()
     *
     * Parameters: map to search, char for the current guess
     * post: updates answerSpace, pattern to show to user,
     * @return the number of occurrences of guess in the new pattern
     */
    private int updateAnswerInfo(Map<String, Set<String>> answerMap, char guess) {
        String bestKey = findBestKey(answerMap);
        answerSpace = answerMap.get(bestKey); //update answerSpace (new answerSpace are set of words mapped to best key)
        int numOccurrences = calcDiff(pattern, bestKey); //calculate # of occurrences of guess char in new pattern
        pattern = bestKey; //best key is now new pattern
        return numOccurrences;
    }

    /**
     * Static helper method for calculating the number of different characters
     * @param str1
     * @param str2
     * @return returns the number of different characters in str1 and str2
     *
     */
    private static int calcDiff(String str1, String str2){
        int numDiff = 0;
        for(int i = 0; i < str1.length(); i++){
            if(str1.charAt(i) != str2.charAt(i))
                numDiff++;
        }
        return numDiff;
    }

    /*
     * Helper method for updateAnswerInfo()
     * Returns the key of the largest Set in a map
     *
     * Parameters: map to search
     * Returns: key which refers to that set
     */
    private String findBestKey(Map<String, Set<String>> answerMap){
        /* Find the largest set of possible answers and store the key for that set in bestKey */
        Set<String> keySet = answerMap.keySet();
        String bestKey = keySet.iterator().next();
        for(String key:keySet) {
            if(answerMap.get(key).size() > answerMap.get(bestKey).size()) {
                bestKey = key;
            }
        }
        return bestKey;
    }
}