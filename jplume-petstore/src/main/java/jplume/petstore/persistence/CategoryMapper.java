package jplume.petstore.persistence;

import java.util.List;

import jplume.petstore.domain.Category;

public interface CategoryMapper {

  List<Category>getCategoryList();

  Category getCategory(String categoryId);

}
