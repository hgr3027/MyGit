package com.example.newpie.bean;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

public class MapUserTrail implements Serializable {

	public MapUserTrail(List<Map<String, Object>> mapUserTrailList,
			Map<String, String> currTime) {
		super();
		this.mapUserTrailList = mapUserTrailList;
		this.currTime = currTime;
	}

	public List<Map<String, Object>> getMapUserTrailList() {
		return mapUserTrailList;
	}

	public void setMapUserTrailList(List<Map<String, Object>> mapUserTrailList) {
		this.mapUserTrailList = mapUserTrailList;
	}

	public MapUserTrail() {
		super();
	}

	private List<Map<String, Object>> mapUserTrailList;
	private Map<String, String> currTime;

	public Map<String, String> getCurrTime() {
		return currTime;
	}

	public void setCurrTime(Map<String, String> currTime) {
		this.currTime = currTime;
	}
}
