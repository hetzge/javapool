package core;

public interface TypeDefinition {

	boolean match(PoolObject poolObject);
	
	default boolean notMatch(PoolObject poolObject){
		return !match(poolObject);
	}

}
