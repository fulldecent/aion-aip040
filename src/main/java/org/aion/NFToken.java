package org.aion;

import avm.Address;
import avm.Blockchain;
import java.math.BigInteger;

/**
 * Reference implementation for the AIP-040 Non-Fungible Token Standard
 * 
 * It is intended that this class can be inherited for every implementation or extension of AIP-040.
 * 
 * @author William Entriken
 */
public class NFToken {

    /**
     * Get the owner of a certain token
     * @param tokenId The token we are interrogating
     * @return The owner of the specified token
     */
    public static Address aip040OwnerOf(BigInteger tokenId) {
        Blockchain.require(tokenId != null);
        return NFTokenStorage.getTokenOwner(tokenId);
    }
    
    /**
     * Get the account (if any) to which a certain token is consigned
     * @apiNote The consignee shall have permission to transfer the token
     * @param tokenId The token we are interrogating
     * @return The consignee of the specified token, or null if none is assigned
     */
    public static Address aip040ConsigneeOf(BigInteger tokenId) {
        Blockchain.require(tokenId != null);
        return NFTokenStorage.getTokenConsignee(tokenId);
    }
    
    /**
     * Find whether an account has authorized access to another authorizee
     * @param owner The account which would delegate authorization to another account
     * @param authorizee The account which would receive the authorization
     * @return True the authorization is action, false otherwise
     */
    public static Boolean aip040IsAuthorized(Address owner, Address authorizee) {
        Blockchain.require(owner != null);
        Blockchain.require(authorizee != null);
        return NFTokenStorage.getAccountAuthorization(owner, authorizee);
    }
    
    /**
     * Get the number of tokens owned by a certain account.
     * @param owner The account whose tokens we are interrogating
     * @return A quantity of tokens owned by the specified owner
     */
    public static BigInteger aip040BalanceOf(Address owner) {
        Blockchain.require(owner != null);
        BigInteger storageBalance = NFTokenStorage.getOwnerBalance(owner);
        return storageBalance == null
            ? BigInteger.ZERO
            : storageBalance;
    }
    
//TODO: consider if tokenId should be an array of token ids. If this cost is VERY close to current single BigInteger then consider to make passing in an array the only standard method    
    /**
     * Transfer specified tokens from one owner to another, if permitted
     * @apiNote The owner parameter is duplicative because it could be looked up
     *          using the token identifier. However the implementation MUST
     *          verify that the owner is correct as this prevents a race
     *          condition due to non-linear settlement of transactions on
     *          blockchain.
    * @param currentOwner The current owner of the token
    * @param newOwner The new owner of the token after transfer is complete
    * @param tokenIds Specific tokens to transfer
    */
    public static void aip040Transfer(Address currentOwner, Address newOwner, BigInteger[] tokenIds) {
        Blockchain.require(currentOwner != null);
        Blockchain.require(newOwner != null);
        Blockchain.require(tokenIds != null);

        boolean isAuthorized = Blockchain.getCaller().equals(currentOwner) ||
            aip040IsAuthorized(currentOwner, Blockchain.getCaller());

        for (BigInteger tokenId : tokenIds) {
            Blockchain.require(aip040OwnerOf(tokenId).equals(currentOwner));
            Blockchain.require(isAuthorized || aip040ConsigneeOf(tokenId).equals(Blockchain.getCaller()));

            BigInteger fromBalance = NFToken.aip040BalanceOf(currentOwner);
            fromBalance = fromBalance.subtract(BigInteger.ONE);
            NFTokenStorage.putOwnerBalance(newOwner, fromBalance);

            BigInteger toBalance = NFToken.aip040BalanceOf(newOwner);
            toBalance = toBalance.add(BigInteger.ONE);
            NFTokenStorage.putOwnerBalance(newOwner, toBalance);

// handle the enumerable array of tokens for sender and receiver
            NFTokenStorage.putTokenConsignee(tokenId, null);
            AIP040Events.AIP040Transferred(currentOwner, newOwner, tokenId);
        }
    }
    
    /**
     * Consigns a specified token to an account, or revoke an existing consignment.
     * This method if
     * @param owner The account that currently owns the specified token
     * @param consignee The account to consign to, or null to revoke consignment
     * @param tokenId A specific token to consign
     */
    public static void aip040Consign(Address owner, Address consignee, BigInteger tokenId) {
        Blockchain.require(owner != null);
        Blockchain.require(tokenId != null);
        Blockchain.require(aip040OwnerOf(tokenId).equals(owner));
        Blockchain.require(
            Blockchain.getCaller().equals(owner) ||
            aip040IsAuthorized(owner, Blockchain.getCaller())
        );
        NFTokenStorage.putTokenConsignee(tokenId, consignee);
        AIP040Events.AIP040Consigned(owner, consignee, tokenId);
    }
    
    /**
     * Authorize an account to act on behalf of the caller
     * @param authorizee The account which receives authorization
     */
    public static void aip040Authorize(Address authorizee) {
        Blockchain.require(authorizee != null);
        NFTokenStorage.putAccountAuthorization(Blockchain.getCaller(), authorizee, true);
        AIP040Events.AIP040Authorized(Blockchain.getCaller(), authorizee);
    }
    
    /**
     * Deauthorize an account to act on behalf of the caller
     * @param authorizee The account which is revoked authorization
     */
    public static void aip040Deauthorize(Address priorAuthorizee) {
        Blockchain.require(priorAuthorizee != null);
        NFTokenStorage.putAccountAuthorization(Blockchain.getCaller(), priorAuthorizee, false);
        AIP040Events.AIP040Deauthorized(Blockchain.getCaller(), priorAuthorizee);
    }
    
    /**
     * Get a descriptive name for this non-fungible token
     * @implSpec It is unspecified what language this text will be and no attempt is made to provide a localization feature
     * @return A string representing the descriptive name
     */
    public static String aip040Name() {
        return NFTokenStorage.getTokenName();
    }
    
    /**
     * Get a brief name (e.g. ticker symbol) for this non-fungible token class
     * @implSpec It is unspecified what language this text will be and no attempt is made to provide a localization feature
     * @return A string representing the brief name
     */
    public static String aip040Symbol() {
        return NFTokenStorage.getTokenSymbol();
    }
    
    /**
     * Get a URI for a specified token
     * @param tokenId A specific token to interrogate
     * @return A string representing the brief name
     */
    public static String aip040TokenUri(BigInteger tokenId) {
        String uriBase = NFTokenStorage.getTokenUriBase();
        Blockchain.require(uriBase != null);
        return uriBase + tokenId.toString();
    }
    
    /**
     * The total quantity of tokens existing
     * @return 
     */
    public static BigInteger aip040TotalSupply() {
        BigInteger storageTotalSupply = NFTokenStorage.getTotalSupply();
        return storageTotalSupply == null
            ? BigInteger.ZERO
            : storageTotalSupply;
    }
    
    /**
    * The n-th token identifier
    * @implSpec It is guaranteed that calling this method for every value -- 0 <= index < totalSupply -- against one specific block will enumerate every token
    * @param index Which ordinal of token identifier you are seeking, 0 <= index < aip040TotalSupply
    * @return The token identifier for the n-th token in the list of all tokens
    */
    public static BigInteger aip040TokenAtIndex(BigInteger index) {
        Blockchain.require(index != null);
        Blockchain.require(index.compareTo(aip040TotalSupply()) < 0);
        return NFTokenStorage.getTokenAtIndex(index);
    }
    
    /**
     * The n-th token identifier for a given account
     * @implSpec It is guaranteed that calling this method for every value -- 0 <= index < totalSupply -- against one specific block will enumerate every token
     * @param owner The account which you are interrogaating owned tokens
     * @param index Which ordinal of token identifier you are seeking, 0 <= index < aip040BalanceOf(owner)
     * @return The token identifier for the n-th token in the list of all tokens
     */
    public static BigInteger aip040TokenOfOwnerAtIndex(Address owner, BigInteger index) {
        Blockchain.require(owner != null);
        Blockchain.require(index != null);
        Blockchain.require(index.compareTo(aip040TotalSupply()) < 0);
        return NFTokenStorage.getTokensOfOwnerArray(owner, index);
    }
    
}