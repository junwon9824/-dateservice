package site.date.dating.member.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.date.dating.member.user.domain.Authority;
import site.date.dating.member.user.domain.Authorization;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findByAuthorizationStatus(Authorization authorization);
}
