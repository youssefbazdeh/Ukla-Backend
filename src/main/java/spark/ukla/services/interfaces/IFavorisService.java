package spark.ukla.services.interfaces;


public interface IFavorisService {
	String add(String username, long recipeId);

	String delete(String username, long recipeId);

	boolean isRecipeLikedByUser(String username , long recipeId );
}
