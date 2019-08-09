package org.aion;

import avm.Address;
import java.math.BigInteger;

/**
 * Reference implementation for the AIP-040 Non-Fungible Token Standard
 * 
 * It is intended that this interface can be inherited for every implementation
 * or extension of AIP-040.
 * 
 * @author William Entriken
 */
public abstract class AIP040 {

  /**
   * Gets the descriptive name for this non-fungible token.
   * 
   * @apiSpec It is unspecified what language this text will be and no
   *          attempt is made to standardize a localization feature.
   * @return  the string representing the descriptive name
   */
  public abstract static String aip040Name();

  /**
   * Gets the brief name (e.g. ticker symbol) for this non-fungible token.
   * 
   * @apiSpec It is unspecified what language this text will be and no
   *          attempt is made to standardize a localization feature.
   * @return  the string representing the brief name
   */
  static String aip040Symbol() {}

  /**
   * Returns the total quantity of tokens existing.
   * 
   * @return the count of all tokens existing
   */
  static BigInteger aip040TotalSupply() {}
  
  /**
   * Returns the owner (if any) of a specific token.
   * 
   * @apiSpec The owner must not be null if the token exists (i.e. it
   *          contributes to <code>aip040TotalSupply</code>).
   * @param   tokenId the token we are interrogating
   * @return          the owner of the specified token, or null if token does
   *                  not exist
   */
  static Address aip040TokenOwner(BigInteger tokenId) {}

  /**
   * Returns the account (if any) to which a certain token is consigned.
   * 
   * @apiNote         A consignee shall have permission to transfer the token.
   * @param   tokenId the token we are interrogating
   * @return          the consignee of the specified token, or null if none is
   *                  assigned or token does not exist
   */
  static Address aip040TokenConsignee(BigInteger tokenId) {}
  
  /**
   * Returns the URI for a specified token.
   * 
   * @apiSpec         Every token in a contract must have a unique URI.
   * @param   tokenId a specific token to interrogate
   * @return          the URI for the specifed token
   * @see             RFC 3986
   */
  static String aip040TokenUri(BigInteger tokenId) {}
  
  /**
   * Returns the count of tokens owned by a specified account.
   * 
   * @param  owner a specific account to interrogate
   * @return       the count of tokens owned by the specified account
   */
  static BigInteger aip040OwnerBalance(Address owner) {}

  /**
   * Find whether an owner has authorized access to an authorizee.
   * 
   * @param  owner      the owner account to interrogate
   * @param  authorizee the authorizee account to interrogate
   * @return            true if authorization is active, false otherwise
   */
  static Boolean aip040OwnerDoesAuthorize(Address owner, Address authorizee) {}

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
  static BigInteger aip040TokenAtIndex(BigInteger index) {}

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
   *                tokens of the speciifed owner
   */
  static BigInteger aip040TokenForOwnerAtIndex(Address owner, BigInteger index) {}

  /**
   * Transfer specified tokens to the caller, if permitted.
   * 
   * @apiSpec              The implementation must revert if
   *                       <code>currentOwner</code> does not match the
   *                       actual owner of the specified tokens. This prevents
   *                       a race condition due to non-linear settlement of
   *                       transactions on blockchain.
   * @apiSpec              The permissibility of transfers is not standardized
   *                       and could depend on the outcome of the last
   *                       Phillies vs. Blue Jays game. Only the intentions of
   *                       actors interacting with the contract are
   *                       communicated in a standardized vocabulary.
   * @apiSpec              This method revokes any consignee for each
   *                       specified token.
   * @apiSpec              Either all transfers are successful or the
   *                       transactions reverts. Partial settlement is not
   *                       allowed.
   * @apiNote              The transfer should be permitted if the caller is
   *                       the current owner, the consignee or is authorized
   *                       by the owner.
   * @param   currentOwner the current owner of all the specified tokens
   * @param   tokenIds     specific tokens to transfer
   */
  static void aip040TakeOwnership(Address currentOwner, BigInteger[] tokenIds) {}

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
  static void aip040Consign(Address owner, Address consignee, BigInteger[] tokenIds) {}

  /**
   * Authorizes an account to consign tokens on behalf of the caller or to
   * take any token owned by the caller.
   * 
   * @param authorizee the account which receives authorization
   */
  static void aip040Authorize(Address authorizee) {}

  /**
   * Deauthorizes an account to consign tokens on behalf of the caller or to
   * take any token owned by the caller.
   *
   * @param authorizee the account which is revoked authorization
   */
  static void aip040Deauthorize(Address priorAuthorizee) {}

}