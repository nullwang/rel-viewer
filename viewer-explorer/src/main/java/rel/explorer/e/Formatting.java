package rel.explorer.e;

import java.util.ArrayList;
import java.util.List;

public class Formatting implements Cloneable{
	 
	List<ImageURL> imgs = new ArrayList<ImageURL>();

	public List<ImageURL> getImgs() {
		return imgs;
	}

	public void setImgs(List<ImageURL> imgs) {
		this.imgs = imgs;
	}
	
	public void addImg(ImageURL imageURL)
	{
		this.imgs.add(imageURL);
	}
	
	public void removeImg(ImageURL imageURL)
	{
		this.imgs.remove(imageURL);
	}
	
	public Object clone()
	{
		Formatting o = null;		
		
			try {
				o = (Formatting)super.clone();
				o.imgs = new ArrayList<ImageURL>();
				for( int i=0; i< imgs.size(); i++){
					o.imgs.add((ImageURL) imgs.get(i).clone());					
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		return o;		
	}
}
