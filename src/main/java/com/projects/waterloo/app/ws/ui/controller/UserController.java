package com.projects.waterloo.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projects.waterloo.app.ws.exception.UserServiceException;
import com.projects.waterloo.app.ws.service.AddressService;
import com.projects.waterloo.app.ws.service.UserService;
import com.projects.waterloo.app.ws.shared.dto.AddressDTO;
import com.projects.waterloo.app.ws.shared.dto.UserDto;
import com.projects.waterloo.app.ws.ui.model.request.UserDetailsRequestModel;
import com.projects.waterloo.app.ws.ui.model.response.AddressRest;
import com.projects.waterloo.app.ws.ui.model.response.ErrorMessages;
import com.projects.waterloo.app.ws.ui.model.response.OperationStatusModel;
import com.projects.waterloo.app.ws.ui.model.response.RequestOperationStatus;
import com.projects.waterloo.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressService;

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String id) {


		UserDto userDto = userService.getUserByUserId(id);
		
		ModelMapper modelMapper = new ModelMapper();
		UserRest returnValue = modelMapper.map(userDto, UserRest.class);

		return returnValue;

	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserRest returnValue = new UserRest();

		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);

		returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnValue = new UserRest();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto createdUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(createdUser, returnValue);

		return returnValue;
	}

	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {

		OperationStatusModel returValue = new OperationStatusModel();
		returValue.setOperationName(RequestOperationName.DELETE.name());
		userService.deleteUser(id);
		returValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returValue;
	}

	@GetMapping
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {
		List<UserRest> returnVal = new ArrayList<>();

		List<UserDto> users = userService.getUsers(page, limit);

		for (UserDto user : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(user, userModel);
			returnVal.add(userModel);
		}

		return returnVal;

	}
	
	//Use of CollectionModel when we are returning list of Entity and we need to return links along with that
	
	@GetMapping(path="/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public CollectionModel<AddressRest> getAddresses(@PathVariable String id){
		
		List<AddressRest> returnValue = new ArrayList<>();
		
		List<AddressDTO> addressesDto = addressService.getAddresses(id);
		
		ModelMapper modelMapper = new ModelMapper();
		
		if(addressesDto != null && !addressesDto.isEmpty()) {
			Type listType = new TypeToken<List<AddressRest>>() {}.getType();
			returnValue = modelMapper.map(addressesDto, listType);
			for(AddressRest address: returnValue) {
				Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddress(id, address.getAddressId()))
						.withSelfRel();
				address.add(selfLink);
			}
		}
		
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
		
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddresses(id))
				.withSelfRel();
		
		return CollectionModel.of(returnValue, userLink, selfLink);
		
		
	}
	
//Using RepresentationModel to send over the links in response
	
//	@GetMapping(path="/{id}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
//	public AddressRest getAddress(@PathVariable String id, @PathVariable String addressId ){
//			
//			AddressRest returnValue = new AddressRest();
//			
//			AddressDTO addressesDto = addressService.getAddress(addressId);
//			
//			BeanUtils.copyProperties(addressesDto, returnValue);
//			
//			Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
//			Link userAddressLink = WebMvcLinkBuilder.linkTo(UserController.class)
//					.slash(id)
//					.slash("addresses")
//					.withRel("addresses");
//			Link selfLink = WebMvcLinkBuilder.linkTo(UserController.class)
//					.slash(id)
//					.slash("addresses")
//					.slash(addressId)
//					.withSelfRel();
//			
//			returnValue.add(userLink);
//			returnValue.add(userAddressLink);
//			returnValue.add(selfLink);
//			
//			return returnValue;
//			
//			
//		}

	
//	Using EntityModel to send over the links in response
	@GetMapping(path="/{id}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public EntityModel<AddressRest> getAddress(@PathVariable String id, @PathVariable String addressId ){
			
			AddressRest returnValue = new AddressRest();
			
			AddressDTO addressesDto = addressService.getAddress(addressId);
			
			BeanUtils.copyProperties(addressesDto, returnValue);
			
			Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
			Link userAddressLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddresses(id))
					.withRel("addresses");
			Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddress(id, addressId))
					.withSelfRel();
			
			return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressLink, selfLink));
			
		}
}
