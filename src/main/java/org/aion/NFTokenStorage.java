package org.aion;

import avm.Address;
import java.math.BigInteger;

/**
 * The methods in this class are fully described by <code>StorageSlots</code>
 * and the comments there. In the future, tooling may automatically generate
 * this class from that specification.
 * 
 * Every method directly gets and puts to storage, which is initially null. So,
 * methods not documented per https://google.github.io/styleguide/javaguide.html#s7.3.1-javadoc-exception-self-explanatory
 */
public class NFTokenStorage {
    /**
     * Best practice with Aion is to use key-value storage for everything except
     * recursive call state and configuration options which apply to all on-
     * chain transactions.
     */
    protected enum StorageSlots {
        TOKEN_NAME, // () => String
        TOKEN_SYMBOL, // () => String
        TOKEN_URI_PREFIX, // () => String
        TOKEN_URI_POSTFIX, // () => String
        TOTAL_SUPPLY, // () => BigInteger

        TOKENS_ARRAY, // (BigInteger) => BigInteger
        TOKEN_OWNERS_MAP, // (BigInteger) => Address
        TOKEN_LOCATION_MAP, // (BigInteger) => BigInteger
        TOKEN_CONSIGNEE_MAP, // (BigInteger) => Address
        ACCOUNT_AUTHORIZATION_MAP, // (Address, Address) => boolean
        OWNER_BALANCE_MAP, // (Address) => BigInteger
        TOKENS_OF_OWNER_ARRAY, // (Address, BigInteger) => BigInteger        
    }

    protected static String getTokenName() {
        return AVMBlockchainWrapper.getStorage​String(StorageSlots.TOKEN_NAME);
    }

    protected static void putTokenName(String name) {
        AVMBlockchainWrapper.putStorage​String(name, StorageSlots.TOKEN_NAME);
    }

    protected static String getTokenSymbol() {
        return AVMBlockchainWrapper.getStorage​String(StorageSlots.TOKEN_SYMBOL);
    }

    protected static void putTokenSymbol(String symbol) {
        AVMBlockchainWrapper.putStorage​String(symbol, StorageSlots.TOKEN_SYMBOL);
    }

    protected static String getTokenUriPrefix() {
        return AVMBlockchainWrapper.getStorage​String(StorageSlots.TOKEN_URI_PREFIX);
    }

    protected static void putTokenUriPrefix(String uriPrefix) {
        AVMBlockchainWrapper.getStorage​String(StorageSlots.TOKEN_SYMBOL);
    }

    protected static String getTokenUriPostfix() {
        return AVMBlockchainWrapper.getStorage​String(StorageSlots.TOKEN_URI_POSTFIX);
    }

    protected static void putTokenUriPostfix(String uriPostfix) {
        AVMBlockchainWrapper.putStorage​String(uriPostfix, StorageSlots.TOKEN_URI_POSTFIX);
    }

    protected static BigInteger getTotalSupply() {
        return AVMBlockchainWrapper.getStorage​BigInteger(StorageSlots.TOTAL_SUPPLY);
    }

    protected static void putTotalSupply(BigInteger totalSupply) {
        AVMBlockchainWrapper.putStorage​BigInteger(totalSupply, StorageSlots.TOTAL_SUPPLY);
    }

    protected static BigInteger getTokenAtIndex(BigInteger index) {
        return AVMBlockchainWrapper.getStorage​BigInteger(StorageSlots.TOKENS_ARRAY, index.toByteArray());
    }

    protected static void putTokenAtIndex(BigInteger index, BigInteger tokenId) {
        AVMBlockchainWrapper.putStorage​BigInteger(tokenId, StorageSlots.TOKENS_ARRAY, index.toByteArray());
    }

    protected static Address getTokenOwner(BigInteger tokenId) {
        return AVMBlockchainWrapper.getStorage​Address(StorageSlots.TOKEN_OWNERS_MAP, tokenId.toByteArray());
    }

    protected static void putTokenOwner(BigInteger tokenId, Address owner) {
        AVMBlockchainWrapper.putStorage​Address(owner, StorageSlots.TOKEN_OWNERS_MAP, tokenId.toByteArray());
    }

    protected static BigInteger getTokenLocation(BigInteger tokenId) {
        return AVMBlockchainWrapper.getStorage​BigInteger(StorageSlots.TOKEN_LOCATION_MAP, tokenId.toByteArray());
    }

    protected static void putTokenLocation(BigInteger tokenId, BigInteger location) {
        AVMBlockchainWrapper.putStorage​BigInteger(location, StorageSlots.TOKEN_LOCATION_MAP, tokenId.toByteArray());
    }

    protected static Address getTokenConsignee(BigInteger tokenId) {
        return AVMBlockchainWrapper.getStorage​Address(StorageSlots.TOKEN_CONSIGNEE_MAP, tokenId.toByteArray());
    }

    protected static void putTokenConsignee(BigInteger tokenId, Address consignee) {
        AVMBlockchainWrapper.putStorage​Address(consignee, StorageSlots.TOKEN_CONSIGNEE_MAP, tokenId.toByteArray());
    }

    protected static boolean getAccountAuthorization(Address owner, Address authorizee) {
        return AVMBlockchainWrapper.getStorage​Boolean(
            StorageSlots.ACCOUNT_AUTHORIZATION_MAP,
            owner.toByteArray(),
            authorizee.toByteArray()
        );
    }

    protected static void putAccountAuthorization(Address owner, Address authorizee, boolean authorized) {
        AVMBlockchainWrapper.putStorage​Boolean(
            authorized,
            StorageSlots.ACCOUNT_AUTHORIZATION_MAP,
            owner.toByteArray(),
            authorizee.toByteArray()
        );
    }

    protected static BigInteger getOwnerBalance(Address owner) {
        return AVMBlockchainWrapper.getStorage​BigInteger(StorageSlots.OWNER_BALANCE_MAP, owner.toByteArray());
    }

    protected static void putOwnerBalance(Address owner, BigInteger balance) {
        AVMBlockchainWrapper.putStorage​BigInteger(balance, StorageSlots.OWNER_BALANCE_MAP, owner.toByteArray());
    }

    protected static BigInteger getTokensOfOwnerArray(Address owner, BigInteger index) {
        return AVMBlockchainWrapper.getStorage​BigInteger(StorageSlots.TOKENS_OF_OWNER_ARRAY, owner.toByteArray(), index.toByteArray());
    }

    protected static void putTokensOfOwnerArray(Address owner, BigInteger index, BigInteger tokenId) {
        AVMBlockchainWrapper.putStorage​BigInteger(tokenId, StorageSlots.TOKENS_OF_OWNER_ARRAY, owner.toByteArray(), index.toByteArray());
    }
}