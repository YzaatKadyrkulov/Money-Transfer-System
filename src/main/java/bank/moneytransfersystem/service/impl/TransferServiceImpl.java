package bank.moneytransfersystem.service.impl;

import bank.moneytransfersystem.exception.IllegalArgumentException;
import bank.moneytransfersystem.repo.CashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import bank.moneytransfersystem.entities.Cash;
import bank.moneytransfersystem.entities.Transfer;
import bank.moneytransfersystem.enums.Status;
import bank.moneytransfersystem.exception.NotFoundException;
import bank.moneytransfersystem.repo.TransferRepository;
import bank.moneytransfersystem.service.CashService;
import bank.moneytransfersystem.service.TransferService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransferServiceImpl implements TransferService {
    private final TransferRepository transferRepository;
    private final CashService cashService;
    private final CashRepository cashRepository;

    @Override
    public String createTransfer(Long cashId, Transfer transfer) throws NotFoundException, IllegalArgumentException {
        log.info("Creating transfer with details: cashId={}, transfer={}", cashId, transfer);

        if (transfer.getFromCash() == null || transfer.getToCash() == null) {
            log.error("FromCash or ToCash is null in transfer: {}", transfer);
            throw new IllegalArgumentException("FromCash or ToCash must not be null");
        }

        Cash fromCash = cashService.getCashById(transfer.getFromCash().getId());
        Cash toCash = cashService.getCashById(transfer.getToCash().getId());

        if (transfer.getCurrency() == null) {
            log.error("Currency is null in transfer: {}", transfer);
            throw new IllegalArgumentException("Currency must not be null");
        }

        if (fromCash.getBalance() < transfer.getAmount()) {
            log.error("Insufficient balance in FromCash: {} for transfer amount: {}", fromCash, transfer.getAmount());
            throw new IllegalArgumentException("Insufficient balance in FromCash");
        }

        String uniqueCode = generateUniqueCode();
        transfer.setCode(uniqueCode);
        transfer.setStatus(Status.CREATED);
        transfer.setCreatedAt(LocalDateTime.now());

        transferRepository.save(transfer);

        fromCash.setBalance(fromCash.getBalance() - transfer.getAmount());
        toCash.setBalance(toCash.getBalance() + transfer.getAmount());

        cashService.updateCashById(fromCash.getId(), fromCash);
        cashService.updateCashById(toCash.getId(), toCash);

        log.info("Transfer created successfully with unique code: {}", uniqueCode);
        return uniqueCode;
    }

    private String generateUniqueCode() {
        String code = UUID.randomUUID().toString();
        log.debug("Generated unique code: {}", code);
        return code;
    }

    @Override
    public Transfer getTransferByCode(String code) throws NotFoundException {
        log.info("Fetching transfer with code: {}", code);
        Transfer transfer = transferRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Transfer with code " + code + " not found"));
        log.info("Fetched transfer: {}", transfer);
        return transfer;
    }

    @Override
    public Transfer getTransferById(Long id) throws NotFoundException {
        log.info("Fetching transfer with ID: {}", id);
        return findTransferById(id);
    }

    @Override
    public Transfer updateTransferStatus(Long transferId, Transfer transfer) throws NotFoundException {
        log.info("Updating status of transfer with ID: {} to {}", transferId, transfer);
        Transfer oldTransfer = findTransferById(transferId);
        oldTransfer.setStatus(transfer.getStatus());
        Transfer updatedTransfer = transferRepository.save(oldTransfer);
        log.info("Updated transfer status: {}", updatedTransfer);
        return updatedTransfer;
    }

    @Override
    public void deleteTransfer(Long transferId) throws NotFoundException {
        log.info("Deleting transfer with ID: {}", transferId);
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new NotFoundException("Transfer not found"));
        transferRepository.delete(transfer);
        log.info("Deleted transfer with ID: {}", transferId);
    }

    public void processTransfer(String code) throws NotFoundException, IllegalArgumentException {
        log.info("Processing transfer with code: {}", code);
        Transfer transfer = transferRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Transfer with code " + code + " not found"));

        if (transfer.getFromCash() == null || transfer.getToCash() == null) {
            log.error("Source or destination cash is not specified in transfer: {}", transfer);
            throw new IllegalArgumentException("Source or destination cash is not specified");
        }

        Cash fromCash = transfer.getFromCash();
        Cash toCash = transfer.getToCash();
        double amount = transfer.getAmount();

        if (fromCash.getBalance() < amount) {
            log.error("Insufficient balance in source cash: {} for transfer amount: {}", fromCash, amount);
            throw new IllegalArgumentException("Insufficient balance in source cash");
        }

        log.debug("Before transfer: fromCash balance = {}, toCash balance = {}", fromCash.getBalance(), toCash.getBalance());

        fromCash.setBalance(fromCash.getBalance() - amount);
        toCash.setBalance(toCash.getBalance() + amount);

        cashRepository.save(fromCash);
        cashRepository.save(toCash);

        log.debug("After transfer: fromCash balance = {}, toCash balance = {}", fromCash.getBalance(), toCash.getBalance());

        transfer.setStatus(Status.ISSUED);
        transferRepository.save(transfer);
    }

    @Override
    public List<Transfer> findAllTransfers(Long cashId) {
        log.info("Fetching all transfers for cashId: {}", cashId);
        List<Transfer> transfers = transferRepository.findAllTransfers(cashId);
        log.info("Fetched {} transfers", transfers.size());
        return transfers;
    }

    @Override
    public boolean processCode(String code) {
        boolean isValid = "VALID_CODE".equals(code);
        log.info("Processing code: {}. Valid: {}", code, isValid);
        return isValid;
    }

    @Override
    public List<Transfer> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("startDate: {}, endDate: {}", startDate, endDate);
        return transferRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public List<Transfer> findTransfers(LocalDateTime fromDate, LocalDateTime toDate, String comment) {
        log.debug("fromDate: {}, toDate: {}, comment: {},", fromDate, toDate, comment);
        return transferRepository.findTransfersByCriteria(fromDate, toDate, comment);
    }

    public Transfer findTransferById(Long transferId) throws NotFoundException {
        log.info("Finding transfer with ID: {}", transferId);
        return transferRepository.findById(transferId)
                .orElseThrow(() -> new NotFoundException("Transfer with ID " + transferId + " not found"));
    }
}

