package com.sadatmalik.customerwebsite;

import com.sadatmalik.customerwebsite.model.Authority;
import com.sadatmalik.customerwebsite.model.Customer;
import com.sadatmalik.customerwebsite.model.User;
import com.sadatmalik.customerwebsite.repositories.RoleRepository;
import com.sadatmalik.customerwebsite.services.CustomerService;
import com.sadatmalik.customerwebsite.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@SpringBootApplication
public class CustomerApplication implements CommandLineRunner {

	@Autowired
	private final CustomerService customerService;

	@Autowired
	private final UserService userService;

	@Autowired
	private final RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}

	// You can also define a run method which performs an operation at runtime
	// In this example, the run method saves some Customer data into the database for testing
	@Override
	public void run(String... args) throws Exception {

		if (customerService.getCustomers().isEmpty() && userService.findAll().isEmpty()) {

			Customer one = Customer.builder()
					.fullName("Customer 1")
					.emailAddress("customer1@gmail.com")
					.address("Customer Address One")
					.age(30)
					.build();

			Customer two = Customer.builder()
					.fullName("Customer 2")
					.emailAddress("customer2@gmail.com")
					.address("Customer Address Two")
					.age(28)
					.build();

			Customer three = Customer.builder()
					.fullName("Customer 3")
					.emailAddress("customer3@gmail.com")
					.address("Customer Address Three")
					.age(32)
					.build();

			customerService.saveAllCustomer(Arrays.asList(one, two, three));

			Authority adminAuth = Authority.builder().authority(Authority.RoleEnum.ADMIN_ROLE).build();
			Authority userAuth = Authority.builder().authority(Authority.RoleEnum.USER_ROLE).build();

			roleRepository.saveAll(Arrays.asList(adminAuth, userAuth));

			User admin = User.builder()
					.username("ADMIN")
					.password("password")
					.authorities(Collections.singletonList(adminAuth))
					.accountNonExpired(true)
					.accountNonLocked(true)
					.credentialsNonExpired(true)
					.enabled(true)
					.build();

			User userOne = User.builder()
					.username("userOne")
					.password("passwordOne")
					.authorities(Collections.singletonList(userAuth))
					.customer(one)
					.accountNonExpired(true)
					.accountNonLocked(true)
					.credentialsNonExpired(true)
					.enabled(true)
					.build();

			User userTwo = User.builder()
					.username("userTwo")
					.password("passwordTwo")
					.authorities(Collections.singletonList(userAuth))
					.customer(two)
					.accountNonExpired(true)
					.accountNonLocked(true)
					.credentialsNonExpired(true)
					.enabled(true)
					.build();

			User userThree = User.builder()
					.username("userThree")
					.password("passwordThree")
					.authorities(Collections.singletonList(userAuth))
					.customer(three)
					.accountNonExpired(true)
					.accountNonLocked(true)
					.credentialsNonExpired(true)
					.enabled(true)
					.build();

			userService.saveUser(admin);
			userService.saveUser(userOne);
			userService.saveUser(userTwo);
			userService.saveUser(userThree);

		}

	}

}
