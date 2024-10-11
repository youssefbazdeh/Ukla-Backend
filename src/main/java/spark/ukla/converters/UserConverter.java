package spark.ukla.converters;

import org.springframework.stereotype.Component;
import spark.ukla.DTO.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserConverter {

	public UserDTO entityToDTO(spark.ukla.entities.User user) {
		UserDTO dto = new UserDTO();
		dto.setId(user.getId());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setBirthdate(user.getBirthdate());
		dto.setUsername(user.getUsername());
		dto.setEmail(user.getEmail());
		dto.setPassword(user.getPassword());
		dto.setRole(user.getRole());
		dto.setProfile(user.getProfile());
		dto.setFavoris(user.getFavoris());
		dto.setPlansOfWeek(user.getPlansOfWeek());
		dto.setEnabled(user.getEnabled());
		dto.setLocked(user.getLocked());
		dto.setGender(user.getGender());
		return dto;
	}

	public List<UserDTO> entitiesToDTOS(List<spark.ukla.entities.User> users) {
		return users.stream().map(x -> entityToDTO(x)).collect(Collectors.toList());
	}

	public spark.ukla.entities.User dtoToEntity(UserDTO userDTO) {
		spark.ukla.entities.User user = new spark.ukla.entities.User();
		user.setId(userDTO.getId());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setBirthdate(userDTO.getBirthdate());
		user.setUsername(userDTO.getUsername());
		user.setEmail(userDTO.getEmail());
		user.setPassword(userDTO.getPassword());
		user.setRole(userDTO.getRole());
		user.setProfile(userDTO.getProfile());
		user.setFavoris(userDTO.getFavoris());
		user.setPlansOfWeek(userDTO.getPlansOfWeek());
		user.setEnabled(userDTO.getEnabled());
		user.setLocked(userDTO.getLocked());
		user.setGender(userDTO.getGender());
		return user;
	}

	public List<spark.ukla.entities.User> dtosToEntities(List<UserDTO> usersDTO) {
		return usersDTO.stream().map(x -> dtoToEntity(x)).collect(Collectors.toList());
	}
}
