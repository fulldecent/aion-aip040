package org.aion;

import avm.Address;
import java.math.BigInteger;
import org.aion.avm.userlib.abi.ABIStreamingEncoder;

/**
 * People wishing to interact with a deployed contract that conforms to AIP-040
 * should use this encoder.
 * 
 * This class is fully described by the AIP-040 ABI. In the future, tooling may
 * automatically generate this file from that.
 * 
 * @see https://github.com/aionnetwork/AVM/issues/401
 */
public class AIP040Encoder {

    public static byte[] aip040Name() {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040Name")
        .toBytes();
    }

    public static byte[] aip040Symbol() {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040Symbol")
        .toBytes();
    }

    public static byte[] aip040TotalSupply() {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040TotalSupply")
        .toBytes();
    }

    public static byte[] aip040TokenOwner(BigInteger tokenId) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040TokenOwner")
        .encodeOneByteArray(tokenId.toByteArray())
        .toBytes();
    }

    public static byte[] aip040TokenConsignee(BigInteger tokenId) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040TokenConsignee")
        .encodeOneByteArray(tokenId.toByteArray())
        .toBytes();
    }

    public static byte[] aip040TokenUri(BigInteger tokenId) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040TokenUri")
        .encodeOneByteArray(tokenId.toByteArray())
        .toBytes();
    }

    public static byte[] aip040OwnerBalance(Address owner) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040OwnerBalance")
        .encodeOneAddress(owner)
        .toBytes();
    }

    public static byte[] aip040OwnerDoesAuthorize(Address owner, Address authorizee) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040OwnerDoesAuthorize")
        .encodeOneAddress(owner)
        .encodeOneAddress(authorizee)
        .toBytes();
    }

    public static byte[] aip040TokenAtIndex(BigInteger index) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040TokenAtIndex")
        .encodeOneByteArray(index.toByteArray())
        .toBytes();
    }

    public static byte[] aip040TokenForOwnerAtIndex(Address owner, BigInteger index) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040TokenForOwnerAtIndex")
        .encodeOneAddress(owner)
        .encodeOneByteArray(index.toByteArray())
        .toBytes();
    }

    public static byte[] aip040TakeOwnership(Address currentOwner, BigInteger[] tokenIds) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040TakeOwnership")
        .encodeOneAddress(currentOwner)
        .encodeOneBigIntegerArray(tokenIds)
        .toBytes();
    }

    public static byte[] aip040Consign(Address owner, Address consignee, BigInteger[] tokenIds) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040Consign")
        .encodeOneAddress(owner)
        .encodeOneAddress(consignee)
        .encodeOneBigIntegerArray(tokenIds)
        .toBytes();

    }

    public static byte[] aip040Authorize(Address authorizee) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040Authorize")
        .encodeOneAddress(authorizee)
        .toBytes();

    }

    public static byte[] aip040Deauthorize(Address priorAuthorizee) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040Deauthorize")
        .encodeOneAddress(priorAuthorizee)
        .toBytes();
    }
}