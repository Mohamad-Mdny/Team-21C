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


	/**
	 * 
	 * @param recipientEmailAddress
	 * @param message
	 * @param subject
	 */
	void sendEmail(String recipientEmailAddress, String message, String subject);

	/**
	 * 
	 * @param messageID
	 */
	void getDeliveryStatus(int messageID);

}