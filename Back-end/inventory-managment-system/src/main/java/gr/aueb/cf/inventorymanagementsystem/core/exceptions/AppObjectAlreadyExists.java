package gr.aueb.cf.inventorymanagementsystem.core.exceptions;

public class AppObjectAlreadyExists extends AppGenericException{
    private static final String DEFAULT_CODE = "AlreadyExists";

    public AppObjectAlreadyExists(String code, String message){
        super(code + DEFAULT_CODE, message);
    }
}
