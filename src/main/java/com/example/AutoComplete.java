package com.example;


/*
 *  By Brian Donaghey
 *  BJD77
 * 
 * An implementation of the AutoCompleteInterface using a DLB Trie.
 */
 public class AutoComplete 
{

  public DLBNode root;
  public StringBuilder currentPrefix;
  public DLBNode currentNode;
  public DLBNode previousNode;
  public DLBNode firstNode;
  boolean flag = false; boolean nonprefix = false;  boolean addcall = false;
  int nonprefixcnt = 0;

  public AutoComplete()
  {
    root = null;
    currentPrefix = new StringBuilder();
    currentNode = null;
  }

    public boolean add(String word)
    {
    if (contains(word)) // checks if word is already in the tree
      return false; // word is there, return false
      root = addHelper(root, word, 0); // add the word, with the pointer starting at the root node
      currentNode = root; // move currNode to root to reset the pointer
      return true;
    }
    
    public DLBNode addHelper(DLBNode node, String word, int pos)
    {
      if (word.equals("")) 
        throw new IllegalArgumentException("cant put a null word in there"); // null "" case, can't add an empty string
      DLBNode currentNode = node; //set currNode to
      if (node == null) //called every single time
      {
        currentNode = new DLBNode(word.charAt(pos)); //creates a new node
        currentNode.size += 1; // adding to its size
        if (pos < word.length()-1)
        {
          currentNode.child = addHelper(currentNode.child, word, pos+1); //recursive call, with the new child and position
          currentNode.child.parent = currentNode;
        }
      }
      else if (node.data == word.charAt(pos))
      {
        currentNode.size += 1; // adding to its size
        if (pos < word.length()-1)
        {
          currentNode.child = addHelper(currentNode.child, word, pos+1); ////recursive call, with the new child and position
          currentNode.child.parent = currentNode;
        }
      }
      else
        currentNode.nextSibling = addHelper(currentNode.nextSibling, word, pos); // recursive call with the new sibling
        if(currentNode.nextSibling !=null)
          currentNode.nextSibling.previousSibling = currentNode;
        if (pos == word.length()-1) // checks if its a word by looking at position and the length of the word
          currentNode.isWord = true;
      return currentNode;  //returns the root
    }
    
    public void addFrequency(String word , Long Frequency)
    {
      DLBNode node = find(root,word,0);
      if (node == null)
      {
        add(word);
        node = find(root,word,0); // costly but oh well, we want more words!
      }
       // System.out.println("Frequency of " + word + ": " + Frequency);
        node.freq = Frequency;
    }

    public boolean contains(String word) //returns true if the word is the tree, returns false if not
    {
      DLBNode node = find(root,word,0);
      if (node == null) 
        return false; //not in the tree
      else
       {
        if (node.isWord) // node is a word in the tree
          return true; 
        else
          return false; // node is not a WORD in the tree
       }
    }

    public DLBNode find(DLBNode node, String word, int pos)
    {
      if (word.equals("")) 
        throw new IllegalArgumentException("cant put a null word in there"); // for null cases

      DLBNode currentNode = null; //initialize curr Node
      if (node != null)
      {
        if (node.data == word.charAt(pos))
        {
          if(pos == word.length()-1)
            currentNode = node;
          else
            currentNode = find(node.child, word, pos + 1);
        }
        else
          currentNode = find(node.nextSibling, word, pos);
      }
      return currentNode;
    }
 
    public boolean advance(char c)
    {
      addcall = false; //boolean reset
      currentPrefix.append(c); //append to current prefix
      firstNode = currentNode; //first node of a sibling list
      if (currentNode == null) // null case
      {
        nonprefixcnt++; // counter for how many times we DONT backtrack a node
        return false;
      }
      if (currentPrefix.length() != 1 && currentNode.child != null)
        currentNode = currentNode.child;
      if (advanceHelper(currentNode, c, 0)) //call advance helper, returns true if its a prefic to a word.
      {
        if (flag) // boolean reset
          flag = false;
        if (!nonprefix)
        {
          nonprefix = false; //boolean reset
          return true; //return true, it is a prefix
        }
      }
      else
      {
        currentNode = firstNode; //go back to first node
        nonprefix = true; // boolean flag for nonprefix used in retreat
        previousNode = currentNode; // store a previous node
        currentNode = null; //set current node to null
        nonprefixcnt++;
        return false; // return false, it isnt a prefix
      }
      if (flag) // if the word is a prefix. my recursion would end up after the else statement, so this counters that error
      {
        nonprefix = false; // boolean reset
        return true; // the word is a prefix
      }
      return false; 
    }

    public boolean advanceHelper(DLBNode node, char c, int depth)
    {
      currentNode = node; // initialize currnode
      if(currentNode == null)
      {
        currentNode = firstNode; //go back to the first node of the subtree
        return false; // not a prefix
      }
      if (currentNode.data == c)
      {
        flag = true; //boolean for a prefix
        nonprefix = false; // its a prefix so this is false
        return true; // return true for prefix
      }
      else
        advanceHelper(currentNode.nextSibling, c, depth +1); //not a prefix, check the next node

        if (flag) // flag for recursion error
          return true;
    return false;
    }

    public void retreat()
    {
      if (currentPrefix.length() == 0) // throw an error
        throw new IllegalStateException("cant delete a null word");
      currentPrefix.deleteCharAt(currentPrefix.length()-1); //delete character at the end
      if (currentNode == null) // nonprefix case
      {
        // delete letter at end of prefix
        if(nonprefixcnt <= 1)  // how many times we DONT recurse back to a node. Needed when we retreat multiple times and its a non prefix.
        {
           currentNode = previousNode; // go back to the previous node
           if (currentNode.size >= 1) // has a word
            nonprefix = false; // non prefix = false
        }
        nonprefixcnt--; //counter
       return;
      }
      if (!nonprefix) //if its a prefix
        {
          while (currentNode.previousSibling != null)
            currentNode = currentNode.previousSibling; // go to the first sibling in the node tree
          if (currentNode.parent != null)
            currentNode = currentNode.parent; // go to the previous node
        }
       if(currentPrefix.length() == 0)
        currentNode = root; 
    }

    public void reset()
    {
      currentPrefix = new StringBuilder(""); // reset stringbuilder to ""
      currentNode = root; //set currNode to root
    }
    
    public boolean isWord()
    {
      if (addcall) //boolean for multiple adds in a row, throws error otherwise
      {
        addcall = false;
        return true;
      }
      if (currentNode == null) //not a word
        return false;
      return currentNode.isWord;
    }

    public void add()
    {
      addcall = true; 
      add(currentPrefix.toString()); // uses add function from earlier
    }

    public int getNumberOfPredictions(){
     
      if(currentNode == null || currentPrefix.length() == 0)
        return 0;
      return currentNode.size;
    }
  
    public String retrievePrediction()
    {
      if(currentNode ==null)
        return null; // return no predictions
      DLBNode temp =currentNode; //create temp node for iterating through the tree
      StringBuilder prediction = new StringBuilder(currentPrefix.toString()); //append current prefix to stringbuilder
      while(temp.child != null) //go down the tree and look for a word
      {
        temp = temp.child;
        temp = maxFrequency(temp);
        prediction.append(temp.data);
        if (temp.isWord) 
          return prediction.toString();
      }
      return prediction.toString();
    }
    public DLBNode maxFrequency(DLBNode node)
    {
      DLBNode max = node;
      while (node.nextSibling != null)
      {
        if(node.freq > max.freq)
          max = node;
          node = node.nextSibling;
      }
      return max;
    }


//debugbing mehtods

  //print the subtrie rooted at the node at the end of the start String
  public void printTrie(String start){
    System.out.println("==================== START: DLB Trie Starting from \""+ start + "\" ====================");
    if(start.equals("")){
      printTrie(root, 0);
    } else {
      DLBNode startNode = getNode(root, start, 0);
      if(startNode != null){
        printTrie(startNode.child, 0);
      }
    }
    
    System.out.println("==================== END: DLB Trie Starting from \""+ start + "\" ====================");
  }

  //a helper method for printTrie
  public void printTrie(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
      System.out.println(" (" + node.size + ")");
      printTrie(node.child, depth+1);
      printTrie(node.nextSibling, depth);
    }
  }

  //return a pointer to the node at the end of the start String 
  //in O(start.length() - index)
  public DLBNode getNode(DLBNode node, String start, int index){
    if(start.length() == 0){
      return node;
    }
    DLBNode result = node;
    if(node != null){
      if((index < start.length()-1) && (node.data == start.charAt(index))) {
          result = getNode(node.child, start, index+1);
      } else if((index == start.length()-1) && (node.data == start.charAt(index))) {
          result = node;
      } else {
          result = getNode(node.nextSibling, start, index);
      }
    }
    return result;
  } 
// end AutoComplete

  //The DLB node class
  public class DLBNode
  {
    public char data;
    public int size;
    public boolean isWord;
    public DLBNode nextSibling;
    public DLBNode previousSibling;
    public DLBNode child;
    public DLBNode parent;
    public Long freq;

    public DLBNode(char data)
    {
        this.data = data;
        size = 0;
        isWord = false;
        nextSibling = previousSibling = child = parent = null;
        freq = 1l;
    }
  }
}
