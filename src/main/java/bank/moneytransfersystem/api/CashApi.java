package bank.moneytransfersystem.api;

import bank.moneytransfersystem.exception.IllegalArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import bank.moneytransfersystem.entities.Cash;
import bank.moneytransfersystem.exception.AlreadyExistsException;
import bank.moneytransfersystem.exception.NotFoundException;
import bank.moneytransfersystem.service.CashService;

import java.util.List;

@Controller
@RequestMapping("/cashes")
@RequiredArgsConstructor
public class CashApi {
    private final CashService cashService;

    @GetMapping("/allCash")
    public String getAllCompanies(Model model) {
        model.addAttribute("allCashes", cashService.findAllCash());
        return "mainPage";
    }

    @GetMapping("/new")
    public String addCash(Model model) {
        model.addAttribute("newCash", new Cash());
        return "/newCash";
    }

    @PostMapping("/save")
    private String saveCash(@ModelAttribute("newCash") Cash cash) throws AlreadyExistsException, IllegalArgumentException {
        cashService.saveCash(cash);
        return "redirect:/cashes/allCash";
    }

    @GetMapping("/{id}/delete")
    public String deleteCash(@PathVariable("id") Long cashId) throws NotFoundException {
        cashService.removeCash(cashId);
        return "redirect:/cashes/allCash";
    }

    @GetMapping("/{id}/update")
    public String getByIdToUpdate(Model model, @PathVariable("id") Long comId) throws NotFoundException {
        model.addAttribute("upCash", cashService.getCashById(comId));
        return "/update";
    }

    @PostMapping("/{id}/updateCash")
    public String update(@ModelAttribute("upCash") Cash cash, @PathVariable Long id) throws NotFoundException {
        cashService.updateCashById(id, cash);
        return "redirect:/cashes/allCash";
    }

    @GetMapping("/lets/{cashId}")
    public String getInfo(@PathVariable Long cashId, Model model) throws NotFoundException {
        model.addAttribute("cash", cashService.getCashById(cashId));
        return "more-cash";
    }

    @GetMapping("/search")
    public String searchForm(Model model) {
        return "searchForm";
    }

    @GetMapping("/cash/search")
    public String findByCashName(@RequestParam("name") String name, Model model) {
        List<Cash> cashList = cashService.findByCashName(name);
        model.addAttribute("cashList", cashList);
        model.addAttribute("searchName", name);
        return "cashList";
    }
}


