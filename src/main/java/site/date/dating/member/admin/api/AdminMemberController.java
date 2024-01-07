package site.date.dating.member.admin.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.date.dating.member.admin.api.dto.MemberAdminCreateRequest;
import site.date.dating.member.admin.api.dto.MemberAdminModifyRequest;
import site.date.dating.member.admin.api.dto.MemberAdminViewInfoResponse;
import site.date.dating.member.admin.service.AdminMemberService;
import site.date.dating.member.user.api.dto.MemberCreateResponse;
import site.date.dating.member.user.api.dto.MemberSearchResponse;
import site.date.dating.member.user.domain.MemberStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/admin/user/search")
    public Page<MemberSearchResponse> adminSearchMembers(
            @RequestParam(value = "allSearch", required = false) String allSearch,
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "memberIdName", required = false) String memberIdName,
            @RequestParam(value = "nickName", required = false) String nickName,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "authorizationStatus", required = false) MemberStatus memberStatus,
            @RequestParam(value = "hospitalNumber", required = false) Long hospitalNumber,
            Pageable pageable
    ) {
        return adminMemberService.searchMembers(
                allSearch,
                memberId,
                memberIdName,
                nickName,
                userName,
                phoneNumber,
                memberStatus,
                hospitalNumber,
                pageable);
    }

    @GetMapping("/admin/user/view/{memberId}")
    public MemberAdminViewInfoResponse adminViewMember(@PathVariable("memberId") Long memberId) {
        return adminMemberService.viewMemberInformation(memberId);
    }

    @PostMapping("/admin/signup")
    public MemberCreateResponse adminSaveMember(
            @RequestBody @Validated MemberAdminCreateRequest request) {
        return adminMemberService.signup(request);
    }

    @DeleteMapping("/admin/user/delete/{memberId}")
    public void adminDeleteMember(@PathVariable("memberId") Long memberId) {
        adminMemberService.deleteMember(memberId);
    }

    @PutMapping("/admin/user/modify/{memberId}")
    public void adminModifyMember(
            @PathVariable("memberId") Long memberId,
            @RequestBody @Validated MemberAdminModifyRequest request
    ) {
        adminMemberService.modifyMember(memberId, request);
    }

}
