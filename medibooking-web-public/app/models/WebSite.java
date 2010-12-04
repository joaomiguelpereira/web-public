package models;

import javax.persistence.Entity;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.URL;

@Entity
public class WebSite extends BaseModel {

	@Required
	@MaxSize(value=20)
	private String name;
	
	@Required
	@URL
	private String url;
	
	@MaxSize(value=200)
	private String description;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
