package spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity;

import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spark.ukla.entities.Image;
import spark.ukla.foodIntakeEstimation.EstimationIngredient.EstimationIngredient;
import spark.ukla.foodIntakeEstimation.EstimationIngredient.EstimationIngredientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EstimationIngredientQuantityTest {
    @Mock
    private EstimationIngredientQuantityRepository estimationIngredientQuantityRepository;

    @Mock
    private EstimationIngredientRepository estimationIngredientRepository;

    @InjectMocks
    private EstimationIngredientQuantityService estimationIngredientQuantityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenEstimationIngredientId_whenGetAll_thenReturnEstimationIngredientQuantityList() {
        Long idEstimationIngredient = 1L;
        List<EstimationIngredientQuantity> expectedQuantities = new ArrayList<>();
        // Add expected quantities to the list

        when(estimationIngredientQuantityRepository.getAllByEstimationIngredient_Id(idEstimationIngredient))
                .thenReturn(expectedQuantities);

        List<EstimationIngredientQuantity> actualQuantities = estimationIngredientQuantityService.getAll(idEstimationIngredient);

        assertEquals(expectedQuantities, actualQuantities);
        verify(estimationIngredientQuantityRepository, times(1))
                .getAllByEstimationIngredient_Id(idEstimationIngredient);
    }

    @Test
    void givenEstimationIngredientIdAndEstimationIngredientQuantityObject_whenGetAll_thenReturnTrue() {
        Long idEstimationIngredient = 1L;
        EstimationIngredientQuantity estimationIngredientQuantity = new EstimationIngredientQuantity();
        Image image = new Image();

        EstimationIngredient estimationIngredient = new EstimationIngredient();
        estimationIngredient.setId(idEstimationIngredient);
        when(estimationIngredientRepository.findById(idEstimationIngredient))
                .thenReturn(Optional.of(estimationIngredient));

        boolean result = estimationIngredientQuantityService.add(idEstimationIngredient, estimationIngredientQuantity, image);

        assertTrue(result);
        assertEquals(estimationIngredient, estimationIngredientQuantity.getEstimationIngredient());
        assertEquals(image, estimationIngredientQuantity.getImage());
        verify(estimationIngredientQuantityRepository, times(1)).save(estimationIngredientQuantity);
    }

    @Test
    void givenEstimationIngredientQuantityId_whenDelete_thenReturnTrue() throws NotFoundException {
        Long id = 1L;

        boolean result = estimationIngredientQuantityService.delete(id);

        assertTrue(result);
        verify(estimationIngredientQuantityRepository, times(1)).deleteById(id);
    }

    @Test
    void givenEstimationIngredientQuantityIdAndNameAndQuantity_whenUpdateName_thenReturnTrue() throws NotFoundException {
        Long id = 1L;
        EstimationIngredientQuantity estimationIngredientQuantity = new EstimationIngredientQuantity();
        estimationIngredientQuantity.setId(id);
        estimationIngredientQuantity.setQuantity(10);

        Optional<EstimationIngredientQuantity> optionalEstimationIngredientQuantity = Optional.of(estimationIngredientQuantity);
        when(estimationIngredientQuantityRepository.findById(id))
                .thenReturn(optionalEstimationIngredientQuantity);
        when(estimationIngredientQuantityRepository.updateName(id, estimationIngredientQuantity.getQuantity()))
                .thenReturn(1);

        boolean result = estimationIngredientQuantityService.update(estimationIngredientQuantity);

        assertTrue(result);
        verify(estimationIngredientQuantityRepository, times(1)).findById(id);
        verify(estimationIngredientQuantityRepository, times(1))
                .updateName(id,estimationIngredientQuantity.getQuantity());
    }
}
