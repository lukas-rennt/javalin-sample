package de.lukas.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DingStore {

  private final Map<Integer, Ding> dinge = new HashMap<>();

  public final static DingStore INSTANCE = new DingStore();

  public DingStore() {
    put(new Ding(0, "Haus", true));
    put(new Ding(1, "Schuh", false));
  }

  public Collection<Ding> getAll() {
    return dinge.values();
  }

  public Ding get(final int id) {
    return dinge.get(Integer.valueOf(id));
  }

  public void put(final Ding ding) {
    dinge.put(ding.getId(), ding);
  }

  public void remove(final int id) {
	dinge.remove(Integer.valueOf(id));
  }

}
