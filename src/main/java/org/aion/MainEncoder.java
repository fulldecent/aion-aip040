package org.aion;

import org.aion.avm.userlib.abi.ABIStreamingEncoder;

/**
 * The methods in this class are fully described by the standard subset of the
 * ABI file ATS-1.0-SNAPSHOT.abi. In the future, tooling may automatically
 * generate this file from that.
 * 
 * @see https://github.com/aionnetwork/AVM/issues/401
 */
public class MainEncoder {
//TODO: what should this function be named?
    public static byte[] deploy(String tokenName, String tokenSymbol, String tokenUriBase) {
        return new ABIStreamingEncoder()
            .encodeOneString(tokenName)
            .encodeOneString(tokenSymbol)
            .encodeOneString(tokenUriBase)
            .toBytes();
    }
}