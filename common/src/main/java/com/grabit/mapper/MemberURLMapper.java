package com.grabit.mapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

@Value
public class MemberURLMapper {
    @Getter(AccessLevel.NONE)
    String domainUrl;

    public MemberURLMapper(String url){
        this.domainUrl=url;
    }

    public String getMemberExistsURL(String memberId){
        return domainUrl+"/member/" + memberId + "/exists";
    }
}
