package net.thesishub.util;

import net.thesishub.core.GenericEntity;

public class Equals {

  /**
   * Checks if both entities are the same
   *
   * @return true, if both entities are null or have same ID
   */
  public static <ID> boolean id(GenericEntity<ID> oldEnt, GenericEntity<ID> newEnt) {
    // If one of them is null while the other is not
    if (!((oldEnt == null) == (newEnt == null)))
      return false;

    // If the oldFac is null (both of them must be at this point)
    if (oldEnt == null || oldEnt.getId().equals(newEnt.getId()))
      return true;

    return false;
  }

  /**
   * Checks if both entities have the same ID
   *
   * @return true, if both entities have the same ID
   */
  public static <ID> boolean id(GenericEntity<ID> entity, ID id) {
    return entity.getId().equals(id);
  }

  /**
   * Checks if both entities are the same
   *
   * @return true, if both entities are null or have the same hashcode
   */
  public static <ID> boolean entity(GenericEntity<ID> oldEnt, GenericEntity<ID> newEnt) {
    // If one of them is null while the other is not
    if (!((oldEnt == null) == (newEnt == null)))
      return false;

    // If the oldFac is null (both of them must be at this point)
    if (oldEnt == null || oldEnt.equals(newEnt))
      return true;

    return false;
  }
}
