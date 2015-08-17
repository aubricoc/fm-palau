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
	private String id;

	private String userImage;

	private String user;

	private String alias;

	private String message;

	@OrderBy(descendant = true)
	private Date date;

	private String link;

	private String pictureThumbnailUrl;

	private String pictureLowUrl;

	private String pictureStandardUrl;

	private byte[] pictureThumbnail;

	private byte[] pictureLow;

	private byte[] pictureStandard;

	@Transient
	private Drawable image;

	@Transient
	private Drawable pictureThumbnailDrawable;

	@Transient
	private Drawable pictureLowDrawable;

	@Transient
	private Drawable pictureStandardDrawable;

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

	public String getPictureThumbnailUrl() {
		return pictureThumbnailUrl;
	}

	public void setPictureThumbnailUrl(String pictureThumbnailUrl) {
		this.pictureThumbnailUrl = pictureThumbnailUrl;
	}

	public String getPictureLowUrl() {
		return pictureLowUrl;
	}

	public void setPictureLowUrl(String pictureLowUrl) {
		this.pictureLowUrl = pictureLowUrl;
	}

	public String getPictureStandardUrl() {
		return pictureStandardUrl;
	}

	public void setPictureStandardUrl(String pictureStandardUrl) {
		this.pictureStandardUrl = pictureStandardUrl;
	}

	public byte[] getPictureThumbnail() {
		return pictureThumbnail;
	}

	public void setPictureThumbnail(byte[] pictureThumbnail) {
		this.pictureThumbnail = pictureThumbnail;
	}

	public byte[] getPictureLow() {
		return pictureLow;
	}

	public void setPictureLow(byte[] pictureLow) {
		this.pictureLow = pictureLow;
	}

	public byte[] getPictureStandard() {
		return pictureStandard;
	}

	public void setPictureStandard(byte[] pictureStandard) {
		this.pictureStandard = pictureStandard;
	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}

	public Drawable getPictureThumbnailDrawable() {
		return pictureThumbnailDrawable;
	}

	public void setPictureThumbnailDrawable(Drawable pictureThumbnailDrawable) {
		this.pictureThumbnailDrawable = pictureThumbnailDrawable;
	}

	public Drawable getPictureLowDrawable() {
		return pictureLowDrawable;
	}

	public void setPictureLowDrawable(Drawable pictureLowDrawable) {
		this.pictureLowDrawable = pictureLowDrawable;
	}

	public Drawable getPictureStandardDrawable() {
		return pictureStandardDrawable;
	}

	public void setPictureStandardDrawable(Drawable pictureStandardDrawable) {
		this.pictureStandardDrawable = pictureStandardDrawable;
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
