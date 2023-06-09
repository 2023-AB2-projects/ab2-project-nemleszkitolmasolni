package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.KeyNotFoundException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.recordHandling.TypeConverter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.ArrayList;

@Slf4j
public class TreeNode {
    @Getter
    private boolean isLeaf;
    @Getter
    private ArrayList<Key> keys;
    @Getter
    private ArrayList<Integer> pointers;
    @Getter
    private final ArrayList<String> keyStructure;
    private final int keySize;
    private final int nodeSize;

    public int keyCount(){
        return keys.size();
    }

    //empty node
    public TreeNode(boolean isLeaf, ArrayList<String> keyStructure){
        this.isLeaf = isLeaf;
        this.keyStructure = keyStructure;

        keySize = (int) TypeConverter.sizeofStructure(keyStructure);

        nodeSize = 1 + Integer.BYTES + (2 * Constants.D) * keySize + (2 * Constants.D + 1) * Integer.BYTES;
        keys = new ArrayList<>();
        pointers = new ArrayList<>();
        pointers.add(Constants.nullPointer);
    }

    public TreeNode(boolean isLeaf, ArrayList<Key> keys, ArrayList<Integer> pointers, ArrayList<String> keyStructure) {
        this.isLeaf = isLeaf;
        this.keys = keys;
        this.pointers = pointers;
        this.keyStructure = keyStructure;

        keySize = (int) TypeConverter.sizeofStructure(keyStructure);
        nodeSize = 1 + Integer.BYTES + (2 * Constants.D) * keySize + (2 * Constants.D + 1) * Integer.BYTES;
    }

    public TreeNode(byte[] bytes, ArrayList<String> keyStructure){
        keys = new ArrayList<>();
        pointers = new ArrayList<>();
        this.keyStructure = keyStructure;
        keySize = (int) TypeConverter.sizeofStructure(keyStructure);
        nodeSize = 1 + Integer.BYTES + (2 * Constants.D) * keySize + (2 * Constants.D + 1) * Integer.BYTES;

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        isLeaf = buffer.get() == 1;
        int keyCount = buffer.getInt();
        pointers.add(buffer.getInt());

        for (int i = 0; i < keyCount; i++){
            byte[] keyBytes = new byte[keySize];
            buffer.get(keyBytes);
            keys.add(new Key(keyBytes, keyStructure));
            pointers.add(buffer.getInt());
        }

    }

    public static TreeNode createDeletedNode(int pointer, ArrayList<String> keyStructure){
        ArrayList<Key> emptyKeys = new ArrayList<>();
        ArrayList<Integer> pointerToNextDeletedNode = new ArrayList<>();
        pointerToNextDeletedNode.add(pointer);
        return new TreeNode(false, emptyKeys, pointerToNextDeletedNode, keyStructure);
    }

    public byte[] toBytes(){
        ByteBuffer buffer = ByteBuffer.allocate(nodeSize);

        if(isLeaf){
            buffer.put((byte) 1);
        }else{
            buffer.put((byte) 0);
        }

        buffer.putInt(keyCount());
        for(int i = 0; i < keyCount(); i++){
            buffer.putInt(pointers.get(i));
            buffer.put(keys.get(i).toBytes());
        }

        buffer.putInt(pointers.get(keyCount()));

        return buffer.array();
    }

    public int findNextNode(Key key){
        for(int i = 0; i < keyCount(); i++){
            if(key.compareTo(keys.get(i)) < 0){
                return pointers.get(i);
            }
        }
        return pointers.get(keyCount());
    }

    public void insertInLeaf(Key key, int pointer){
        ArrayList<Key> newKeys = new ArrayList<>();
        ArrayList<Integer> newPointers = new ArrayList<>();

        int i = 0;
        while(i < keyCount() && key.compareTo(keys.get(i)) > 0){
            newKeys.add(keys.get(i));
            newPointers.add(pointers.get(i));
            i++;
        }
        newKeys.add(key);
        newPointers.add(pointer);
        while(i < keyCount()){
            newKeys.add(keys.get(i));
            newPointers.add(pointers.get(i));
            i++;
        }
        newPointers.add(pointers.get(keyCount()));

        keys = newKeys;
        pointers = newPointers;
    }

    public void insertInNode(Key key, int pointer){
        ArrayList<Key> newKeys = new ArrayList<>();
        ArrayList<Integer> newPointers = new ArrayList<>();

        int i = 0;
        newPointers.add(pointers.get(0));
        while(i < keyCount() && key.compareTo(keys.get(i)) > 0){
            newKeys.add(keys.get(i));
            i++;
            newPointers.add(pointers.get(i));
        }
        newKeys.add(key);
        newPointers.add(pointer);
        while(i < keyCount()){
            newKeys.add(keys.get(i));
            i++;
            newPointers.add(pointers.get(i));
        }
        //newPointers.add(pointers.get(keyCount()));

        keys = newKeys;
        pointers = newPointers;
    }

    public TreeNode splitLeaf(int splitLocation){
        ArrayList<Key> leftKeys = new ArrayList<>(), rightKeys = new ArrayList<>();
        ArrayList<Integer> leftPointers = new ArrayList<>(), rightPointers = new ArrayList<>();

        for(int i = 0; i < Constants.D; i++){
            leftKeys.add(keys.get(i));
            leftPointers.add(pointers.get(i));
            rightKeys.add(keys.get(Constants.D + i));
            rightPointers.add(pointers.get(Constants.D + i));
        }
        leftPointers.add(splitLocation);
        rightPointers.add(pointers.get(keyCount()));


        TreeNode node = new TreeNode(isLeaf, rightKeys, rightPointers, keyStructure);
        keys = leftKeys;
        pointers = leftPointers;
        return node;
    }

    public TreeNode splitNode(){
        ArrayList<Key> leftKeys = new ArrayList<>(), rightKeys = new ArrayList<>();
        ArrayList<Integer> leftPointers = new ArrayList<>(), rightPointers = new ArrayList<>();

        for(int i = 0; i < Constants.D; i++){
            leftKeys.add(keys.get(i));
            leftPointers.add(pointers.get(i));
            rightKeys.add(keys.get(Constants.D + i + 1));
            rightPointers.add(pointers.get(Constants.D + i + 1));
        }
        leftPointers.add(pointers.get(Constants.D));
        rightPointers.add(pointers.get(keyCount()));


        TreeNode node = new TreeNode(isLeaf, rightKeys, rightPointers, keyStructure);
        keys = leftKeys;
        pointers = leftPointers;
        return node;
    }

    public boolean isAlmostFull(){
        return keyCount() == Constants.D * 2 - 1;
    }

    public boolean isFull() {
        return keyCount() == Constants.D * 2;
    }

    public boolean isTooSmall(){
        return keyCount() < Constants.D;
    }

    public void removeKeyAndPointer(Key key, Integer pointer) throws KeyNotFoundException {
        if(!keys.contains(key) || !pointers.contains(pointer)){
            throw new KeyNotFoundException();
        }
        keys.remove(key);
        pointers.remove(pointer);
    }

    public void replaceKey(Key oldKey, Key newKey){
        keys.set(keys.indexOf(oldKey), newKey);
    }

    public Integer getValueOfKey(Key key) throws KeyNotFoundException {
        int i = keys.indexOf(key);
        if(i == -1){
            throw new KeyNotFoundException();
        }
        return pointers.get(i);
    }
    public Key getKeyBetween(int pointer1, int pointer2){
        int i1 = pointers.indexOf(pointer1), i2 = pointers.indexOf(pointer2);
        return keys.get(Integer.min(i1, i2));
    }

    public Key getSmallestKey(){
        return keys.get(0);
    }

    public Key getMiddleKey() {return keys.get(Constants.D);}

    public Integer getLeftSiblingPointer(int childPointer){
        if(isLeaf){
            return null;
        }
        int i = pointers.indexOf(childPointer);
        if(i <= 0){
            return null;
        }
        return pointers.get(i - 1);
    }

    public Integer getRightSiblingPointer(int childPointer){
        if(isLeaf){
            return null;
        }
        int i = pointers.indexOf(childPointer);
        if(i == keyCount()){
            return null;
        }
        return pointers.get(i + 1);
    }

    public void joinLeaves(TreeNode sibling){
        if(!isLeaf || !sibling.isLeaf()){
            log.warn("Invalid join method called");
            return;
        }
        pointers.remove(keyCount());
        pointers.addAll(sibling.getPointers());
        keys.addAll(sibling.getKeys());

    }

    public void join(TreeNode sibling, Key key){
        keys.add(key);
        keys.addAll(sibling.getKeys());
        pointers.addAll(sibling.getPointers());

    }

    public Integer getFirstPointer(){
        return pointers.get(0);
    }

    public Key popBackKey(){
        return keys.remove(keyCount() - 1);
    }

    public Key popFrontKey() {
        return keys.remove(0);
    }

    public Integer popFrontPointer(){
        return pointers.remove(0);
    }

    public Integer popBackPointerFromNode() {return  pointers.remove(pointers.size() - 1);}

    public Integer popSecondToLastPointer(){
        return pointers.remove(pointers.size() - 2);
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "isLeaf=" + isLeaf +
                ", keyCount=" + keyCount() +
                ", keys=" + keys +
                ", pointers=" + pointers +
                '}';
    }
}
