package site.date.dating.member.user.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.date.dating.member.user.api.dto.MemberCreateRequest;
import site.date.dating.member.user.api.dto.MemberLoginRequest;
import site.date.dating.member.user.api.dto.MemberLoginResponse;
import site.date.dating.member.user.api.dto.MemberModifyRequest;
import site.date.dating.member.user.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/check")
    public String checkServerStatus(){
        return "check complete";
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponse> login(@RequestBody @Validated MemberLoginRequest request) {
        return memberService.login(request);
    }

    @PostMapping("/signup")
    public MemberCreateResponse signup(@RequestBody @Validated MemberCreateRequest request) {
        return memberService.signup(request);
    }

    @GetMapping("/user/{memberId}/view")
    public MemberViewInfoResponse viewUserInformation(@PathVariable("memberId") Long memberId) {
        return memberService.viewUserInformation(memberId);
    }

    @PutMapping("/user/{memberId}/modify")
    public void modifyMemberByUser(
            @PathVariable("memberId") Long memberId,
            @RequestBody @Validated MemberModifyRequest request
    ) {
        memberService.modifyMemberByUser(memberId, request);
    }

}
