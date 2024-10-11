package spark.ukla.services.interfaces;

import spark.ukla.entities.Tag;
import spark.ukla.services.generic.ServiceGeneric;

public interface ITagService extends ServiceGeneric<Tag> {

    void delete(Long id) throws Exception;

}
