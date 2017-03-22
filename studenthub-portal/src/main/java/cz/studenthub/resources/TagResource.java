package cz.studenthub.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cz.studenthub.core.Topic;
import cz.studenthub.core.User;
import cz.studenthub.db.TopicDAO;
import cz.studenthub.db.UserDAO;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/tags")
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {

  private final UserDAO userDao;
  private final TopicDAO topicDao;

  public TagResource(UserDAO userDao, TopicDAO topicDao) {
    this.userDao = userDao;
    this.topicDao = topicDao;
  }

  @GET
  @Path("/{tag}/users")
  @UnitOfWork
  public List<User> fetchUsers(@PathParam("tag") String tag) {
    return userDao.findByTag(tag);
  }

  @GET
  @Path("/{tag}/topics")
  @UnitOfWork
  public List<Topic> fetchTopics(@PathParam("tag") String tag) {
    return topicDao.findByTag(tag);
  }
}
