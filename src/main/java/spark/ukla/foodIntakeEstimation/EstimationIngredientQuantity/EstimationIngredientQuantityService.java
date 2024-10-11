package spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spark.ukla.entities.Image;
import spark.ukla.foodIntakeEstimation.EstimationIngredient.EstimationIngredient;
import spark.ukla.foodIntakeEstimation.EstimationIngredient.EstimationIngredientRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EstimationIngredientQuantityService implements IEstimationIngredientQuantityService {
    private final EstimationIngredientQuantityRepository estimationIngredientQuantityRepository;
    private final EstimationIngredientRepository estimationIngredientRepository;

    public EstimationIngredientQuantityService(EstimationIngredientQuantityRepository estimationIngredientQuantityRepository, EstimationIngredientRepository estimationIngredientRepository) {
        this.estimationIngredientQuantityRepository = estimationIngredientQuantityRepository;
        this.estimationIngredientRepository = estimationIngredientRepository;
    }

    @Override
    public List<EstimationIngredientQuantity> getAll(Long idEstimationIngredient) {

        return (List<EstimationIngredientQuantity>) estimationIngredientQuantityRepository.getAllByEstimationIngredient_Id(idEstimationIngredient);
    }


    @Override
    public Boolean add(Long idEstimationIngredient,EstimationIngredientQuantity estimationIngredientQuantity, Image image) {
        EstimationIngredient estimationIngredient = estimationIngredientRepository.findById(idEstimationIngredient).get();
        estimationIngredientQuantity.setEstimationIngredient(estimationIngredient);
        estimationIngredientQuantity.setImage(image);
        estimationIngredientQuantityRepository.save(estimationIngredientQuantity);
        return true;
    }

    @Override
    public boolean delete(Long id) {
        estimationIngredientQuantityRepository.deleteById(id);
        return true;
    }

    @Override
    public Boolean update(EstimationIngredientQuantity estimationIngredientQuantity) throws NotFoundException {
        boolean status = false;
        if(estimationIngredientQuantity == null || estimationIngredientQuantity.getId() == null){
            throw new NotFoundException("Ingredient or ID must not be null! ");

        }
        Optional<EstimationIngredientQuantity> optionalestimationIngredientQuantity = estimationIngredientQuantityRepository.findById(estimationIngredientQuantity.getId());
        if(!optionalestimationIngredientQuantity.isPresent()){
            throw new NotFoundException("estimationIngredientQuantity with ID: " + estimationIngredientQuantity.getId() + " does not exist. ");
        }
        if(optionalestimationIngredientQuantity.isPresent()){
        Long id = estimationIngredientQuantity.getId();
        int Quantity =estimationIngredientQuantity.getQuantity();
        estimationIngredientQuantityRepository.updateName(id, Quantity);
        status=true;}
        return status ;
    }

}
