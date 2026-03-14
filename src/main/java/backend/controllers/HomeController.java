package backend.controllers;

import backend.models.Member;

public class HomeController {

    public void switchToCommercialRegister(){
        Member member = new Member();
        member.createMember();
    }
    public void switchToNonCommercialRegister(){

    }
    public void switchToCommercialLogin(){

    }
    public void switchToNonCommercialLogin(){

    }


}
