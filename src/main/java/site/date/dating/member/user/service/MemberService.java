package site.date.dating.member.user.service;

import org.springframework.http.ResponseEntity;
import site.hospital.member.user.api.dto.*;
import site.hospital.member.user.domain.Authority;
import site.hospital.member.user.domain.Member;

public interface MemberService {

    ResponseEntity<MemberLoginResponse> login(MemberLoginRequest request);

    MemberCreateResponse signup(MemberCreateRequest request);

    void modifyMemberByUser(Long memberId, MemberModifyRequest request);

    MemberViewInfoResponse viewUserInformation(Long memberId);

    void validateDuplicateMember(Member member);

    Authority findUserAuthority();

    void saveMemberWithAuthority(Member createdMember, Authority authority);

}
