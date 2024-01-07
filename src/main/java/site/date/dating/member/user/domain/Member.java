package site.date.dating.member.user.domain;

import com.sun.istack.NotNull;
import lombok.*;
import site.date.dating.common.domain.BaseTimeEntity;

import javax.persistence.*;
 import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

//
//    @OneToMany(mappedBy = "member")
//    private List<Question> questions = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<Bookmark> bookmarks = new ArrayList<>();
//    @OneToMany(mappedBy = "member")
//    private List<Review> reviews = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<Answer> answers = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<ReviewLike> reviewLikes = new ArrayList<>();


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberAuthority> memberAuthorities = new ArrayList<>();

    //회원 아이디
    @Column(unique = true, nullable = false)
    @NotNull
    private String memberIdName;

    private String password;

    @NotNull
    private String nickName;

    @NotNull
    private String userName;

    private String phoneNumber;

    private Long hospitalNumber;

    @Enumerated(EnumType.STRING)
    @NotNull
    MemberStatus memberStatus;

//
//    @Builder
//    public Member(
//            String memberIdName,
//            String password,
//            String userName,
//            String nickName,
//            String phoneNumber,
//            MemberStatus memberStatus,
//            Long hospitalNumber
//    ) {
//        this.memberIdName = memberIdName;
//        this.password = password;
//        this.userName = userName;
//        this.nickName = nickName;
//        this.phoneNumber = phoneNumber;
//        this.memberStatus = memberStatus;
//
//        if (memberStatus == MemberStatus.STAFF) {
//            this.hospitalNumber = hospitalNumber;
//        }
//    }

    public void adminModifyMember(Member member) {
        this.nickName = member.getNickName();
        this.phoneNumber = member.getPhoneNumber();
        this.userName = member.getUserName();
        this.memberStatus = member.getMemberStatus();

        if (member.memberStatus == MemberStatus.STAFF) {
            this.hospitalNumber = member.getHospitalNumber();
        }
    }

    public void modifyMember(Member member) {
        this.nickName = member.getNickName();
        this.phoneNumber = member.getPhoneNumber();
        this.userName = member.getUserName();
    }

    public Member updateOauth(String name){
        this.nickName = name;
        this.userName = name;

        return this;
    }

}
