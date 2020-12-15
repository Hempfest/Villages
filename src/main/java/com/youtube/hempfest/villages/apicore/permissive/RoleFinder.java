package com.youtube.hempfest.villages.apicore.permissive;

import com.youtube.hempfest.villages.apicore.library.Permission;
import com.youtube.hempfest.villages.apicore.library.Position;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RoleFinder {
	private static final Map<String, Position> POSITION_ALIAS = new HashMap<>();

	static {
		for (Position position : Position.values()) {
			POSITION_ALIAS.put(position.name().toLowerCase().replace("_", ""), position);
		}
	}

	public static Position getRole(String name) {
		return POSITION_ALIAS.get(name.toLowerCase().replaceAll("_", ""));
	}

	public static List<String> getRoles() {
		List<String> array = new ArrayList<>();
		for (Map.Entry<String, Position> entry : POSITION_ALIAS.entrySet()) {
			array.add(entry.getKey());
		}
		return array;
	}

}
