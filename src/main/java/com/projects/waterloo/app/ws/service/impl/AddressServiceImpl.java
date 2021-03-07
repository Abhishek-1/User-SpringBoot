package com.projects.waterloo.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.waterloo.app.ws.io.entity.AddressEntity;
import com.projects.waterloo.app.ws.io.entity.UserEntity;
import com.projects.waterloo.app.ws.io.repository.AddressRepository;
import com.projects.waterloo.app.ws.io.repository.UserRepository;
import com.projects.waterloo.app.ws.service.AddressService;
import com.projects.waterloo.app.ws.shared.dto.AddressDTO;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AddressRepository addressRepository;

	@Override
	public List<AddressDTO> getAddresses(String userId) {
		List<AddressDTO> returnValue = new ArrayList<>();

		UserEntity userEntity = userRepository.findByUserId(userId);

		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);

		ModelMapper modelMapper = new ModelMapper();

		for (AddressEntity address : addresses) {

			returnValue.add(modelMapper.map(address, AddressDTO.class));

		}

		return returnValue;
	}

	@Override
	public AddressDTO getAddress(String addressId) {

		AddressDTO returnValue = new AddressDTO();

		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

		BeanUtils.copyProperties(addressEntity, returnValue);

		return returnValue;
	}

}
