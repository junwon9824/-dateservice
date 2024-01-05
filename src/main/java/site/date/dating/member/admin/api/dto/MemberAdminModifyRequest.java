package site.date.dating.member.admin.api.dto;

import lombok.Data;
import site.hospital.member.user.domain.MemberStatus;

import javax.validation.constraints.NotNull;

@Data
public class MemberAdminModifyRequest {

    @NotNull(message = "권한을 넣어주세요.")
    private MemberStatus memberStatus;
    private String nickName;
    private String phoneNumber;
    private String userName;
    private Long hospitalId;
}
