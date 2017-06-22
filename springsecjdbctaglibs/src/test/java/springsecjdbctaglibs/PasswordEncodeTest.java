package springsecjdbctaglibs;

import org.junit.Test;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncodeTest {

	@Test
	public void test() {
		String password = "udit";
		Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
		String encodedPassword = passwordEncoder.encodePassword(password, null);
		System.out.println("encodedPassword = " + encodedPassword);
	}
	
	@Test
	public void testBcrypt() {
		String password = "udit123";
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(password);
		System.out.println("encodedPassword = " + encodedPassword);
	}
	
	@Test
	public void testBcrypt2() {
		String password = "ravi123";
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(password);
		System.out.println("encodedPassword = " + encodedPassword);
	}
	
	@Test
	public void testBcrypt3() {
		String password = "amit123";
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(password);
		System.out.println("encodedPassword = " + encodedPassword);
	}
	
	@Test
	public void testBcrypt4() {
		String password = "mahesh123";
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(password);
		System.out.println("encodedPassword = " + encodedPassword);
	}

}
