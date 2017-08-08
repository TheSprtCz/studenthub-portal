/*******************************************************************************
 *     Copyright (C) 2017  Petr Hala
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.thesishub.resources;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import net.thesishub.core.Topic;
import net.thesishub.core.User;
import net.thesishub.db.TopicDAO;
import net.thesishub.db.UserDAO;
import net.thesishub.util.PagingUtil;

@Path("/tags")
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {

  @Inject
  private UserDAO userDao;

  @Inject
  private TopicDAO topicDao;

  @GET
  @Timed
  @Path("/{tag}/users")
  @UnitOfWork
  @PermitAll
  public List<User> fetchUsers(@PathParam("tag") String tag,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {
    return PagingUtil.paging(userDao.findByTag(tag), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Timed
  @Path("/{tag}/topics")
  @UnitOfWork
  public List<Topic> fetchTopics(@PathParam("tag") String tag,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {
    return PagingUtil.paging(topicDao.findByTag(tag), startParam.get(), sizeParam.get(), response);
  }
}
