package site.date.dating.member.user.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import site.date.dating.member.admin.repository.dto.AdminMemberSearchCondition;
import site.date.dating.member.user.domain.Authorization;
import site.date.dating.member.user.domain.Member;
import site.date.dating.member.user.domain.*;

import javax.persistence.EntityManager;
import java.util.List;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    
    @Override
    public List<MemberAuthority> findMemberAuthorities(String memberIdName) {
        return queryFactory
                .select(memberAuthority)
                .from(memberAuthority)
                .join(memberAuthority.member, member)
                .join(memberAuthority.authority, authority).fetchJoin()
                .where(memberIdNameEq(memberIdName))
                .fetch();
    }

    
    @Override
    public MemberAuthority findManagerAuthority(Long memberId, Authorization authorization) {
        return queryFactory
                .select(memberAuthority)
                .from(memberAuthority)
                .join(memberAuthority.member, member)
                .join(memberAuthority.authority, authority)
                .where(
                        memberIdEq(memberId),
                        authorizationStatusEq(authorization)
                )
                .fetchOne();
    }
    
    @Override
    public void adminDeleteAllAuthority(Member member) {
        queryFactory.delete(memberAuthority)
                .where(memberEq(member))
                .execute();
    }
    
    @Override
    public Page<Member> adminSearchMembers(
            AdminMemberSearchCondition condition,
            Pageable pageable
    ) {
        if (searchAllConditions(condition)) {
            return searchUserWithAllConditions(condition, pageable);
        }
        else {
            return searchEachCondition(condition, pageable);
        }
    }

    private PageImpl<Member> searchEachCondition(AdminMemberSearchCondition condition, Pageable pageable) {
        QueryResults<Member> result = queryFactory
                .select(member)
                .from(member)
                .where(
                        memberIdEq(condition.getMemberId())
                        , (memberIdNameLike(condition.getMemberIdName()))
                        , (memberNickNameEq(condition.getNickName()))
                        , (memberUserNameEq(condition.getUserName()))
                        , (memberPhoneNumberEq(condition.getPhoneNumber()))
                        , (memberHospitalNumberEq(condition.getHospitalNumber()))
                        , (memberStatusEq(condition.getMemberStatus()))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Member> content = result.getResults();
        long total = result.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    private boolean searchAllConditions(AdminMemberSearchCondition condition) {
        return condition.getAllSearch() != null;
    }

    private PageImpl<Member> searchUserWithAllConditions(AdminMemberSearchCondition condition, Pageable pageable) {
        QueryResults<Member> result = queryFactory
                .select(member)
                .from(member)
                .where(
                        memberNickNameEq(condition.getAllSearch())
                                .or(memberIdNameLike(condition.getAllSearch()))
                                .or(memberUserNameEq(condition.getAllSearch()))
                                .or(memberPhoneNumberEq(condition.getAllSearch()))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Member> content = result.getResults();
        long total = result.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression memberEq(Member member) {
        return member == null ? null : memberAuthority.member.eq(member);
    }

    private BooleanExpression memberIdNameEq(String memberIdName) {
        return memberIdName == null ? null : member.memberIdName.eq(memberIdName);
    }

    private BooleanExpression authorizationStatusEq(Authorization authorization) {
        return authorization == null ? null : authority.authorizationStatus.eq(authorization);
    }

    private BooleanExpression memberIdEq(Long id) {
        return id == null ? null : member.id.eq(id);
    }

    private BooleanExpression memberIdNameLike(String memberIdName) {
        return memberIdName == null ? null : member.memberIdName.contains(memberIdName);
    }

    private BooleanExpression memberNickNameEq(String nickName) {
        return nickName == null ? null : member.nickName.eq(nickName);
    }

    private BooleanExpression memberUserNameEq(String userName) {
        return userName == null ? null : member.userName.eq(userName);
    }

    private BooleanExpression memberPhoneNumberEq(String phoneNumber) {
        return phoneNumber == null ? null : member.phoneNumber.eq(phoneNumber);
    }

    private BooleanExpression memberHospitalNumberEq(Long hospitalNumber) {
        return hospitalNumber == null ? null : member.hospitalNumber.eq(hospitalNumber);
    }

    private BooleanExpression memberStatusEq(MemberStatus memberStatus) {
        return memberStatus == null ? null : member.memberStatus.eq(memberStatus);
    }

}
