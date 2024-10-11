package spark.ukla.creator_feature;

import org.springframework.stereotype.Component;
import spark.ukla.DTO.RecipeCardDTO;
import spark.ukla.repositories.projection.CreatorProjection;

import java.util.List;

@Component
public class CreatorConverter {

    public CreatorDTOForRecipe projectionToDTO(CreatorProjection creatorProjection){
        CreatorDTOForRecipe creatorDTOForRecipe = new CreatorDTOForRecipe();
        creatorDTOForRecipe.setId(creatorProjection.getId());
        creatorDTOForRecipe.setFirstName(creatorProjection.getFirstName());
        creatorDTOForRecipe.setDescription(creatorProjection.getDescription());
        creatorDTOForRecipe.setImage(creatorProjection.getImage());
        creatorDTOForRecipe.setLastName(creatorProjection.getLastName());
        return creatorDTOForRecipe;
    }

    public CreatorDTOForRecipe entityToCreatorDTOForRecipe(Creator creator) {
        CreatorDTOForRecipe dto = new CreatorDTOForRecipe();
        dto.setId(creator.getId());
        dto.setFirstName(creator.getFirstName());
        dto.setDescription(creator.getDescription());
        dto.setImage(creator.getImage());
        dto.setLastName(creator.getLastName());
        return dto;
    }
    public CreatorDTO entityToDTO(Creator creator) {
        CreatorDTO dto = new CreatorDTO();
        dto.setId(creator.getId());
        dto.setFirstName(creator.getFirstName());
        dto.setDescription(creator.getDescription());
        dto.setImage(creator.getImage());
        dto.setLastName(creator.getLastName());
        dto.setLastName(creator.getLastName());
        dto.setBirthdate(creator.getBirthdate());
        dto.setUsername(creator.getUsername());
        dto.setEmail(creator.getEmail());
        dto.setPassword(creator.getPassword());
        dto.setRole(creator.getRole());
        dto.setProfile(creator.getProfile());
        dto.setFavoris(creator.getFavoris());
        dto.setPlansOfWeek(creator.getPlansOfWeek());
        dto.setEnabled(creator.getEnabled());
        dto.setLocked(creator.getLocked());
        dto.setGender(creator.getGender());
        return dto;
    }

    public CreatorDTOForProfile entityToCreatorDTO(Creator creator, boolean followed, List<RecipeCardDTO> recipeCardDTOList) {
        CreatorDTOForProfile dto = new CreatorDTOForProfile();
        dto.setId(creator.getId());
        dto.setFirstName(creator.getFirstName());
        dto.setDescription(creator.getDescription());
        dto.setImage(creator.getImage());
        dto.setLastName(creator.getLastName());
        dto.setUsername(creator.getUsername());
        dto.setFollowed(followed);
        dto.setCreatedRecipe(recipeCardDTOList);
        dto.setFollowersNumber(creator.getFollowersNumber());
        return dto;
    }

}
