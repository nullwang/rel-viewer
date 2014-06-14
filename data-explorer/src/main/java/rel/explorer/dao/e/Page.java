package rel.explorer.dao.e;

public class Page {
	
	public static final int NUMBERS_PER_PAGE = 20;
	
	//
	private int numPerPage=NUMBERS_PER_PAGE;

	private int totalRows=0;

	private int totalPages=0;

	private int currentPage=1;

	private int startIndex=0;

	private int lastIndex=0;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		if( currentPage > 1){
			this.currentPage = currentPage;
			doCal();
		}
	}

	public int getNumPerPage() {
		return numPerPage;
	}

	public void setNumPerPage(int numPerPage) {
		if( numPerPage > 1 ) {
			this.numPerPage = numPerPage;
			doCal();
		}
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
		doCal();
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getLastIndex() {
		return lastIndex;
	}
	
	private void doCal()
	{
		this.startIndex = (currentPage - 1) * numPerPage;
		
		if (totalRows % numPerPage == 0) {
			this.totalPages = totalRows / numPerPage;
		} else {
			this.totalPages = (totalRows / numPerPage) + 1;		
		}
		
		if (totalRows < numPerPage) {
			this.lastIndex = totalRows;
		} else if ((totalRows % numPerPage == 0)
				|| (totalRows % numPerPage != 0 && currentPage < totalPages)) {
			this.lastIndex = currentPage * numPerPage;
		} else if (totalRows % numPerPage != 0 && currentPage == totalPages) {// ���һҳ
			this.lastIndex = totalRows;
		}
	}

}
