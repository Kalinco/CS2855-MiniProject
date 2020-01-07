import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class LoaderTest {
  private Loader loader;
  
  @Before
  void setup() {
    this.loader = new Loader();
  }
  
  @Test
  void test() {
    loader = new Loader();
  }
  
  @Test
  void test2() {
    loader = new Loader();
    loader.loadUrls();
  }
  
  @Test
  void test3() {
    loader = new Loader();
    loader.loadUrls();
    assertEquals(loader.getUrls()[0][0],"1");
  }
  
  @Test
  void test4() {
    loader = new Loader();
    loader.loadUrls();
    assertEquals(loader.getUrls()[0][1],"google");
  }
  
  @Test
  void test5() {
    loader = new Loader();
    loader.loadUrls();
    assertEquals(loader.getUrls()[12][3],"cn");
  }
  
  @Test
  void test6() {
    loader = new Loader();
    loader.loadMappings();
    assertEquals(loader.getMappings()[0][0],"ac");
  }

  @Test
  void test7() {
    loader = new Loader();
    loader.loadMappings();
    assertEquals(loader.getMappings()[57][0],"coop");
  }
}
