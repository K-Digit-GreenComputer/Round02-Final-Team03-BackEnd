package shop.readmecorp.userserverreadme.modules.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.readmecorp.userserverreadme.modules.book.dto.BookDTO;
import shop.readmecorp.userserverreadme.modules.user.dto.UserDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewHistoryDTO {

    private Integer id;

    private UserDTO user;

    private BookDTO book;

    private Integer lastPageNum;

    private String status;

}
