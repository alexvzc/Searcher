/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.util;

import java.security.SecureRandom;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author alexv
 */
public class IntegerSetTest {

    public static final float BIT_THRESHOLD = 0.4f;

    private static final int BIT_LENGTH = 256;

    private Random randomFeed;

    private int[] testVector1;

    private int[] testVector2;

    private BitSet generateRandomBitSet(int bitlength, float bitthreshold) {
        BitSet ret = new BitSet(bitlength);

        for(int i = 0; i < bitlength; i++) {
            if(randomFeed.nextFloat() < bitthreshold) {
                ret.set(i);
            }
        }

        return ret;
    }

    private int[] bitsetToIntArray(BitSet bitset) {
        int[] array = new int[bitset.size()];

        int i = 0;
        for(int bit = bitset.nextSetBit(0); bit >= 0;
                bit = bitset.nextSetBit(bit + 1)) {
            array[i++] = bit;
        }

        return array;
    }
    
    private Set<Integer> addTestVector(Set<Integer> set, int[] test_vector) {
        for(int elem : test_vector) {
            set.add(elem);
        }
        return set;
    }

    private void generateTestVectors() {
        BitSet forcedInterception =
                generateRandomBitSet(BIT_LENGTH, BIT_THRESHOLD * 0.3f);
        
        BitSet forcedDisyunction =
                generateRandomBitSet(BIT_LENGTH, BIT_THRESHOLD * 0.4f);
        forcedDisyunction.andNot(forcedInterception);

        BitSet disyunctionDistribution =
                generateRandomBitSet(BIT_LENGTH, 0.5f);

        BitSet forcedDisyunctionDistributed1 = (BitSet)forcedDisyunction.clone();
        forcedDisyunctionDistributed1.and(disyunctionDistribution);
        
        BitSet forcedDisyunctionDistributed2 = (BitSet)forcedDisyunction.clone();
        forcedDisyunctionDistributed2.andNot(disyunctionDistribution);

        BitSet randomSet1 = generateRandomBitSet(BIT_LENGTH, BIT_THRESHOLD);
        randomSet1.or(forcedInterception);
        randomSet1.andNot(forcedDisyunction);
        randomSet1.or(forcedDisyunctionDistributed1);

        BitSet randomSet2 = generateRandomBitSet(BIT_LENGTH, BIT_THRESHOLD);
        randomSet2.or(forcedInterception);
        randomSet2.andNot(forcedDisyunction);
        randomSet2.or(forcedDisyunctionDistributed2);

        testVector1 = bitsetToIntArray(randomSet1);
        testVector2 = bitsetToIntArray(randomSet2);
    }

    @Before
    public void setUp() {
        randomFeed = new SecureRandom();

        generateTestVectors();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of size method, of class IntegerSet.
     */
    @Test
    public void testSize() {
        IntegerSet instance = new IntegerSet();
        addTestVector(instance, testVector1);
        
        int expResult = testVector1.length;
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of isEmpty method, of class IntegerSet.
     */
    @Test
    public void testIsEmpty() {
        IntegerSet instance = new IntegerSet();
        boolean expResult = true;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
        
        instance = new IntegerSet();
        addTestVector(instance, testVector1);
        expResult = false;
        result = instance.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of contains method, of class IntegerSet.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        Object that = null;
        IntegerSet instance = new IntegerSet();
        boolean expResult = false;
        boolean result = instance.contains(that);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of iterator method, of class IntegerSet.
     */
    @Test
    public void testIterator() {
        System.out.println("iterator");
        IntegerSet instance = new IntegerSet();
        Iterator expResult = null;
        Iterator result = instance.iterator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toArray method, of class IntegerSet.
     */
    @Test
    public void testToArray_0args() {
        System.out.println("toArray");
        IntegerSet instance = new IntegerSet();
        Object[] expResult = null;
        Object[] result = instance.toArray();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toArray method, of class IntegerSet.
     */
    @Test
    public void testToArray_GenericType() {
        System.out.println("toArray");
        T[] array = null;
        IntegerSet instance = new IntegerSet();
        Object[] expResult = null;
        Object[] result = instance.toArray(array);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class IntegerSet.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Integer that = null;
        IntegerSet instance = new IntegerSet();
        boolean expResult = false;
        boolean result = instance.add(that);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class IntegerSet.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        Object that = null;
        IntegerSet instance = new IntegerSet();
        boolean expResult = false;
        boolean result = instance.remove(that);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of containsAll method, of class IntegerSet.
     */
    @Test
    public void testContainsAll() {
        System.out.println("containsAll");
        Collection<?> those = null;
        IntegerSet instance = new IntegerSet();
        boolean expResult = false;
        boolean result = instance.containsAll(those);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addAll method, of class IntegerSet.
     */
    @Test
    public void testAddAll() {
        System.out.println("addAll");
        Collection<? extends Integer> those = null;
        IntegerSet instance = new IntegerSet();
        boolean expResult = false;
        boolean result = instance.addAll(those);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of retainAll method, of class IntegerSet.
     */
    @Test
    public void testRetainAll() {
        System.out.println("retainAll");
        Collection<?> those = null;
        IntegerSet instance = new IntegerSet();
        boolean expResult = false;
        boolean result = instance.retainAll(those);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeAll method, of class IntegerSet.
     */
    @Test
    public void testRemoveAll() {
        System.out.println("removeAll");
        Collection<?> those = null;
        IntegerSet instance = new IntegerSet();
        boolean expResult = false;
        boolean result = instance.removeAll(those);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clear method, of class IntegerSet.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        IntegerSet instance = new IntegerSet();
        instance.clear();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
