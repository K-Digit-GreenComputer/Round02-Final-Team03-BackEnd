package shop.readmecorp.userserverreadme.modules.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import shop.readmecorp.userserverreadme.common.jpa.BaseTime;
import shop.readmecorp.userserverreadme.common.jpa.RoleType;
import shop.readmecorp.userserverreadme.modules.file.entity.FileInfo;
import shop.readmecorp.userserverreadme.modules.user.enums.UserStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_TB")
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("고유번호")
    private Integer id;

    @Comment("아이디")
    private String username;

    @Comment("비밀번호")
    private String password;

    @Comment("권한")
    private RoleType role;

    @Comment("멤버십 가입 여부")
    private Boolean isMembership;

    @Comment("자동 결제 여부")
    private Boolean isAutoPayment;

    //TODO 이거 써도되나?
    @Comment("유저 가입 시간")
    private LocalDateTime joinTime;

    @Comment("프로필 사진")
    @ManyToOne
    private FileInfo fileInfo;

    @Comment("유저 활성화 상태")
    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
