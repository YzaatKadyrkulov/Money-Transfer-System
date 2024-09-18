package bank.moneytransfersystem.api;

import bank.moneytransfersystem.entities.Cash;
import bank.moneytransfersystem.entities.Transfer;
import bank.moneytransfersystem.enums.Status;
import bank.moneytransfersystem.exception.IllegalArgumentException;
import bank.moneytransfersystem.exception.NotFoundException;
import bank.moneytransfersystem.service.CashService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import bank.moneytransfersystem.service.TransferService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/transfer/{cashId}")
@RequiredArgsConstructor
public class TransferApi {
    private final TransferService transferService;
    private final CashService cashService;

    @GetMapping
    public String getTransfers(Model model, @PathVariable Long cashId) {
        List<Transfer> allTransfers = transferService.findAllTransfers(cashId);
        model.addAttribute("allTransfers", allTransfers);
        model.addAttribute("cashId", cashId);
        return "/transfer/transfer-main";
    }

    @GetMapping("/new")
    public String creatTransfer(@PathVariable Long cashId, Model model, HttpSession session) throws NotFoundException {
        Long defaultCashId = (Long) session.getAttribute("currentCashId");

        if (defaultCashId == null) {
            defaultCashId = cashId;
            session.setAttribute("currentCashId", defaultCashId);
        }

        Transfer newTransfer = new Transfer();
        Cash fromCash = cashService.getCashById(defaultCashId);
        newTransfer.setFromCash(fromCash);

        model.addAttribute("newTransfer", newTransfer);

        List<Cash> cashList = cashService.findAllCash();
        model.addAttribute("cashList", cashList);
        return "/transfer/newTransfer";
    }

    @GetMapping("/created")
    public String created(@RequestParam("code") String uniqueCode, @RequestParam("status") String statusStr, Model model) {
        Status status;
        try {
            status = Status.valueOf(statusStr);
        } catch (java.lang.IllegalArgumentException e) {
            throw new java.lang.IllegalArgumentException("Invalid status value: " + statusStr);
        }
        model.addAttribute("uniqueCode", uniqueCode);
        model.addAttribute("status", status);
        return "/transfer/created_transfer";
    }

    @PostMapping("/save")
    public String saveTransfer(@ModelAttribute("newTransfer") Transfer transfer, HttpSession session) throws NotFoundException, IllegalArgumentException {
        Long cashId = (Long) session.getAttribute("currentCashId");
        if (cashId == null) {
            throw new IllegalStateException("Current cash ID is not set in the session");
        }

        String uniqueCode = transferService.createTransfer(cashId, transfer);
        return "redirect:/transfer/{cashId}/created?code=" + URLEncoder.encode(uniqueCode, StandardCharsets.UTF_8) + "&status=" + URLEncoder.encode(transfer.getStatus().name(), StandardCharsets.UTF_8);
    }

    @GetMapping("/{transferId}/getById")
    public String getTransferById(@PathVariable("transferId") Long transferId, Model model, @PathVariable("cashId") Long cashId) throws NotFoundException {
        model.addAttribute("findTransfer", transferService.getTransferById(transferId));
        model.addAttribute("cashId", cashId);
        return "/transfer/updateTransfer";
    }

    @PostMapping("/update/{transferId}")
    public String updateTransfer(@PathVariable("cashId") Long cashId,
                                 @ModelAttribute("findTransfer") Transfer transfer,
                                 @PathVariable("transferId") Long transferId) throws NotFoundException {
        transferService.updateTransferStatus(transferId, transfer);
        return "redirect:/transfer/{cashId}";
    }

    @GetMapping("/delete/{transferId}")
    public String deleteTransferById(@PathVariable("transferId") Long transferId) throws NotFoundException {
        transferService.deleteTransfer(transferId);
        return "redirect:/transfer/{cashId}";
    }

    @GetMapping("/getMoney")
    public String getMoneyPage() {
        return "/transfer/get_money";
    }

    @PostMapping("/processTransfer")
    public String processTransfer(@RequestParam("code") String code, Model model) {
        try {
            Transfer transfer = transferService.getTransferByCode(code);
            transferService.processTransfer(code);
            model.addAttribute("message", "Transfer successful");
            model.addAttribute("transfer", transfer);
        } catch (Exception e) {
            model.addAttribute("message", "Transfer failed: " + e.getMessage());
        }
        return "/transfer/process_result";
    }

    @GetMapping("/search")
    public String searchTransfers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Model model, @PathVariable String cashId) {

        model.addAttribute("cashId", cashId);

        List<Transfer> transfers = transferService.findByDateRange(startDate, endDate);
        model.addAttribute("transfers", transfers);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "/transfer/search-results";
    }
}
