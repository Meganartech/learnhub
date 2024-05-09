package com.knowledgeVista.License;

import java.util.List;
import java.util.Map;

public class UserListWithStatus {


    private boolean isEmpty;
    private boolean valid;
    private boolean type;
    private List<Map<String, Object>> dataList;

    public UserListWithStatus(boolean isEmpty, boolean valid, boolean type, List<Map<String, Object>> dataList) {
        this.isEmpty = isEmpty;
        this.valid = valid;
        this.type = type;
        this.dataList = dataList;
    }

	

    public boolean getType() {
		return type;
	}
    
    public List<Map<String, Object>> getDataList() {
        return dataList;
    }


	public void setType(boolean type) {
		this.type = type;
	}



	public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isValid() {
        return valid;
    }
}
