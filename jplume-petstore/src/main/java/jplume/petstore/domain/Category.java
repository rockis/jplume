package jplume.petstore.domain;

import java.io.Serializable;

public class Category implements Serializable {

  private static final long serialVersionUID = 3992469837058393712L;

  private String categoryId;
  private String name;
  private String image;
  private String description;

  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId.trim();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImage() {
	return image;
}

public void setImage(String image) {
	this.image = image;
}

public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String toString() {
    return getCategoryId();
  }

}
