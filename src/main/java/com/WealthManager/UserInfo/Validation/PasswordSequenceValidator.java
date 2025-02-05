package com.WealthManager.UserInfo.Validation;

import java.util.*;
import java.util.regex.Pattern;

public class PasswordSequenceValidator {
    // Regex pattern for basic complexity requirements
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public static boolean matchesPattern(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean noSequences(String password) {
        Map<Character, Set<Character>> sequenceMap = createSequenceMap();

        int sequenceCounter = 1;
        Character previousChar = null;

        for (char currentChar : password.toCharArray()) {
            if (previousChar != null && sequenceMap.getOrDefault(previousChar, Collections.emptySet()).contains(currentChar)) {
                sequenceCounter++;
                if (sequenceCounter >= 4) {
                    return false;
                }
            } else {
                sequenceCounter = 1;
            }
            previousChar = currentChar;
        }
        return true;
    }

    private static Map<Character, Set<Character>> createSequenceMap() {
        Map<Character, Set<Character>> map = new HashMap<>();
        addSequence(map, "0123456789");
        addSequence(map, "abcdefghijklmnopqrstuvwxyz");
        addSequence(map, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        addSequence(map, "qwertzuiop");
        addSequence(map, "asdfghjklöä");
        addSequence(map, "yxcvbnm");
        return map;
    }

    private static void addSequence(Map<Character, Set<Character>> map, String sequence) {
        for (int i = 0; i < sequence.length(); i++) {
            char currentChar = sequence.charAt(i);
            map.putIfAbsent(currentChar, new HashSet<>());

            if (i > 0) {
                map.get(currentChar).add(sequence.charAt(i - 1));
            }
            if (i < sequence.length() - 1) {
                map.get(currentChar).add(sequence.charAt(i + 1));
            }
        }
    }

}


