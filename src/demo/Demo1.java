package demo;

import core.Attribute;
import core.Pool;
import core.PoolObject;
import core.TypeSet;
import core.TypeState;
import core.TypeSet.Link;
import core.tool.Counter;

public class Demo1 {

	public enum Player {
		X, O;
	}
	
	public enum Attributes implements Attribute {
		ROW, COLUMN, PLAYER
	}
	
	public static class PlayerTypeState implements TypeState {

		public final Player player;

		public PlayerTypeState(Player player) {
			this.player = player;
		}

		@Override
		public boolean match(PoolObject poolObject) {
			return poolObject.hasNotNullAndEquals(Attributes.PLAYER, player);
		}

		@Override
		public void setup(PoolObject poolObject) {
			poolObject.setup(Attributes.PLAYER, player);
			Counter.count("PlayerTypeState#setup()");
		}
		
	}

	public static class IndexTypeState implements TypeState {

		public final int index;
		public final Attribute attribute;

		public IndexTypeState(int index, Attribute attribute) {
			this.index = index;
			this.attribute = attribute;
		}

		@Override
		public boolean match(PoolObject poolObject) {
			return poolObject.hasNotNullAndEquals(attribute, index);
		}

		@Override
		public void setup(PoolObject poolObject) {
			poolObject.setup(attribute, index);
			Counter.count("IndexTypeState#setup()");
		}
	}

	public static class ColumnTypeState extends IndexTypeState {
		public ColumnTypeState(int index) {
			super(index, Attributes.COLUMN);
		}
	}

	public static class RowTypeState extends IndexTypeState {
		public RowTypeState(int index) {
			super(index, Attributes.ROW);
		}
	}

	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();
		int size = 100;
		
		RowTypeState[] rowTypeStates = new RowTypeState[size];
		for (int i = 0; i < rowTypeStates.length; i++) {
			rowTypeStates[i] = new RowTypeState(i);
		}
		Pool.G.registerTypes(rowTypeStates);

		ColumnTypeState[] columnTypeStates = new ColumnTypeState[size];
		for (int i = 0; i < columnTypeStates.length; i++) {
			columnTypeStates[i] = new ColumnTypeState(i);
		}
		Pool.G.registerTypes(columnTypeStates);

		for (IndexTypeState rowTypeState : rowTypeStates) {
			for (ColumnTypeState columnTypeState : columnTypeStates) {
				PoolObject poolObject = Pool.G.createPoolObject();
				rowTypeState.setup(poolObject);
				columnTypeState.setup(poolObject);
			}
		}
		
		PlayerTypeState playerOTypeState = new PlayerTypeState(Player.O);
		Pool.G.registerType(playerOTypeState);
		PlayerTypeState playerXTypeState = new PlayerTypeState(Player.X);
		Pool.G.registerType(playerXTypeState);

		for (int i = 0; i < size; i++) {
			Pool.G.setup(TypeSet.of(rowTypeStates[i], columnTypeStates[0]), playerOTypeState);
			Pool.G.setup(TypeSet.of(rowTypeStates[i], columnTypeStates[1]), playerOTypeState);
		}
		
		Counter.start("count");
		Pool.G.on(TypeSet.of(columnTypeStates[0], TypeSet.of(Link.OR, rowTypeStates)), (PoolObject poolObject) -> {
			System.out.println(poolObject.get(Attributes.PLAYER));
		});
		Counter.end("count");
		
		Pool.G.ifSatisfyAll(TypeSet.of(TypeSet.of(Link.OR, rowTypeStates), columnTypeStates[2]), playerOTypeState, ()->{
			System.out.println("Blub");
		});
		
		Counter.start("count1");
		Pool.G.ifSatisfyAll(TypeSet.of(Link.OR, TypeSet.of(rowTypeStates), TypeSet.of(columnTypeStates)), playerOTypeState, ()-> {
			System.out.println("O won");
		});
		Counter.end("count1");
		
		Counter.start("count2");
		Pool.G.ifSatisfyAll(TypeSet.of(Link.OR, TypeSet.of(rowTypeStates), TypeSet.of(columnTypeStates)), playerXTypeState, ()-> {
			System.out.println("X won");
		});
		Counter.end("count2");
		
		
		Pool.G.on(rowTypeStates[2], (PoolObject rowEntry) -> {
//			printPoolObject(rowEntry);
		});

		System.out.println(System.currentTimeMillis() - startTime);
		
		
		Counter.print();
	}
	
	private static void printPoolObject(PoolObject poolObject){
		System.out.println(poolObject.get(Attributes.ROW) + "/" + poolObject.get(Attributes.COLUMN) + " = " + poolObject.get(Attributes.PLAYER));
	}

}
