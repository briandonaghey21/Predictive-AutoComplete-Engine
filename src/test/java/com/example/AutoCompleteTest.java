package com.example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AutoCompleteTest {
    private AutoComplete autoComplete;

    @BeforeEach
    public void setUp() {
        autoComplete = new AutoComplete();
    }

    //  adding a word
    @Test
    public void testAddWord() {
        assertTrue(autoComplete.add("hello"), "Should successfully add 'hello'");
        assertTrue(autoComplete.add("hi"), "Should successfully add 'hi'");
        assertFalse(autoComplete.add("hello"), "Adding duplicate word should return false");
    }

    //  checking if a word is in the trie
    @Test
    public void testContains() {
        autoComplete.add("test");
        assertTrue(autoComplete.contains("test"), "Trie should contain 'test'");
        assertFalse(autoComplete.contains("hello"), "Trie should not contain 'hello'");
    }

    // Test adding and retrieving frequency
    @Test
    public void testAddFrequency() {
        autoComplete.add("word");
        autoComplete.addFrequency("word", 5L);
        assertEquals(5L, autoComplete.find(autoComplete.root, "word", 0).freq, "Frequency of 'word' should be 5");
    }

    //  checking prefixes
    @Test
    public void testAdvance() {
        autoComplete.add("car");
        autoComplete.add("cat");
        autoComplete.add("cart");

        assertTrue(autoComplete.advance('c'), "Prefix 'c' should exist");
        assertTrue(autoComplete.advance('a'), "Prefix 'ca' should exist");
        assertTrue(autoComplete.advance('r'), "Prefix 'car' should exist");
        assertFalse(autoComplete.advance('x'), "'x' is not a valid next character");
    }

    //  retreat function
    @Test
    public void testRetreat() {
        autoComplete.add("cat");
        autoComplete.add("car");

        autoComplete.advance('c');
        autoComplete.advance('a');
        autoComplete.advance('t');
        autoComplete.retreat();
        
        assertFalse(autoComplete.isWord(), "After retreat, 'ca' should not be a word");
    }

    //  isWord() function
    @Test
    public void testIsWord() {
        autoComplete.add("apple");
        autoComplete.add("app");
        
        autoComplete.advance('a');
        autoComplete.advance('p');
        autoComplete.advance('p');
        
        assertTrue(autoComplete.isWord(), "'app' should be a word");
    }

    //  getNumberOfPredictions()
    @Test
    public void testGetNumberOfPredictions() {
        autoComplete.add("apple");
        autoComplete.add("application");
        autoComplete.add("appetizer");

        autoComplete.advance('a');
        autoComplete.advance('p');
        autoComplete.advance('p');

        assertEquals(3, autoComplete.getNumberOfPredictions(), "Prefix 'app' should have 3 predictions");
    }

    //  retrievePrediction()
    @Test
    public void testRetrievePrediction() {
        autoComplete.add("apple");
        autoComplete.add("application");
        autoComplete.add("appetizer");
        
        autoComplete.advance('a');
        autoComplete.advance('p');
        autoComplete.advance('p');

        String prediction = autoComplete.retrievePrediction();
        assertNotNull(prediction, "Prediction should not be null");
        assertTrue(prediction.startsWith("app"), "Prediction should start with 'app'");
    }
}
