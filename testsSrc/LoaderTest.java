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
    loader.loadURLs();
  }
  
  @Test
  void test3() {
    loader = new Loader();
    loader.loadURLs();
    assertEquals(loader.getURLs().get(0)[0],"1");
  }
  
  @Test
  void test4() {
    loader = new Loader();
    loader.loadURLs();
    assertEquals(loader.getURLs().get(0)[1],"google");
  }
  
  @Test
  void test5() {
    loader = new Loader();
    loader.loadURLs();
    assertEquals(loader.getURLs().get(12)[3],"cn");
  }
  
  @Test
  void test6() {
    loader = new Loader();
    loader.loadMappings();
    assertEquals(loader.getMappings().get(0)[0],"ac");
  }

  @Test
  void test7() {
    loader = new Loader();
    loader.loadMappings();
    assertEquals(loader.getMappings().get(57)[0],"coop");
  }
}
