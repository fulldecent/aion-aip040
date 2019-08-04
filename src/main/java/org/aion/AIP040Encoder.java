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
public class AIP040Encoder {
    public static byte[] aip040OwnerOf(BigInteger tokenId) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040OwnerOf")
        .encodeOneByteArray(tokenId.toByteArray())
        .toBytes();
    }
    
    public static byte[] aip040ConsigneeOf(BigInteger tokenId) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040ConsigneeOf")
        .encodeOneByteArray(tokenId.toByteArray())
        .toBytes();
    }
    
    public static byte[] aip040IsAuthorized(Address owner, Address authorizee) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040IsAuthorized")
        .encodeOneAddress(owner)
        .encodeOneAddress(authorizee)
        .toBytes();
    }
    
    public static byte[] aip040BalanceOf(Address owner) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040BalanceOf")
        .encodeOneAddress(owner)
        .toBytes();
    }
    
    public static byte[] aip040Transfer(Address currentOwner, Address newOwner, BigInteger tokenId) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040Transfer")
        .encodeOneAddress(currentOwner)
        .encodeOneAddress(newOwner)
        .encodeOneByteArray(tokenId.toByteArray())
        .toBytes();
    }
    
    public static byte[] aip040Consign(Address owner, Address consignee, BigInteger tokenId) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040Consign")
        .encodeOneAddress(owner)
        .encodeOneAddress(consignee)
        .encodeOneByteArray(tokenId.toByteArray())
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
    
    public static byte[] aip040TokenURI(BigInteger tokenId) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040TokenURI")
        .encodeOneByteArray(tokenId.toByteArray())
        .toBytes();
    }
    
    public static byte[] aip040TotalSupply() {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040TotalSupply")
        .toBytes();
    }
    
    public static byte[] aip040TokenByIndex(BigInteger index) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040TokenByIndex")
        .encodeOneByteArray(index.toByteArray())
        .toBytes();
    }
    
    public static byte[] aip040TokenOfOwnerByIndex(Address owner, BigInteger index) {
        return new ABIStreamingEncoder()
        .encodeOneString("aip040TokenOfOwnerByIndex")
        .encodeOneAddress(owner)
        .encodeOneByteArray(index.toByteArray())
        .toBytes();
    }
}