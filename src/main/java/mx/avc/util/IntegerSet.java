/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.util;

import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * @author alexv
 */
public class IntegerSet implements Set<Integer> {

    private BitSet bitSet;

    public int size() {
        return bitSet.cardinality();
    }

    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    public boolean contains(Object that) {
        if(that == null || !(that instanceof Integer)) {
            return false;
        }
        int bit_index = (Integer)that;
        return bit_index >= 0 && bitSet.get(bit_index);
    }

    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {

            private int lastBitIndex = -1;

            private int nextBitIndex = bitSet.nextSetBit(0);

            public boolean hasNext() {
                return nextBitIndex >= 0;
            }

            public Integer next() {
                lastBitIndex = nextBitIndex;

                if(lastBitIndex < 0) {
                    throw new NoSuchElementException();
                }

                nextBitIndex = bitSet.nextSetBit(lastBitIndex + 1);

                return lastBitIndex;
            }

            public void remove() {
                if(lastBitIndex < 0) {
                    throw new IllegalStateException();
                }

                bitSet.clear(lastBitIndex);
            }

        };
    }

    public Object[] toArray() {
        return toArray(new Object[bitSet.cardinality()]);
    }

    public <T> T[] toArray(T[] array) {
        int cardinality = bitSet.cardinality();
        if(array.length < cardinality) {
            Class<?> clazz = array.getClass().getComponentType();
            array = (T[])Array.newInstance(clazz, cardinality);
        }
        int array_index = 0;
        for(int bit_index = bitSet.nextSetBit(0); bit_index >= 0;
                bit_index = bitSet.nextSetBit(bit_index + 1)) {
            Array.setInt(array, array_index++, bit_index);
        }
        return array;
    }

    public boolean add(Integer that) {
        int bit_index = that;
        if(bit_index < 0) {
            throw new IllegalArgumentException();
        }

        boolean cleared = !bitSet.get(bit_index);
        if(cleared) {
            bitSet.set(bit_index);
        }
        return cleared;
    }

    public boolean remove(Object that) {
        if(that == null || !(that instanceof Integer)) {
            return false;
        }
        int bit_index = (Integer)that;
        boolean set = bit_index >= 0 && bitSet.get(bit_index);
        if(set) {
            bitSet.clear(bit_index);
        }
        return set;
    }

    public boolean containsAll(Collection<?> those) {
        if(those.isEmpty()) {
            return true;

        } else if(those instanceof IntegerSet) {
            IntegerSet that_set = (IntegerSet)those;

            BitSet intersection = (BitSet)that_set.bitSet.clone();
            intersection.and(bitSet);

            return that_set.bitSet.equals(intersection);

        } else {
            for(Object that : those) {
                if(that != null && that instanceof Integer) {
                    int bit_index = (Integer)that;
                    if(bit_index >= 0 && bitSet.get(bit_index)) {
                        continue;
                    }
                }
                return false;
            }

            return true;
        }
    }

    public boolean addAll(Collection<? extends Integer> those) {
        if(those instanceof IntegerSet) {
            IntegerSet that_set = (IntegerSet)those;

            BitSet original = (BitSet)bitSet.clone();
            bitSet.or(that_set.bitSet);

            return !bitSet.equals(original);

        } else {
            boolean added = false;
            for(Integer that : those) {
                added |= add(that);
            }

            return added;
        }
    }

    public boolean retainAll(Collection<?> those) {
        if(those.isEmpty()) {
            boolean not_empty = !bitSet.isEmpty();
            if(not_empty) {
                bitSet.clear();
            }
            return not_empty;

        } else if(those instanceof IntegerSet) {
            IntegerSet that_set = (IntegerSet)those;

            BitSet original = (BitSet)bitSet.clone();
            bitSet.and(that_set.bitSet);

            return !bitSet.equals(original);

        } else {
            boolean removed = false;
            for(int bit_index = bitSet.nextSetBit(0); bit_index >= 0;
                    bit_index = bitSet.nextSetBit(bit_index + 1)) {
                if(!those.contains(bit_index)) {
                    bitSet.clear(bit_index);
                    removed = true;
                }
            }

            return removed;
        }
    }

    public boolean removeAll(Collection<?> those) {
        if(those instanceof IntegerSet) {
            IntegerSet that_set = (IntegerSet)those;
            boolean changed = bitSet.intersects(that_set.bitSet);
            if(changed) {
                bitSet.andNot(that_set.bitSet);
            }
            return changed;
        } else {
            boolean removed = false;
            for(Object that : those) {
                if(that != null && that instanceof Integer) {
                    int bit_index = (Integer)that;
                    if(bit_index >= 0 && bitSet.get(bit_index)) {
                        bitSet.clear(bit_index);
                        removed = true;
                    }
                }
            }
            return removed;
        }
    }

    public void clear() {
        bitSet.clear();
    }

}
