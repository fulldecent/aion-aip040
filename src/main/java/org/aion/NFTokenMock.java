package org.aion;

import avm.Address;
import avm.Blockchain;
import java.math.BigInteger;

/**
 * This mock class includes additional functionality which will be useful for
 * some, if not all, AIP-040 implementations. This functionality is not
 * standardized so it is not included in the <code>NFToken</code> class.
 */
public class NFTokenMock extends NFToken {

    /**
     * This could be called once during contract deployment.
     * 
     * @implNote                 It is not expected that this will be exposed as
     *                           a callable method. So no encoder is provided
     *                           in <code>NFTokenMockEncoder</code>
     * @param    tokenName       this will be returned from aip040Name
     * @param    tokenSymbol     this will be returned from aip040Symbol
     * @param    tokenUriPrefix  this will be used in aip040TokenUri
     * @param    tokenUriPostfix this will be used in aip040TokenUri
     */
    // @NotCallable
    public static void setTokenNameSymbolAndUriAffixes(String tokenName, String tokenSymbol, String uriPrefix, String uriPostfix) {
        Blockchain.require(tokenName != null);
        Blockchain.require(tokenSymbol != null);
        Blockchain.require(uriPrefix != null);
        Blockchain.require(uriPostfix != null);
        NFTokenStorage.putTokenName(tokenName);
        NFTokenStorage.putTokenSymbol(tokenSymbol);
        NFTokenStorage.putTokenUriPrefix(uriPrefix);
        NFTokenStorage.putTokenUriPostfix(uriPostfix);
    }
    
    /**
     * Create a specified token and assign to an account.
     * 
     * @param newOwner The new owner of the token
     * @param tokenIds Token identifiers to create
     */
    public static void mint(Address newOwner, BigInteger[] tokenIds) {
        Blockchain.require(newOwner != null);
        Blockchain.require(tokenIds != null);

        BigInteger toBalance = aip040OwnerBalance(newOwner);

        for (BigInteger tokenId : tokenIds) {
            Blockchain.require(aip040TokenOwner(tokenId) == null);
            assert tokenId != null; // Confirmed on previous line
            BigInteger totalSupply = aip040TotalSupply();
            NFTokenStorage.putTokenAtIndex(totalSupply, tokenId);
            if (totalSupply == null) {
                totalSupply = BigInteger.ONE;
            } else {
                totalSupply = totalSupply.add(BigInteger.ONE);
            }
            
            NFTokenStorage.putTotalSupply(totalSupply);

            // Add to new owner array, O(1) algorithm
            NFTokenStorage.putTokensOfOwnerArray(newOwner, toBalance, tokenId);
            NFTokenStorage.putTokenLocation(tokenId, toBalance);
            if (toBalance == null) {
                toBalance = BigInteger.ONE;
            } else {
                toBalance = toBalance.add(BigInteger.ONE);
            }
    
            AIP040Events.AIP040Transferred(null, newOwner, tokenId);
        }
        NFTokenStorage.putOwnerBalance(newOwner, toBalance);
    }
}