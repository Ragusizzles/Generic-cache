import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hid.cache.CacheManager;


public class CacheServiceApplicationTests {
	
	private static Integer maxEntry;                  // Global property to obtain maximum number of entries.
	private static Long globalTimeOut;					// Global property to obtain maximum timeout for cache entries.
	
	private static CacheManager<String,JSONObject> gc;

	public CacheServiceApplicationTests() {
		
	}
	
	@BeforeClass
	public static void loadProperties() {	
		
		Properties properties = new Properties();		
		try {

			FileInputStream ipStr = new FileInputStream(System.getProperty("user.dir") + File.separator + "src\\test\\resources\\config.properties");
			properties.load(ipStr);
			// Load the property for 'maxEntry' and 'Global timeout' from properties file.
			String maxEntries = Optional.ofNullable(properties.getProperty("maxEntries")).orElse("100");
			String maxTimeout = Optional.ofNullable(properties.getProperty("maxTimeout")).orElse("1000");
			
			globalTimeOut = Long.parseLong(maxTimeout);
			maxEntry = Integer.parseInt(maxEntries);
			
			gc = new CacheManager<String, JSONObject>(globalTimeOut, maxEntry);

		} catch(Exception e) {
			System.out.println();
		}
	}
	
	@Test
	public void contextLoads() {
		System.out.println("Inside test 1 =>"+ globalTimeOut +" & "+ maxEntry);
	}
	
	@Test
	public void test1() {
		JSONObject obj = new JSONObject();
		obj.put("name", "foo");
		obj.put("num", new Integer(100));
		obj.put("balance", new Double(1000.21));
		obj.put("is_vip", new Boolean(true));
		
		
		Optional<Long> ttl = Optional.of(500L);
		gc.addEntry("users/Ragu",obj, ttl);
		
		Assert.assertEquals(gc.get("users/Ragu"), Optional.of(obj));
		System.out.println("Test1!");
	}
	
	@Test
	public void test2() {
		JSONObject obj = new JSONObject();		
	
		Optional<Long> ttl = Optional.of(50L);
		gc.addEntry("users/XXX",obj, ttl);
		
		Assert.assertEquals(gc.get("users/XXX"), Optional.of(obj));
		System.out.println("Test2!!");
	}
	
	@Test
	public void test3() {
		JSONObject obj = new JSONObject();		

		Optional<Long> ttl = Optional.of(5L);
		gc.addEntry("users/XXX",obj, ttl);

		Assert.assertEquals(gc.get("users/YYY"), Optional.empty());
		System.out.println("Test3!");
	}
	
	

}
