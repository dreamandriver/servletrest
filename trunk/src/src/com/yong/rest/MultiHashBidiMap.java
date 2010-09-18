package com.yong.rest;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;

/**
 *	构建一个Map实现，可以通过键(key)得到值(value);可以通过值(value)得到多值键(values)数组
 *	因为仅仅需要一个可以单向反转的Map数组元素，Apache Commons collections 和 Google Collections 都提供了BidiMap实现，但无法做到反转过来的一对多的Map
 *  为了使依赖尽可能的少，只好重构一个，继承HashMap，仅仅覆盖平常常用方法。添加一个reverse方法，进行反转.
 *	有点装饰者模式的味道，继承只好，被附加了新的职责
 **/
public class MultiHashBidiMap<K,V> extends HashMap<K,V> {
	private HashMap<K,V> map = null;
	private HashMap<V,Set<K>> bidiMap = null;

	public MultiHashBidiMap(){
		map = new HashMap<K,V>();

		bidiMap = new HashMap<V,Set<K>>();
	}

	@Override
	public Set<Map.Entry<K,V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

	@Override
	public V put(K key, V value) {
		Set<K> coll = bidiMap.get(value);
        if (coll == null) {
            coll = createCollection(null);
            bidiMap.put(value, coll);
        }
        coll.add(key);

		return map.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m){
		if(m == null || m.isEmpty()){
			return;
		}

		for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<? extends K, ? extends V> e = i.next();
            this.put(e.getKey(), e.getValue());
        }
	}

	@Override
	public V remove(Object key) {
		V value = map.remove(key);

		bidiMap.remove(value);

        return value;
    }

    @Override
    public void clear() {
    	map.clear();

    	bidiMap.clear();
    }

    @Override
    public boolean containsValue(Object value){
    	return map.containsValue(value);
    }

	@Override
	public Object clone() {
		return map.clone();
	}

	public HashMap<V,Set<K>> reverse(){
		return bidiMap;
	}

	private Set<K> createCollection(Set<K> coll) {
        if (coll == null) {
            return new HashSet<K>();
        } else {
            return new HashSet<K>(coll);
        }
    }

	@Override
    public Set<K> keySet() {
        return map.keySet();
    }

	@Override
    public Collection<V> values() {
    	return map.values();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
}