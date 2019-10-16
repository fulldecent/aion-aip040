package org.aion;

import avm.Address;
import avm.Blockchain;
import java.math.BigInteger;

/**
 * Reference implementation for the AIP-040 Non-Fungible Token Standard
 * 
 * It is intended that this class can be inherited for every implementation or
 * extension of AIP-040.
 * 
 * @author William Entriken
 */
public class NFToken {

    /**
     * Gets the descriptive name for this non-fungible token.
     * 
     * @apiSpec It is unspecified what language this text will be and no
     *          attempt is made to standardize a localization feature.
     * @return  the string representing the descriptive name
     */
    public static String aip040Name() {
        return NFTokenStorage.getTokenName();
    }

    /**
     * Gets the brief name (e.g. ticker symbol) for this non-fungible token.
     * 
     * @apiSpec It is unspecified what language this text will be and no
     *          attempt is made to standardize a localization feature.
     * @return  the string representing the brief name
     */
    public static String aip040Symbol() {
        return NFTokenStorage.getTokenSymbol();
    }

    /**
     * Returns the total quantity of tokens existing.
     * 
     * @return the count of all tokens existing
     */
    public static BigInteger aip040TotalSupply() {
        BigInteger storageTotalSupply = NFTokenStorage.getTotalSupply();
        return storageTotalSupply == null
            ? BigInteger.ZERO
            : storageTotalSupply;
    }
    
    /**
     * Returns the owner (if any) of a specific token.
     * 
     * @apiSpec All tokens that exist (i.e. they contribute to
     *          <code>aip040TotalSupply</code>) must have a non-null owner.
     * @param   tokenId the token we are interrogating
     * @return          the owner of the specified token, or null if token does
     *                  not exist
     */
    public static Address aip040TokenOwner(BigInteger tokenId) {
        Blockchain.require(tokenId != null);
        return NFTokenStorage.getTokenOwner(tokenId);
    }

    /**
     * Returns the account (if any) to which a certain token is consigned.
     * 
     * @apiNote         A consignee shall have permission to transfer the token.
     * @param   tokenId the token we are interrogating
     * @return          the consignee of the specified token, or null if none is
     *                  assigned or token does not exist
     */
    public static Address aip040TokenConsignee(BigInteger tokenId) {
        Blockchain.require(tokenId != null);
        return NFTokenStorage.getTokenConsignee(tokenId);
    }
    
    /**
     * Returns the URI for a specified token.
     * 
     * @apiSpec         Every token in a contract must have a unique URI.
     * @param   tokenId a specific token to interrogate
     * @return          the URI for the specified token
     * @see             RFC 3986
     */
    public static String aip040TokenUri(BigInteger tokenId) {
        Blockchain.require(tokenId != null);
        if (NFTokenStorage.getTokenOwner(tokenId) == null) {
            return null;
        }
        String uriPrefix = NFTokenStorage.getTokenUriPrefix();
        String uriPostfix = NFTokenStorage.getTokenUriPostfix();
        if (uriPostfix == null){
            uriPostfix = "";
        }
        if (uriPrefix == null){
            uriPrefix = "";
        }
        return uriPrefix + tokenId.toString() + uriPostfix;
    }
    
    /**
     * Returns the count of tokens owned by a specified account.
     * 
     * @param  owner a specific account to interrogate
     * @return       the count of tokens owned by the specified account
     */
    public static BigInteger aip040OwnerBalance(Address owner) {
        Blockchain.require(owner != null);
        BigInteger storageBalance = NFTokenStorage.getOwnerBalance(owner);
        return storageBalance == null
            ? BigInteger.ZERO
            : storageBalance;
    }

    /**
     * Find whether an owner has authorized access to an authorizee.
     * 
     * @param  owner      the owner account to interrogate
     * @param  authorizee the authorizee account to interrogate
     * @return            true if authorization is active, false otherwise
     */
    public static boolean aip040OwnerDoesAuthorize(Address owner, Address authorizee) {
        Blockchain.require(owner != null);
        Blockchain.require(authorizee != null);
        return NFTokenStorage.getAccountAuthorization(owner, authorizee);
    }

    /**
     * Returns the n-th token identifier.
     * 
     * @apiSpec       Calling this method for every value, 0 <= index <
     *                <code>aip040TotalSupply()</code> on a finalized block will
     *                enumerate every token.
     * @apiNote       The order and stability of ordering of tokens is not
     *                specified.
     * @param   index the specified ordinal of token, 0 <= index <
     *                <code>aip040TotalSupply()</code>
     * @return        the token identifier for the n-th token in a list of all
     *                tokens
     */
    public static BigInteger aip040TokenAtIndex(BigInteger index) {
        Blockchain.require(index != null);
        Blockchain.require(index.compareTo(aip040TotalSupply()) < 0);
        return NFTokenStorage.getTokenAtIndex(index);
    }

    /**
     * Returns the n-th token identifier for a given account.
     * 
     * @apiSpec       Calling this method for every value, 0 <= index <
     *                <code>aip040TotalSupply()</code> on a finalized block will
     *                enumerate every token for the specified account.
     * @param   owner the account to interrogate
     * @param   index the specified ordinal of token, 0 <= index <
     *                <code>aip040TotalSupply()</code>
     * @return        the token identifier for the n-th token in a list of all
     *                tokens of the specified owner
     */
    public static BigInteger aip040TokenForOwnerAtIndex(Address owner, BigInteger index) {
        Blockchain.require(owner != null);
        Blockchain.require(index != null);
        Blockchain.require(index.compareTo(BigInteger.ZERO) >= 0);
        Blockchain.require(index.compareTo(aip040OwnerBalance(owner)) < 0);
        return NFTokenStorage.getTokensOfOwnerArray(owner, index);
    }

    /**
     * Transfer specified tokens to the caller, if permitted.
     * 
     * @apiSpec              The implementation must revert if
     *                       <code>currentOwner</code> does not match the
     *                       actual owner of the specified tokens. This prevents
     *                       a race condition due to non-linear settlement of
     *                       transactions on blockchain.
     * @apiSpec              The implementation must revert if the caller is not
     *                       the current owner, is not authorized by the current
     *                       owner and is not the current consignee of the
     *                       token.
     * @apiSpec              The implementation may revert in other
     *                       circumstances and could depend on whether the
     *                       contract is paused or the outcome of the last
     *                       Phillies vs. Blue Jays game.
     * @apiSpec              This method revokes any consignee for each
     *                       specified token.
     * @apiSpec              Either all transfers and specifications are
     *                       followed or the transactions reverts. Partial
     *                       settlement is not allowed.
     * @param   currentOwner the current owner of all the specified tokens
     * @param   tokenIds     specific tokens to transfer
     */
    public static void aip040TakeOwnership(Address currentOwner, BigInteger[] tokenIds) {
        Blockchain.require(currentOwner != null);
        Blockchain.require(tokenIds != null);
        Address caller = Blockchain.getCaller();

        BigInteger fromBalance = NFToken.aip040OwnerBalance(currentOwner);
        BigInteger toBalance = NFToken.aip040OwnerBalance(caller);
        boolean isAuthorized = caller.equals(currentOwner) || aip040OwnerDoesAuthorize(currentOwner, caller);

        for (BigInteger tokenId : tokenIds) {
            Blockchain.require(aip040TokenOwner(tokenId).equals(currentOwner));
            // assert tokenId != null; // aip040TokenOwner throws if tokenId is null
            Blockchain.require(isAuthorized || aip040TokenConsignee(tokenId).equals(caller));
            NFTokenStorage.putTokenConsignee(tokenId, null);
            NFTokenStorage.putTokenOwner(tokenId, caller);

            // General O(1) algorithm to remove an item from an ordered array:
            //   1. Know where the value because it is indexed
            //   2. Copy the last item over the value to be removed
            //   3. Shrink the array

            // Remove from old owner array, O(1) algorithm
            BigInteger tokenToRemoveLocation = NFTokenStorage.getTokenLocation(tokenId);
            BigInteger lastTokenLocation = fromBalance.subtract(BigInteger.ONE);
            if (!lastTokenLocation.equals(tokenToRemoveLocation)) {
                BigInteger lastToken = NFTokenStorage.getTokensOfOwnerArray(currentOwner, lastTokenLocation);
                NFTokenStorage.putTokensOfOwnerArray(currentOwner, tokenToRemoveLocation, lastToken);
                NFTokenStorage.putTokenLocation(tokenId, tokenToRemoveLocation);
            }
            fromBalance = fromBalance.subtract(BigInteger.ONE);

            // Add to new owner array, O(1) algorithm
            NFTokenStorage.putTokensOfOwnerArray(caller, toBalance, tokenId);
            NFTokenStorage.putTokenLocation(tokenId, toBalance);
            NFTokenStorage.putTokenOwner(tokenId, caller);
            if (toBalance == null) {
                toBalance = BigInteger.ONE;
            } else {
                toBalance = toBalance.add(BigInteger.ONE);
            }
    
            AIP040Events.AIP040Transferred(currentOwner, caller, tokenId);
        }
        if (!caller.equals(currentOwner)) {
            NFTokenStorage.putOwnerBalance(currentOwner, fromBalance.signum() == 0 ? null : fromBalance);
            NFTokenStorage.putOwnerBalance(caller, toBalance);    
        }
    }

    /**
     * Consigns specified tokens to an account, or revokes an existing
     * consignment. Reverts if not permitted. Permission is specified iff the
     * caller is the token owner or the owner does authorize the caller.
     * 
     * @apiSpec         The implementation must revert if
     *                  <code>currentOwner</code> does not match the actual
     *                  owner of the specified tokens. This prevents a race
     *                  condition due to non-linear settlement of transactions
     *                  on blockchain.
     * @param owner     the account that currently owns the specified tokens
     * @param consignee the account to consign to, or null to revoke consignment
     * @param tokenId   the token to consign
     */
    public static void aip040Consign(Address owner, Address consignee, BigInteger[] tokenIds) {
        Blockchain.require(owner != null);
        Blockchain.require(tokenIds != null);
        Address caller = Blockchain.getCaller();
        Blockchain.require(
            caller.equals(owner) ||
            aip040OwnerDoesAuthorize(owner, caller)
        );

        for (BigInteger tokenId : tokenIds) {
            Blockchain.require(aip040TokenOwner(tokenId).equals(owner));
            // assert tokenId != null; // aip040TokenOwner throws if tokenId is null
            NFTokenStorage.putTokenConsignee(tokenId, consignee);
            AIP040Events.AIP040Consigned(owner, consignee, tokenId);    
        }
    }

    /**
     * Authorizes an account to consign tokens on behalf of the caller or to
     * take any token owned by the caller.
     * 
     * @param authorizee the account which receives authorization
     */
    public static void aip040Authorize(Address authorizee) {
        Blockchain.require(authorizee != null);
        Address caller = Blockchain.getCaller();
        NFTokenStorage.putAccountAuthorization(caller, authorizee, true);
        AIP040Events.AIP040Authorized(caller, authorizee);
    }

    /**
     * Deauthorizes an account to consign tokens on behalf of the caller or to
     * take any token owned by the caller.
     *
     * @param authorizee the account which is revoked authorization
     */
    public static void aip040Deauthorize(Address priorAuthorizee) {
        Blockchain.require(priorAuthorizee != null);
        Address caller = Blockchain.getCaller();
        NFTokenStorage.putAccountAuthorization(caller, priorAuthorizee, false);
        AIP040Events.AIP040Deauthorized(caller, priorAuthorizee);
    }

}