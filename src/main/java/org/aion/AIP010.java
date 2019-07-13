package org.aion;

import avm.Address;
import avm.Blockchain;
import org.aion.avm.tooling.abi.Callable;
import org.aion.avm.userlib.abi.ABIDecoder;

import java.math.BigInteger;

//TODO: move reference implementation to an INTERFACE and use it as a mixin!
//TODO: Which parts of JavaDoc should be sentences (have periods)?
//TODO: Aion specifies Google Java code style SOURCE https://github.com/aionnetwork/aion/wiki/Aion-Code-Conventions so we should validate
//TODO: we use byte[] and don't use bignumber, is that a problem?
//TODO: need to get 100% coverage and confirm that we have 100% in the travis test
//TODO: reformat this and all other files with Google Java convention

/**
 * Reference implementation for the AIP-010 Non-Fungible Token Standard
 * 
 * It is intended that this class can be inherited for every implementation or extension of AIP-010.
 * 
 * @author William Entriken
 */
public class AIP010 {

//TODO: that
//region MOVE THESE TO STORAGE
private static String tokenName;

private static String tokenSymbol;

//TODO: check google java style guide // URI or Uri?
private static String tokenUriBase;
//endregion

//TODO: that
//region remove these
private static int tokenGranularity;
//endregion

//TODO: this is implementation-specific behavior and should be moved to a mock class that implements a base class and where only the base class is the reference implementation    
    /**
     * This is the deployment function and is responsible for taking parameters passed in using the Aion AVM and then standing up any contract initialization
     */
    static {
        ABIDecoder decoder = new ABIDecoder(Blockchain.getData());
        // FYI, Java specifies that argument lists are evaluated left-to-right https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.7.4
        deploy(
            decoder.decodeOneString(),
            decoder.decodeOneString(),
            decoder.decodeOneString()
        );
    }

//todo: it is expected that Aion toolchain will change and in the future a specific deploy function will be required in order to introspect deployment parameter types (for type safety) see https://github.com/aionnetwork/AVM/issues/401 and update the name of this function if necessary

    /**
     * This is called once during contract deployment
     * @param tokenName This will be returned from aip010Name
     * @param tokenSymbol This will be returned from aip010Symbol
     * @param tokenUriBase This will be used in aip010TokenUri
     */
    private static void deploy(String tokenName, String tokenSymbol, String tokenUriBase) {
        Blockchain.require(tokenName.length() > 0);
        Blockchain.require(tokenSymbol.length() > 0);
        Blockchain.require(tokenUriBase.length() > 0);
        AIP010.tokenName = tokenName;
        AIP010.tokenSymbol = tokenSymbol;
        AIP010.tokenUriBase = tokenUriBase;
    }

    /**
     * Get the owner of a certain token
     * @param tokenId The token we are interrogating, greater than or equal to zero
     * @return The owner of the specified token
     */
    @Callable
    public static Address aip010OwnerOf(byte[] tokenId) {
        Blockchain.require(new BigInteger(tokenId).signum() >= 0);

        byte[] key = AIP010KeyValueStorage.tokenOwnersMapKey(tokenId);
        byte[] owner = Blockchain.getStorage(key);
        return new Address(owner);
//TODO: confirm if returning null is allowed here or if we use the Null Object Pattern
    }

    /**
     * Get the account (if any) to which a certain token is consigned
     * @apiNote The consignee shall have permission to transfer the token
     * @param tokenId The token we are interrogating, greater than or equal to zero
     * @return The consignee of the specified token, or null if none is assigned
     */
    @Callable
    public static Address aip010ConsigneeOf(byte[] tokenId) {
        Blockchain.require(new BigInteger(tokenId).signum() >= 0);
        byte[] key = AIP010KeyValueStorage.tokenConsigneeMapKey(tokenId);
        byte[] consignee = Blockchain.getStorage(key);
        return new Address(consignee);
//TODO: confirm if returning null is allowed here or if we use the Null Object Pattern
    }

    /**
     * Find whether an account has authorized access to another authorizee
     * @param owner The account which would delegate authorization to another account
     * @param authorizee The account which would receive the authorization
     * @return True the authorization is action, false otherwise
     */
    @Callable
    public static boolean aip010IsAuthorized(Address owner, Address authorizee) {
        byte[] key = AIP010KeyValueStorage.accountAuthorizationMapKey(owner, authorizee);
        byte[] isAuthorized = Blockchain.getStorage(key);
        return isAuthorized != null
                ? true
                : false;
    }

    /**
     * Get the number of tokens owned by a certain account.
     * @param owner The account whose tokens we are interrogating
     * @return A quantity of tokens owned by the specified owner
     */
    @Callable
    public static byte[] aip010BalanceOf(Address owner) {
//TODO: if we are not using the Null Object Pattern then Address(0x0) is our special value and we need to throw an invalid input exception if this method is run on 0x0
        byte[] key = AIP010KeyValueStorage.ownerBalanceMapKey(owner);
        byte[] balance = Blockchain.getStorage(key);
        return balance != null
                ? balance
                : BigInteger.ZERO.toByteArray();
    }

//TODO: consider if tokenId should be an array of token ids. If this cost is VERY close to current byte[] then consider to make passing in an array the only standard function    
    /**
     * Transfer a specified token from one owner to another, if permitted
     * @apiNote The owner parameter is duplicative because it could be looked up using the token identifier. However the implementation MUST verify that the owner is correct as this prevents a race condition due to non-linear settlement of transactions on blockchain.
     * @param currentOwner The current owner of the token
     * @param newOwner The new owner of the token after transfer is complete
     * @param tokenId A specific token to transfer, greater than or equal to zero
     */
    @Callable
    public static void aip010Transfer(Address currentOwner, Address newOwner, byte[] tokenId) {
//TODO: this code assumes it is impossible for AVM to call this contract using NULL address values. Validate that assumption.
        Blockchain.require(aip010OwnerOf(tokenId).equals(currentOwner));
        Blockchain.require(
            Blockchain.getCaller().equals(currentOwner) ||
            Blockchain.getCaller().equals(aip010ConsigneeOf(tokenId)) ||
            aip010IsAuthorized(currentOwner, Blockchain.getCaller())
        );

        byte[] fromBalance = Blockchain.getStorage(AIP010KeyValueStorage.ownerBalanceMapKey(currentOwner));
        Blockchain.putStorage(AIP010KeyValueStorage.ownerBalanceMapKey(currentOwner),
            new BigInteger(fromBalance).subtract(BigInteger.valueOf(1)).toByteArray()
        );

        byte[] toBalance = Blockchain.getStorage(AIP010KeyValueStorage.ownerBalanceMapKey(newOwner));
        Blockchain.putStorage(AIP010KeyValueStorage.ownerBalanceMapKey(newOwner),
            new BigInteger(toBalance).add(BigInteger.valueOf(1)).toByteArray()
        );

        Blockchain.putStorage(AIP010KeyValueStorage.tokenOwnersMapKey(tokenId),
            newOwner.toByteArray()
        );

        Blockchain.putStorage(AIP010KeyValueStorage.tokenConsigneeMapKey(tokenId),
            null
        );

        AIP010Events.AIP010Transferred(currentOwner, newOwner, new BigInteger(tokenId));
    }

//TODO: should consignee be allowed to consign to another account? ERC-721 says no, but here says yes
    /**
     * Consigns a specified token to an account, or renoke an existing consignment
     * @param owner The account that currently owns the specified token
     * @param consignee The account to consign to, or null to revoke consignment
     * @param tokenId A specific token to consign, greater than or equal to zero
     */
    @Callable
    public static void aip010Consign(Address owner, Address consignee, byte[] tokenId) {
        Blockchain.require(aip010OwnerOf(tokenId).equals(owner));
        Blockchain.require(
            Blockchain.getCaller().equals(owner) ||
            Blockchain.getCaller().equals(aip010ConsigneeOf(tokenId)) ||
            aip010IsAuthorized(owner, Blockchain.getCaller())
        );

        Blockchain.putStorage(AIP010KeyValueStorage.tokenConsigneeMapKey(tokenId),
            consignee.toByteArray()
        );

        AIP010Events.AIP010Consigned(owner, consignee, new BigInteger(tokenId));
    }

    /**
     * Authorize an account to act on behalf of the caller
     * @param authorizee The account which receives authorization
     */
    @Callable
    public static void aip010Authorize(Address authorizee) {
        Blockchain.putStorage(AIP010KeyValueStorage.accountAuthorizationMapKey(Blockchain.getCaller(), authorizee),
            BigInteger.valueOf(1).toByteArray()
        );
        AIP010Events.AIP010Authorized(Blockchain.getCaller(), authorizee);
    }
    
    /**
     * Deauthorize an account to act on behalf of the caller
     * @param authorizee The account which is revoked authorization
     */
    @Callable
    public static void aip010Deauthorize(Address priorAuthorizee) {
        Blockchain.putStorage(AIP010KeyValueStorage.accountAuthorizationMapKey(Blockchain.getCaller(), priorAuthorizee),
            null
        );
        AIP010Events.AIP010Authorized(Blockchain.getCaller(), priorAuthorizee);
    }

//region Optional methods
    /**
     * Get a descriptive name for this non-fungible token
     * @implSpec It is unspecified what language this text will be and no attempt is made to provide a localization feature
     * @return A string representing the descriptive name
     */
    @Callable
    public static String aip010Name() {
        return tokenName;
    }

    /**
     * Get a brief name (e.g. ticker symbol) for this non-fungible token class
     * @implSpec It is unspecified what language this text will be and no attempt is made to provide a localization feature
     * @return A string representing the brief name
     */
    @Callable
    public static String aip010Symbol() {
        return tokenSymbol;
    }

//todo: check google java style guide for ID stying (should it be ID/Id?) / also for URI/Uri
    /**
     * Get a URI for a specified token
     * @param tokenId A specific token to interrogate, greater than or equal to zero
     * @return A string representing the brief name
     */
    @Callable
    public static String aip010TokenURI(byte[] tokenId) {
        return tokenUriBase + new BigInteger(tokenId).toString();
    }

    /**
     * Get a URI for a specified token
     * @param tokenId A specific token to interrogate, greater than or equal to zero
     * @return A string representing the brief name
     */
    @Callable
    public static byte[] aip010TotalSupply() {
        byte[] totalSupply = Blockchain.getStorage(AIP010KeyValueStorage.totalSupplyKey());
        return totalSupply != null
                ? totalSupply
                : BigInteger.ZERO.toByteArray();
    }

    /**
     * The n-th token identifier
     * @implSpec It is guaranteed that calling this function for every value -- 0 <= index < totalSupply -- against one specific block will enumerate every token
     * @param index Which ordinal of token identifier you are seeking, 0 <= index < aip010TotalSupply
     * @return The token identifier for the n-th token in the list of all tokens
     */
    @Callable
    public static byte[] aip010TokenByIndex(byte[] index) {
//TODO: implement this
        return new byte[] {};
    }

    /**
     * The n-th token identifier for a given account
     * @implSpec It is guaranteed that calling this function for every value -- 0 <= index < totalSupply -- against one specific block will enumerate every token
     * @param owner The account which you are interrogaating owned tokens
     * @param index Which ordinal of token identifier you are seeking, 0 <= index < aip010BalanceOf(owner)
     * @return The token identifier for the n-th token in the list of all tokens
     */
    @Callable
    public static byte[] aip010TokenOfOwnerByIndex(Address owner, byte[] index) {
//TODO: implement this
        return new byte[] {};
    }



//endregion


//region Implementation details / these should be separated into a MOCK class that inherits from the reference implementation

    /**
     * Create a specified token and assign to an account
     * @implNote This implementation is permissive, anybody can create any token, production applications will likely be more restrictive
     * @param newOwner The new owner of the token after creating is complete
     * @param tokenId A specific token to create, greater than or equal to zero
     */
    @Callable
    public static void mint(Address newOwner, byte[] tokenId) {
//TODO: this code assumes it is impossible for AVM to call this contract using NULL address values. Validate that assumption.
        Blockchain.require(new BigInteger(tokenId).signum() >= 0);
//TODO: this assumes aip010OwnerOf is allowed to return null, doubleczech that        
        Blockchain.require(aip010OwnerOf(tokenId).equals(null));

        byte[] toBalance = Blockchain.getStorage(AIP010KeyValueStorage.ownerBalanceMapKey(newOwner));
        Blockchain.putStorage(AIP010KeyValueStorage.ownerBalanceMapKey(newOwner),
            new BigInteger(toBalance).add(BigInteger.valueOf(1)).toByteArray()
        );

        byte[] totalSupply = Blockchain.getStorage(AIP010KeyValueStorage.totalSupplyKey());
        Blockchain.putStorage(AIP010KeyValueStorage.totalSupplyKey(),
            new BigInteger(totalSupply).add(BigInteger.valueOf(1)).toByteArray()
        );

        Blockchain.putStorage(AIP010KeyValueStorage.tokenOwnersMapKey(tokenId),
            newOwner.toByteArray()
        );

        Blockchain.putStorage(AIP010KeyValueStorage.tokenConsigneeMapKey(tokenId),
            null
        );

        AIP010Events.AIP010Transferred(null, newOwner, new BigInteger(tokenId));
    }

//endsection

}