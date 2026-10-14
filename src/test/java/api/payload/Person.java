package api.payload;

public class Person {
	
	private String merchantSecretKey;
	private String name;
	private String surname;
	private String building;
	private String street;
	private String postcode;
	private String dob;
	private String countrycode;
	private String email;
	
	public String getMerchantSecretKey() {
		return merchantSecretKey;
	}
	public void setMerchantSecretKey(String merchantSecretKey) {
		this.merchantSecretKey = merchantSecretKey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getCountrycode() {
		return countrycode;
	}
	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
