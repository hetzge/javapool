package core.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Counter {

	private static final Map<String, Integer> map = new HashMap<String, Integer>();
	private static final Map<String, Long> time = new HashMap<String, Long>();

	public static void count(String key) {
		map.put(key, ((int) map.getOrDefault(key, 0)) + 1);
	}
	
	public static void print(){
		Set<Entry<String,Integer>> entrySet = map.entrySet();
		for (Entry<String, Integer> entry : entrySet) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}
	
	public static void start(String key){
		time.put(key, System.currentTimeMillis());
	}
	
	public static void end(String key){
		System.out.println(key + " -> " + (System.currentTimeMillis() - time.get(key)));
	}

}
