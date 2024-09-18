package bank.moneytransfersystem.service;

import bank.moneytransfersystem.entities.Transfer;
import bank.moneytransfersystem.exception.IllegalArgumentException;
import bank.moneytransfersystem.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface TransferService {
    String createTransfer(Long cashId, Transfer transfer) throws NotFoundException, IllegalArgumentException;

    Transfer getTransferByCode(String code) throws NotFoundException;

    Transfer getTransferById(Long id) throws NotFoundException;

    Transfer updateTransferStatus(Long transferId, Transfer transfer) throws NotFoundException;

    void deleteTransfer(Long transferId) throws NotFoundException;

    void processTransfer(String transfer) throws Exception;

    boolean processCode(String code);

    List<Transfer> findTransfers(LocalDateTime fromDate, LocalDateTime toDate, String comment);

    List<Transfer> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Transfer> findAllTransfers(Long cashId);
}

