package shop.readmecorp.userserverreadme.modules.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.readmecorp.userserverreadme.common.auth.session.MyUserDetails;
import shop.readmecorp.userserverreadme.common.dto.ResponseDTO;
import shop.readmecorp.userserverreadme.common.exception.Exception400;
import shop.readmecorp.userserverreadme.modules.book.entity.Book;
import shop.readmecorp.userserverreadme.modules.book.service.BookService;
import shop.readmecorp.userserverreadme.modules.bootpay.request.BootPaySaveRequest;
import shop.readmecorp.userserverreadme.modules.bootpay.service.BootPayService;
import shop.readmecorp.userserverreadme.modules.membership.entity.Membership;
import shop.readmecorp.userserverreadme.modules.membership.service.MembershipService;
import shop.readmecorp.userserverreadme.modules.payment.dto.BookPaymentDTO;
import shop.readmecorp.userserverreadme.modules.payment.dto.MembershipPaymentDTO;
import shop.readmecorp.userserverreadme.modules.payment.entity.MembershipPayment;
import shop.readmecorp.userserverreadme.modules.payment.request.BookPaymentSaveRequest;
import shop.readmecorp.userserverreadme.modules.payment.request.MembershipPaymentSaveRequest;
import shop.readmecorp.userserverreadme.modules.payment.response.MyPaymentResponse;
import shop.readmecorp.userserverreadme.modules.payment.service.BookPaymentService;
import shop.readmecorp.userserverreadme.modules.payment.service.MembershipPaymentService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final BookPaymentService bookPaymentService;
    private final MembershipPaymentService membershipPaymentService;
    private final MembershipService membershipService;
    private final BookService bookService;

    public PaymentController(BookPaymentService bookPaymentService, MembershipPaymentService membershipPaymentService, MembershipService membershipService, BookService bookService, BootPayService bootPayService) {
        this.bookPaymentService = bookPaymentService;
        this.membershipPaymentService = membershipPaymentService;
        this.membershipService = membershipService;
        this.bookService = bookService;
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseDTO<MyPaymentResponse>> getMyPaymentResponse(
            @AuthenticationPrincipal MyUserDetails myUserDetails
    ) {
        List<BookPaymentDTO> bookPayments = bookPaymentService.getMyList(myUserDetails.getUser());
        List<MembershipPaymentDTO> membershipPayments = membershipPaymentService.getMyList(myUserDetails.getUser());
        return ResponseEntity.ok(new ResponseDTO<>(1, "결제 목록 조회가 완료되었습니다.", new MyPaymentResponse(bookPayments, membershipPayments)));
    }

    @GetMapping("/membership/{id}")
    public ResponseEntity<ResponseDTO<MembershipPaymentDTO>> getMembershipPaymentResponse(
            @AuthenticationPrincipal MyUserDetails myUserDetails,
            @PathVariable Integer id
    ) {
        Optional<MembershipPaymentDTO> membershipPaymentOptional = membershipPaymentService.getMemberShipPayment(id, myUserDetails.getUser());
        if (membershipPaymentOptional.isEmpty()) {
            throw new Exception400("멤버십 결제 내역이 없습니다.");
        }
        return ResponseEntity.ok(new ResponseDTO<>(1, "멤버십 결제가 완료되었습니다.", membershipPaymentOptional.get()));
    }

    @GetMapping("/books/{paymentNo}")
    public ResponseEntity<ResponseDTO<List<BookPaymentDTO>>> getBooksPaymentResponse(
            @AuthenticationPrincipal MyUserDetails myUserDetails,
            @PathVariable Integer paymentNo
    ) {
        List<BookPaymentDTO> bookPayments = bookPaymentService.getBookPayments(paymentNo, myUserDetails.getUser());
        if(bookPayments.size() == 0) {
            throw new Exception400("도서 구매 내역이 없습니다.");
        }
        return ResponseEntity.ok(new ResponseDTO<>(1, "도서 구매가 완료 완료되었습니다.", bookPayments));
    }


    @PostMapping("/book")
    public ResponseEntity<ResponseDTO<Integer>> saveBook(
            @AuthenticationPrincipal MyUserDetails myUserDetails,
            @Valid @RequestBody BookPaymentSaveRequest request
    ) {
        List<Book> books = bookService.getBooks(request.getBookId());
        if (books.size() != request.getBookId().size()) {
            throw new Exception400("존재하지 않는 도서입니다.");
        }

        Integer paymentNo = bookPaymentService.save(myUserDetails.getUser(), books);

        return ResponseEntity.ok(new ResponseDTO<>(1, "결제 데이터 삽입 완료되었습니다.", paymentNo));
    }

    @PostMapping("/membership")
    public ResponseEntity<ResponseDTO<Integer>> saveMembership(
            @AuthenticationPrincipal MyUserDetails myUserDetails,
            @Valid @RequestBody MembershipPaymentSaveRequest request
    ) {
        Optional<Membership> optionalMembership = membershipService.getMembership(request.getMembershipId());
        if (optionalMembership.isEmpty()) {
            throw new Exception400("존재하지 않는 멤버십입니다.");
        }

        Integer paymentNo = membershipPaymentService.save(myUserDetails.getUser(), request, optionalMembership.get());
        return ResponseEntity.ok(new ResponseDTO<>(1, "결제 데이터 삽입 완료되었습니다.", paymentNo));
    }

    @DeleteMapping("/{membershipId}/membership")
    public ResponseEntity<ResponseDTO<Void>> deleteMemberShip (
            @AuthenticationPrincipal MyUserDetails myUserDetails,
            @PathVariable Integer membershipId
    ) {
        Optional<MembershipPayment> membershipPaymentOptional = membershipPaymentService.getMyMemberShip(myUserDetails.getUser(), membershipId);
        if (membershipPaymentOptional.isEmpty()) {
            throw new Exception400("멤버십 상태가 아닙니다.");
        }

        membershipPaymentService.delete(membershipPaymentOptional.get());
        return ResponseEntity.ok(new ResponseDTO<>(1, "멤버십 해지가 완료되었습니다.", null));
    }
}