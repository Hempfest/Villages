package com.youtube.hempfest.villages.apicore.permissive;

import com.youtube.hempfest.clans.metadata.ClanMeta;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.apicore.library.Permission;
import com.youtube.hempfest.villages.apicore.library.Position;
import java.util.List;

public abstract class Inheritance {

	public abstract List<Permission> getPermissions();

	public abstract List<Position> getRoles();

	public abstract ClanMeta villageMeta();

	public abstract Village village();

	public boolean hasPermission(Permission permission) {
		if (getPermissions().contains(Permission.ALL)) {
			return true;
		}
		return getPermissions().contains(permission);
	}

	public boolean hasRole(Position role) {
		return getRoles().contains(role);
	}

}
