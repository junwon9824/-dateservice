package site.date.dating.member.user.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.date.dating.common.jwt.CustomUserDetail;
import site.date.dating.common.jwt.JwtFilter;
import site.date.dating.common.jwt.TokenProvider;
import site.date.dating.member.user.api.dto.*;
import site.date.dating.member.user.domain.*;
import site.date.dating.member.user.repository.AuthorityRepository;
import site.date.dating.member.user.repository.MemberAuthorityRepository;
import site.date.dating.member.user.repository.MemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true) //수정은 불가능 ..
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;
    private final MemberAuthorityRepository memberAuthorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    @Override
    public ResponseEntity<MemberLoginResponse> login(MemberLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                createAuthenticationToken(request);

        Authentication authentication = setAuthentication(authenticationToken);
        CustomUserDetail user = createUser(authentication);
        String jwt = createJWToken(authentication, user);

        HttpHeaders httpHeaders = addJwtInHeader(jwt);

        return createLoginResponseEntity(user, jwt, httpHeaders);
    }

    @Transactional
    @Override
    public MemberCreateResponse signup(MemberCreateRequest request) {
        Member member = Member
                .builder()
                .memberIdName(request.getMemberIdName())
                .password(request.getPassword())
                .nickName(request.getNickName())
                .userName(request.getUserName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        validateDuplicateMember(member);

        Member createdMember = createMember(member);
        Authority authority = findUserAuthority();
        saveMemberWithAuthority(createdMember, authority);

        return MemberCreateResponse.from(createdMember.getId());
    }

    @Override
    @Transactional
    public void saveMemberWithAuthority(Member createdMember, Authority authority) {
        memberRepository.save(createdMember);

        MemberAuthority memberAuthority = MemberAuthority
                .builder()
                .member(createdMember)
                .authority(authority)
                .build();

        memberAuthorityRepository.save(memberAuthority);
    }

    @Transactional
    @Override
    public void modifyMemberByUser(Long memberId, MemberModifyRequest request) {
        Member memberChange = Member
                .builder()
                .phoneNumber(request.getPhoneNumber())
                .nickName(request.getNickName())
                .userName(request.getUserName())
                .build();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("멤버가 존재하지 않습니다."));

        member.modifyMember(memberChange);
    }


    @Override
    public MemberViewInfoResponse viewUserInformation(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("멤버가 존재하지 않습니다."));

        return MemberViewInfoResponse.from(member);
    }

    @Override
    public void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByMemberIdName(member.getMemberIdName());

        if (findMembers!=null && !findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원.");
        }
    }

    @Override
    public Authority findUserAuthority() {
        Authority authority = authorityRepository.findByAuthorizationStatus(Authorization.ROLE_USER);

        if (authority == null) {
            throw new IllegalStateException("USER 권한 데이터가 없습니다.");
        }

        return authority;
    }

    private ResponseEntity<MemberLoginResponse> createLoginResponseEntity(
            CustomUserDetail user,
            String jwt,
            HttpHeaders httpHeaders
    ) {
        return new ResponseEntity<>
                ( MemberLoginResponse.from(user.getMemberId(),
                        user.getNickName(),
                        user.getMemberStatus(),
                        jwt),
                        httpHeaders,
                        HttpStatus.OK);

    }

    private HttpHeaders addJwtInHeader(String jwt) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return httpHeaders;
    }

    private String createJWToken(Authentication authentication, CustomUserDetail user) {
        String jwt = getJwToken(authentication, user);

        if (jwt == null) {
            throw new IllegalStateException("토큰 값이 null 입니다.");
        }

        return jwt;
    }

    private CustomUserDetail createUser(Authentication authentication) {
        return (CustomUserDetail) authentication.getPrincipal();
    }

    private Authentication setAuthentication(UsernamePasswordAuthenticationToken authenticationToken) {
        try {
            Authentication authentication = authenticationManagerBuilder.getObject()
                    .authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return authentication;

        } catch(AuthenticationException e){
//            throw new ServiceException(HttpStatus.NOT_FOUND, "아이디와 비밀번호가 일치하지 않습니다.");
            throw new RuntimeException("아이디와 비밀번호가 일치하지 않습니다.", e);

        }

    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(
            MemberLoginRequest request
    ) {

        return new UsernamePasswordAuthenticationToken(
                request.getMemberIdName(),
                request.getPassword());
    }


    private boolean confirmAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean confirmManager(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) &&

                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
    }

    private boolean cofirmUser(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_MANAGER")) &&

                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    private Member createMember(Member member) {
        return Member
                .builder()
                .userName(member.getUserName())
                .nickName(member.getNickName())
                .phoneNumber(member.getPhoneNumber())
                .memberIdName(member.getMemberIdName())
                .memberStatus(MemberStatus.NORMAL)
                .password(passwordEncoder.encode(member.getPassword()))
                .build();
    }

    private String getJwToken(
            Authentication authentication,
            CustomUserDetail user
    ) {
        if (cofirmUser(authentication)) {
            return tokenProvider.createToken(authentication, user.getPhoneNumber());
        }
        else if (confirmManager(authentication)) {
            return tokenProvider.createManagerToken(
                    authentication,
                    user.getPhoneNumber(),
                    user.getHospitalNumber());
        }
        else if (confirmAdmin(authentication)) {
            return tokenProvider.createToken(authentication, user.getPhoneNumber());
        } else {
            throw new IllegalStateException("권한이 존재하지 않습니다.");
        }
    }

}
