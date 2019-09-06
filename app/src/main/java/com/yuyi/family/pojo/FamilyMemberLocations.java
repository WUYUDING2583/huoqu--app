package com.yuyi.family.pojo;

import java.util.List;

import lombok.Data;

@Data
public class FamilyMemberLocations extends CommonData{
    private List<LocationResult> familyMemberLocations;

}
