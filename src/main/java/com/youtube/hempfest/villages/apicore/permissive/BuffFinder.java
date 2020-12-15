package com.youtube.hempfest.villages.apicore.permissive;

import com.youtube.hempfest.villages.apicore.library.Buff;
import com.youtube.hempfest.villages.apicore.library.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BuffFinder {
	private static final Map<String, Buff> BUFF_ALIAS = new HashMap<>();

	static {
		for (Buff buff : Buff.values()) {
			BUFF_ALIAS.put(buff.name().toLowerCase().replace("_", ""), buff);
		}
	}

	public static Buff getBuff(String name) {
		return BUFF_ALIAS.get(name.toLowerCase().replaceAll("_", ""));
	}

	public static List<String> getBuffs() {
		List<String> array = new ArrayList<>();
		for (Map.Entry<String, Buff> entry : BUFF_ALIAS.entrySet()) {
			array.add(entry.getKey());
		}
		return array;
	}

}
