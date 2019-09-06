package com.yuyi.family.listener.interfaces;

import com.yuyi.family.pojo.FamilyMember;

import java.util.List;

public interface OnLatestFamilyListener {
     void onLatestFamily(List<FamilyMember> familyMembers);
}
