package org.aion;

import org.aion.avm.userlib.abi.ABIStreamingEncoder;

/**
 * People wishing to deploy this example should use this encoder.
 *
 * This class is fully described by a portion of the compiled contract ABI. In
 * the future, tooling may automatically generate this file from that.
 * 
 * @see https://github.com/aionnetwork/AVM/issues/401
 */
public class MainEncoder {
    public static byte[] deploy(String tokenName, String tokenSymbol, String tokenUriPrefix, String tokenUriPostfix) {
        return new ABIStreamingEncoder()
            .encodeOneString(tokenName)
            .encodeOneString(tokenSymbol)
            .encodeOneString(tokenUriPrefix)
            .encodeOneString(tokenUriPostfix)
            .toBytes();
    }
}