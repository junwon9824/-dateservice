package site.date.dating.member.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.hospital.member.admin.repository.dto.AdminMemberSearchCondition;
import site.hospital.member.user.domain.Authorization;
import site.hospital.member.user.domain.Member;
import site.hospital.member.user.domain.MemberAuthority;

import java.util.List;

public interface MemberRepositoryCustom {

    Page<Member> adminSearchMembers(AdminMemberSearchCondition condition, Pageable pageable);

    List<MemberAuthority> findMemberAuthorities(String memberIdName);

    MemberAuthority findManagerAuthority(Long memberId, Authorization authorization);

    void adminDeleteAllAuthority(Member member);
}
