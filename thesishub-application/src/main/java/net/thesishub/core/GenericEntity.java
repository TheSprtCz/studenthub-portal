package net.thesishub.core;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Generic Entity with generated and generic Id 
 * 
 * @author phala
 * @since 1.1
 *
 */
@MappedSuperclass
public abstract class GenericEntity<ID> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected ID id;

  public ID getId() {
    return id;
  }

  public void setId(ID id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }

    return Integer.compare(this.hashCode(), obj.hashCode()) == 0;
  }

}
