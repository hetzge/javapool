package core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LazyPool extends Pool {

	private final Set<PoolObject> lazy;
	
	public LazyPool() {
		this.lazy = new HashSet<>();
	}
	
	public static LazyPool of(PoolObject... poolObjects){
		return of(Arrays.asList(poolObjects));
	}
	
	public static LazyPool of(Collection<PoolObject> poolObjects){
		LazyPool lazyPool = new LazyPool();
		for (PoolObject poolObject : poolObjects) {
			lazyPool.register(poolObject);
		}
		return lazyPool;
	}
	
	@Override
	protected void registerImpl(PoolObject poolObject) {
		poolObject.inPools.add(this);
		this.lazy.add(poolObject);
	}
	
	@Override
	protected void refreshRegistration(PoolObject poolObject) {
		// nothing
	}

	@Override
	protected Pool collectSatisfyPoolObjects(TypeDefinition typeDefinition) {
		Set<PoolObject> result = new HashSet<>(); 
		for (PoolObject poolObject : this.lazy) {
			if (typeDefinition.match(poolObject)) {
				result.add(poolObject);
			}
		}
		return LazyPool.of(result);
	}

	@Override
	protected Collection<PoolObject> getPoolObjects() {
		return new HashSet<PoolObject>(lazy);
	}

}
