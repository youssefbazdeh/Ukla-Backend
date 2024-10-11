package spark.ukla.services.implementations;


import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.DTO.IngredientDTO;
import spark.ukla.converters.IngredientConverter;
import spark.ukla.entities.Image;
import spark.ukla.entities.Ingredient;
import spark.ukla.entities.enums.TypeIngredient;
import spark.ukla.repositories.ImageDbRepository;
import spark.ukla.repositories.IngredientRepository;
import spark.ukla.repositories.TranslatedIngredientRepository;
import spark.ukla.repositories.UnitAlternativeRepository;
import spark.ukla.services.interfaces.IIngredientsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class IngredientService implements IIngredientsService {

    private final FileLocationService fileLocationService;
    private final ImageDbRepository imageDbRepository;
    private final IngredientRepository ingredientRepository;
    private final TranslatedIngredientRepository translatedIngredientRepository;
    private final UnitAlternativeRepository unitAlternativeRepository ;
    private final IngredientConverter ingredientConverter ;
    public IngredientService(IngredientConverter ingredientConverter,FileLocationService fileLocationService, ImageDbRepository imageDbRepository, IngredientRepository ingredientRepository, TranslatedIngredientRepository translatedIngredientRepository, UnitAlternativeRepository unitAlternativeRepository) {
        this.fileLocationService = fileLocationService;
        this.imageDbRepository = imageDbRepository;
        this.ingredientRepository = ingredientRepository;
        this.translatedIngredientRepository = translatedIngredientRepository;
        this.unitAlternativeRepository = unitAlternativeRepository;
        this.ingredientConverter=ingredientConverter;
    }
//    @Override
    public Ingredient retrieveById(Long id) {
        return ingredientRepository.findById(id).orElse(null);

    }
//    @Override
    public void deleteById(Long id) {
        boolean exists = ingredientRepository.existsById(id);
        Ingredient ingredient=ingredientRepository.findById(id).get();
        if (exists ) {
            Image image = ingredient.getImage();
            if (image!=null){
                fileLocationService.deleteImage(image.getLocation());
            }
            ingredientRepository.deleteById(id);
        }
    }
    public long getRecipeCount() {
        return ingredientRepository.count();
    }



    @Override
    public Boolean add(Ingredient ingredient, Image image) {
        translatedIngredientRepository.saveAll(ingredient.getTranslatedIngredients());
        ingredient.setImage(image);
        unitAlternativeRepository.saveAll(ingredient.getUnitAlternatives());
        ingredientRepository.save(ingredient) ;
        return true ;

    }
    public Boolean update(Ingredient ingredient,Image image){
        ingredient.setImage(image);
        unitAlternativeRepository.saveAll(ingredient.getUnitAlternatives());
        translatedIngredientRepository.saveAll(ingredient.getTranslatedIngredients());
        ingredientRepository.save(ingredient) ;
        return true ;
    }
    public String updateImaage(Long ingredientId, Image image) {

        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElse(null);
        if (ingredient == null)
            return "ingredient doesn't exist" ;
        ingredient.setImage(image);
        ingredientRepository.save(ingredient) ;
        return "ingredient saved" ;

    }

    @Override
    public Boolean nameExists(String ingredientName) {
        return ingredientRepository.existsByName(ingredientName) ;
    }

    @Override
    public List<Ingredient> importIngredientsFromExcel(MultipartFile file) throws IOException {
        List<Ingredient> ingredients = new ArrayList<>();
        // Load the Excel file using Apache POI
        Workbook workbook = new  XSSFWorkbook(file.getInputStream()) {};
        // Get the first sheet of the workbook
        Sheet sheet = workbook.getSheetAt(0);
        // Loop through the rows of the sheet

        int startRowNum = 1; // Start from the third row (index 2)
        int lastRowNum = sheet.getLastRowNum();

        for (int i = startRowNum; i <= lastRowNum; i++) {
            try {

                Row row = sheet.getRow(i);
                // Set the properties of the ingredient object based on the cell values in the row
                // if this ingredient is not already inserted
                if(ingredientRepository.findByName(row.getCell(0).getStringCellValue())==null) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setName(row.getCell(0).getStringCellValue());
                    ingredient.setType(TypeIngredient.valueOf(row.getCell(1).getStringCellValue()));
                    ingredient.setNbrCalories100gr(Float.parseFloat(row.getCell(2).getStringCellValue()));
                    ingredient.setFat(Float.parseFloat(row.getCell(3).getStringCellValue()));
                    ingredient.setProtein(Float.parseFloat(row.getCell(4).getStringCellValue()));
                    ingredient.setCarbs(Float.parseFloat(row.getCell(5).getStringCellValue()));
                    ingredient.setSugar(Float.parseFloat(row.getCell(6).getStringCellValue()));
                    ingredient.setFiber(Float.parseFloat(row.getCell(7).getStringCellValue()));
                    ingredient.setSaturatedFattyAcids(Float.parseFloat(row.getCell(8).getStringCellValue()));

                    ingredient.setCalcium(Float.parseFloat(row.getCell(9).getStringCellValue()));
                    ingredient.setMagnesium(Float.parseFloat(row.getCell(10).getStringCellValue()));
                    ingredient.setZinc(   Float.parseFloat(row.getCell(11).getStringCellValue()));
                    ingredient.setIron(Float.parseFloat(row.getCell(12).getStringCellValue()));
                    ingredients.add(ingredient);
                }
           }

            catch (Exception e ) {
                log.error(e.getMessage() + i );
            }


  }
        ingredientRepository.saveAll(ingredients);
        return ingredients ;

     }

    @Override
    public List<String> getAllIngredientName() {
        List<String> names = new ArrayList<>();
        for (Ingredient ingredient : ingredientRepository.findAll()) {
            names.add(ingredient.getName());
        }
        return names;


    }

    @Override
    public List<Ingredient> searchIngredient(String query) {
        return ingredientRepository.findByNameContaining(query) ;
    }

    public List<IngredientDTO> searchTanslatedIngredient(String query, String languageCode) {
        List<Ingredient> ingredientList = new ArrayList<>(ingredientRepository.findIngredientsByTranslatedIngredients(languageCode, query));
        return ingredientConverter.entityToIngredientDTOTranslated(ingredientList,languageCode);
    }
    public Iterable<Ingredient> getAll() {
       return  ingredientRepository.findAll();
    }

    public List<Ingredient> getAllIngredientsWithPagination(int page, int size) {
        Pageable pageable;
        if (page == 0 && size == 0) {
            return  (List<Ingredient>) ingredientRepository.findAll();
        }
        pageable = PageRequest.of(page - 1, size);
        // Fetch the first 6 recipes with pagination
        Page<Ingredient> firstPage = ingredientRepository.findAll(pageable);
        return firstPage.getContent();
    }



    public List<IngredientDTO> getAllTranslatedIngredientsWithPagination(int page, int size, String languageCode) {
        Pageable pageable;
        List<Ingredient> ingredientList=new ArrayList<>();
        if (page == 0 && size == 0) {
            ingredientList.addAll((List<Ingredient>) ingredientRepository.findAll());
            return ingredientConverter.entityToIngredientDTOTranslated(ingredientList,languageCode);

        }

        pageable = PageRequest.of(page - 1, size);
        Page<Ingredient> firstPage = ingredientRepository.findAll(pageable);
        ingredientList.addAll(firstPage.getContent());
        return ingredientConverter.entityToIngredientDTOTranslated(ingredientList,languageCode);
    }





}
