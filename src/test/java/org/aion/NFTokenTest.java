package org.aion;

import avm.Address;
import org.aion.avm.core.util.LogSizeUtils;
import org.aion.avm.tooling.AvmRule;

import org.aion.avm.userlib.AionBuffer;
import org.aion.vm.api.interfaces.IExecutionLog;
import org.aion.vm.api.interfaces.ResultCode;
//TODO: fx this next line, it is not best practice
import org.junit.*;


import java.math.BigInteger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

//TODO: need to get 100% coverage and confirm that we have 100% in the travis test

public class NFTokenTest {
    @Rule
    public AvmRule avmRule = new AvmRule(true);

    // The default address is preloaded with some energy
    private Address deployer =  avmRule.getPreminedAccount();
    private Address contractAddress;

    private String tokenName = "Planets";
    private String tokenSymbol = "PL";
    private String tokenUriBase = "https://example.com/ownershipOfPlanets/";

    @Before
    public void deployDapp() {
        byte[] data = MainEncoder.deploy(tokenName, tokenSymbol, tokenUriBase);
        byte[] contractData = avmRule.getDappBytes(Main.class, data, AIP040Events.class, NFToken.class, NFTokenMock.class, NFTokenStorage.class, BigInteger.class);
        contractAddress = avmRule.deploy(deployer, BigInteger.ZERO, contractData).getDappAddress();
    }

    @Test
    public void testInitialization() {

        System.out.print("Hello World !");
        AVMBlockchainWrapper wrapper = new AVMBlockchainWrapper();

        byte[] callData = AIP040Encoder.aip040Name();
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, callData);
        String resStr = (String) result.getDecodedReturnData();
        Assert.assertTrue(resStr.equals(tokenName));

        callData = AIP040Encoder.aip040Symbol();
        result = avmRule.call(deployer,contractAddress, BigInteger.ZERO, callData);
        resStr = (String) result.getDecodedReturnData();
        Assert.assertTrue(resStr.equals(tokenSymbol));

        callData = AIP040Encoder.aip040TotalSupply();
        result = avmRule.call(deployer,contractAddress, BigInteger.ZERO, callData);
        byte[] resBytes = (byte[]) result.getDecodedReturnData();
        Assert.assertTrue(new BigInteger(resBytes).equals(BigInteger.ZERO));

        callData = AIP040Encoder.aip040OwnerBalance(deployer);
        result = avmRule.call(deployer,contractAddress, BigInteger.ZERO, callData);
        resBytes = (byte[]) result.getDecodedReturnData();
        Assert.assertTrue(new BigInteger(resBytes).equals(BigInteger.ZERO));
    }

}
