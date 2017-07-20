package net.thesishub.core;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotEmpty;

import net.thesishub.auth.ThesisHubPasswordEncoder;

@Entity
@Table(name = "Activations")
@NamedQueries({ @NamedQuery(name = "Activation.findByUser", query = "SELECT activation FROM Activation activation WHERE user = :user"),
  @NamedQuery(name = "Activation.findByUserAndType", query = "SELECT activation FROM Activation activation WHERE user = :user AND type = :type"),
  @NamedQuery(name = "Activation.findAll", query = "SELECT activation FROM Activation activation")})
public class Activation extends GenericEntity<Long>{

    @NotNull
    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @NotEmpty
    private String activationCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ActivationType type;
    
    public Activation(){
    }

    public Activation(User user, String activationCode, ActivationType type) {
      this.user = user;
      this.activationCode = activationCode;
      this.type = type;
    }

    public Activation(User user, ActivationType type) {
      this(user, ThesisHubPasswordEncoder.genSecret(), type);
    }

    public User getUser() {
      return user;
    }

    public void setUser(User user) {
      this.user = user;
    }

    public String getActivationCode() {
      return activationCode;
    }

    public void setActivationCode(String activationCode) {
      this.activationCode = activationCode;
    }

    public ActivationType getType() {
      return type;
    }

    public void setType(ActivationType type) {
      this.type = type;
    }

    @Override
    public int hashCode() {
      return Objects.hash(user, activationCode);
    }

    @Override
    public String toString() {
      return String.format("Activation[user=%d, code=%s]", user.getId(), activationCode);
    }
}
