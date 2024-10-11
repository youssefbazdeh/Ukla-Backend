package spark.ukla.foodIntakeEstimation.EstimationRecipe;

import javassist.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity.EstimationIngredientQuantity;
import spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity.EstimationIngredientQuantityRepository;
import spark.ukla.foodIntakeEstimation.EstimationMeal.EstimationMeal;
import spark.ukla.foodIntakeEstimation.EstimationMeal.EstimationMealRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstimationRecipeTest {

    @Mock
    private EstimationRecipeRepository estimationRecipeRepository;

    @Mock
    private EstimationIngredientQuantityRepository estimationIngredientQuantityRepository;

    @Mock
    private EstimationMealRepository estimationMealRepository;
    @InjectMocks
    private EstimationRecipeService estimationRecipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        estimationRecipeService = new EstimationRecipeService(estimationRecipeRepository, estimationIngredientQuantityRepository, estimationMealRepository);
    }

    @DisplayName("Getting all the estimationRecipes method")
    @Test
    void givenEstimationRecipesList_whenGetAll_thenReturnEstimationRecipesList() {
        // Arrange
        List<EstimationRecipe> expectedRecipes = new ArrayList<>();
        when(estimationRecipeRepository.findAll()).thenReturn(expectedRecipes);

        // Act
        List<EstimationRecipe> result = estimationRecipeService.getAll();
        // Assert
        Assertions.assertEquals(expectedRecipes, result);
        verify(estimationRecipeRepository, times(1)).findAll();

    }

    @DisplayName("Adding EstimationRecipe and assign it to an estimationMeal")
    @Test
    void givenEstimationRecipe_whenAdd_thenReturnEstimationRecipe() {
        // Arrange
        EstimationRecipe estimationRecipe = new EstimationRecipe();
        Long idEstimationMeal = 1L;
        EstimationMeal estimationMeal = new EstimationMeal();
        when(estimationMealRepository.findById(idEstimationMeal)).thenReturn(Optional.of(estimationMeal));
        when(estimationRecipeRepository.save(estimationRecipe)).thenReturn(estimationRecipe);

        // Act
        EstimationRecipe result = estimationRecipeService.add(estimationRecipe, idEstimationMeal);

        // Assert
        Assertions.assertEquals(estimationMeal, estimationRecipe.getEstimationMeal());
        Assertions.assertEquals(estimationRecipe, result);
        verify(estimationMealRepository).findById(idEstimationMeal);
        verify(estimationRecipeRepository).save(estimationRecipe);
    }

    @DisplayName("Deleting an EstimationRecipe")
    @Test
    void givenEstimationRecipeId_whenDelete_thenReturnTrue() {
        // Arrange
        Long id = 1L;

        // Act
        boolean result = estimationRecipeService.delete(id);

        // Assert
        Assertions.assertTrue(result);
        verify(estimationRecipeRepository).deleteById(id);
    }

    @DisplayName("Updating an EstimationRecipe's name and frequency")
    @Test
    void givenEstimationRecipe_whenUpdate_thenReturnTrue() throws NotFoundException {
        // Arrange
        EstimationRecipe estimationRecipe = new EstimationRecipe();
        estimationRecipe.setId(1L);
        estimationRecipe.setName("Recipe");
        estimationRecipe.setFrequency(3);
        when(estimationRecipeRepository.findById(estimationRecipe.getId())).thenReturn(Optional.of(estimationRecipe));

        // Act
        Boolean result = estimationRecipeService.update(estimationRecipe);

        // Assert
        Assertions.assertTrue(result);
        verify(estimationRecipeRepository).findById(estimationRecipe.getId());
        verify(estimationRecipeRepository).update(
                estimationRecipe.getId(),
                estimationRecipe.getName(),
                estimationRecipe.getFrequency()
        );
    }

    @DisplayName("Adding a list of EstimationIngredientQuantity to an EstimationRecipe")
    @Test
    void givenEstimationRecipeIdAndEstimationIngredientQuantityIds_whenAddListQuantity_thenReturnTrue() {
        // Arrange
        Long id = 1L;
        EstimationRecipe estimationRecipe = new EstimationRecipe();
        estimationRecipe.setId(id);
        estimationRecipe.setEstimationIngredientQuantities(new ArrayList<>()); // Initialize the collection

        List<Long> estimationIngredientQuantityIds = Arrays.asList(2L, 3L);
        EstimationIngredientQuantity estimationIngredientQuantity1 = new EstimationIngredientQuantity();
        EstimationIngredientQuantity estimationIngredientQuantity2 = new EstimationIngredientQuantity();
        when(estimationIngredientQuantityRepository.findById(2L)).thenReturn(Optional.of(estimationIngredientQuantity1));
        when(estimationIngredientQuantityRepository.findById(3L)).thenReturn(Optional.of(estimationIngredientQuantity2));
        when(estimationRecipeRepository.findById(id)).thenReturn(Optional.of(estimationRecipe));

        // Act
        Boolean result = estimationRecipeService.addListQuantity(id, estimationIngredientQuantityIds);

        // Assert
        Assertions.assertTrue(result);
        List<EstimationIngredientQuantity> estimationIngredientQuantities = estimationRecipe.getEstimationIngredientQuantities();
        Assertions.assertEquals(2, estimationIngredientQuantities.size());
        Assertions.assertTrue(estimationIngredientQuantities.contains(estimationIngredientQuantity1));
        Assertions.assertTrue(estimationIngredientQuantities.contains(estimationIngredientQuantity2));
        verify(estimationRecipeRepository).findById(id);
        verify(estimationRecipeRepository).save(estimationRecipe);
    }

    @DisplayName("Deleting an EstimationIngredientQuantity from an EstimationRecipe")
    @Test
    void givenEstimationRecipeIdAndEstimationIngredientQuantityId_whenDeleteQuantity_thenReturnTrue() {
        // Arrange
        Long estimationRecipeId = 1L;
        Long estimationIngredientQuantityId = 2L;

        EstimationIngredientQuantity estimationIngredientQuantity = new EstimationIngredientQuantity();
        EstimationRecipe estimationRecipe = new EstimationRecipe();
        estimationRecipe.setId(estimationRecipeId);
        estimationRecipe.setEstimationIngredientQuantities(new ArrayList<>());
        estimationRecipe.getEstimationIngredientQuantities().add(estimationIngredientQuantity);

        when(estimationIngredientQuantityRepository.findById(estimationIngredientQuantityId)).thenReturn(Optional.of(estimationIngredientQuantity));
        when(estimationRecipeRepository.findById(estimationRecipeId)).thenReturn(Optional.of(estimationRecipe));

        // Act
        boolean result = estimationRecipeService.deleteQuantity(estimationRecipeId, estimationIngredientQuantityId);

        // Assert
        Assertions.assertTrue(result);
        Assertions.assertTrue(estimationRecipe.getEstimationIngredientQuantities().isEmpty());
        verify(estimationIngredientQuantityRepository).findById(estimationIngredientQuantityId);
        verify(estimationRecipeRepository).findById(estimationRecipeId);
        verify(estimationRecipeRepository).save(estimationRecipe);
    }
}
