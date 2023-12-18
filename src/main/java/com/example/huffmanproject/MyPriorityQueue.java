package com.example.huffmanproject;

public class MyPriorityQueue {
    private Node[] heap;
    private int size;
    private int capacity;

    public MyPriorityQueue(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.heap = new Node[capacity];
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void add(Node newNode) {
        if (size == capacity) {
            throw new IllegalStateException("Priority queue is full");
        }

        heap[size] = newNode;
        size++;

        // Restore heap property by heapifying up
        heapifyUp(size - 1);
    }

    public Node remove() {
        if (isEmpty()) {
            throw new IllegalStateException("Priority queue is empty");
        }

        Node removedNode = heap[0];

        // Replace the root with the last node and heapify down
        heap[0] = heap[size - 1];
        size--;

        heapifyDown(0);

        return removedNode;
    }

    private void heapifyUp(int index) {
        int parentIndex = (index - 1) / 2;

        while (index > 0 && heap[index].compareTo(heap[parentIndex]) < 0) {
            swap(index, parentIndex);
            index = parentIndex;
            parentIndex = (index - 1) / 2;
        }
    }

    private void heapifyDown(int index) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int smallest = index;

        if (leftChild < size && heap[leftChild].compareTo(heap[smallest]) <0) {
            smallest = leftChild;
        }

        if (rightChild < size && heap[rightChild].compareTo(heap[smallest]) <= 0) {
            smallest = rightChild;
        }

        if (smallest != index) {
            swap(index, smallest);
            heapifyDown(smallest);
        }
    }


    private void swap(int i, int j) {
        Node temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    public void printHeap() {
        for (int i = 0; i < size; i++) {
            System.out.print(heap[i].getFreqs()+ " ");
        }
        System.out.println();
    }
    public boolean isEmpty() {
        return size == 0;
    }

    public Node peek() {
        return isEmpty() ? null : heap[0];
    }


}

