package net.thesishub.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 * Utility class to provide paging support, including num of pages, to resource
 * classes (API endpoints)
 * 
 * @author phala
 * @since 1.1
 */
public class PagingUtil {

  public static <T> List<T> paging(List<T> list, int start, int pageSize, HttpServletResponse response) {
    int listSize = list.size();

    if (listSize == 0)
      return list;

    // set response header containing num. of pages
    response.setHeader("Pages", String.valueOf(calculatePages(list, pageSize)));

    int remaining = listSize - start;

    if (pageSize > remaining || pageSize == 0)
      pageSize = remaining;

    try { 
      return list.subList(start, start + pageSize);
    } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }
  }

  public static <T> List<T> paging(Set<T> set, int start, int size, HttpServletResponse response) {
    return paging(new ArrayList<T>(set), start, size, response);
  }
 
  @SuppressWarnings("rawtypes")
  private static int calculatePages(final List list, int pageSize) {
      if (pageSize == 0)
        pageSize = list.size();
      
      // calculate how many pages there are
      if (list.size() % pageSize == 0) {
        return list.size() / pageSize;
      } else {
        return (list.size() / pageSize) + 1;
      }
  }
}