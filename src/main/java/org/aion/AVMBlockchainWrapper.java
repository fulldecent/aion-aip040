package org.aion;

import org.aion.avm.userlib.AionBuffer;
import avm.Blockchain;
import org.aion.avm.userlib.abi.ABIException;

/**
 * We assert that <code>putStorage(byte[] key, byte[] value)</code> and
 * <code>getStorage(byte[] key)</code> should be avoided because their method
 * signatures encourage two unsafe practices:
 * 
 * 1. Key injection -- when the end user can directly choose a key for storage,
 *    possibly colliding this keys used for other purposes
 * 2. Keyspace reuse -- when the application architect accidently chooses keys
 *    that may overlap
 * 
 * As an alternative, we present the following API which should be preferred
 * to the above two methods in nearly every circumstance. This class is
 * designed so that it could be added to AVM and/or Aion's userlib.
 * 
 * @see org.avm.Blockchain.getStorage​
 * @see org.avm.Blockchain.putStorage
 * @see org.aion.avm.userlib
 */
public class AVMBlockchainWrapper {

    /**
     * Gets a value from the key-value store of the current account at the key
     * location described by <code>realm</code> and <code>keyPath</code>.
     * 
     * @param realm   an enum constant which qualifies the key path
     * @param keyPath an array which, along with the <code>realm</code>, fully
     *                qualifies the storage location
     */
    public static byte[] getStorage​(Enum realm, byte[]... keyPath) {
        byte[] serializedRealmAndKey = serializeRealmAndKey(realm, keyPath);
        byte[] storageKey = Blockchain.blake2b(serializedRealmAndKey);
        return Blockchain.getStorage(storageKey);
    }

    /**
     * Stores <code>value</code> into the key-value store of the current account
     * at the key location described by <code>realm</code> and
     * <code>keyPath</code>.
     * 
     * @param value   what will be stored
     * @param realm   an enum constant which qualifies the key path
     * @param keyPath an array which, along with the <code>realm</code>, fully
     *                qualifies the storage location
     */
    public static void putStorage​(byte[] value, Enum realm, byte[]... keyPath) {
        byte[] serializedRealmAndKey = serializeRealmAndKey(realm, keyPath);
        byte[] storageKey = Blockchain.blake2b(serializedRealmAndKey);
        Blockchain.putStorage(storageKey, value);
    }

    // Using approach from org.aion.avm.userlib/src/org/aion/avm/userlib/abi/ABIStreamingEncoder.java
    private static void checkLengthIsAShort(int size) {
        if (size > Short.MAX_VALUE) {
            throw new ABIException("Array length must fit in 2 bytes");
        }
    }

    /**
     * Injectively converts a fully qualified realm and key path into a byte
     * array.
     * 
     * @param  realm   an enum constant which qualifies the key path
     * @param  keyPath an array of byte arrays
     * @return         a byte array which uniquely represents the
     *                 (<code>realm</code>, <code>keyPath</code>) pair 
     */
    private static byte[] serializeRealmAndKey(Enum realm, byte[][] keyPath) {
        int outputSize = Integer.BYTES;
        for (byte[] keyPathItem : keyPath) {
            checkLengthIsAShort(keyPathItem.length);
            outputSize += Short.BYTES + keyPathItem.length;
        }

        AionBuffer buffer = AionBuffer.allocate(outputSize);
        //TODO: Add reference to AVM specification that hashCodes will never collide
        //TODO: is it specified that all hashcodes would fit in a short?
        buffer.putInt(realm.hashCode());
        for (byte[] keyPathItem : keyPath) {
            buffer.putShort((short) keyPathItem.length); // because [[a], [b]] ± [[a, b]]
            buffer.put(keyPathItem);
        }
        return buffer.getArray();
    }
}