package de.lukas.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DingStoreSimple implements DingStore {

  private final Map<Integer, Ding> dinge = new HashMap<>();

  public DingStoreSimple() {
    put(new Ding(0, "Haus", true));
    put(new Ding(1, "Schuh", false));
  }

  @Override
  public Collection<Ding> getAll() {
    return dinge.values();
  }

  @Override
  public Ding get(final int id) {
    return dinge.get(Integer.valueOf(id));
  }

  @Override
  public void put(final Ding ding) {
    dinge.put(ding.getId(), ding);
  }

  @Override
  public void remove(final int id) {
    dinge.remove(Integer.valueOf(id));
  }

}
