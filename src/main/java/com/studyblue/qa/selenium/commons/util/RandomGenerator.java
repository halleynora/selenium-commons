package com.studyblue.qa.selenium.commons.util;

/**
 * Created by halleyshort on 12/10/15.
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomGenerator {

    /**
     * generate strings from a-z and number 0-9
     *
     * @param setSize
     * @param stringLength
     * @return
     */
    public static Set<String> generateSimpleStrings(int setSize, int stringLength, boolean ignoreCaps) {
        Set<String> strings = new HashSet<String>();
        String sourceString = "abcdefghijklmnopqrstuvwxyz0123456789";
        if (!ignoreCaps) {
            sourceString = sourceString.concat("ABCDEFGHIGKLMNOPQRSTUVWXYZ");
        }
        Random rand = new Random();
        while (strings.size() < setSize) {
            StringBuffer sbf = new StringBuffer();
            for (int i = 0; i < stringLength; i++) {
                sbf.append(sourceString.charAt(rand.nextInt(sourceString.length())));
            }
            strings.add(sbf.toString());
        }
        return strings;
    }

    /**
     * generate strings a-z,
     *
     * @param setSize
     * @param stringLength
     * @param ignoreCaps
     * @return
     */
    public static Set<String> generateAlphabetStrings(int setSize, int stringLength, boolean ignoreCaps) {
        Set<String> strings = new HashSet<String>();
        String sourceString = "abcdefghijklmnopqrstuvwxyz";
        if (!ignoreCaps) {
            sourceString = sourceString.concat("ABCDEFGHIGKLMNOPQRSTUVWXYZ");
        }
        Random rand = new Random();
        while (strings.size() < setSize) {
            StringBuffer sbf = new StringBuffer();
            for (int i = 0; i < stringLength; i++) {
                sbf.append(sourceString.charAt(rand.nextInt(sourceString.length())));
            }
            strings.add(sbf.toString());
        }
        return strings;
    }

    /**
     * generate a set of int
     *
     * @param setSize
     * @param from
     *            inclusive
     * @param to
     *            exclusive
     * @return
     */
    public static Set<Integer> generateInts(int setSize, int from, int to) {
        Set<Integer> intSet = new HashSet<Integer>();
        Random rand = new Random();
        int length = to - from;
        while (intSet.size() < setSize) {
            intSet.add(rand.nextInt(length)+from);
        }
        return intSet;
    }

    /**
     * generate a list of random duplicate int
     * @param listSize
     * @param from
     * @param to
     * @return
     */
    public static List<Integer> generateDuplicateInts(int listSize, int from, int to) {
        List<Integer> intList = new ArrayList<Integer>(listSize);
        Random rand = new Random();
        int length = to - from;
        for (int i = 0; i < listSize; i++) {
            intList.add(rand.nextInt(length)+from);
        }
        return intList;
    }

    public static void main(String[] args) {
        for (String string : generateAlphabetStrings(4, 4, false)) {
            System.out.println(string);
        }
    }
}

