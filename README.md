

Autocomplete is a commonly used feature in mobile phones, text editors, and search engines. As a user types in letters, the system shows a list of word predictions to help the user complete the word they are typing. The core of an efficient autocompletion system is a fast algorithm for retrieving word predictions based on the user input. The word predictions are all the words (in a given dictionary) that start with what the user has typed so far (i.e., the list of words for which the user's input is a prefix).

This is a simple autocompletion system using a DLB trie which allows 
the user to add a new word to its dictionary when none of the predictions are selected by the user.

The `AutoCompleteInterface` defines a Java interface for a dictionary that provides word predictions for such an autocompletion system. Besides storing a set of words, the dictionary keeps track of a prefix String, which starts with the empty String. 
