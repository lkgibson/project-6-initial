package com.example.aggregator.service;

import com.example.aggregator.client.AggregatorRestClient;
import com.example.aggregator.model.Entry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AggregatorService {

    private AggregatorRestClient aggregatorRestClient;

    public AggregatorService(AggregatorRestClient aggregatorRestClient) {
        this.aggregatorRestClient = aggregatorRestClient;
    }

    public Entry getDefinitionFor(String word) {
        return aggregatorRestClient.getDefinitionFor(word);
    }

    public List<Entry> getWordsStartingWith(String chars) {
        return aggregatorRestClient.getWordsStartingWith(chars);
    }

    public List<Entry> getWordsThatContain(String chars) {
        return aggregatorRestClient.getWordsThatContain(chars);
    }

    public List<Entry> getWordsThatContainSuccessiveLettersAndStartsWith(String chars) {

        List<Entry> wordsThatStartWith = aggregatorRestClient.getWordsStartingWith(chars);
        List<Entry> wordsThatContainSuccessiveLetters = aggregatorRestClient.getWordsThatContainConsecutiveLetters();

        List<Entry> common = new ArrayList<>(wordsThatStartWith);
        common.retainAll(wordsThatContainSuccessiveLetters);

        return common;
    }

    public List<Entry> getWordsThatContainSpecificConsecutiveLetters(String chars) {

        List<Entry> wordsThatContainSuccessiveLetters = aggregatorRestClient.getWordsThatContainConsecutiveLetters();

        List<Entry> common = new ArrayList<>(wordsThatContainSuccessiveLetters);
        common.removeIf(entry -> !entry.getWord().contains(chars));

        return common;
    }

    public List<Entry> getAllPalindromes() {
        final List<Entry> candidates = new ArrayList<>();

        IntStream.range('a', '{')
                .mapToObj(i -> Character.toString(i))
                .forEach(c -> {
                    List<Entry> startsWith = aggregatorRestClient.getWordsStartingWith(c);
                    List<Entry> endsWith = aggregatorRestClient.getWordsEndingWith(c);
                    List<Entry> startsAndEndsWith = new ArrayList<>(startsWith);
                    startsAndEndsWith.retainAll(endsWith);
                    candidates.addAll(startsAndEndsWith);
                });

        return candidates.stream()
                .filter(entry -> {
                    String word = entry.getWord();
                    String reverse = new StringBuilder(word).reverse().toString();
                    return word.equals(reverse);
                })
                .sorted()
                .collect(Collectors.toList());
    }

    // Extra Credit: Rewrite getAllPalindromes without using streams
    public List<Entry> getAllPalindromesWithoutStreams() {
        // Create a list to hold candidate entries
        final List<Entry> candidates = new ArrayList<>();

        // Iterate from 'a' to 'z'
        for (char c = 'a'; c <= 'z'; c++) {
            String character = Character.toString(c);

            // Get words starting with the current character
            List<Entry> startsWith = aggregatorRestClient.getWordsStartingWith(character);

            // Get words ending with the current character
            List<Entry> endsWith = aggregatorRestClient.getWordsEndingWith(character);

            // Find common entries that start and end with the current character
            List<Entry> startsAndEndsWith = new ArrayList<>(startsWith);
            startsAndEndsWith.retainAll(endsWith);

            // Add the common entries to the candidates list
            candidates.addAll(startsAndEndsWith);
        }

        // Create a list to hold palindrome entries
        List<Entry> palindromes = new ArrayList<>();

        // Check each candidate entry to see if it is a palindrome
        for (Entry entry : candidates) {
            String word = entry.getWord();
            String reverse = new StringBuilder(word).reverse().toString();
            if (word.equals(reverse)) {
                palindromes.add(entry);
            }
        }

        // Sort the list of palindromes
        palindromes.sort(Entry::compareTo);

        // Return the sorted list of palindromes
        return palindromes;
    }

}
