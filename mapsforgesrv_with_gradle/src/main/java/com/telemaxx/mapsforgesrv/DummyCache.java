package com.telemaxx.mapsforgesrv;
import java.util.HashSet;
import java.util.Observer;
import java.util.Set;

import org.mapsforge.core.graphics.TileBitmap;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.queue.Job;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DummyCache implements TileCache {

	HashSet<Integer> set = new HashSet<>(10000);

	@Override
	public void put(Job job, TileBitmap tile) {
		set.add(job.hashCode());
	}

	@Override
	public boolean containsKey(Job job) {
		return set.contains(job.hashCode());
	}

	@Override
	public void destroy() {
	}

	@Override
	public TileBitmap get(Job job) {
		throw new NotImplementedException();
	}

	@Override
	public int getCapacity() {
		throw new NotImplementedException();
	}

	@Override
	public int getCapacityFirstLevel() {
		throw new NotImplementedException();
	}

	@Override
	public TileBitmap getImmediately(Job job) {
		throw new NotImplementedException();
	}

	@Override
	public void setWorkingSet(Set<Job> jobs) {
		throw new NotImplementedException();
	}

	@Override
	public void purge() {
		set.clear();
	}

	public void addObserver(Observer observer) {
		throw new NotImplementedException();
	}

	public void removeObserver(Observer observer) {
		throw new NotImplementedException();
	}

	@Override
	public void addObserver(org.mapsforge.map.model.common.Observer observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObserver(org.mapsforge.map.model.common.Observer observer) {
		// TODO Auto-generated method stub
		
	}

}
