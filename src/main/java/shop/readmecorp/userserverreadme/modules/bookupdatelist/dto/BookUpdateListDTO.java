package shop.readmecorp.userserverreadme.modules.bookupdatelist.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.readmecorp.userserverreadme.modules.book.dto.BookDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateListDTO {

    private Integer id;

    private BookDTO book;

    private String content;

    private String staus;
}