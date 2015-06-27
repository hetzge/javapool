package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.tool.Counter;

public class PoolObject {

	public final Pool pool;
	protected final Set<Pool> inPools;
	private final Map<Attribute, Object> attributes;
	public boolean lazy = false;

	PoolObject(Pool pool) {
		this.pool = pool;
		this.attributes = new HashMap<>();
		this.inPools = new HashSet<>();
	}

	@SuppressWarnings("unchecked")
	public <TYPE> TYPE get(Attribute attribute) {
		return (TYPE) this.attributes.get(attribute);
	}

	public <TYPE> void set(Attribute attribute, TYPE value) {
		if (!this.attributes.containsKey(attribute))
			throw new IllegalAccessError("Only attributs which already exists can be set. Attribute: " + attribute.toString() + ", Value: " + value.toString());
		this.attributes.put(attribute, value);
		refreshInPools();
	}

	public <TYPE> void setup(Attribute attribute, TYPE value) {
		this.attributes.put(attribute, value);
		refreshInPools();
	}
	
	public boolean has(Attribute attribute){
		return this.attributes.containsKey(attribute);
	}
	
	public boolean hasNotNull(Attribute attribute){
		return this.attributes.get(attribute) != null;
	}
	
	public boolean hasNotNullAndEquals(Attribute attribute, Object value){
		return hasNotNull(attribute) && get(attribute).equals(value);
	}
	
	private void refreshInPools(){
		for (Pool pool : this.inPools) {
			pool.refreshRegistration(this);
			Counter.count("PoolObject#refreshInPools()");
		}
	}

}
