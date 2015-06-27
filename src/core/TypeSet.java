package core;

import java.util.Arrays;
import java.util.LinkedList;

public class TypeSet implements TypeDefinition {

	public enum Link {
		AND, OR
	}

	public final LinkedList<TypeDefinition> typeDefinitions;
	public final Link link;

	public TypeSet(LinkedList<TypeDefinition> typeDefinitions, Link link) {
		this.typeDefinitions = typeDefinitions;
		this.link = link;
	}

	public static TypeSet of(TypeDefinition... typeDefinitions) {
		return new TypeSet(new LinkedList<TypeDefinition>(Arrays.asList(typeDefinitions)), Link.AND);
	}

	public static TypeSet of(Link link, TypeDefinition... typeDefinitions) {
		return new TypeSet(new LinkedList<TypeDefinition>(Arrays.asList(typeDefinitions)), link);
	}

	public static TypeSet of(TypeDefinition[]... typeDefinitionss) {
		LinkedList<TypeDefinition> typeDefinitions = new LinkedList<TypeDefinition>();
		for (TypeDefinition[] typeDefinitions1 : typeDefinitionss) {
			for (TypeDefinition typeDefinition : typeDefinitions1) {
				typeDefinitions.add(typeDefinition);
			}
		}
		return new TypeSet(typeDefinitions, Link.AND);
	}

	@Override
	public boolean match(PoolObject poolObject) {
		switch (link) {
		case AND:
			for (TypeDefinition typeDefinition : typeDefinitions) {
				if (typeDefinition.notMatch(poolObject))
					return false;
			}
			return true;
		case OR:
			for (TypeDefinition typeDefinition : typeDefinitions) {
				if (typeDefinition.match(poolObject))
					return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((typeDefinitions == null) ? 0 : typeDefinitions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeSet other = (TypeSet) obj;
		if (link != other.link)
			return false;
		if (typeDefinitions == null) {
			if (other.typeDefinitions != null)
				return false;
		} else if (!typeDefinitions.equals(other.typeDefinitions))
			return false;
		return true;
	}

}
