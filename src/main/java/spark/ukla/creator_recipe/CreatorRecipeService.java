package spark.ukla.creator_recipe;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import spark.ukla.entities.*;
import spark.ukla.entities.enums.CreatorRecipeStatus;
import spark.ukla.entities.enums.Status;

import javax.persistence.EntityNotFoundException;
import java.util.List;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatorRecipeService implements ICreatorRecipeService {

    private final CreatorRecipeRepository creatorRecipeRepository;

    public CreatorRecipeService(CreatorRecipeRepository creatorRecipeRepository) {
        this.creatorRecipeRepository = creatorRecipeRepository;
    }

    @Override
    public CreatorRecipe add(String title, Video video, String username, String description) {
        try {
            CreatorRecipe newCreatorRecipe = new CreatorRecipe();
            newCreatorRecipe.setTitle(title);
            newCreatorRecipe.setDescription(description);
            newCreatorRecipe.setVideo(video);
            newCreatorRecipe.setCreator(username);
            newCreatorRecipe.setStatus(CreatorRecipeStatus.valueOf("TO_ADD"));
            return creatorRecipeRepository.save(newCreatorRecipe);
        }catch (Exception e){
            System.err.println("Failed to add recipe: " + e.getMessage());
            return null;
        }
    }
    @Override
    public Boolean updateRecipeStatus(Long id, String status){
        CreatorRecipe creatorRecipe = creatorRecipeRepository.findById(id).orElse(null);
        if(creatorRecipe!=null){
        creatorRecipe.setStatus(CreatorRecipeStatus.valueOf(status));
         creatorRecipeRepository.save(creatorRecipe);
        return true;
        }
        else
            return false;

    }

    @Override
    public List<CreatorRecipe> getAllByCreatorUsername(String username) {
        return creatorRecipeRepository.findAllByCreator(username);
    }
    @Override
    public List<CreatorRecipe> getAll(int page, int size) {
        Pageable pageable;
        if (page == 0 && size == 0) {
        return creatorRecipeRepository.findAll();}
        pageable = PageRequest.of(page - 1, size);
        Page<CreatorRecipe> firstPage = creatorRecipeRepository.findAll(pageable);
        return firstPage.getContent();
    }

    @Override
    public CreatorRecipe getbyid(Long id){
        return  creatorRecipeRepository.findById(id).orElse(null);
    }


    @Override
    public int deleteCreatorRecipeById(long id) {
        return creatorRecipeRepository.deleteCreatorRecipeById(id);
    }

    @Override
    public CreatorRecipe updateCreatorRecipe(Long id,String title, Video video, String description) {
        try{
            CreatorRecipe existingRecipe = creatorRecipeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("CreatorRecipe not found with id: "));
            existingRecipe.setTitle(title);
            existingRecipe.setDescription(description);
            existingRecipe.setVideo(video);
            return creatorRecipeRepository.save(existingRecipe);
        }catch (Exception e){
            System.err.println("Failed to update recipe: " + e.getMessage());
            return null;
        }

    }

}
