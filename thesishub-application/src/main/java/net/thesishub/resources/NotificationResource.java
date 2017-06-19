/*******************************************************************************
 *     Copyright (C) 2017  Stefan Bunciak
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

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.codahale.metrics.annotation.ExceptionMetered;

import net.thesishub.core.Notification;
import net.thesishub.core.User;
import net.thesishub.db.NotificationDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class NotificationResource {

  @Inject
  private NotificationDAO notifDao;

  @POST
  @Path("/{id}/read")
  @UnitOfWork
  public Response read(@Auth User authUser, @PathParam("id") LongParam idParam) {
    Notification notification = notifDao.findById(idParam.get());
    if (notification == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    if (notification.getTarget().equals(authUser)) {
      if (notification.isRead())
        throw new WebApplicationException("This notification is already read", Status.FOUND);

      notification.setRead(true);
      notifDao.update(notification);

      return Response.ok().build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @DELETE
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  public Response delete(@Auth User authUser, @PathParam("id") LongParam idParam) {
    Notification notification = notifDao.findById(idParam.get());
    if (notification == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    // Only target can delete his notification
    if (notification.getTarget().equals(authUser)) {
      notifDao.delete(notification);
      return Response.noContent().build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

}
