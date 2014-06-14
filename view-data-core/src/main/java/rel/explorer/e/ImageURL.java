package rel.explorer.e;


public class ImageURL implements Cloneable{
	
	private int height = -1;
	
	private int width = -1;

	private String URL;

	private String imgCode;
	
	public int getHeight() {
		return height;
	}
	
	public Object clone()
	{
		ImageURL o = null;
		
			try {
				o = (ImageURL) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		
		return o;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setHeight(String height)
	{
		try{
			int h = Integer.parseInt(height);
			this.setHeight(h);
		}catch(NumberFormatException e){};
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setWidth(String width)
	{
		try{
			int w = Integer.parseInt(width);
			this.setWidth(w);
		}catch(NumberFormatException e){};
	}

	public String getImgCode() {
		return imgCode;
	}

	public void setImgCode(String imgCode) {
		this.imgCode = imgCode;
	}
	
	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}
}
