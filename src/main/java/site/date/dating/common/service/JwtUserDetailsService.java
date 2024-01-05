package site.date.dating.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.date.dating.common.jwt.CustomUserDetail;
import site.date.dating.member.user.domain.Authorization;
import site.date.dating.member.user.domain.Member;
import site.date.dating.member.user.domain.MemberAuthority;
import site.date.dating.member.user.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String memberIdName) {
        Optional<Member> loginMember = memberRepository
                .findOneEmailByMemberIdName(memberIdName);

        Member member = loginMember
                .orElseThrow(() -> new IllegalStateException("로그인하려는 아이디가 존재하지 않습니다."));

        List<MemberAuthority> memberAuthorities = memberRepository.findMemberAuthorities(memberIdName);

        List<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(memberAuthorities);

        if(confirmUser(grantedAuthorities)){
            return createCustomUserDetail(member, grantedAuthorities);
        }

        return createCustomUserDetail(member, memberAuthorities, grantedAuthorities);
    }

    private CustomUserDetail createCustomUserDetail(
            Member member,
            List<MemberAuthority> memberAuthorities,
            List<GrantedAuthority> grantedAuthorities
    ) {
        return new CustomUserDetail(
                member.getMemberIdName(),
                member.getPassword(),
                grantedAuthorities,
                member.getPhoneNumber(),
                findHospitalNumber(memberAuthorities),
                member.getId(),
                member.getNickName(),
                member.getMemberStatus());
    }

    private CustomUserDetail createCustomUserDetail(
            Member member,
            List<GrantedAuthority> grantedAuthorities
    ) {
        return new CustomUserDetail(
                member.getMemberIdName(),
                member.getPassword(),
                grantedAuthorities,
                member.getPhoneNumber(),
                0L,
                member.getId(),
                member.getNickName(),
                member.getMemberStatus());
    }

    private Long findHospitalNumber(List<MemberAuthority> memberAuthorities) {
        Optional<MemberAuthority> managerAuthority = memberAuthorities
                .stream()
                .filter(this::confirmRoleManager)
                .findFirst();

        return managerAuthority
                .orElseThrow(() -> new IllegalStateException("MANAGER 권한이 없습니다."))
                .getHospitalNo();
    }

    private boolean confirmRoleManager(MemberAuthority a) {
        return a.getAuthority().getAuthorizationStatus() == Authorization.ROLE_MANAGER;
    }

    private boolean confirmUser(List<GrantedAuthority> grantedAuthorities) {
        if(grantedAuthorities.size()==1){
            return true;
        }

        return false;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<MemberAuthority> memberAuthorities) {

        return memberAuthorities
                .stream()
                .map(a -> new SimpleGrantedAuthority(
                        a.getAuthority().getAuthorizationStatus().toString()))
                .collect(Collectors.toList());

    }
}
