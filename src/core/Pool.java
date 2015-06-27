package core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Pool {

	/**
	 * Global pool
	 */
	public static final DefaultPool G = new DefaultPool();

	protected final Set<TypeDefinition> typeDefinitions;

	public Pool() {
		this.typeDefinitions = new HashSet<>();
	}

	public final void on(TypeDefinition typeDefinition, Consumer<PoolObject> consumer) {
		Collection<PoolObject> poolObjects = collectSatisfyPoolObjects(typeDefinition).getPoolObjects();
		for (PoolObject poolObject : poolObjects) {
			consumer.accept(poolObject);
		}
	}

	public final void ifSatisfyAll(TypeDefinition typeDefinitionX, TypeDefinition typeDefinitionY, TypeDefinition satisfyWithTypeDefinition, Runnable runnable) {
		TypeSet typeSetY = TypeSet.of(typeDefinitionY);
		if (typeDefinitionY instanceof TypeSet) {
			typeSetY = (TypeSet) typeDefinitionY;
		}
		
		for (TypeDefinition typeDefinition : typeSetY.typeDefinitions) {
			ifSatisfyAll(typeDefinitionX, typeDefinition, ()->{
				break;
			});
		}
	}

	public final void ifSatisfyAll(TypeDefinition typeDefinition, TypeDefinition satisfyWithTypeDefinition, Runnable runnable) {
		Pool pool1 = collectSatisfyPoolObjects(typeDefinition);
		Pool pool2 = pool1.collectSatisfyPoolObjects(satisfyWithTypeDefinition);
		if (!pool1.getPoolObjects().isEmpty() && pool1.getPoolObjects().size() == pool2.getPoolObjects().size()) {
			runnable.run();
		}
	}

	public final void setup(TypeDefinition typeDefinition, TypeSetup typeSetup) {
		Collection<PoolObject> collectSatisfyPoolObjects = collectSatisfyPoolObjects(typeDefinition).getPoolObjects();
		for (PoolObject poolObject : collectSatisfyPoolObjects) {
			typeSetup.setup(poolObject);
		}
	}

	public final void register(PoolObject poolObject) {
		registerImpl(poolObject);
		if (this != Pool.G) {
			Pool.G.register(poolObject);
		}
	}

	protected abstract Pool collectSatisfyPoolObjects(TypeDefinition typeDefinition);

	protected abstract void refreshRegistration(PoolObject poolObject);

	protected abstract void registerImpl(PoolObject poolObject);

	protected abstract Collection<PoolObject> getPoolObjects();

	public void registerType(TypeDefinition typeDefinition) {
		typeDefinitions.add(typeDefinition);
	}

	public void registerTypes(TypeDefinition... typeDefinitions) {
		for (TypeDefinition typeDefinition : typeDefinitions) {
			registerType(typeDefinition);
		}
	}

	public PoolObject createPoolObject() {
		return createPoolObject(new DefaultPool());
	}

	public PoolObject createPoolObject(Pool pool) {
		PoolObject poolObject = new PoolObject(pool);
		register(poolObject);
		return poolObject;
	}

}
