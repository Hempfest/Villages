package com.youtube.hempfest.villages.apicore.entities;

import com.youtube.hempfest.clans.metadata.ClanMeta;
import com.youtube.hempfest.villages.apicore.library.Permission;
import com.youtube.hempfest.villages.apicore.library.Position;
import com.youtube.hempfest.villages.apicore.permissive.Inheritance;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Role extends Inheritance implements Serializable {

	private final Position role;

	private final Village village;

	private final int priNum;

	private final List<Permission> rolePermissions = new ArrayList<>();

	public Role(Position role, int priority, Village village) {
        this.role = role;
        this.priNum = priority;
        this.village = village;
	}

	@Override
	public List<Permission> getPermissions() {
		return rolePermissions;
	}

	@Override
	public List<Position> getRoles() {
		return Arrays.asList(Position.values());
	}

	public void addPermission(Permission permission) {
		rolePermissions.add(permission);
	}

	public void removePermission(Permission permission) {
		rolePermissions.remove(permission);
	}

	@Override
	public ClanMeta villageMeta() {
		return village.getMeta();
	}

	@Override
	public Village village() {
		return village;
	}

	public String getName() {
		return role.name();
	}

	public int getPriority() {
		return priNum;
	}

}
