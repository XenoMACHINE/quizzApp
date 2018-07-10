
package com.example.duelmanagerlib.Adapter;

import java.util.LinkedHashMap;

public class LinkedHashMapAdapter<K, V> {

    private LinkedHashMap<K, V> mMapData;
    private final Object mLock = new Object();
    private LinkedHashMap<K, V> mOriginalMapData;

    //Maps the specified key to the specified value.
    public V put(K key, V value) {
        V result;
        synchronized (mLock) {
            if (mOriginalMapData != null) {
                result = mOriginalMapData.put(key, value);
            } else {
                result = mMapData.put(key, value);
            }
        }
        return result;
    }

    //Removes the mapping with the specified key from this map.
    public V remove(K key) {
        V result;
        synchronized (mLock) {
            if (mOriginalMapData != null) {
                result = mOriginalMapData.remove(key);
            } else {
                result = mMapData.remove(key);
            }
        }
        return result;
    }

    //Removes the mapping with the specified key from this map.
    public V get(K key) {
        V result;
        synchronized (mLock) {
            result = mMapData.get(key);
        }
        return result;
    }

    //This override is done for LinkedHashMap performance: iteration is cheaper
    public boolean containsValue(V value) {
        boolean result;
        synchronized (mLock) {
            result = mMapData.containsValue(value);
        }
        return result;
    }

    //Remove all elements from the list.
    public void clear() {
        synchronized (mLock) {
            if (mOriginalMapData != null) {
                mOriginalMapData.clear();
            } else {
                mMapData.clear();
            }
        }
    }
}