package org.aion;

import avm.Address;
import avm.Blockchain;
import java.math.BigInteger;
import org.aion.avm.userlib.AionBuffer;
//import org.aion.avm.core.util;

public class AIP040Events {

    /**
     * Log event for creating a token
     * 
     * @apiSpec          This event must be emitted for each token created.
     * @apiSpec          The tokenId is padded to 32-bytes using signed padding.
     * @param newOwner   the account that is the owner of the token after the
     *                   creation
     * @param tokenId    the identifier for the token which is being created
     */
    protected static void AIP040Minted(Address newOwner, BigInteger tokenId) {
        Blockchain.log("AIP040Minted".getBytes(),
            newOwner.toByteArray(),
            encodeBigInteger32Bytes(tokenId),
            new byte[0]);
    }

    /**
     * Log event for burning a token
     * 
     * @apiSpec          This event must be emitted for each token destroyed.
     * @apiSpec          The tokenId is padded to 32-bytes using signed padding.
     * @param newOwner   the account that is the owner of the token after the
     *                   destruction
     * @param tokenId    the identifier for the token which is being destroyed
     */
    protected static void AIP040Burned(Address newOwner, BigInteger tokenId) {
        Blockchain.log("AIP040Destroyed".getBytes(),
            newOwner.toByteArray(),
            encodeBigInteger32Bytes(tokenId),
            new byte[0]);
    }

    /**
     * Log event for transferring a token
     * 
     * @apiSpec          When a token is transferred, the consignee is also
     *                   implicitly unset (i.e. set to null).
     * @apiSpec          This event must be emitted for each token transferred,
     *                   even if the transfer was a result of other than
     *                   <code>aip040TakeOwnership</code>.
     * @apiSpec          The tokenId is padded to 32-bytes using signed padding.
     * @param priorOwner the account that was the owner of the token before
     *                   transfer
     * @param newOwner   the account that is the owner of the token after the
     *                   transfer
     * @param tokenId    the identifier for the token which is being transferred
     */
    protected static void AIP040Transferred(Address priorOwner, Address newOwner, BigInteger tokenId) {
        Blockchain.log("AIP040Transferred".getBytes(),
            priorOwner == null ? null : priorOwner.toByteArray(),
            newOwner == null ? null : newOwner.toByteArray(),
            encodeBigInteger32Bytes(tokenId),
            new byte[0]);
    }

    /**
     * Log event for consigning a token
     * 
     * @apiNote         The owner parameter is duplicative because it could be
     *                  looked up using the token identifier. It is included
     *                  here because log events are indexed by topic and
     *                  searching this log by owner is an expected use case.
     * @apiSpec         This log event is NOT fired during a transfer. All
     *                  transfers result in unsetting the consignee and all
     *                  transfers fire the transfer log event. Therefore, also
     *                  firing the consignee log event at that time would be
     *                  unhelpfully duplicative.
     * @apiSpec         The tokenId is padded to 32-bytes using signed padding.
     * @param owner     the current owner of the token which is being consigned
     * @param consignee the consignee being assigned to the token (or null if
     *                  consignment is being revoked)
     * @param tokenId   the identifier for the token which is being consigned (or
     *                  revoking consignment)
     */
    protected static void AIP040Consigned(Address owner, Address consignee, BigInteger tokenId) {
        Blockchain.log("AIP040Consigned".getBytes(),
            owner.toByteArray(),
            consignee.toByteArray(),
            encodeBigInteger32Bytes(tokenId),
            new byte[0]);
    }

    /**
     * Log event for setting an authorized account of an account
     * 
     * @param account    the account which will delegate authorization to
     *                   another account
     * @param authorizee the account which receives the authorization
     */
    protected static void AIP040Authorized(Address account, Address authorizee) {
        Blockchain.log("AIP040Authorized".getBytes(),
            account.toByteArray(),
            authorizee.toByteArray(),
            new byte[0]);
    }

    /**
     * Log event for removing an authorized account of an account
     * 
     * @param account    the account which will revoke authorization from
     *                   another account
     * @param authorizee the account which loses the authorization
     */
    protected static void AIP040Deauthorized(Address account, Address priorAuthorizee) {
        Blockchain.log("AIP040Deauthorized".getBytes(),
            account.toByteArray(),
            priorAuthorizee.toByteArray(),
            new byte[0]);
    }

    // Aion will truncate + pad log topics to 32 bytes, this makes negative
    // BigIntegers smaller than 32 bytes indistinguishable from similar positive
    // integers which are similar (MOD 2^bits).
    //TODO: Add reference, this may be unspecified Aion behavior
    private static byte[] encodeBigInteger32Bytes(BigInteger input) {
        return AionBuffer.allocate(/*LogSizeUtils.TOPIC_SIZE*/ 32).put32ByteInt(input).getArray();
    }
}