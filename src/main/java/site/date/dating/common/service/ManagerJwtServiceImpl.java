package site.date.dating.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.date.dating.common.jwt.TokenProvider;

import java.nio.file.AccessDeniedException;
import java.util.logging.Logger;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ManagerJwtServiceImpl implements ManagerJwtService {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Logger logger = LoggerFactory.getLogger(ManagerJwtServiceImpl.class);
    private final TokenProvider tokenProvider;

    public void accessManager(
            ServletRequest servletRequest,
            Long hospitalIdRequest
    ) {
        Long hospitalNumberInJwt = getHospitalNumberInJwt(servletRequest);

        if (hospitalNumberInJwt == null) {
            throw new AccessDeniedException("병원 번호가 존재하지 않습니다.");
        }

        if (confirmAdmin(hospitalNumberInJwt)) {
            throw new AccessDeniedException("관리자 계정은 관리자 기능을 이용해주세요.");
        } else if (confirmMatchHospitalNumber(hospitalIdRequest, hospitalNumberInJwt)) {
            throw new AccessDeniedException("자신의 병원 번호만 조작이 가능합니다.");
        }
    }

    public Long getHospitalNumber(ServletRequest servletRequest) {
        return getHospitalNumberInJwt(servletRequest);
    }

    private Long getHospitalNumberInJwt(ServletRequest servletRequest) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = takeToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        if (checkTokenValue(jwt) && tokenProvider.validateToken(jwt)) {
            return tokenProvider.getHospitalNumberInManager(jwt);
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);

            return null;
        }
    }

    private String takeToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (checkBearerPrefix(bearerToken)) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private boolean checkTokenValue(String jwt) {
        return StringUtils.hasText(jwt);
    }

    private boolean checkBearerPrefix(String bearerToken) {
        return StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ");
    }

    private boolean confirmAdmin(Long hospitalNumberInJwt) {
        return hospitalNumberInJwt.equals(0L);
    }

    private boolean confirmMatchHospitalNumber(Long hospitalId, Long hospitalNumberInJwt) {
        return hospitalNumberInJwt.equals(hospitalId)? false: true;
    }

}
