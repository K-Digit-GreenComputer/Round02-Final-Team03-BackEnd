package shop.readmecorp.userserverreadme.modules.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import shop.readmecorp.userserverreadme.common.auth.session.MyUserDetails;
import shop.readmecorp.userserverreadme.common.dto.ResponseDTO;
import shop.readmecorp.userserverreadme.common.exception.Exception400;
import shop.readmecorp.userserverreadme.modules.book.HeartConst;
import shop.readmecorp.userserverreadme.modules.book.dto.HeartDTO;
import shop.readmecorp.userserverreadme.modules.book.entity.Book;
import shop.readmecorp.userserverreadme.modules.book.entity.Heart;
import shop.readmecorp.userserverreadme.modules.book.request.HeartSaveRequest;
import shop.readmecorp.userserverreadme.modules.book.response.HeartResponse;
import shop.readmecorp.userserverreadme.modules.book.service.BookService;
import shop.readmecorp.userserverreadme.modules.book.service.HeartService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hearts")
public class HeartController {

    private final HeartService heartService;
    private final BookService bookService;

    public HeartController(HeartService heartService, BookService bookService) {
        this.heartService = heartService;
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<HeartDTO>> getPage(Pageable pageable) {
        Page<Heart> page = heartService.getPage(pageable);
        List<HeartDTO> content = page.getContent()
                .stream()
                .map(Heart::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PageImpl<>(content, pageable, page.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HeartResponse> getHeart(@PathVariable Integer id) {
        var optionalHeart = heartService.getHeart(id);
        if (optionalHeart.isEmpty()) {
            throw new Exception400(HeartConst.notFound);
        }

        return ResponseEntity.ok(optionalHeart.get().toResponse());
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> saveAndDeleteBook(
            @Valid @RequestBody HeartSaveRequest request,
            Errors error,
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ) {
        if (error.hasErrors()) {
            throw new Exception400(error.getAllErrors().get(0).getDefaultMessage());
        }

        Optional<Book> optionalBook = bookService.getBook(request.getBookId());
        if(optionalBook.isEmpty()) {
            throw new Exception400("도서 정보가 없습니다.");
        }
        heartService.save(request, optionalBook.get(), myUserDetails.getUser());
        return ResponseEntity.ok(new ResponseDTO<>(1, request.getCheck() ? "좋아요가 완료되었습니다." : "좋아요가 취소되었습니다.", null));
    }
}
