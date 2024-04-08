package com.knowledgeVista.License;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="license")
public class License {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
	
    @Column(name="company_name")
    private String company_name ;
    
    @Column(name="product_name")
    private String product_name ;
    
    @Column(name="key_value")
    private String key ;
    
    @Column(name="start_date")
    private Date start_date ;
    
    @Column(name="end_date")
    private Date end_date ;
    
    @Transient
	 private MultipartFile LicenseFile;
    

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
	public MultipartFile getLicenseFile() {
		return LicenseFile;
	}

	public void setLicenseFile(MultipartFile licenseFile) {
		LicenseFile = licenseFile;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public License() {
		super();
	}

	public License(long id, String company_name, String product_name,MultipartFile LicenseFile, String key, Date start_date, Date end_date) {
		super();
		this.id = id;
		this.company_name = company_name;
		this.product_name = product_name;
		this.key = key;
		this.start_date = start_date;
		this.LicenseFile = LicenseFile;
		this.end_date = end_date;
	}

	@Override
	public String toString() {
		return "License [id=" + id + ", company_name=" + company_name + ", product_name=" + product_name + ", key="
				+ key + ", start_date=" + start_date +",LicenseFile="+LicenseFile+ ", end_date=" + end_date + "]";
	}

	
    

}
