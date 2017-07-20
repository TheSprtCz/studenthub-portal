package net.thesishub.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.thesishub.core.Faculty;
import net.thesishub.core.University;

public class EqualsTest {

  @Test
  public void testBothNull() {
    Faculty one = new Faculty("New", null);
    Faculty two = new Faculty("New", null);
    assertTrue(Equals.id(two.getUniversity(), one.getUniversity()));
  }

  @Test
  public void testOneNull() {
    Faculty one = new Faculty("New", new University());
    Faculty two = new Faculty("New", null);
    assertFalse(Equals.id(two.getUniversity(), one.getUniversity()));
    assertFalse(Equals.id(one.getUniversity(), two.getUniversity()));
  }

  @Test
  public void testDifferentId() {
    Faculty one = new Faculty("New", new University());
    one.setId((long) 1);

    Faculty two = new Faculty("New", null);
    two.setId((long) 2);
    assertFalse(Equals.id(two, one));
    assertFalse(Equals.id(one, two));
  }

  @Test
  public void testSameId() {
    Faculty one = new Faculty("New", new University());
    one.setId((long) 1);

    Faculty two = new Faculty("New", null);
    two.setId((long) 1);
    assertTrue(Equals.id(two, one));
    assertTrue(Equals.id(one, two));
  }
}
