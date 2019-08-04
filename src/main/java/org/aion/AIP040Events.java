package org.aion;

import avm.Address;
import avm.Blockchain;
import java.math.BigInteger;

public class AIP040Events {
    /**
     * Log event for transferring a token
     * @implSpec When a token is transferred, the consignee is also implicitly
     * unset (i.e. set to null).
     * @param priorOwner The account that was the owner of the token before
     *                   transfer (or null if token is being created)
     * @param newOwner The account that is the owner of the token after the
     *                 transfer (or null if token is being destroyed)
     * @param tokenId The identifier for the token which is being transferred,
     *                created or destroyed
     */
    protected static void AIP040Transferred(Address priorOwner, Address newOwner, BigInteger tokenId) {
        Blockchain.log("AIP040Transferred".getBytes(),
            priorOwner.toByteArray(),
            newOwner.toByteArray(),
            tokenId.toByteArray(),
            new byte[0]);
    }

    /**
     * Log event for consigning a token
     * @apiNote The owner parameter is duplicative because it could be looked up
     *          using the token identifier. It is included here because log
     *          events are indexed by topic and searching this log by owner is
     *          an expected use case.
     * @implSpec This log event is NOT fired during a transfer. All transfers
     *           result in unsetting the consignee and all transfers fire the
     *           transfer log event. Therefore, also firing the consignee log
     *           event at that time would be unhelpfully duplicative.
     * @param owner The current owner of the token which is being consigned
     * @param consignee The consignee being assigned to the token (or null if
     *                  consignement is being revoked)
     * @param tokenId The identifier for the token which is being consigned (or
     *                revoking consignment)
     */
    protected static void AIP040Consigned(Address owner, Address consignee, BigInteger tokenId) {
        Blockchain.log("AIP040Consigned".getBytes(),
            owner.toByteArray(),
            consignee.toByteArray(),
            tokenId.toByteArray(),
            new byte[0]);
    }

    /**
     * Log event for setting an authorized account of an account
     * @param account The account which will delegate authorization to another
     *                account
     * @param authorizee The account which receives the authorization
     */
    protected static void AIP040Authorized(Address account, Address authorizee) {
        Blockchain.log("AIP040Authorized".getBytes(),
            account.toByteArray(),
            authorizee.toByteArray(),
            new byte[0]);
    }

    /**
     * Log event for removing an authorized account of an account
     * @param account The account which will revoke authorization to another
     *                account
     * @param authorizee The account which loses the authorization
     */
    protected static void AIP040Deauthorized(Address account, Address priorAuthorizee) {
        Blockchain.log("AIP040Deauthorized".getBytes(),
            account.toByteArray(),
            priorAuthorizee.toByteArray(),
            new byte[0]);
    }
}