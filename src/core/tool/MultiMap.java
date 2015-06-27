package core.tool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MultiMap<KEY_TYPE, VALUE_TYPE> extends HashMap<KEY_TYPE, Set<VALUE_TYPE>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<VALUE_TYPE> get(Object key) {
		Set<VALUE_TYPE> value = super.get(key);
		if(value == null){
			value = new HashSet<VALUE_TYPE>();
			put((KEY_TYPE) key, value);
		}
		return value;
	}
	
	public void addValue(KEY_TYPE key, VALUE_TYPE value){
		get(key).add(value);
	}
	
}
