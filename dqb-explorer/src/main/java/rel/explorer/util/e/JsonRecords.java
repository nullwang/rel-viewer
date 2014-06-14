package rel.explorer.util.e;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import rel.explorer.dao.e.Page;
import rel.explorer.domain.e.PropertyList;

public class JsonRecords {
	
	private int page;
	
	private int total;
	
	private int records;
	
	private List rows = new ArrayList(); 
	
	public static class Row{
		private String id;
		
		private List cell = new ArrayList();

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public List getCell() {
			return cell;
		}

		public void setCell(List cell) {
			this.cell = cell;
		}		
	}
	
	public int getPage() {
		return page;
	}


	public void setPage(int page) {
		this.page = page;
	}

	public int getTotal() {
		return total;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}
	public int getRecords() {
		return records;
	}
	public void setRecords(int records) {
		this.records = records;
	}	
	
	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}
	
	public static String JsonStringFrom(Map map) throws IOException {
		JsonRecords jsonRecords = new JsonRecords();
		
		Page page = (Page) map.get("page");
		if( page !=null){
			jsonRecords.setPage(page.getCurrentPage());
			jsonRecords.setRecords(page.getTotalRows());
			jsonRecords.setTotal(page.getTotalPages());			
		}
		List data = (List) map.get("data");
		if( data !=null){
			for(int i=0; i<data.size();i++){
				Row row = new Row();
				
				Object cols =  data.get(i);
				if( cols instanceof PropertyList)
				{
					row.getCell().addAll(((PropertyList) cols).getProperties());
					row.setId(((PropertyList) cols).getId());
				}
				
				jsonRecords.getRows().add(row);
					
			}
		}
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(jsonRecords);
	}

}
