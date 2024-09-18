package bank.moneytransfersystem.service.impl;

import bank.moneytransfersystem.exception.IllegalArgumentException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import bank.moneytransfersystem.entities.Cash;
import bank.moneytransfersystem.exception.AlreadyExistsException;
import bank.moneytransfersystem.exception.NotFoundException;
import bank.moneytransfersystem.repo.CashRepository;
import bank.moneytransfersystem.service.CashService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CashServiceImpl implements CashService {
    private final CashRepository cashRepository;

    @Override
    public void saveCash(Cash cash) throws AlreadyExistsException, IllegalArgumentException {
        log.info("Attempting to save cash: {}", cash);

        if (cash == null) {
            log.error("Cash object is null");
            throw new IllegalArgumentException("Cash object must not be null");
        }

        if (cashRepository.existsByCashName(cash.getCashName())) {
            log.error("Cash with name '{}' already exists", cash.getCashName());
            throw new AlreadyExistsException("Cash with name '" + cash.getCashName() + "' already exists");
        }

        if (cash.getBalance() < 1000) {
            log.info("Balance is below minimum threshold. Setting balance to 1000.0");
            cash.setBalance(1000.0);
        }
        cashRepository.save(cash);
        log.info("Successfully saved cash: {}", cash);
    }

    @Override
    public void updateCashById(Long cashId, Cash cash) throws NotFoundException {
        log.info("Attempting to update cash with ID: {} with details: {}", cashId, cash);

        Cash existingCash = getCashByIdWithLogin(cashId);

        existingCash.setCashName(cash.getCashName());
        existingCash.setBalance(cash.getBalance());

        cashRepository.save(existingCash);

        log.info("Successfully updated cash with ID: {} to details: {}", cashId, existingCash);
    }

    @Override
    public List<Cash> findAllCash() {
        log.info("Fetching all cash records");
        List<Cash> cashList = cashRepository.findAll();
        log.info("Fetched {} cash records", cashList.size());
        return cashList;
    }

    @Override
    public List<Cash> findByCashName(String name) {
        log.info("Finding Cash by name: {}", name);
        List<Cash> result = cashRepository.findByCashName(name);
        log.info("Found {} Cash entries with name '{}'", result.size(), name);
        return result;
    }

    @Override
    public void removeCash(Long id) throws NotFoundException {
        log.info("Attempting to remove cash with ID: {}", id);

        Cash oldCash = getCashByIdWithLogin(id);
        cashRepository.delete(oldCash);

        log.info("Successfully removed cash with ID: {}", id);
    }

    @Override
    public Cash getCashById(Long id) throws NotFoundException {
        log.info("Fetching cash with ID: {}", id);
        return getCashByIdWithLogin(id);
    }

    private Cash getCashByIdWithLogin(Long cashId) throws NotFoundException {
        log.info("Attempting to retrieve Cash with ID: {}", cashId);
        return cashRepository.findById(cashId)
                .orElseThrow(() -> {
                    log.error("Cash with ID {} not found", cashId);
                    return new NotFoundException("Cash with ID " + cashId + " not found");
                });
    }
}
