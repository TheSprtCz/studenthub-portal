package net.thesishub.core;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "Notifications")
@NamedQueries({ @NamedQuery(name = "Notification.findByUser", query = "SELECT notification FROM Notification notification WHERE notification.target = :user") })
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User target;

  @ManyToOne
  @NotNull
  private User from;

  @NotNull
  private Date date;
  private String content;

  @NotNull
  @Enumerated(EnumType.STRING)
  private NotificationType type;

  private boolean read = false;

  public Notification(){
  }

  public Notification(User target, User from, String content, NotificationType type) {
    this(from, content, type);
    this.target = target;
  }

  public Notification(User from, String content, NotificationType type) {
    this.from = from;
    this.content = content;
    this.type = type;
    this.date = new Date();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getTarget() {
    return target;
  }

  public void setTarget(User target) {
    this.target = target;
  }

  public User getFrom() {
    return from;
  }

  public void setFrom(User from) {
    this.from = from;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public NotificationType getType() {
    return type;
  }

  public void setType(NotificationType type) {
    this.type = type;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, content, target, date, type);
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

  @Override
  public String toString() {
    return String.format("Notification[id=%s, target=%s, type=%s, date=%s]", id, target.getName(), type, date.toString());
  }
}
