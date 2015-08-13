package cat.aubricoc.palaudenoguera.festamajor.model;

import android.graphics.drawable.Drawable;

import com.canteratech.apa.annotation.Entity;
import com.canteratech.apa.annotation.Id;
import com.canteratech.apa.annotation.OrderBy;
import com.canteratech.apa.annotation.Transient;

import java.util.Date;

@Entity
public class Instagram {

	@Id
	@OrderBy(descendant = true)
	private String id;

	private String userImage;

	private String user;

	private String alias;

	private String message;

	private Date date;

	private String link;

	@Transient
	private Drawable image;

	@Transient
	private Drawable picture;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}

	public Drawable getPicture() {
		return picture;
	}

	public void setPicture(Drawable picture) {
		this.picture = picture;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instagram other = (Instagram) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return message;
	}
}
