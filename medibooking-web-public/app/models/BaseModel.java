package models;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import play.db.jpa.Model;

@MappedSuperclass
public class BaseModel extends Model{

	
	private Date createdAt;
	private Date modifiedAt;
	
	
	@SuppressWarnings("unused")
	@PrePersist
	private void prepareEntity() {
		if ( !this.isPersistent() ) {
			
			setCreatedAt(new Date());
			setModifiedAt(new Date());
		} else {
			setModifiedAt(new Date());
		}
		
	}


	public void setCreatedAt(Date createAt) {
		this.createdAt = createAt;
	}


	public Date getCreatedAt() {
		return createdAt;
	}


	public void setModifiedAt(Date modiofiedAt) {
		this.modifiedAt = modiofiedAt;
	}


	public Date getModifiedAt() {
		return modifiedAt;
	}
	
}
