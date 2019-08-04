package org.aion;

import avm.Address;
import avm.Blockchain;
import org.aion.avm.tooling.abi.Callable;
import org.aion.avm.userlib.abi.ABIDecoder;

import java.math.BigInteger;

//TODO: can @callable used in other files? this might remove the need for inheritance and boilerplate forwarding

/**
 * Reference implementation for the AIP-040 Non-Fungible Token Standard
 * 
 * It is intended that this class can be inherited for every implementation or extension of AIP-040.
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
        String tokenUriBase = decoder.decodeOneString();
        NFTokenMock.setTokenNameSymbolAndUriBase(tokenName, tokenSymbol, tokenUriBase);
    }


    //region NFToken standard implementation boilerplate
    // This attaches code from NFToken implementation to the blockchain callsite
    
    @Callable
    public static Address aip040OwnerOf(BigInteger tokenId) {
        return NFToken.aip040OwnerOf(tokenId);
    }
    
    @Callable
    public static Address aip040ConsigneeOf(BigInteger tokenId) {
        return NFToken.aip040ConsigneeOf(tokenId);
    }
    
    @Callable
    public static Boolean aip040IsAuthorized(Address owner, Address authorizee) {
        return NFToken.aip040IsAuthorized(owner, authorizee);
    }
    
    @Callable
    public static BigInteger aip040BalanceOf(Address owner) {
        return NFToken.aip040BalanceOf(owner);
    }
    
    @Callable
    public static void aip040Transfer(Address currentOwner, Address newOwner, BigInteger[] tokenIds) {
        NFToken.aip040Transfer(currentOwner, newOwner, tokenIds);
    }
    
    @Callable
    public static void aip040Consign(Address owner, Address consignee, BigInteger tokenId) {
        NFToken.aip040Consign(owner, consignee, tokenId);
    }
    
    @Callable
    public static void aip040Authorize(Address authorizee) {
        NFToken.aip040Authorize(authorizee);
    }
    
    @Callable
    public static void aip040Deauthorize(Address priorAuthorizee) {
        NFToken.aip040Deauthorize(priorAuthorizee);
    }
    
    @Callable
    public static String aip040Name() {
        return NFToken.aip040Name();
    }

    @Callable
    public static String aip040Symbol() {
        return NFToken.aip040Symbol();
    }

    @Callable
    public static String aip040TokenUri(BigInteger tokenId) {
        return NFToken.aip040TokenUri(tokenId);
    }
    
    @Callable
    public static BigInteger aip040TotalSupply() {
        return NFToken.aip040TotalSupply();
    }
    
    @Callable
    public static BigInteger aip040TokenAtIndex(BigInteger index) {
        return NFToken.aip040TokenAtIndex(index);
    }
    
    @Callable
    public static BigInteger aip040TokenOfOwnerAtIndex(Address owner, BigInteger index) {
        return NFToken.aip040TokenOfOwnerAtIndex(owner, index);
    }

    //endregion
    
  
    //region NFTokenMock additions boilerplate

    @Callable
    public static void mint(Address newOwner, BigInteger tokenId) {
        NFTokenMock.mint(newOwner, tokenId);
    }

    //endregion
}