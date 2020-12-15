package com.youtube.hempfest.villages.apicore.activities;

import com.youtube.hempfest.villages.apicore.entities.Village;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;

public class Objective implements Serializable {

	private boolean completed;

	private final int level;

	private final int max;

	private int completion = 0;

	private final String name;

	private final String info;

	private final Village village;

	public Objective(int level, int max, String name, String info, Village village) {
		this.level = level;
		this.max = max;
		this.name = name;
		this.info = info;
		this.village = village;
	}

	public int getLevel() {
		return level;
	}

	public void addProgress(int step) {
		completion += step;
		village.updateObjective(village.getObjective(level), this);
	}

	public int getCompletion() {
		return completion;
	}

	public double completionPercentage() {
		double x = completion;
		double y = max;
		return Math.round(x * 100 / y* 100.0) / 100.0;
	}

	public String getName() {
		return name;
	}

	public String info() {
		return info;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

}
