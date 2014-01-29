package org.processmining.tests.gettingstarted;
import junit.framework.TestCase;

import org.junit.Test;
import org.processmining.contexts.cli.CLI;

public class GettingStartedTest extends TestCase {

  @Test
  public void testGettingStarted1() throws Throwable {
    String args[] = new String[] {"-l"};
    CLI.main(args);
  }

  @Test
  public void testGettingStarted2() throws Throwable {
    String testFileRoot = System.getProperty("test.testFileRoot", ".");
    String args[] = new String[] {"-f", testFileRoot+"/GettingStarted_Example.txt"};
    
    CLI.main(args);
  }
  
  public static void main(String[] args) {
    junit.textui.TestRunner.run(GettingStartedTest.class);
  }
  
}
