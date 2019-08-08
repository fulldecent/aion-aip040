package org.aion;

import avm.Address;
import java.math.BigInteger;
import org.aion.avm.userlib.abi.ABIStreamingEncoder;

/**
* The methods in this class are fully described by the standard subset of the
* ABI file ATS-1.0-SNAPSHOT.abi. In the future, tooling may automatically
* generate this file from that.
* 
* @see https://github.com/aionnetwork/AVM/issues/401
*/
public class NFTokenMockEncoder extends AIP040Encoder {
    public static byte[] mint(Address newOwner, BigInteger tokenId) {
        return new ABIStreamingEncoder()
        .encodeOneString("mint")
        .encodeOneAddress(newOwner)
        .encodeOneBigInteger(tokenId)
        .toBytes();
    }
}