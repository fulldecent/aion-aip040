package org.aion;
import avm.Address;

import org.aion.avm.core.util.LogSizeUtils;
import org.aion.avm.embed.AvmRule;
import org.aion.avm.userlib.AionBuffer;
import org.aion.avm.userlib.abi.ABIStreamingEncoder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class NFTokenTest {
    @Rule
    public AvmRule avmRule = new AvmRule(true);

    private Address tokenOwner = avmRule.getPreminedAccount();//Account deploys the token contract.
    private Address contractAddress;

    private BigInteger nAmp = BigInteger.valueOf(1_000_000_000_000_000_000L);
    private String tokenName = "JENNIJUJU";
    private String tokenSymbol = "J3N";
    private String tokenUriPrefix= "0x3";
    private String tokenUriPostfix= "JEN";

    @Before
    public void deployDapp() {
        ABIStreamingEncoder encoder = new ABIStreamingEncoder();
        byte[] data = encoder.encodeOneString(tokenName)
            .encodeOneString(tokenSymbol)
            .encodeOneString(tokenUriPrefix)
            .encodeOneString(tokenUriPostfix)
            .toBytes();
        byte[] contractData = avmRule.getDappBytes(Main.class, data, 1, AIP040Encoder.class, AIP040Events.class, AVMBlockchainWrapper.class, NFToken.class, NFTokenMock.class, NFTokenMockEncoder.class, NFTokenStorage.class);
        AvmRule.ResultWrapper deployedContract =  avmRule.deploy(tokenOwner, BigInteger.ZERO, contractData);
        contractAddress = deployedContract.getDappAddress();
        long energy =  deployedContract.getTransactionResult().energyUsed;
        System.out.println("Deployment energy cost: " + energy);
    }


    @Test
    public void testInitialization() {
        ABIStreamingEncoder encoder = new ABIStreamingEncoder();
        AvmRule.ResultWrapper result = avmRule.call(tokenOwner, contractAddress, BigInteger.ZERO, encoder.encodeOneString("aip040Name").toBytes());
        String resStr = (String) result.getDecodedReturnData();
        Assert.assertTrue(resStr.equals("JENNIJUJU"));

        result = avmRule.call(tokenOwner, contractAddress, BigInteger.ZERO, encoder.encodeOneString("aip040Symbol").toBytes());
        resStr = (String) result.getDecodedReturnData();
        Assert.assertTrue(resStr.equals("J3N"));
        

    }

//    @Test
//    public void testaip040TotalSupply() {
//        ABIStreamingEncoder encoder = new ABIStreamingEncoder();
//        AvmRule.ResultWrapper result = avmRule.call(tokenOwner, contractAddress, BigInteger.ZERO, encoder.encodeOneString("aip040TotalSupply").toBytes());
//        BigInteger resBI= (BigInteger) result.getDecodedReturnData();
//        Assert.assertTrue(resBI.compareTo(BigInteger.ZERO) == 0);
//    }
}
