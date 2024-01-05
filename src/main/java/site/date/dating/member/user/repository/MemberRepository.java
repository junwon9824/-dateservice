package site.date.dating.member.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.date.dating.member.user.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByMemberIdName(String memberIdName);

    Optional<Member> findOneEmailByMemberIdName(String memberIdName);
}
