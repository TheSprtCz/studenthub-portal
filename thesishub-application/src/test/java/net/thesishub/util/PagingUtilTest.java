package net.thesishub.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import org.junit.BeforeClass;
import org.junit.Test;

public class PagingUtilTest {

  private static List<Integer> list;
  private final HttpServletResponse response = mock(HttpServletResponse.class);

  // Generate list of 20 numbers starting from 0
  @BeforeClass
  public static void generateList() {
    Integer[] numbers = new Integer[20];
    for (int i = 0; i < 20; i++) {
      numbers[i] = i;
    }
    list = Arrays.asList(numbers);
  }

  @Test
  public void testStart() {
    List<Integer> result = PagingUtil.paging(list, 10, 0, response);
    assertEquals(10, result.size());
    assertEquals((Integer) 10, result.get(0));
  }

  @Test
  public void testSize() {
    List<Integer> result = PagingUtil.paging(list, 0, 5, response);
    assertEquals(5, result.size());
    assertEquals((Integer) 0, result.get(0));
    assertEquals((Integer) 4, result.get(4));
  }

  @Test
  public void testSizeAndStart() {
    List<Integer> result = PagingUtil.paging(list, 2, 7, response);
    assertEquals(7, result.size());
    assertEquals((Integer) 2, result.get(0));
    assertEquals((Integer) 8, result.get(6));
  }

  @Test
  public void testNotFound() {
    try {
      PagingUtil.paging(list, 25, 7, response);
    } catch (WebApplicationException e) {
      assertEquals(404, e.getResponse().getStatus());
      return;
    }
    assertTrue(false);
  }

}
