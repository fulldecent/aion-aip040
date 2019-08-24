package org.aion;

import avm.Address;
import org.aion.avm.core.util.LogSizeUtils;
import org.aion.avm.embed.AvmRule;

import org.aion.avm.userlib.abi.ABIStreamingEncoder;
//TODO: fx this next line, it is not best practice
import org.junit.*;

import java.math.BigInteger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

//TODO: need to get 100% coverage and confirm that we have 100% in the travis test
// See: https://github.com/fulldecent/aion-aip040/issues/7

public class NFTokenTest {


    @Rule
    public AvmRule avmRule = new AvmRule(true);

    // The default address is preloaded with some energy
    private Address deployer =  avmRule.getPreminedAccount();
    private Address contractAddress;

    private String tokenName = "Planets";
    private String tokenSymbol = "PL";
    private String tokenUriPrefix = "pre";
    private String tokenUriPostfix = "post";

    private BigInteger balance = BigInteger.valueOf(1_000_000_000_000_000_000L).multiply(BigInteger.TEN);
    @Before
    public void deployDapp() {

        byte[] data = MainEncoder.deploy(tokenName, tokenSymbol, tokenUriPrefix, tokenUriPostfix);
        byte[] contractData = avmRule.getDappBytes(Main.class, data, 1, AIP040Events.class, NFToken.class, NFTokenMock.class, NFTokenStorage.class, AVMBlockchainWrapper.class);
        contractAddress = avmRule.deploy(deployer, BigInteger.ZERO, contractData).getDappAddress();

    }

    /******************************Basics******************************/
    @Test
    public void testName() {
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Name());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals("Planets"));
    }

    @Test
    public void testSymbol() {
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Symbol());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals("PL"));
    }
    
    @Test
    public void testTotalSupplyAfterDeployment() {
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TotalSupply());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ZERO));
    }
    
    @Test
    public void testTokenOwner() {
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(BigInteger.TEN));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData() == null);

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(null));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
    }
    
    @Test
    public void testTokenConsignee() {
        
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenConsignee(BigInteger.TEN));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData() == null);

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenConsignee(null));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
 
    }
    
    @Test
    public void testConsign(){

        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Consign(null, avmRule.getRandomAddress(BigInteger.TEN), new BigInteger[]{BigInteger.ZERO, BigInteger.ONE}));
        Assert.assertTrue(result.getReceiptStatus().isFailed());

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Consign(deployer, null, new BigInteger[]{BigInteger.ZERO, BigInteger.ONE}));
        Assert.assertTrue(result.getReceiptStatus().isFailed());

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Consign(deployer, avmRule.getRandomAddress(BigInteger.TEN), null));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
        
    }

    @Test
    public void testTokenUri() {
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenUri(BigInteger.TEN));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData() == null);

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenUri(null));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
    }
    
    @Test
    public void testOwnerBalance() {
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(avmRule.getRandomAddress(BigInteger.TEN)));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ZERO));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(null));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
    }
    
    @Test
    public void testOwnerDoesAuthorize() {
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerDoesAuthorize(avmRule.getRandomAddress(BigInteger.TEN), avmRule.getRandomAddress(BigInteger.TEN)));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(false));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerDoesAuthorize(null, avmRule.getRandomAddress(BigInteger.TEN)));
        Assert.assertTrue(result.getReceiptStatus().isFailed());

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerDoesAuthorize(avmRule.getRandomAddress(BigInteger.TEN), null));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
    }

    @Test
    public void testTokenAtIndex() {
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isFailed());

    }

    @Test
    public void testTokenForOwnerAtIndex() {
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(avmRule.getRandomAddress(BigInteger.TEN), BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
    }

    
    /****************Operator***************/
    
    @Test
    public void testAuthorizeAndOwnerDoesAuthorizeAndDeauthorize() {
        //Authorize null
        AvmRule.ResultWrapper result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Authorize(null));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
        
        //Authorize
        Address authorizee = avmRule.getRandomAddress(BigInteger.ZERO);
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Authorize(authorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());

        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Authorized".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(deployer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(authorizee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());
        
        //Authorize an existing authorizee
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Authorize(authorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());

        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Authorized".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(deployer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(authorizee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());
        
        
        //Does Authorize
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerDoesAuthorize(deployer, authorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(true));
        
        //Deauthorize null
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Deauthorize(null));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
        
        //Deauthorize
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Deauthorize(authorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());

        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Deauthorized".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(deployer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(authorizee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerDoesAuthorize(deployer, authorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(false));
        
        //Deauthorize non-existing authorizee
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Deauthorize(authorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());

        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Deauthorized".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(deployer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(authorizee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerDoesAuthorize(deployer, authorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(false));

        //Authorize/deauthorize self
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Authorize(deployer));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());

        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Authorized".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(deployer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(deployer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Deauthorize(deployer));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());

        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Deauthorized".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(deployer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(deployer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());
        
        
    }

    /****************Mint, consign,take the ownership***************/
    @Test
    public void testMintOneTokenToSelfAndTakeOwnerShipAndConsign() {
        Address tokenIssuer = avmRule.getRandomAddress(balance);
        Address tokenConsignee = avmRule.getRandomAddress(balance);
        BigInteger[] tokenIDs = new BigInteger[]{BigInteger.valueOf(333)};
        
        //mint one token to self
        AvmRule.ResultWrapper result = avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, new ABIStreamingEncoder().encodeOneString("mint").encodeOneAddress(tokenIssuer).encodeOneBigIntegerArray(tokenIDs).toBytes());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Minted".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenIssuer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());
        
        //total supply
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TotalSupply());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ONE));
        
        //token owner
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIssuer));
        
        //ower balance
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenIssuer));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ONE));
        
        //token at index
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));
        
        
        //token for owner at index
        result =  avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenIssuer, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));
        
        //owner is null
        result =  avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(null,tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
        
        //tokenID is null
        result =  avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(tokenIssuer,null));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
        
        //take from self
        result =  avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(tokenIssuer,tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Transferred".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenIssuer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenIssuer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());
        
        //take from wrong owner
        result =  avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(avmRule.getRandomAddress(balance),tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isFailed());

        //token owner after take from self
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIssuer));

        //ower balance after take from self
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenIssuer));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertEquals(result.getDecodedReturnData(), BigInteger.ONE);

        //token at index  after take from self
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));
        
        //token for owner at index  after take from self
        result =  avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenIssuer, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));
        
        //consignee is null
        result =  avmRule.call(avmRule.getRandomAddress(balance), contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(tokenIssuer,tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
        
        //consign to other by other
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Consign(tokenIssuer,tokenConsignee,tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isFailed());

        //consign to other by other with wrong owner
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Consign(deployer,tokenConsignee,tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
        
        //consign to other by self
        result = avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Consign(tokenIssuer,tokenConsignee,tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Consigned".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenIssuer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenConsignee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        //check consignee
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenConsignee(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenConsignee));
        
        //other takes token
        result =  avmRule.call(tokenConsignee, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(tokenIssuer,tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Transferred".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenIssuer.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenConsignee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        //check consignee after taken
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenConsignee(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertNull(result.getDecodedReturnData());
        
        //token owner after taken from other
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenConsignee));

        //owner balance after taken from other
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenIssuer));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ZERO));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenConsignee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ONE));

        //token at index after taken from other
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));

        //token for owner at index after taken from other
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenConsignee, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenIssuer, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
    }

    @Test
    public void testMintOneTokenToOtherAndTakeOwnershipAndConsign() {
        Address tokenIssuer = avmRule.getRandomAddress(balance);
        Address tokenOwner = avmRule.getRandomAddress(balance);
        Address tokenConsignee = avmRule.getRandomAddress(balance);
        BigInteger[] tokenIDs = new BigInteger[]{BigInteger.valueOf(333)};

        //mint one token to other
        AvmRule.ResultWrapper result = avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, new ABIStreamingEncoder().encodeOneString("mint").encodeOneAddress(tokenOwner).encodeOneBigIntegerArray(tokenIDs).toBytes());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Minted".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        //total supply
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TotalSupply());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ONE));

        //token owner
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenOwner));

        //ower balance
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenIssuer));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ZERO));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenOwner));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ONE));

        //token at index
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));


        //token for owner at index
        result =  avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenOwner, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));
        
        //consign by token owner
        result = avmRule.call(tokenOwner, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Consign(tokenOwner,tokenConsignee,tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Consigned".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenConsignee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());
        
        //taken ownership with wrong owner
        result = avmRule.call(tokenConsignee, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(deployer, tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
        
        //taken ownership
        result = avmRule.call(tokenConsignee, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(tokenOwner, tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Transferred".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenConsignee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());
        
        //check consignee
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenConsignee(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertNull(result.getDecodedReturnData());
        
        //check owner
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenConsignee));
    }


    /****************Mint, authorize,take the ownership***************/
    @Test
    public void testMintOneTokenToOtherAndTakeOwnerShipAndAuthorize() {
        Address tokenIssuer = avmRule.getRandomAddress(balance);
        Address tokenOwner = avmRule.getRandomAddress(balance);
        Address tokenAuhorizee = avmRule.getRandomAddress(balance);
        BigInteger[] tokenIDs = new BigInteger[]{BigInteger.valueOf(333)};

        //mint one token to other
        AvmRule.ResultWrapper result = avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, new ABIStreamingEncoder().encodeOneString("mint").encodeOneAddress(tokenOwner).encodeOneBigIntegerArray(tokenIDs).toBytes());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Minted".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());
        
        //total supply
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TotalSupply());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ONE));

        //token owner
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenOwner));

        //ower balance
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenOwner));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ONE));

        //token at index
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));


        //token for owner at index
        result =  avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenOwner, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));

        //authorize
        result = avmRule.call(tokenOwner, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Authorize(tokenAuhorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        
        //take with wrong owner
        result = avmRule.call(tokenAuhorizee, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(deployer, tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
        
        //take ownership
        result = avmRule.call(tokenAuhorizee, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(tokenOwner, tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Transferred".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenAuhorizee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        //token owner after transfer
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenAuhorizee));

        //ower balance after transfer
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenAuhorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ONE));

        //token at index after transfer
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));


        //token for owner at index after transfer
        result =  avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenAuhorizee, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));

    }

    /****************Mint, authorize,consign take the ownership***************/
    @Test
    public void testMintOneTokenToOtherAndTakeOwnerShipAndAuthorizeAndConsign() {
        
        Address tokenIssuer = avmRule.getRandomAddress(balance);
        Address tokenOwner = avmRule.getRandomAddress(balance);
        Address tokenAuhorizee = avmRule.getRandomAddress(balance);
        Address tokenConsignee = avmRule.getRandomAddress(balance);
        BigInteger[] tokenIDs = new BigInteger[]{BigInteger.valueOf(333)};

        //mint one token to other
        AvmRule.ResultWrapper result = avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, new ABIStreamingEncoder().encodeOneString("mint").encodeOneAddress(tokenOwner).encodeOneBigIntegerArray(tokenIDs).toBytes());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Minted".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        //total supply
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TotalSupply());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ONE));

        //token owner
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenOwner));

        //ower balance
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenOwner));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ONE));

        //token at index
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));


        //token for owner at index
        result =  avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenOwner, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));

        //authorize
        result = avmRule.call(tokenOwner, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Authorize(tokenAuhorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());

        //consign
        result = avmRule.call(tokenAuhorizee, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Consign(tokenOwner,tokenConsignee,tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Consigned".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenConsignee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());
        
        
        //take ownership with ownership as authorizee
        result = avmRule.call(tokenConsignee, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(tokenAuhorizee, tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
      
        //take ownership
        result = avmRule.call(tokenConsignee, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(tokenOwner, tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Transferred".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenConsignee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        //token owner after transfer
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenConsignee));

        //ower balance after transfer
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenConsignee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.ONE));

        //token at index after transfer
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));


        //token for owner at index after transfer
        result =  avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenConsignee, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));

    }

    /****************Mint, authorize, deauthorize, take the ownership***************/
    @Test
    public void testMintOneTokenToOtherAndTakeOwnerShipAndAuthorizeAndDeauthorize() {

        Address tokenIssuer = avmRule.getRandomAddress(balance);
        Address tokenOwner = avmRule.getRandomAddress(balance);
        Address tokenAuhorizee = avmRule.getRandomAddress(balance);
        BigInteger[] tokenIDs = new BigInteger[]{BigInteger.valueOf(333)};

        //mint one token to other
        AvmRule.ResultWrapper result = avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, new ABIStreamingEncoder().encodeOneString("mint").encodeOneAddress(tokenOwner).encodeOneBigIntegerArray(tokenIDs).toBytes());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(1, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Minted".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        //authorize
        result = avmRule.call(tokenOwner, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Authorize(tokenAuhorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        
        //deauthorize
        result = avmRule.call(tokenOwner, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Deauthorize(tokenAuhorizee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        

        //take ownership 
        result = avmRule.call(tokenAuhorizee, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(tokenOwner, tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isFailed());
    }

    /****************Mint 5 tokens***************/
    @Test
    public void testMintFiveTokens() {

        Address tokenIssuer = avmRule.getRandomAddress(balance);
        Address tokenOwner = avmRule.getRandomAddress(balance);
        Address tokenConsignee = avmRule.getRandomAddress(balance);
        BigInteger[] tokenIDs = new BigInteger[]{BigInteger.valueOf(333), BigInteger.valueOf(666), BigInteger.valueOf(999), BigInteger.valueOf(3333), BigInteger.valueOf(6666)};

        //mint five tokens to other
        AvmRule.ResultWrapper result = avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, new ABIStreamingEncoder().encodeOneString("mint").encodeOneAddress(tokenOwner).encodeOneBigIntegerArray(tokenIDs).toBytes());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(5, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Minted".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[0]), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Minted".getBytes()), result.getTransactionResult().logs.get(1).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(1).copyOfTopics().get(1));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[1]), result.getTransactionResult().logs.get(1).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(1).copyOfData());

        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Minted".getBytes()), result.getTransactionResult().logs.get(2).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(2).copyOfTopics().get(1));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[2]), result.getTransactionResult().logs.get(2).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(2).copyOfData());

        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Minted".getBytes()), result.getTransactionResult().logs.get(3).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(3).copyOfTopics().get(1));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[3]), result.getTransactionResult().logs.get(3).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(3).copyOfData());

        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Minted".getBytes()), result.getTransactionResult().logs.get(4).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(4).copyOfTopics().get(1));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[4]), result.getTransactionResult().logs.get(4).copyOfTopics().get(2));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(4).copyOfData());

        //total supply
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TotalSupply());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.valueOf(5)));
        
        //token at index
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ONE));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[1]));
        
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.TWO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[2]));
        
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.valueOf(3)));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[3]));
        
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.valueOf(4)));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[4]));
        
        //token for owner at index
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenOwner, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenOwner, BigInteger.ONE));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[1]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenOwner, BigInteger.TWO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[2]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenOwner, BigInteger.valueOf(3)));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[3]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenOwner, BigInteger.valueOf(4)));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[4]));
        
        //consign last two tokens
        result = avmRule.call(tokenOwner, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Consign(tokenOwner,tokenConsignee, new BigInteger[]{tokenIDs[3], tokenIDs[4]}));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        
        assertEquals(2, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Consigned".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenConsignee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[3]), result.getTransactionResult().logs.get(0).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Consigned".getBytes()), result.getTransactionResult().logs.get(1).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(1).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenConsignee.toByteArray()), result.getTransactionResult().logs.get(1).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[4]), result.getTransactionResult().logs.get(1).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());
        
        //check consignee
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenConsignee(tokenIDs[3]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenConsignee));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenConsignee(tokenIDs[4]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenConsignee));
        
        //take ownership
        result = avmRule.call(tokenConsignee, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(tokenOwner, new BigInteger[]{tokenIDs[3], tokenIDs[4]}));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        assertEquals(2, result.getTransactionResult().logs.size());
        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Transferred".getBytes()), result.getTransactionResult().logs.get(0).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenConsignee.toByteArray()), result.getTransactionResult().logs.get(0).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[3]), result.getTransactionResult().logs.get(0).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(0).copyOfData());

        assertArrayEquals(LogSizeUtils.truncatePadTopic("AIP040Transferred".getBytes()), result.getTransactionResult().logs.get(1).copyOfTopics().get(0));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenOwner.toByteArray()), result.getTransactionResult().logs.get(1).copyOfTopics().get(1));
        assertArrayEquals(LogSizeUtils.truncatePadTopic(tokenConsignee.toByteArray()), result.getTransactionResult().logs.get(1).copyOfTopics().get(2));
        assertArrayEquals(AIP040Events.padBigInteger32Bytes(tokenIDs[4]), result.getTransactionResult().logs.get(1).copyOfTopics().get(3));
        assertArrayEquals(new byte[0], result.getTransactionResult().logs.get(1).copyOfData());
        
        //token owner
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[0]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenOwner));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[1]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenOwner));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[2]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenOwner));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[3]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenConsignee));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenOwner(tokenIDs[4]));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenConsignee));
        
        
        //ower balance
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenOwner));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.valueOf(3)));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040OwnerBalance(tokenConsignee));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(BigInteger.TWO));

        //token at index
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.ONE));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[1]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.TWO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[2]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.valueOf(3)));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[3]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenAtIndex(BigInteger.valueOf(4)));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[4]));


        //token for owner at index
        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenOwner, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[0]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenOwner, BigInteger.ONE));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[1]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenOwner, BigInteger.TWO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[2]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenConsignee, BigInteger.ZERO));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[3]));

        result = avmRule.call(deployer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TokenForOwnerAtIndex(tokenConsignee, BigInteger.ONE));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        Assert.assertTrue(result.getDecodedReturnData().equals(tokenIDs[4]));


    }
    
    
    
    /***********Energy Test**************/
    @Test
    public void testMintOneTokenToSelfAndTakeOwnerShipAndConsignWithArray() {
        Address tokenIssuer = avmRule.getRandomAddress(balance);
        Address tokenConsignee = avmRule.getRandomAddress(balance);
        BigInteger[] tokenIDs = new BigInteger[]{BigInteger.valueOf(333), BigInteger.valueOf(666)};
        
        //mint one token to self
        AvmRule.ResultWrapper result = avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, new ABIStreamingEncoder().encodeOneString("mint").encodeOneAddress(tokenIssuer).encodeOneBigIntegerArray(tokenIDs).toBytes());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        System.out.println("Energy cost for minting two token using array: " + result.getTransactionResult().energyUsed);
       
        //consign 
        result = avmRule.call(tokenIssuer, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040Consign(tokenIssuer,tokenConsignee,tokenIDs));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        System.out.println("Energy cost for consigning two token using array: " + result.getTransactionResult().energyUsed);

        //take ownership 
        result =  avmRule.call(tokenConsignee, contractAddress, BigInteger.ZERO, AIP040Encoder.aip040TakeOwnership(tokenIssuer,new BigInteger[]{BigInteger.valueOf(333)}));
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        System.out.println("Energy cost for taking one token using array: " + result.getTransactionResult().energyUsed);

        result =  avmRule.call(tokenConsignee, contractAddress, BigInteger.ZERO,  new ABIStreamingEncoder()
            .encodeOneString("aip040TakeOneOwnership")
            .encodeOneAddress(tokenIssuer)
            .encodeOneBigInteger(tokenIDs[1])
            .toBytes());
        Assert.assertTrue(result.getReceiptStatus().isSuccess());
        System.out.println("Energy cost for taking one token without using array: " + result.getTransactionResult().energyUsed);

    }
    
    

}
