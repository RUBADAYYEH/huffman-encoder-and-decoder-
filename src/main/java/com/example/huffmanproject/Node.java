package com.example.huffmanproject;


public class Node implements Comparable {
    private char value;
    private int freqs;
    private Node left;
    private Node right;

    public Node() {

    }
    public Node(char value) {
    this.value=value;
    }

    public char getValue() {
        return value;
    }


    public void setValue(char value) {
        this.value = value;
    }

    public Node(char value, int freqs) {
        this.value = value;
        this.freqs = freqs;
    }

    public Node(Node left, Node right) {
        this.left = left;
        this.right = right;
    }

    public int getFreqs() {
        return freqs;
    }

    public void setFreqs(int freqs) {
        this.freqs = freqs;
    }


    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    @Override
    public int compareTo(Object o) {

        if (this.freqs>((Node)o).getFreqs()){
            return 1;
        }
        else if (this.freqs<((Node)o).getFreqs()){
            return -1;
        }
        else return 0;
    }
    public String getPaths() {
        return getPathsHelper(this, new StringBuilder());
    }

    private String getPathsHelper(Node node, StringBuilder path) {
        StringBuilder result = new StringBuilder();

        if (node != null) {
            // Leaf node, append the path
            if (node.getLeft() == null && node.getRight() == null) {
                result.append(" newRecord ").append("Value:").append(node.getValue()).append(":Path:").append(path);
               // result.append(System.lineSeparator()); // Add a newline for better readability
            }

            // Recursive call for left and right children
            path.append('0');
            result.append(getPathsHelper(node.getLeft(), new StringBuilder(path)));
            path.setLength(path.length() - 1); // Backtrack by removing the last character

            path.append('1');
            result.append(getPathsHelper(node.getRight(), new StringBuilder(path)));
            path.setLength(path.length() - 1); // Backtrack by removing the last character
        }

        return result.toString();
    }
    public StringBuilder postOrderTraversal() {
        StringBuilder result = new StringBuilder();

        // Perform post-order traversal
        postOrderTraversalHelper(this, result);

        // Add a marker '0' to indicate the end of the Huffman coding tree
        result.append('0');

        return result;
    }

    private void postOrderTraversalHelper(Node node, StringBuilder result) {
        if (node != null) {
            // Traverse left and right subtrees
            postOrderTraversalHelper(node.getLeft(), result);
            postOrderTraversalHelper(node.getRight(), result);

            // Append a '1' followed by the ASCII character for leaf nodes
            if (node.getLeft() == null && node.getRight() == null) {
                result.append('1').append(node.getValue());
            } else {
                // Append '0' for non-leaf nodes
                result.append('0');
            }
        }
    }


}
