package cz.studenthub.util;

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

  public static <T> List<T> paging(ArrayList<T> list, int start, int size, HttpServletResponse response) {
    int listSize = list.size();

    if (start >= listSize)
      throw new WebApplicationException(Status.NOT_FOUND);

    if (listSize == 0)
      return list;

    int remaining = listSize - start;

    if (size > remaining || size == 0)
      size = remaining;

    response.setHeader("Pages", String.valueOf((int) Math.ceil(listSize / size)));

    return list.subList(start, start + size);
  }

  public static <T> List<T> paging(List<T> list, int start, int size, HttpServletResponse response) {
    return paging(new ArrayList<T>(list), start, size, response);
  }

  public static <T> List<T> paging(Set<T> set, int start, int size, HttpServletResponse response) {
    return paging(new ArrayList<T>(set), start, size, response);
  }
}