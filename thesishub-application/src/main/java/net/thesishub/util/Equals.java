/*******************************************************************************
 * Copyright (C) 2017  Stefan Bunciak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.thesishub.util;

import net.thesishub.core.GenericEntity;

/**
 * Helper class for easier Object comparisons.
 * 
 * @author phala
 * @since 1.1
 */
public class Equals {

  /**
   * Checks if both entities are the same
   * 
   * @param oldEnt - entity to be checked
   * @param newEnt - entity to be checked
   * @param <ID> entity class
   * @return true if both entities are null or have same ID
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
   * @param entity to be checked
   * @param id to be checked
   * @param <ID> entity class
   * @return true if both entities have the same ID
   */
  public static <ID> boolean id(GenericEntity<ID> entity, ID id) {
    return entity.getId().equals(id);
  }

  /**
   * Checks if both entities are the same
   *
   * @param <ID> entity class
   * @param oldEnt - entity to be checked
   * @param newEnt - entity to be checked
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
