package org.aion.mcf.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.aion.base.db.IContractDetails;
import org.aion.base.db.IPruneConfig;
import org.aion.base.db.IRepositoryConfig;
import org.aion.base.type.AionAddress;
import org.aion.base.util.ByteArrayWrapper;
import org.aion.base.vm.IDataWord;
import org.aion.db.impl.DBVendor;
import org.aion.db.impl.DatabaseFactory;
import org.aion.mcf.config.CfgPrune;
import org.aion.mcf.vm.types.DataWord;
import org.aion.mcf.vm.types.DoubleDataWord;
import org.aion.zero.db.AionRepositoryCache;
import org.aion.zero.impl.db.AionRepositoryImpl;
import org.aion.zero.impl.db.ContractDetailsAion;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AionRepositoryCacheTest {
    private AionRepositoryCache cache;

    @Before
    public void setup() {
        IRepositoryConfig repoConfig =
                new IRepositoryConfig() {
                    @Override
                    public String getDbPath() {
                        return "";
                    }

                    @Override
                    public IPruneConfig getPruneConfig() {
                        return new CfgPrune(false);
                    }

                    @Override
                    public IContractDetails contractDetailsImpl() {
                        return ContractDetailsAion.createForTesting(0, 1000000).getDetails();
                    }

                    @Override
                    public Properties getDatabaseConfig(String db_name) {
                        Properties props = new Properties();
                        props.setProperty(DatabaseFactory.Props.DB_TYPE, DBVendor.MOCKDB.toValue());
                        props.setProperty(DatabaseFactory.Props.ENABLE_HEAP_CACHE, "false");
                        return props;
                    }
                };
        cache = new AionRepositoryCache(AionRepositoryImpl.createForTesting(repoConfig));
    }

    @After
    public void tearDown() {
        cache = null;
    }

    @Test
    public void testGetStorageValueNoSuchAddress() {
        assertNull(
                cache.getStorageValue(
                        getNewAddress(),
                        new DataWord(RandomUtils.nextBytes(DataWord.BYTES)).toWrapper()));
    }

    @Test
    public void testGetStorageValueIsSingleZero() {
        AionAddress address = getNewAddress();
        IDataWord key = new DataWord(RandomUtils.nextBytes(DataWord.BYTES));
        cache.addStorageRow(address, key.toWrapper(), DataWord.ZERO.toWrapper());
        assertNull(cache.getStorageValue(address, key.toWrapper()));

        key = new DoubleDataWord(RandomUtils.nextBytes(DoubleDataWord.BYTES));
        cache.addStorageRow(address, key.toWrapper(), DataWord.ZERO.toWrapper());
        assertNull(cache.getStorageValue(address, key.toWrapper()));
    }

    @Test
    public void testGetStorageValueIsDoubleZero() {
        AionAddress address = getNewAddress();
        IDataWord key = new DataWord(RandomUtils.nextBytes(DataWord.BYTES));
        cache.addStorageRow(address, key.toWrapper(), DoubleDataWord.ZERO.toWrapper());
        assertNull(cache.getStorageValue(address, key.toWrapper()));

        key = new DoubleDataWord(RandomUtils.nextBytes(DoubleDataWord.BYTES));
        cache.addStorageRow(address, key.toWrapper(), DoubleDataWord.ZERO.toWrapper());
        assertNull(cache.getStorageValue(address, key.toWrapper()));
    }

    @Test
    public void testGetStorageValueWithSingleZeroKey() {
        AionAddress address = getNewAddress();
        ByteArrayWrapper value = new DataWord(RandomUtils.nextBytes(DataWord.BYTES)).toWrapper();
        cache.addStorageRow(address, DataWord.ZERO.toWrapper(), value);
        assertEquals(value, cache.getStorageValue(address, DataWord.ZERO.toWrapper()));

        value = new DoubleDataWord(RandomUtils.nextBytes(DoubleDataWord.BYTES)).toWrapper();
        cache.addStorageRow(address, DataWord.ZERO.toWrapper(), value);
        assertEquals(value, cache.getStorageValue(address, DataWord.ZERO.toWrapper()));
    }

    @Test
    public void testGetStorageValueWithDoubleZeroKey() {
        AionAddress address = getNewAddress();
        ByteArrayWrapper value = new DataWord(RandomUtils.nextBytes(DataWord.BYTES)).toWrapper();
        cache.addStorageRow(address, DoubleDataWord.ZERO.toWrapper(), value);
        assertEquals(value, cache.getStorageValue(address, DoubleDataWord.ZERO.toWrapper()));

        value = new DoubleDataWord(RandomUtils.nextBytes(DoubleDataWord.BYTES)).toWrapper();
        cache.addStorageRow(address, DoubleDataWord.ZERO.toWrapper(), value);
        assertEquals(value, cache.getStorageValue(address, DoubleDataWord.ZERO.toWrapper()));
    }

    @Test
    public void testGetStorageValueWithZeroKeyAndValue() {
        AionAddress address = getNewAddress();

        // single-single
        cache.addStorageRow(address, DataWord.ZERO.toWrapper(), DataWord.ZERO.toWrapper());
        assertNull(cache.getStorageValue(address, DataWord.ZERO.toWrapper()));

        // single-double
        cache.addStorageRow(address, DataWord.ZERO.toWrapper(), DoubleDataWord.ZERO.toWrapper());
        assertNull(cache.getStorageValue(address, DataWord.ZERO.toWrapper()));

        // double-single
        cache.addStorageRow(address, DoubleDataWord.ZERO.toWrapper(), DataWord.ZERO.toWrapper());
        assertNull(cache.getStorageValue(address, DoubleDataWord.ZERO.toWrapper()));

        // double-double
        cache.addStorageRow(
                address, DoubleDataWord.ZERO.toWrapper(), DoubleDataWord.ZERO.toWrapper());
        assertNull(cache.getStorageValue(address, DoubleDataWord.ZERO.toWrapper()));
    }

    @Test
    public void testOverwriteValueWithSingleZero() {
        AionAddress address = getNewAddress();
        ByteArrayWrapper key = new DataWord(RandomUtils.nextBytes(DataWord.BYTES)).toWrapper();
        ByteArrayWrapper value =
                new DoubleDataWord(RandomUtils.nextBytes(DoubleDataWord.BYTES)).toWrapper();
        cache.addStorageRow(address, key, value);
        assertEquals(value, cache.getStorageValue(address, key));
        cache.addStorageRow(address, key, DataWord.ZERO.toWrapper());
        assertNull(cache.getStorageValue(address, key));
    }

    @Test
    public void testOverwriteValueWithDoubleZero() {
        AionAddress address = getNewAddress();
        ByteArrayWrapper key =
                new DoubleDataWord(RandomUtils.nextBytes(DoubleDataWord.BYTES)).toWrapper();
        ByteArrayWrapper value = new DataWord(RandomUtils.nextBytes(DataWord.BYTES)).toWrapper();
        cache.addStorageRow(address, key, value);
        assertEquals(value, cache.getStorageValue(address, key));
        cache.addStorageRow(address, key, DoubleDataWord.ZERO.toWrapper());
        assertNull(cache.getStorageValue(address, key));
    }

    @Test
    public void testGetStorageValueEnMass() {
        int numEntries = RandomUtils.nextInt(300, 700);
        int deleteOdds = 5;
        int numAddrs = 8;
        List<AionAddress> addresses = getAddressesInBulk(numAddrs);
        List<ByteArrayWrapper> keys = getKeysInBulk(numEntries);
        List<ByteArrayWrapper> values = getValuesInBulk(numEntries);

        for (AionAddress address : addresses) {
            massAddToCache(address, keys, values);
            deleteEveryNthEntry(address, keys, deleteOdds);
        }

        for (AionAddress address : addresses) {
            checkStorage(address, keys, values, deleteOdds);
        }
    }

    // <-----------------------------------------HELPERS-------------------------------------------->

    /** Returns a new random address. */
    private AionAddress getNewAddress() {
        return new AionAddress(RandomUtils.nextBytes(AionAddress.SIZE));
    }

    private List<AionAddress> getAddressesInBulk(int num) {
        List<AionAddress> addresses = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            addresses.add(getNewAddress());
        }
        return addresses;
    }

    /**
     * Checks that cache's storage, given by cache.getStorage(), contains all key-value pairs in
     * keys and values, where it is assumed every n'th pair was deleted.
     */
    private void checkStorage(
            AionAddress address,
            List<ByteArrayWrapper> keys,
            List<ByteArrayWrapper> values,
            int n) {
        Map<ByteArrayWrapper, ByteArrayWrapper> storage = cache.getStorage(address, keys);
        int count = 1;
        for (ByteArrayWrapper key : keys) {
            if (count % n == 0) {
                assertNull(storage.get(key));
            } else {
                assertEquals(values.get(count - 1), storage.get(key));
            }
            count++;
        }
    }

    /**
     * Iterates over every key in keys -- which are assumed to exist in cache -- and then deletes
     * any key-value pair in cache for every n'th key in keys.
     */
    private void deleteEveryNthEntry(AionAddress address, List<ByteArrayWrapper> keys, int n) {
        int count = 1;
        for (ByteArrayWrapper key : keys) {
            if (count % n == 0) {
                cache.addStorageRow(address, key, DataWord.ZERO.toWrapper());
            }
            count++;
        }
    }

    /** Puts all of the key-value pairs in keys and values into cache under address. */
    private void massAddToCache(
            AionAddress address, List<ByteArrayWrapper> keys, List<ByteArrayWrapper> values) {
        int size = keys.size();
        assertEquals(size, values.size());
        for (int i = 0; i < size; i++) {
            cache.addStorageRow(address, keys.get(i), values.get(i));
        }
    }

    /** Returns a list of numKeys keys, every other one is single and then double. */
    private List<ByteArrayWrapper> getKeysInBulk(int numKeys) {
        List<ByteArrayWrapper> keys = new ArrayList<>(numKeys);
        boolean isSingleKey = true;
        for (int i = 0; i < numKeys; i++) {
            keys.add(getRandomWord(isSingleKey).toWrapper());
            isSingleKey = !isSingleKey;
        }
        return keys;
    }

    /** Returns a list of numValues values, every other one is single and then double. */
    private List<ByteArrayWrapper> getValuesInBulk(int numValues) {
        List<ByteArrayWrapper> values = new ArrayList<>(numValues);
        boolean isSingleValue = true;
        for (int i = 0; i < numValues; i++) {
            values.add(getRandomWord(isSingleValue).toWrapper());
            isSingleValue = !isSingleValue;
        }
        return values;
    }

    /** Returns a random DataWord if isSingleWord is true, otherwise a random DoubleDataWord. */
    private IDataWord getRandomWord(boolean isSingleWord) {
        return (isSingleWord)
                ? new DataWord(RandomUtils.nextBytes(DataWord.BYTES))
                : new DoubleDataWord(RandomUtils.nextBytes(DoubleDataWord.BYTES));
    }
}
