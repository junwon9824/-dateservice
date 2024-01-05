package site.date.dating.member.user.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long id;


    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Authorization authorizationStatus;


    public Authority(Authorization authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }

}
