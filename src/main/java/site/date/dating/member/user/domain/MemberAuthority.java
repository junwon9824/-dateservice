package site.date.dating.member.user.domain;

import lombok.*;


@Entity
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"member_id", "authority_id"})})

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_authority_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_id")
    private Authority authority;

    private Long hospitalNo;

    @Builder
    public MemberAuthority(Member member, Authority authority, long hospitalNo) {
        this.member = member;
        this.authority = authority;
        this.hospitalNo = hospitalNo;
    }

    public void giveHospitalNumber(Long hospitalNo) {
        this.hospitalNo = hospitalNo;
    }

    public void giveAuthority(Authority authority) {
        this.authority = authority;
    }

}
