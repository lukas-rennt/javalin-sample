package de.lukas.web;

import java.util.Collection;

public interface DingStore {

  public Collection<Ding> getAll();

  public Ding get(final int id);

  public void put(final Ding ding);

  public void remove(final int id);

}
