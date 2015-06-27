package core;

import java.util.Collection;
import java.util.HashSet;

public class DefaultPool extends Pool {

	private final LazyPool lazyPool;
	private final EagerPool eagerPool;
	
	private final LazyPool defaultLazyPool;

	public DefaultPool() {
		this.lazyPool = new LazyPool();
		this.eagerPool = new EagerPool();
		this.defaultLazyPool = new LazyPool();
	}

	public DefaultPool(PoolObject... poolObjects) {
		this();
		for (PoolObject poolObject : poolObjects) {
			register(poolObject);
		}
	}

	@Override
	protected void registerImpl(PoolObject poolObject) {
		if(poolObject.lazy){
			lazyPool.registerImpl(poolObject);
		} else {
			eagerPool.registerImpl(poolObject);
		}
		defaultLazyPool.registerImpl(poolObject);
	}
	
	@Override
	protected void refreshRegistration(PoolObject poolObject) {
		eagerPool.refreshRegistration(poolObject);
	}

	@Override
	protected Pool collectSatisfyPoolObjects(TypeDefinition typeDefinition) {
		if(typeDefinition instanceof TypeSet){
			return this.defaultLazyPool.collectSatisfyPoolObjects(typeDefinition);
		}
		
		HashSet<PoolObject> result = new HashSet<>();
		result.addAll(this.lazyPool.collectSatisfyPoolObjects(typeDefinition).getPoolObjects());
		result.addAll(this.eagerPool.collectSatisfyPoolObjects(typeDefinition).getPoolObjects());
		return LazyPool.of(result);
	}

	@Override
	protected Collection<PoolObject> getPoolObjects() {
		HashSet<PoolObject> result = new HashSet<>();
		result.addAll(lazyPool.getPoolObjects());
		result.addAll(eagerPool.getPoolObjects());
		return result;
	}
}
