package spark.ukla.controllers.TagController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spark.ukla.controllers.genericController.ControllerGenericImpl;
import spark.ukla.entities.Tag;
import spark.ukla.repositories.TagRepository;
import spark.ukla.services.generic.ServiceGeneric;
import spark.ukla.services.implementations.TagService;

import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping(path = "/tag")
public class TagController extends ControllerGenericImpl<Tag> implements ITagController  {
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ServiceGeneric<Tag> genericService;
    public TagController(TagService tagService, TagRepository tagRepository, ServiceGeneric<Tag> genericService ) {
        super(genericService);
        this.tagService = tagService;
        this.tagRepository = tagRepository;
        this.genericService = genericService;


    }
    @GetMapping("getAllT")
    public Iterable<Tag> getAll() {

        return   tagRepository.findAll();
    }

    @GetMapping("getById/{tagId}")
    public Set<Tag> getTagById(@PathVariable(value = "tagId") Set<Long> tagId){
        return  tagService.findByIdIn(tagId);
    }

    @Override
    @PostMapping("add")
    public ResponseEntity<Object> save(Tag entity) {
        try {
            return new ResponseEntity(genericService.save(entity), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    @DeleteMapping("deleteById/{id}")
    public ResponseEntity<Boolean> delete( @PathVariable Long id) {
        try {


            tagService.delete(id);
            return new ResponseEntity("Success!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/tagUpdate")
    public Tag updateUser(@RequestBody Tag e) {
        return tagService.updateUser(e);
    }
}
