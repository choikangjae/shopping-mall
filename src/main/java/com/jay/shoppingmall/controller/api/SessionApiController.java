package com.jay.shoppingmall.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
@RequestMapping("/session")
@PreAuthorize("hasRole('USER')")
public class SessionApiController {

    @ApiIgnore
    @GetMapping("/clear")
    public ResponseEntity<?> sessionClear(HttpSession session) {
        session.removeAttribute("itemTotalPrice");
        session.removeAttribute("itemTotalQuantity");
        session.removeAttribute("itemOptions");

        return ResponseEntity.ok(null);
    }
}
