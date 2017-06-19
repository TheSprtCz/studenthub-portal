package net.thesishub.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thesishub.core.Notification;
import net.thesishub.core.NotificationType;
import net.thesishub.core.Project;
import net.thesishub.core.Task;
import net.thesishub.core.Topic;
import net.thesishub.core.TopicApplication;
import net.thesishub.core.User;
import net.thesishub.db.NotificationDAO;
import net.thesishub.db.TopicApplicationDAO;
import net.thesishub.db.TopicDAO;
import net.thesishub.db.UserDAO;

public class NotificationUtil {

  @Inject
  private MailClient mail;

  @Inject
  private NotificationDAO notifDao;

  @Inject
  private TopicDAO topicDao;

  @Inject
  private UserDAO userDao;

  @Inject
  private TopicApplicationDAO appDao;

  @Inject
  private ObjectMapper mapper;

  @Inject
  private UrlUtil urlUtil;

  private void saveNotification(Notification notification, Map<String, Object> args, String template, String title) {
    if (notifDao.create(notification) == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    User target = notification.getTarget();
    if (target.isSendMail()) {
      mail.sendMessage(target.getEmail(), title, template, args);
    }
  }

  // Creates single notification
  private Notification createNotification(User target, User from, NotificationType type, Map<String, Object> objects) throws JsonProcessingException {
    String content = mapper.writeValueAsString(objects);
    return new Notification(target, from, content, type);
  }

  // Creates base notitifcation where targets are gonna be added later
  private Notification createBaseNotification(User from, NotificationType type, Map<String, Object> objects) throws JsonProcessingException {
    String content = mapper.writeValueAsString(objects);
    return new Notification(from, content, type);   
  }

  // Creates and saves notifications from base and list of users
  private void saveNotifications(Notification base, Map<String, Object> args, String template, String title, List<User> targets) {
    for (User user : targets) {
      base.setTarget(user);
      saveNotification(base, args, template, title);
    }
  }

  public void applicationCreated(TopicApplication application) throws JsonProcessingException {
    Topic topic = topicDao.findById(application.getTopic().getId());
    if (topic == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    User student = userDao.findById(application.getStudent().getId());
    if (student == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    // Set arguments
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("url", urlUtil.getUri("applications", application.getId().toString()));
    args.put("student-name", student.getName());
    args.put("student-url", urlUtil.getUri("users", student.getId().toString()));
    args.put("topic-name", topic.getTitle());
    args.put("topic-url", urlUtil.getUri("topics", topic.getId().toString()));
    Notification base = createBaseNotification(student, NotificationType.APPLICATION_CREATED, args);

    // Set all targets
    List<User> targets = new ArrayList<User>();
    targets.add(topic.getCreator());
    for (User supervisor : topic.getAcademicSupervisors()) {
      if (supervisor.getFaculty().getId().equals(student.getFaculty().getId())) {
        targets.add(supervisor);
      }
    }

    // Save notifications
    saveNotifications(base, args, "appCreated.html", "New Application was created for your Topic ${topic-name}", targets);
  }

  public void taskCreated(Task task, User submitter) throws JsonProcessingException {
    TopicApplication app = appDao.findById(task.getApplication().getId());
    if (app == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    Map<String, Object> args = new HashMap<String, Object>();
    args.put("task-url", urlUtil.getUri("tasks", task.getId().toString()));
    args.put("task-title", task.getTitle());
    args.put("submitter-name", submitter.getName());
    args.put("submitter-url", urlUtil.getUri("users", submitter.getId().toString()));
    args.put("app-name", app.getTopic().getTitle());
    args.put("app-url", urlUtil.getUri("applications", app.getId().toString()));
    Notification base = createBaseNotification(submitter, NotificationType.TASK_CREATED, args);

    List<User> targets = new ArrayList<User>();
    // Add Student into targets
    if (!app.getStudent().equals(submitter)) {
      targets.add(app.getStudent());
    }

    User techLeader = app.getTechLeader();
    if (techLeader != null && !techLeader.equals(submitter)) {
      targets.add(techLeader);
    }

    User supervisor = app.getAcademicSupervisor();
    if (supervisor != null && !supervisor.equals(submitter)) {
      targets.add(supervisor);
    }
    saveNotifications(base, args, "taskCreated.html", "New task was created", targets);
  }

  // All info is taken from old application before it was updated
  public void applicationGraded(TopicApplication oldApp, TopicApplication app, User submitter) throws JsonProcessingException {
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("submitter-name", submitter.getName());
    args.put("submitter-url", urlUtil.getUri("users", submitter.getId().toString()));
    args.put("app-name", oldApp.getTopic().getTitle());
    args.put("app-url", urlUtil.getUri("applications", oldApp.getId().toString()));
    args.put("grade", app.getGrade().toString());
    Notification base = createBaseNotification(submitter, NotificationType.APPLICATION_GRADED, args);

    List<User> targets = new ArrayList<User>();
    targets.add(oldApp.getStudent());
    if (!Equals.id(oldApp.getStudent(), app.getStudent())) {
      User student = userDao.findById(app.getStudent().getId());
      if (student == null)
        throw new WebApplicationException(Status.NOT_FOUND);

      targets.add(student);
    }
    saveNotifications(base, args, "appGraded.html", "Your application has received grade ${grade}", targets);
  }

  public void supervisorAdded(Topic topic, User submitter) throws JsonProcessingException {
    User creator = userDao.findById(topic.getCreator().getId());
    if (creator == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    Map<String, Object> args = new HashMap<String, Object>();
    args.put("submitter-name", submitter.getName());
    args.put("submitter-url", urlUtil.getUri("users", submitter.getId().toString()));
    args.put("topic-name", topic.getTitle());
    args.put("topic-url", urlUtil.getUri("topics", topic.getId().toString()));
    saveNotification(createNotification(creator, submitter, NotificationType.SUPERVISOR_ADDED, args), args, "supervisorAdded.html", "Supervisor was added to your Topic ${topic-name}");
  }

  // All info is taken from old application before it was updated
  public void statusChanged(TopicApplication oldApp, TopicApplication app, User submitter) throws JsonProcessingException {
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("submitter-name", submitter.getName());
    args.put("submitter-url", urlUtil.getUri("users", submitter.getId().toString()));
    args.put("app-name", oldApp.getTopic().getTitle());
    args.put("app-url", urlUtil.getUri("topics", oldApp.getTopic().getId().toString()));
    args.put("before", oldApp.getStatus().toString());
    args.put("after", app.getStatus().toString());
    Notification base = createBaseNotification(submitter, NotificationType.STATUS_CHANGED, args);

    List<User> targets = new ArrayList<User>();
    targets.add(oldApp.getStudent());
    if (!Equals.id(oldApp.getStudent(), app.getStudent())) {
      User student = userDao.findById(app.getStudent().getId());
      if (student == null)
        throw new WebApplicationException(Status.NOT_FOUND);

      targets.add(student);
    }

    saveNotifications(base, args, "statusChanged.html", "Status of your Application ${app-name} was changed", targets);
  }

  // All info is taken from old application before it was updated
  public void applicationUpdated(TopicApplication oldApp, TopicApplication app, User submitter) throws JsonProcessingException {
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("submitter-name", submitter.getName());
    args.put("submitter-url", urlUtil.getUri("users", submitter.getId().toString()));
    args.put("app-name", oldApp.getTopic().getTitle());
    args.put("app-url", urlUtil.getUri("applications", oldApp.getId().toString()));
    Notification base = createBaseNotification(submitter, NotificationType.APPLICATION_UPDATED, args);

    List<User> targets = new ArrayList<User>();
    targets.add(oldApp.getStudent());
    if (!Equals.id(oldApp.getStudent(), app.getStudent())) {
      User student = userDao.findById(app.getStudent().getId());
      if (student == null)
        throw new WebApplicationException(Status.NOT_FOUND);

      targets.add(student);
    }

    saveNotifications(base, args, "appUpdated.html", "Your Application ${app-name} was updated", targets);
  }

  // All info is taken from old application before it was updated
  public void projectUpdated(Project oldProject, Project project, User submitter) throws JsonProcessingException {
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("submitter-name", submitter.getName());
    args.put("submitter-url", urlUtil.getUri("users", submitter.getId().toString()));
    args.put("project-name", oldProject.getName());
    args.put("project-url", urlUtil.getUri("projects", oldProject.getId().toString()));
    Notification base = createBaseNotification(submitter, NotificationType.PROJECT_UPDATED, args);

    List<User> targets = new ArrayList<User>();
    for (User creator : oldProject.getCreators()) {
      if (!creator.equals(submitter))
        targets.add(creator);
    }
    for (User user : project.getCreators()) {
      User creator = userDao.findById(user.getId());
      if (creator == null)
        throw new WebApplicationException(Status.NOT_FOUND);

      if (!targets.contains(creator) && !creator.equals(submitter))
        targets.add(creator);

    }

    saveNotifications(base, args, "projectUpdated.html", "Project ${project-name} was updated", targets);
  }
}
