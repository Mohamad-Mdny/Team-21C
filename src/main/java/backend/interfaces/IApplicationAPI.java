package backend.interfaces;

public interface IApplicationAPI {

	/**
	 * 
	 * @param companyRegNumber
	 * @param businessType
	 * @param businessAddress
	 * @param emailAddress
	 */
	void submitCommercialApplication(String emailAddress, String password, int companyRegNumber, String CompanyDirector, String businessType, String businessAddress);
}
