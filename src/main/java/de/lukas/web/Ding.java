package de.lukas.web;

public class Ding {

  private int id;
  private String name;
  private boolean toll;

  public Ding() {
  }

  public Ding( //
      final int id, //
      final String name, //
      final boolean toll) {
    super();
    this.id = id;
    this.name = name;
    this.toll = toll;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public boolean isToll() {
    return toll;
  }

}
