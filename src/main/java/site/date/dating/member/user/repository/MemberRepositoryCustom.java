package site.date.dating.member.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.date.dating.member.admin.repository.dto.AdminMemberSearchCondition;
import site.date.dating.member.user.domain.Member;
import site.date.dating.member.user.domain.*;

import java.util.List;

public interface MemberRepositoryCustom {

    Page<Member> adminSearchMembers(AdminMemberSearchCondition condition, Pageable pageable);

    List<MemberAuthority> findMemberAuthorities(String memberIdName);

    MemberAuthority findManagerAuthority(Long memberId, Authorization authorization);

    void adminDeleteAllAuthority(Member member);
}
