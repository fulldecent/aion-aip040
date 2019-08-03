package org.aion;

import avm.Blockchain;
import avm.Address;
import org.aion.avm.userlib.AionBuffer;

//TODO: this file should not be named AIP040... because this is the implementation. The implementation should have a different name like nftoken see https://github.com/0xcert/ethereum-erc721/tree/master/src/contracts/tokens

//TODO: THROUGHOUT THIS FILE this is incesure, the StorageSlots.OWNER_BALANCE_MAP.ordinal() may conflict with the same number in a parent class. Instead, we need to use the fully qualified name inside this key. E.g. org.aion.AIP040....StorageSlots.OWNER_BALANCE_MAP

//TODO: note, some ENUMS and methods represent optional methods that are not required to be in the implementation. Let's confirm if Java is smart enough to remove the methods from the runtime if an implementation is not using them. Otherwise we may want to separate the code for performance reasons

public class AIP040KeyValueStorage {
    /**
     * Best practice with Aion is to explictly define all storage locations using an enum and then use these enums for all calls to putStorage. This avoids overlapping storage, which is also a vulnerability. Also, use static storage only for recursive call state and configuration options which apply to all onchain transactions. 
     */
    protected enum StorageSlots {
        TOKEN_OWNERS_MAP, // [tokenId] => owner
        TOKEN_CONSIGNEE_MAP, // [token] => consignee
        ACCOUNT_AUTHORIZATION_MAP, // [account, authorizee] => bool
        OWNER_BALANCE_MAP, // [account] => BigInteger
        
        TOKEN_NAME,
        TOKEN_SYMBOL,
        TOKEN_URI_BASE,
        TOTAL_SUPPLY,
        TOKENS_OF_OWNER_ARRAY, // [account, index] => tokenId
    }

    public static byte[] tokenOwnersMapKey(byte[] tokenId) {
        return Blockchain.blake2b(AionBuffer.allocate(Integer.BYTES + tokenId.length)
                .putInt(StorageSlots.OWNER_BALANCE_MAP.ordinal())
                .put(tokenId)
                .getArray());      
    }

    public static byte[] tokenConsigneeMapKey(byte[] tokenId) {
        return Blockchain.blake2b(AionBuffer.allocate(Integer.BYTES + tokenId.length)
                .putInt(StorageSlots.TOKEN_CONSIGNEE_MAP.ordinal())
                .put(tokenId)
                .getArray());
    }

    public static byte[] accountAuthorizationMapKey(Address account, Address authorizee) {
        return Blockchain.blake2b(AionBuffer.allocate(Integer.BYTES + Address.LENGTH * 2)
                .putInt(StorageSlots.ACCOUNT_AUTHORIZATION_MAP.ordinal())
                .putAddress(account)
                .putAddress(authorizee)
                .getArray());
    }

    public static byte[] ownerBalanceMapKey(Address owner) {
        return Blockchain.blake2b(AionBuffer.allocate(Integer.BYTES + Address.LENGTH)
                .putInt(StorageSlots.OWNER_BALANCE_MAP.ordinal())
                .putAddress(owner)
                .getArray());
    }

    public static byte[] tokenNameKey() {
        return Blockchain.blake2b(AionBuffer.allocate(Integer.BYTES)
                .putInt(StorageSlots.TOKEN_NAME.ordinal())
                .getArray());
    }

    public static byte[] tokenSymbolKey() {
        return Blockchain.blake2b(AionBuffer.allocate(Integer.BYTES)
                .putInt(StorageSlots.TOKEN_SYMBOL.ordinal())
                .getArray());
    }

    public static byte[] tokenUriBaseKey() {
        return Blockchain.blake2b(AionBuffer.allocate(Integer.BYTES)
                .putInt(StorageSlots.TOKEN_URI_BASE.ordinal())
                .getArray());
    }

    public static byte[] totalSupplyKey() {
        return Blockchain.blake2b(AionBuffer.allocate(Integer.BYTES)
                .putInt(StorageSlots.TOTAL_SUPPLY.ordinal())
                .getArray());
    }

    public static byte[] tokensOfOwnerArray(Address owner) {
        return Blockchain.blake2b(AionBuffer.allocate(Integer.BYTES + Address.LENGTH)
                .putInt(StorageSlots.TOKENS_OF_OWNER_ARRAY.ordinal())
                .putAddress(owner)
                .getArray());
    }
}
