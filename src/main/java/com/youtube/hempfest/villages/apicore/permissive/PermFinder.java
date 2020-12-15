package com.youtube.hempfest.villages.apicore.permissive;

import com.youtube.hempfest.villages.apicore.library.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PermFinder {
	private static final Map<String, Permission> PERMISSION_ALIAS = new HashMap<>();

	static {
		for (Permission permission : Permission.values()) {
			PERMISSION_ALIAS.put(permission.name().toLowerCase().replace("_", ""), permission);
		}
	}

	public static Permission getPermission(String name) {
		return PERMISSION_ALIAS.get(name.toLowerCase().replaceAll("_", ""));
	}

	public static List<String> getPermissions() {
		List<String> array = new ArrayList<>();
		for (Map.Entry<String, Permission> entry : PERMISSION_ALIAS.entrySet()) {
			array.add(entry.getKey());
		}
		return array;
	}

}
