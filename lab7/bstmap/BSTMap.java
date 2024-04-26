package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left;
        private BSTNode right;

        private BSTNode(K k, V v, BSTNode left, BSTNode right) {
            key = k;
            value = v;
            this.left = left;
            this.right = right;
        }
    }

    private BSTNode root;
    private int size;

    public BSTMap() {
        clear();
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return (getHelper(key,root) != null);
    }

    @Override
    public V get(K key) {
        BSTNode node = getHelper(key, root);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    private BSTNode getHelper(K key, BSTNode node) {
        if (node == null) {
            return null;
        }
        int comp = key.compareTo(node.key);
        if (comp == 0) {
            return node;
        } else if (comp < 0) {
            return getHelper(key, node.left);
        } else {
            return getHelper(key, node.right);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = putHelper(key, value, root);
    }

    private BSTNode putHelper(K key, V value, BSTNode node) {
        if (node == null) {
            size += 1;
            return new BSTNode(key, value, null, null);
        }
        int comp = key.compareTo(node.key);
        if (comp < 0) {
            node.left = putHelper(key, value, node.left);
        } else if (comp > 0) {
            node.right = putHelper(key, value, node.right);
        } else {
            // override old value
            node.value = value;
        }
        return node;
    }

    public void printInOrder() {
        System.out.println("teehee :)");
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
