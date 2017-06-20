package net.thesishub;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.thesishub.util.EqualsTest;
import net.thesishub.util.PagingUtilTest;
import net.thesishub.util.UrlUtilTest;

@RunWith(Suite.class)
@SuiteClasses({ EqualsTest.class, PagingUtilTest.class, UrlUtilTest.class })
public class UtilTestSuite {

}
