package org.aion;

import java.math.BigInteger;
import org.aion.avm.userlib.AionBuffer;
import avm.Blockchain;
import org.aion.avm.userlib.abi.ABIException;
import avm.Address;

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
     * @param keyPath an array of non-null byte arrays which, along with the
     *                <code>realm</code>, fully qualifies the storage location
     * @return        the value from storage
     */
    public static byte[] getStorage​ByteArray(Enum realm, byte[]... keyPath) {
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
     * @param keyPath an array of non-null byte arrays which, along with the
     *                <code>realm</code>, fully qualifies the storage location
     */
    public static void putStorage​ByteArray(byte[] value, Enum realm, byte[]... keyPath) {
        byte[] serializedRealmAndKey = serializeRealmAndKey(realm, keyPath);
        byte[] storageKey = Blockchain.blake2b(serializedRealmAndKey);
        Blockchain.putStorage(storageKey, value);
    }

    /**
     * Gets a String from the key-value store of the current account at the key
     * location described by <code>realm</code> and <code>keyPath</code>.
     * 
     * @param realm    an enum constant which qualifies the key path
     * @param keyPath  an array of non-null byte arrays which, along with the
     *                 <code>realm</code>, fully qualifies the storage location
     * @return         the value from storage
     */
    public static String getStorage​String(Enum realm, byte[]... keyPath) {
        byte[] encodedStorage = getStorage​ByteArray(realm, keyPath);
        if (encodedStorage == null) {
            return null;
        }
        return new String(encodedStorage);
    }

    /**
     * Stores <code>value</code> into the key-value store of the current account
     * at the key location described by <code>realm</code> and
     * <code>keyPath</code>.
     * 
     * @param value   what will be stored
     * @param realm   an enum constant which qualifies the key path
     * @param keyPath an array of non-null byte arrays which, along with the
     *                <code>realm</code>, fully qualifies the storage location
     */
    public static void putStorage​String(String value, Enum realm, byte[]... keyPath) {
        putStorage​ByteArray(
            value == null ? null : value.getBytes(),
            realm,
            keyPath
        );
    }

    /**
     * Gets an Address from the key-value store of the current account at the
     * key location described by <code>realm</code> and <code>keyPath</code>.
     * 
     * @param realm    an enum constant which qualifies the key path
     * @param keyPath  an array of non-null byte arrays which, along with the
     *                 <code>realm</code>, fully qualifies the storage location
     * @return         the value from storage
     */
    public static Address getStorage​Address(Enum realm, byte[]... keyPath) {
        byte[] encodedStorage = getStorage​ByteArray(realm, keyPath);
        if (encodedStorage == null) {
            return null;
        }
        return new Address(encodedStorage);
    }

    /**
     * Stores <code>value</code> into the key-value store of the current account
     * at the key location described by <code>realm</code> and
     * <code>keyPath</code>.
     * 
     * @param value   what will be stored
     * @param realm   an enum constant which qualifies the key path
     * @param keyPath an array of non-null byte arrays which, along with the
     *                <code>realm</code>, fully qualifies the storage location
     */
    public static void putStorage​Address(Address value, Enum realm, byte[]... keyPath) {
        putStorage​ByteArray(
            value == null ? null : value.toByteArray(),
            realm,
            keyPath
        );
    }

    /**
     * Gets a BigInteger from the key-value store of the current account at the
     * key location described by <code>realm</code> and <code>keyPath</code>.
     * 
     * @param  realm   an enum constant which qualifies the key path
     * @param  keyPath an array of non-null byte arrays which, along with the
     *                 <code>realm</code>, fully qualifies the storage location
     * @return         the value from storage
     */
    public static BigInteger getStorage​BigInteger(Enum realm, byte[]... keyPath) {
        byte[] encodedStorage = getStorage​ByteArray(realm, keyPath);
        if (encodedStorage == null) {
            return null;
        }
        return new BigInteger(encodedStorage);
    }

    /**
     * Stores <code>value</code> into the key-value store of the current account
     * at the key location described by <code>realm</code> and
     * <code>keyPath</code>.
     * 
     * @param value   what will be stored
     * @param realm   an enum constant which qualifies the key path
     * @param keyPath an array of non-null byte arrays which, along with the
     *                <code>realm</code>, fully qualifies the storage location
     */
    public static void putStorage​BigInteger(BigInteger value, Enum realm, byte[]... keyPath) {
        putStorage​ByteArray(
            value == null ? null : value.toByteArray(),
            realm,
            keyPath
        );
    }

    /**
     * Gets a boolean from the key-value store of the current account at the key
     * location described by <code>realm</code> and <code>keyPath</code>.
     * 
     * @implSpec       Only a value generated by <code>putStorageBoolean</code>
     *                 is properly decoded.
     * @param  realm   an enum constant which qualifies the key path
     * @param  keyPath an array of non-null byte arrays which, along with the
     *                 <code>realm</code>, fully qualifies the storage location
     * @return         the value from storage
     */
    public static boolean getStorage​Boolean(Enum realm, byte[]... keyPath) {
        byte[] encodedStorage = getStorage​ByteArray(realm, keyPath);
        return encodedStorage != null && encodedStorage[0] == 0x1;
    }

    /**
     * Stores <code>value</code> into the key-value store of the current account
     * at the key location described by <code>realm</code> and
     * <code>keyPath</code>.
     * 
     * @param value   what will be stored
     * @param realm   an enum constant which qualifies the key path
     * @param keyPath an array of non-null byte arrays which, along with the
     *                <code>realm</code>, fully qualifies the storage location
     */
    public static void putStorage​Boolean(boolean value, Enum realm, byte[]... keyPath) {
        putStorage​ByteArray(
            value ? new byte[]{0x1} : null,
            realm,
            keyPath
        );
    }    

    // Using approach from org.aion.avm.userlib/src/org/aion/avm/userlib/abi/ABIStreamingEncoder.java
    private static void checkLengthIsAShort(int size) {
        if (size > Short.MAX_VALUE) {
            throw new ABIException(/* "Array length must fit in 2 bytes" */);
            // Including debugging strings inside a contract is not best
            // practice because they cost energy, are not optimized away and
            // the debugging information is never available to the end user.
            // TODO: Add reference for this
        }
    }

    /**
     * Injectively converts a fully qualified realm and key path into a byte
     * array.
     * 
     * @param  realm   an enum constant which qualifies the key path
     * @param  keyPath an array of non-null byte arrays
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
        // Enum constants have identity hash codes which cannot collide with
        // each other. https://github.com/aionnetwork/AVM/wiki/Hash-Code
        buffer.putInt(realm.hashCode());
        for (byte[] keyPathItem : keyPath) {
            buffer.putShort((short) keyPathItem.length); // because [[a], [b]] ≠ [[a, b]]
            buffer.put(keyPathItem);
        }
        return buffer.getArray();
    }

}
