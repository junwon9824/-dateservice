package site.date.dating.member.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.date.dating.member.user.domain.MemberAuthority;


public interface MemberAuthorityRepository extends JpaRepository<MemberAuthority, Long> {

}
