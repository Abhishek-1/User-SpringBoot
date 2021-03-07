package com.projects.waterloo.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.projects.waterloo.app.ws.exception.UserServiceException;
import com.projects.waterloo.app.ws.io.entity.AddressEntity;
import com.projects.waterloo.app.ws.io.entity.UserEntity;
import com.projects.waterloo.app.ws.io.repository.UserRepository;
import com.projects.waterloo.app.ws.shared.Utils;
import com.projects.waterloo.app.ws.shared.dto.AddressDTO;
import com.projects.waterloo.app.ws.shared.dto.UserDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	UserRepository userRepository;

	@Mock
	Utils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	String userId = "gfhfbfjhgfn";
	
	String encryptdPassword = "gfjffghgjnghjg";
	
	UserEntity userEntity;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Abhishek");
		userEntity.setLastName("Ranjan");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptdPassword);
		userEntity.setEmail("test@test.com");
		userEntity.setAddresses(getAddressEntity());
	}
	

	@Test
	final void testFindUser() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = userService.findUser("test@test.com");
		assertNotNull(userDto);
		assertEquals("Abhishek", userDto.getFirstName());
		assertEquals("Ranjan", userDto.getLastName());
		assertEquals(encryptdPassword, userDto.getEncryptedPassword());
		assertEquals(userId, userDto.getUserId());
	}
	
	@Test
	final void testFindUser_UsernameNotFoundException()
	{
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		
		assertThrows(UsernameNotFoundException.class, 
				() -> {
					userService.findUser("test@test.com");
				});
	}
	
	@Test
	final void testCreateUser_UserServiceException() {
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = new UserDto();
		userDto.setFirstName("Abhishek");
		userDto.setLastName("Ranjan");
		userDto.setPassword("Abcvgbh1234");
		userDto.setEmail("test@test.com");
		
		assertThrows(UserServiceException.class, () -> {
			userService.createUser(userDto);
		});
	}
	
	@Test
	final void testCreateUser()
	{
		
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("gdghdfnfbf");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptdPassword);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		
		UserDto userDto = new UserDto();
		userDto.setFirstName("Abhishek");
		userDto.setLastName("Ranjan");
		userDto.setPassword("Abcvgbh1234");
		userDto.setEmail("test@test.com");
		userDto.setAddresses(getAddress());
		
		
		UserDto storedUserDetails = userService.createUser(userDto);
		
		assertNotNull(storedUserDetails);
		assertEquals("Abhishek", storedUserDetails.getFirstName());
		assertEquals("Ranjan", storedUserDetails.getLastName());
		assertEquals(encryptdPassword, storedUserDetails.getEncryptedPassword());
		assertNotNull(storedUserDetails.getUserId());
		assertEquals(userId, storedUserDetails.getUserId());
		assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
		verify(utils, times(2)).generateAddressId(30);
		verify(bCryptPasswordEncoder, times(1)).encode("Abcvgbh1234");
		verify(userRepository, times(1)).save(any(UserEntity.class));
		
	}
	
	private List<AddressDTO> getAddress() {
		AddressDTO shippingAddress = new AddressDTO();
		shippingAddress.setType("shipping");
		shippingAddress.setCity("Vancouver");
		shippingAddress.setCountry("Canada");
		shippingAddress.setPostalCode("N2L0J8");
		shippingAddress.setStreetName("Real Street");
		
		AddressDTO billingAddress = new AddressDTO();
		billingAddress.setType("shipping");
		billingAddress.setCity("Waterloo");
		billingAddress.setCountry("Canada");
		billingAddress.setPostalCode("ABC123");
		billingAddress.setStreetName("Fake Street");
		
		List<AddressDTO> addresses = new ArrayList<>();
		addresses.add(shippingAddress);
		addresses.add(billingAddress);
		return addresses;
	}
	
	private List<AddressEntity> getAddressEntity(){
		
		List<AddressDTO> addresses = getAddress();
		
		Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
		ModelMapper modelMapper = new ModelMapper();
		List<AddressEntity> addressesEntity = modelMapper.map(addresses, listType);
		
		return addressesEntity;
	}

}
