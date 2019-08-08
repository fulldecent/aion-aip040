package org.aion;

import avm.Address;
import avm.Blockchain;
import org.aion.avm.tooling.abi.Callable;
import org.aion.avm.userlib.abi.ABIDecoder;

import java.math.BigInteger;

/**
 * Reference implementation for the AIP-040 Non-Fungible Token Standard
 * 
 * It is intended that this class can be inherited for every implementation or
 * extension of AIP-040.
 * 
 * @author William Entriken
 */
public class Main {

    /**
     * This initialization is run at deploy time.
     */
    static {
        ABIDecoder decoder = new ABIDecoder(Blockchain.getData());
        String tokenName = decoder.decodeOneString();
        String tokenSymbol = decoder.decodeOneString();
        String tokenUriPrefix = decoder.decodeOneString();
        String tokenUriPostfix = decoder.decodeOneString();
        NFTokenMock.setTokenNameSymbolAndUriAffixes(tokenName, tokenSymbol, tokenUriPrefix, tokenUriPostfix);
    }

    //region NFToken standard implementation boilerplate
    // This attaches code from NFToken implementation to the blockchain callsite
    
    @Callable
    public static String aip040Name() {
        return NFTokenMock.aip040Name();
    }
    
    @Callable
    public static String aip040Symbol() {
        return NFTokenMock.aip040Symbol();
    }
    
    @Callable
    public static BigInteger aip040TotalSupply() {
        return NFTokenMock.aip040TotalSupply();
    }
    
    @Callable
    public static Address aip040TokenOwner(BigInteger tokenId) {
        return NFTokenMock.aip040TokenOwner(tokenId);
    }
    
    @Callable
    public static Address aip040TokenConsignee(BigInteger tokenId) {
        return NFTokenMock.aip040TokenConsignee(tokenId);
    }
    
    @Callable
    public static String aip040TokenUri(BigInteger tokenId) {
        return NFTokenMock.aip040TokenUri(tokenId);
    }
    
    @Callable
    public static BigInteger aip040OwnerBalance(Address owner) {
        return NFTokenMock.aip040OwnerBalance(owner);
    }
    
    @Callable
    public static boolean aip040OwnerDoesAuthorize(Address owner, Address authorizee) {
        return NFTokenMock.aip040OwnerDoesAuthorize(owner, authorizee);
    }
    
    @Callable
    public static BigInteger aip040TokenAtIndex(BigInteger index) {
        return NFTokenMock.aip040TokenAtIndex(index);
    }
    
    @Callable
    public static BigInteger aip040TokenForOwnerAtIndex(Address owner, BigInteger index) {
        return NFTokenMock.aip040TokenForOwnerAtIndex(owner, index);
    }
    
    @Callable
    public static void aip040TakeOwnership(Address currentOwner, BigInteger[] tokenIds) {
        NFTokenMock.aip040TakeOwnership(currentOwner, tokenIds);
    }
    
    @Callable
    public static void aip040Consign(Address owner, Address consignee, BigInteger[] tokenIds) {
        NFTokenMock.aip040Consign(owner, consignee, tokenIds);
    }
    
    @Callable
    public static void aip040Authorize(Address authorizee) {
        NFTokenMock.aip040Authorize(authorizee);
    }
    
    @Callable
    public static void aip040Deauthorize(Address priorAuthorizee) {
        NFTokenMock.aip040Deauthorize(priorAuthorizee);
    }
    
    //endregion
    
  
    //region NFTokenMock additions boilerplate

    @Callable
    public static void mint(Address newOwner, BigInteger tokenIds[]) {
        NFTokenMock.mint(newOwner, tokenIds);
    }

    //endregion
}