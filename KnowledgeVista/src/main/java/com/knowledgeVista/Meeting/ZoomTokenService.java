package com.knowledgeVista.Meeting;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;


import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import com.knowledgeVista.zoomJar.ZoomMethods;

import org.springframework.stereotype.Service;
@Service
public class ZoomTokenService {
	@Autowired
		private ZoomAccountkeyrepo zoomacrepo;
		 @Autowired
		 private JwtUtil jwtUtil;
		 @Autowired
			private MuserRepositories muserRepository;
		 @Autowired
		 private ZoomMethods zoomMethod;
		 
	    public ZoomAccountKeys getZoomAccounts(String institution) {
        	Optional<ZoomAccountKeys> opAccountdetails= zoomacrepo.findbyInstitutionName(institution);
        	if(opAccountdetails.isPresent()) {
        		ZoomAccountKeys Accountdetails=opAccountdetails.get();
        		return Accountdetails;
        	}else {
        		Optional<ZoomAccountKeys> opdefaultkeys=zoomacrepo.findbyInstitutionName("SYSADMIN");
        		if(opdefaultkeys.isPresent()) {
        			ZoomAccountKeys ZoomDefaultKeys =opdefaultkeys.get();
        			return ZoomDefaultKeys;
        		}else {
        			return null;
        		}
        	}
        }
	    public String getAccessToken(String institution) {
	    	try {
	       
            ZoomAccountKeys zoomkeys=this.getZoomAccounts(institution);
            if (zoomkeys == null) {
                return null;
            }
            String CLIENT_ID = zoomkeys.getClient_id();
    	     String CLIENT_SECRET = zoomkeys.getClient_secret();
    	     String ACCOUNT_ID = zoomkeys.getAccount_id();
    	     return zoomMethod.getAccessToken(CLIENT_ID,CLIENT_SECRET,ACCOUNT_ID);
	    }catch(Exception e) {
	    e.printStackTrace();
	    return null;
	    }
	    }
}



