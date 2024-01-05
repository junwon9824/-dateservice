package site.date.dating.common.service;


public interface ManagerJwtService {


    void accessManager(
            ServletRequest servletRequest,
            Long hospitalIdRequest
    );

    Long getHospitalNumber(ServletRequest servletRequest);

}
