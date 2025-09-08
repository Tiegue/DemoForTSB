package nz.co.tsb.demofortsb.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nz.co.tsb.demofortsb.entity.Transaction;
import nz.co.tsb.demofortsb.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transactions", description = "Transaction operations")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
}
