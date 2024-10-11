package spark.ukla.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spark.ukla.entities.Tag;

import spark.ukla.repositories.TagRepository;
import spark.ukla.services.generic.ServiceGenericImpl;
import spark.ukla.services.interfaces.ITagService;

import java.util.Set;


@Service
public class TagService extends ServiceGenericImpl<Tag> implements ITagService {

    @Autowired
    TagRepository tagRepository;
    @Override
    public void delete(Long id) throws Exception {
            tagRepository.deleteById(id);
    }
    public Tag updateUser( Tag e) {
        return tagRepository.save(e);
    }

    public Set<Tag> findByIdIn(Set<Long> id) {
        return tagRepository.findByIdIn(id);
    }
}
