package org.aion;

import avm.Address;
import avm.Blockchain;
import java.math.BigInteger;

/**
 * This mock class includes additional functionality which will be useful for
 * some, if not all, AIP #040 implementations. This functionality is not
 * standardized so it is not included in the <code>NFToken</code> class.
 */
public class NFTokenMock {

    /**
     * This could be called once during contract deployment
     * @param tokenName This will be returned from aip040Name
     * @param tokenSymbol This will be returned from aip040Symbol
     * @param tokenUriBase This will be used in aip040TokenUri
     */
    public static void setTokenNameSymbolAndUriBase(String tokenName, String tokenSymbol, String uriBase) {
        Blockchain.require(tokenName != null);
        Blockchain.require(tokenSymbol != null);
        Blockchain.require(uriBase != null);
        Blockchain.require(tokenName.length() > 0);
        Blockchain.require(tokenSymbol.length() > 0);
        Blockchain.require(uriBase.length() > 0);
        NFTokenStorage.putTokenName(tokenName);
        NFTokenStorage.putTokenSymbol(tokenSymbol);
        NFTokenStorage.putTokenUriBase(tokenSymbol);
    }
    
    /**
     * Create a specified token and assign to an account
     * @param newOwner The new owner of the token
     * @param tokenId A specific token to create
     */
    public static void mint(Address newOwner, BigInteger tokenId) {
        Blockchain.require(newOwner != null);
        Blockchain.require(tokenId != null);
        Blockchain.require(NFToken.aip040OwnerOf(tokenId) == null);

        BigInteger toBalance = NFToken.aip040BalanceOf(newOwner);
        toBalance = toBalance.add(BigInteger.ONE);
        NFTokenStorage.putOwnerBalance(newOwner, toBalance);

        BigInteger totalSupply = NFToken.aip040TotalSupply();
        totalSupply = totalSupply.add(BigInteger.ONE);
        NFTokenStorage.putTotalSupply(totalSupply);

//TODO: add to total list of all tokens
//TODO: add to list of tokens by owner

        NFTokenStorage.putTokenOwner(tokenId, newOwner);
        NFTokenStorage.putTokenConsignee(tokenId, null);
        AIP040Events.AIP040Transferred(null, newOwner, tokenId);
    }
}