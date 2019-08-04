package org.aion;

import org.aion.avm.userlib.AionBuffer;
import avm.Blockchain;

/**
 * We assert that putStorage​(byte[] key, byte[] value) and
 * getStorage(byte[] key) shoud be avoided because their method signatures
 * encourage two unsafe practices:
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
     * location described by <code>realm</code> and <code>keyPath</code>
     * 
     * @param realm An enum constant which qualifies the key path
     * @param keyPath An array which, along with the <code>realm</code>, fully
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
     * <code>keyPath</code>
     * 
     * @param value What will be stored
     * @param realm An enum constant which qualifies the key path
     * @param keyPath An array which, along with the <code>realm</code>, fully
     *                qualifies the storage location
     */
    public static void putStorage​(byte[] value, Enum realm, byte[]... keyPath) {
        byte[] serializedRealmAndKey = serializeRealmAndKey(realm, keyPath);
        byte[] storageKey = Blockchain.blake2b(serializedRealmAndKey);
        Blockchain.putStorage(storageKey, value);
    }

    /**
     * Injectively converts a fully qualified realm and key path into a byte
     * array
     * 
     * @param realm An enum constant which qualifies the key path
     * @param keyPath An array of byte arrays
     * @return A byte array which uniquely represents the (<code>realm</code>, 
     *         <code>keyPath</code>) pair 
     */
    private static byte[] serializeRealmAndKey(Enum realm, byte[][] keyPath) {
        //TODO: review code at https://github.com/aionnetwork/AVM/blob/master/org.aion.avm.userlib/src/org/aion/avm/userlib/abi/ABIStreamingEncoder.java#L957 and possibly reuse that for consistency
        int outputSize = Integer.BYTES;
        for (byte[] keyPathItem : keyPath) {
            //TODO: Add reference to AVM specification that arrays length can never exceed Integer.MAX_VALUE
            outputSize += Integer.BYTES + keyPathItem.length;
        }

        AionBuffer buffer = AionBuffer.allocate(outputSize);
        //TODO: Add reference to AVM specification that hashCodes will never collide
        buffer.putInt(realm.hashCode());
        for (byte[] keyPathItem : keyPath) {
            buffer.putInt(keyPathItem.length); // because [[a], [b]] ± [[a, b]]
            buffer.put(keyPathItem);
        }
        return buffer.getArray();
    }
}