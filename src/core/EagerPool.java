package core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import core.tool.Counter;
import core.tool.MultiMap;

public class EagerPool extends Pool{

	private final MultiMap<TypeDefinition, PoolObject> eager;
	private final MultiMap<PoolObject, TypeDefinition> eagerBackMapping;
	
	public EagerPool() {
		this.eager = new MultiMap<>();
		this.eagerBackMapping = new MultiMap<>();
	}
	
	@Override
	protected void registerImpl(PoolObject poolObject) {
		poolObject.inPools.add(this);
		Set<TypeDefinition> typeDefinitions = new HashSet<>();
		typeDefinitions.addAll(this.typeDefinitions);
		typeDefinitions.addAll(Pool.G.typeDefinitions);
		for (TypeDefinition typeDefinition : typeDefinitions) {
			if (typeDefinition.match(poolObject)) {
				this.eager.addValue(typeDefinition, poolObject);
				this.eagerBackMapping.addValue(poolObject, typeDefinition);
			}
		}
	}
	
	@Override
	protected void refreshRegistration(PoolObject poolObject){
		
		Counter.count("EagerPool#refreshRegistration()");
		
		Set<TypeDefinition> typeDefinitions = this.eagerBackMapping.get(poolObject);
		for (TypeDefinition typeDefinition : typeDefinitions) {
			this.eager.get(typeDefinition).remove(poolObject);
		}
		this.eagerBackMapping.remove(poolObject);
		registerImpl(poolObject);
	}

	@Override
	protected Pool collectSatisfyPoolObjects(TypeDefinition typeDefinition) {
		return LazyPool.of(this.eager.get(typeDefinition));
	}

	@Override
	protected Collection<PoolObject> getPoolObjects() {
		return new HashSet<PoolObject>(eagerBackMapping.keySet());
	}

}
