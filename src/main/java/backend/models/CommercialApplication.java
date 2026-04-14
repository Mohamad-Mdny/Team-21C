package backend.models;

public class CommercialApplication {
    String emailAddress;
    String companyRegNumber;
    String companyDirector;
    String businessType;
    String businessAddress;
    String json;


    public CommercialApplication(String lowerCase,
                                 String companyRegNumber,
                                 String companyDirector,
                                 String businessType,
                                 String businessAddress) {
        this.emailAddress = lowerCase;
        this.companyRegNumber = companyRegNumber;
        this.companyDirector = companyDirector;
        this.businessType = businessType;
        this.businessAddress = businessAddress;
    }

    private String toJSON(){



        return json;
    }
}
