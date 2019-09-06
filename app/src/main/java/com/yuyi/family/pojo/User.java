package com.yuyi.family.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class User extends CommonData {
    private String phone;
    private String name;
    private String id;
    private String portrait;
    private boolean isRegister;//是否注册
    private List<String> familyMemberPhone=new ArrayList<>();
    private List<FamilyMember> familyMembers=new ArrayList<>();
    private String password;

    public FamilyMember getFamilyMemberByPhone(String phone){
        for(int i=0;i<familyMembers.size();i++){
            if(familyMembers.get(i).getPhone().equals(phone)){
                return familyMembers.get(i);
            }
        }
        return null;
    }
}
